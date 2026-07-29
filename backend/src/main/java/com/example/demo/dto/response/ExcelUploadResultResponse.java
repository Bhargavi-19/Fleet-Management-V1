package com.example.demo.dto.response;

import java.util.ArrayList;
import java.util.List;

/**
 * What happened when a rate sheet was uploaded.
 *
 * Good rows are saved even when other rows are bad, so this reports both:
 * how many went in, and exactly what was wrong with the ones that did not.
 */
public class ExcelUploadResultResponse {

    private String fileName;

    /** Data rows found in the sheet (the header row is not counted). */
    private int totalRows;

    private int inserted;
    private int updated;
    private int failed;

    private List<RowError> errors = new ArrayList<>();

    public ExcelUploadResultResponse() {
    }

    /** One rejected row, with the spreadsheet row number so it is easy to find. */
    public static class RowError {

        private int rowNumber;
        private String message;

        public RowError() {
        }

        public RowError(int rowNumber, String message) {
            this.rowNumber = rowNumber;
            this.message = message;
        }

        public int getRowNumber() { return rowNumber; }
        public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public void addError(int rowNumber, String message) {
        this.errors.add(new RowError(rowNumber, message));
        this.failed++;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }

    public int getInserted() { return inserted; }
    public void setInserted(int inserted) { this.inserted = inserted; }

    public int getUpdated() { return updated; }
    public void setUpdated(int updated) { this.updated = updated; }

    public int getFailed() { return failed; }
    public void setFailed(int failed) { this.failed = failed; }

    public List<RowError> getErrors() { return errors; }
    public void setErrors(List<RowError> errors) { this.errors = errors; }
}
