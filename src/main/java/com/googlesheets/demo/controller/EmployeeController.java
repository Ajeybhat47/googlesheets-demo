package com.googlesheets.demo.controller;


import com.googlesheets.demo.model.Employee;
import com.googlesheets.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/get-all-employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/get-employee-by-id/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employee);
    }

    @PostMapping("/add-employee")
    public ResponseEntity<String> addEmployee(@RequestBody Employee employee) {
        try {
            return ResponseEntity.ok(employeeService.addEmployee(employee));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to add employee: " + e.getMessage());
        }
    }

    @PutMapping("/update-employee")
    public ResponseEntity<String> updateEmployee(@RequestBody Employee employee) {
        try {
            return ResponseEntity.ok(employeeService.updateEmployee(employee));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to update employee: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-employee/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(employeeService.deleteEmployee(id));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to delete employee: " + e.getMessage());
        }
    }

}
