package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.response.ExcelUploadResultResponse;
import com.example.demo.response.ApiResponse;

public interface ExcelUploadService {

    /**
     * Reads a vehicle rate sheet and saves the valid rows against the
     * logged-in staff member's hub (BRD 3.5).
     */
    ApiResponse<ExcelUploadResultResponse> uploadCarTypeRates(MultipartFile file);

    /** The blank .xlsx template staff download before filling it in. */
    byte[] buildCarTypeRateTemplate();
}
