package com.googlesheets.demo.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;



public interface GoogleSheetsService {

    /**
     * Retrieves data from Google Sheets and updates the local database.
     *
     * @throws IOException if an I/O error occurs
     * @throws GeneralSecurityException if a security error occurs
     */
    void getSpreadsheetValues() throws IOException, GeneralSecurityException;

    /**
     * Updates Google Sheets with the data from the local database.
     *
     * @throws IOException if an I/O error occurs
     * @throws GeneralSecurityException if a security error occurs
     */
    void updateSheetFromDatabase() throws IOException, GeneralSecurityException;

}
