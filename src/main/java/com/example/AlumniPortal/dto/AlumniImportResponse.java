package com.example.AlumniPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlumniImportResponse {

    private int totalRows;
    private int importedCount;
    private int updatedCount;
    private int skippedCount;
    private String message;
}
