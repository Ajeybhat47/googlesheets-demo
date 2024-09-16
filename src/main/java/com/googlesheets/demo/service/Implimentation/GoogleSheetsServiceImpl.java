package com.googlesheets.demo.service.Implimentation;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.googlesheets.demo.config.GoogleAuthorizationConfig;
import com.googlesheets.demo.model.Employee;
import com.googlesheets.demo.repo.EmployeeRepository;
import com.googlesheets.demo.service.GoogleSheetsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GoogleSheetsServiceImpl implements GoogleSheetsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSheetsServiceImpl.class);

    @Value("${spreadsheet.id}")
    private String spreadsheetId;

    @Autowired
    private GoogleAuthorizationConfig googleAuthorizationConfig;

    @Autowired
    private EmployeeRepository employeeRepository;

    private final Sheets sheetsService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public GoogleSheetsServiceImpl(GoogleAuthorizationConfig googleAuthorizationConfig) throws GeneralSecurityException, IOException {
        this.sheetsService = googleAuthorizationConfig.getSheetsService();
    }

    @Override
    public void getSpreadsheetValues() throws IOException {
        Sheets.Spreadsheets.Values.BatchGet request =
                sheetsService.spreadsheets().values().batchGet(spreadsheetId);

        request.setRanges(getSpreadSheetRange());
        request.setMajorDimension("ROWS");

        BatchGetValuesResponse response = request.execute();
        List<List<Object>> spreadSheetValues = response.getValueRanges().get(0).getValues();

        if (spreadSheetValues == null || spreadSheetValues.isEmpty()) {
            LOGGER.info("No data found in Google Sheets.");
            return;
        }

        spreadSheetValues.remove(0); // Remove headers

        for (List<Object> row : spreadSheetValues) {
            Long id = Long.parseLong((String) row.get(0));
            String name = (String) row.get(1);
            String city = (String) row.get(2);
            int age = Integer.parseInt((String) row.get(3));
            double salary = Double.parseDouble((String) row.get(4));

            String sheetModifiedTime = (String) row.get(5);
            LocalDateTime parsedSheetTime = LocalDateTime.parse(sheetModifiedTime, FORMATTER);

            Employee employee = new Employee(id, name, age, city, salary, sheetModifiedTime, (long) spreadSheetValues.indexOf(row) + 2);

            Employee existingEmployee = employeeRepository.findById(id).orElse(null);
            if (existingEmployee != null) {
                LocalDateTime dbModifiedTime = LocalDateTime.parse(existingEmployee.getLastModifiedTime(), FORMATTER);
                if (parsedSheetTime.isAfter(dbModifiedTime)) {
                    updateEmployeeData(existingEmployee, name, city, age, salary, sheetModifiedTime);
                }
            } else {
                employeeRepository.save(employee);
            }
        }
        LOGGER.info("Employee data updated in the database from Google Sheets.");
    }

    private void updateEmployeeData(Employee existingEmployee, String name, String city, int age, double salary, String lastModifiedTime) {
        existingEmployee.setName(name);
        existingEmployee.setCity(city);
        existingEmployee.setAge(age);
        existingEmployee.setSalary(salary);
        existingEmployee.setLastModifiedTime(lastModifiedTime);
        employeeRepository.save(existingEmployee);
    }

    @Override
    public void updateSheetFromDatabase() throws IOException {
        List<Employee> employees = employeeRepository.findAll();
        List<List<Object>> sheetValues = getSheetValues();
        Map<Long, String> sheetDataMap = new HashMap<>();

        if (sheetValues != null && !sheetValues.isEmpty()) {
            for (int i = 1; i < sheetValues.size(); i++) {
                List<Object> row = sheetValues.get(i);
                Long id = Long.parseLong((String) row.get(0));
                String timestamp = (String) row.get(5);
                sheetDataMap.put(id, timestamp);
            }
        }

        List<List<Object>> values = new ArrayList<>();
        values.add(Arrays.asList("ID", "Name", "City", "Age", "Salary", "Last Modified Time"));

        for (Employee employee : employees) {
            String currentTime = employee.getLastModifiedTime();
            if (!sheetDataMap.containsKey(employee.getId()) ||
                    currentTime.compareTo(sheetDataMap.get(employee.getId())) > 0) {
                values.add(Arrays.asList(
                        employee.getId().toString(),
                        employee.getName(),
                        employee.getCity(),
                        String.valueOf(employee.getAge()),
                        String.valueOf(employee.getSalary()),
                        currentTime
                ));
            }
        }

        String range = "Sheet1!A1:Z";
        sheetsService.spreadsheets().values()
                .clear(spreadsheetId, range, null)
                .execute();

        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .update(spreadsheetId, "Sheet1!A1", body)
                .setValueInputOption("RAW")
                .execute();

        LOGGER.info("Google Sheet updated with employee data from the database.");
    }




    void deleteRowFromSheet(Long employeeId) {
        try {
            List<List<Object>> sheetValues = getSheetValues();
            if (sheetValues != null) {
                for (int i = 1; i < sheetValues.size(); i++) {
                    List<Object> row = sheetValues.get(i);
                    if (Long.parseLong((String) row.get(0)) == employeeId) {
                        String range = "Sheet1!A" + (i + 1) + ":F" + (i + 1);
                        sheetsService.spreadsheets().values().clear(spreadsheetId, range, null).execute();
                        LOGGER.info("Deleted row for employee ID {} in Google Sheets", employeeId);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to delete row in Google Sheets: {}", e.getMessage());
        }
    }



    void updateGoogleSheetWithEmployee(Employee employee) {
        try {
            List<Object> employeeData = Arrays.asList(
                    employee.getId().toString(),
                    employee.getName(),
                    employee.getCity(),
                    String.valueOf(employee.getAge()),
                    String.valueOf(employee.getSalary()),
                    employee.getLastModifiedTime()
            );
            String range = "Sheet1!A" + employee.getRowNum();
            ValueRange body = new ValueRange().setValues(Collections.singletonList(employeeData));
            sheetsService.spreadsheets().values().update(spreadsheetId, range, body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (IOException e) {
            LOGGER.error("Failed to update Google Sheets: {}", e.getMessage());
        }
    }

    Long getLastRowNum() throws IOException {
        String range = "Sheet1!A:A";
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();
        return values != null ? (long) values.size() : 0;
    }

    private List<List<Object>> getSheetValues() throws IOException {
        String range = "Sheet1!A1:Z";
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        return response.getValues();
    }

    private List<String> getSpreadSheetRange() throws IOException {
        Sheets.Spreadsheets.Get request = sheetsService.spreadsheets().get(spreadsheetId);
        Spreadsheet spreadsheet = request.execute();
        Sheet sheet = spreadsheet.getSheets().get(0);
        int row = sheet.getProperties().getGridProperties().getRowCount();
        int col = sheet.getProperties().getGridProperties().getColumnCount();
        return Collections.singletonList("R1C1:R" + row + "C" + col);
    }


}
