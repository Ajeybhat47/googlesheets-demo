package com.googlesheets.demo.service.Implimentation;

import com.googlesheets.demo.model.Employee;
import com.googlesheets.demo.repo.EmployeeRepository;
import com.googlesheets.demo.service.EmployeeService;
import com.googlesheets.demo.service.GoogleSheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;
import static org.hibernate.type.LocalTimeType.FORMATTER;

@Service
public class EmployeeServiceImpl implements EmployeeService {

//    repository for employee
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private GoogleSheetsServiceImpl googleSheetsService;


    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }


    @Override
    public String addEmployee(Employee employee) {
        employee.setLastModifiedTime(LocalDateTime.now().format(FORMATTER));
        Long lastRowNum = 0L;
        try {
            lastRowNum = googleSheetsService.getLastRowNum();
        } catch (IOException e) {
            LOGGER.error("Failed to get the last row number: {}");
        }

        employee.setRowNum(lastRowNum + 1);
        employeeRepository.save(employee);

        googleSheetsService.updateGoogleSheetWithEmployee(employee);
        return "Employee added successfully!";
    }

    @Override
    public String updateEmployee(Employee employee) {

        employee.setLastModifiedTime(LocalDateTime.now().format(FORMATTER));
        Employee existingEmployee = employeeRepository.findById(employee.getId()).orElse(null);
        if (existingEmployee == null) {
            return "Employee not found!";
        }

        if(employee.getRowNum() == null )
        {
            employee.setRowNum(existingEmployee.getRowNum());
        }
        employeeRepository.save(employee);
        googleSheetsService.updateGoogleSheetWithEmployee(employee);

        return "Employee updated successfully!";
    }

    @Override
    public String deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            googleSheetsService.deleteRowFromSheet(id);
            return "Employee deleted successfully!";
        }
        return "Employee not found!";
    }
}


