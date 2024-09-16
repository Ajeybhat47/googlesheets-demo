package com.googlesheets.demo.service;

import com.googlesheets.demo.model.Employee;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


public interface EmployeeService {

    /**
     * Retrieves all employee records from the local database.
     *
     * @return a list of all employees
     */
    List<Employee> getAllEmployees();

    /**
     * Retrieves an employee record by ID from the local database.
     *
     * @param id the ID of the employee
     * @return the employee with the given ID, or null if not found
     */
    Employee getEmployeeById(Long id);

    /**
     * Adds a new employee record to both the local database and Google Sheets.
     *
     * @param employee the employee to add
     * @return a message indicating the result of the operation
     */
    String addEmployee(Employee employee) throws IOException;

    /**
     * Updates an existing employee record in both the local database and Google Sheets.
     *
     * @param employee the employee with updated details
     * @return a message indicating the result of the operation
     */
    String updateEmployee(Employee employee) throws IOException;

    /**
     * Deletes an employee record from both the local database and Google Sheets.
     *
     * @param id the ID of the employee to delete
     * @return a message indicating the result of the operation
     */
    String deleteEmployee(Long id) throws IOException;
}
