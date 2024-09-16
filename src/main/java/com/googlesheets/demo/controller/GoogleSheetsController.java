package com.googlesheets.demo.controller;

import com.googlesheets.demo.service.EmployeeService;
import com.googlesheets.demo.service.GoogleSheetsService;
import com.googlesheets.demo.model.Employee; // Ensure this import is correct
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/googlesheets")
public class GoogleSheetsController {

    @Autowired
    private GoogleSheetsService googleSheetsService;


    @GetMapping("/get-spreadsheet-values")
    public ResponseEntity<String> getSpreadsheetValues() {
        try {
            googleSheetsService.getSpreadsheetValues();
            return ResponseEntity.ok("Google Sheets data retrieved successfully!");
        } catch (IOException | GeneralSecurityException e) {
            return ResponseEntity.badRequest().body("Failed to retrieve Google Sheets data: " + e.getMessage());
        }
    }

    @GetMapping("/update-sheet-from-database")
    public ResponseEntity<String> updateSheetFromDatabase() {
        try {
            googleSheetsService.updateSheetFromDatabase();
            return ResponseEntity.ok("Google Sheets updated successfully!");
        } catch (IOException | GeneralSecurityException e) {
            return ResponseEntity.badRequest().body("Failed to update Google Sheets: " + e.getMessage());
        }
    }




}
