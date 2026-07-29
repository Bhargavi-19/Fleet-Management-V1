package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.response.ExcelUploadResultResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.ExcelUploadService;

/**
 * Bulk vehicle rate upload (BRD 3.5).
 *
 * Sits under /api/staff so the existing security rules already restrict it
 * to ROLE_STAFF. Rates always apply to the uploading staff member's own hub.
 */
@RestController
@RequestMapping("/api/staff/excel")
public class ExcelUploadController {

    private final ExcelUploadService excelUploadService;

    public ExcelUploadController(ExcelUploadService excelUploadService) {
        this.excelUploadService = excelUploadService;
    }

    /** Upload a filled-in rate sheet. */
    @PostMapping(value = "/car-types", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ExcelUploadResultResponse> uploadCarTypeRates(
            @RequestParam("file") MultipartFile file) {

        return excelUploadService.uploadCarTypeRates(file);
    }

    /** Download the blank template - "Excel format to enter the data". */
    @GetMapping("/car-types/template")
    public ResponseEntity<byte[]> downloadTemplate() {

        byte[] workbook = excelUploadService.buildCarTypeRateTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData(
                "attachment", "vehicle-rate-template.xlsx");
        headers.setContentLength(workbook.length);

        return new ResponseEntity<>(workbook, headers, HttpStatus.OK);
    }
}
