package com.example.demo.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.response.ExcelUploadResultResponse;
import com.example.demo.entity.base.CarType;
import com.example.demo.entity.base.Hub;
import com.example.demo.entity.base.Staff;
import com.example.demo.enums.CarClass;
import com.example.demo.exception.error.BusinessException;
import com.example.demo.exception.error.ResourceNotFoundException;
import com.example.demo.exception.error.UnauthorizedActionException;
import com.example.demo.repository.CarTypeRepository;
import com.example.demo.repository.StaffRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.ExcelUploadService;

/**
 * ============================================================
 *  EXCEL UPLOAD - VEHICLE RATE DATA  (BRD 3.5)
 * ============================================================
 *
 * "The system should provide user to upload vehicle rate data in bulk thru'
 *  Excel. Excel format to enter the data should be provided."
 *
 * EXPECTED SHEET
 * --------------
 *  Row 1 is the header and is skipped. From row 2 onwards:
 *
 *   A  Car Class      SMALL | COMPACT | INTERMEDIATE | SEDAN | SUV
 *   B  Car Type       free text, e.g. "Full Size SUV"
 *   C  Daily Rate     number greater than 0
 *   D  Weekly Rate    number greater than 0
 *   E  Monthly Rate   number greater than 0
 *   F  Effective From date (yyyy-MM-dd or a real Excel date cell)
 *   G  Effective To   date, must not be before Effective From
 *   H  Image URL      optional
 *
 * RULES
 * -----
 *  - Rates always apply to the hub the uploading staff member belongs to.
 *    Nobody can change another hub's pricing.
 *  - One rate row per car class per hub. If that combination already exists
 *    the rates are UPDATED, otherwise a new row is INSERTED. That is what
 *    makes this a rate *upload* rather than a duplicate-creating import.
 *  - A bad row never stops the file. Valid rows are saved and every rejected
 *    row is reported back with its row number and the reason.
 */
@Service
public class ExcelUploadServiceImpl implements ExcelUploadService {

    private static final Logger log =
            LoggerFactory.getLogger(ExcelUploadServiceImpl.class);

    /** Column order of the template. */
    private static final String[] HEADERS = {
            "Car Class", "Car Type", "Daily Rate", "Weekly Rate",
            "Monthly Rate", "Effective From", "Effective To", "Image URL"
    };

    private static final DateTimeFormatter ISO_DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** Reject anything larger, before POI even opens it. */
    private static final long MAX_FILE_SIZE_BYTES = 2 * 1024 * 1024; // 2 MB

    private final CarTypeRepository carTypeRepository;
    private final StaffRepository staffRepository;

    public ExcelUploadServiceImpl(
            CarTypeRepository carTypeRepository,
            StaffRepository staffRepository) {

        this.carTypeRepository = carTypeRepository;
        this.staffRepository = staffRepository;
    }

    // =====================================================
    // Upload
    // =====================================================

    @Override
    @Transactional
    public ApiResponse<ExcelUploadResultResponse> uploadCarTypeRates(MultipartFile file) {

        validateFile(file);

        Hub hub = getLoggedInStaffHub();

        ExcelUploadResultResponse result = new ExcelUploadResultResponse();
        result.setFileName(file.getOriginalFilename());

        try (InputStream in = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(in)) {

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null || sheet.getPhysicalNumberOfRows() < 2) {
                throw new BusinessException(
                        "The sheet has no data rows. Download the template and fill it in first.");
            }

            // Row 0 is the header, so start at 1.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (isBlankRow(row)) {
                    continue;      // trailing empty rows are normal, ignore them
                }

                result.setTotalRows(result.getTotalRows() + 1);

                // +1 because spreadsheets are 1-based, so the user sees row 2 as row 2.
                int displayRow = i + 1;

                try {
                    boolean created = saveRateRow(row, hub);
                    if (created) {
                        result.setInserted(result.getInserted() + 1);
                    } else {
                        result.setUpdated(result.getUpdated() + 1);
                    }
                } catch (Exception rowError) {
                    // One bad row must not lose the whole file.
                    result.addError(displayRow, rowError.getMessage());
                }
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Could not read the uploaded rate sheet", e);
            throw new BusinessException(
                    "That file could not be read. Please upload a valid .xlsx file.");
        }

        String message = String.format(
                "%d row(s) read: %d added, %d updated, %d rejected.",
                result.getTotalRows(), result.getInserted(),
                result.getUpdated(), result.getFailed());

        log.info("Rate sheet '{}' uploaded for hub {} - {}",
                result.getFileName(), hub.getHubId(), message);

        return new ApiResponse<>(true, message, result);
    }

    /**
     * Validates and saves one row.
     *
     * @return true when a new rate row was created, false when an existing
     *         one was updated.
     */
    private boolean saveRateRow(Row row, Hub hub) {

        // ---- Car class ----
        String carClassText = readString(row, 0);
        if (carClassText == null) {
            throw new BusinessException("Car Class is required");
        }

        CarClass carClass;
        try {
            carClass = CarClass.valueOf(carClassText.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "'" + carClassText + "' is not a valid Car Class. Use one of: SMALL, COMPACT, INTERMEDIATE, SEDAN, SUV");
        }

        // ---- Car type name ----
        String carTypeName = readString(row, 1);
        if (carTypeName == null) {
            throw new BusinessException("Car Type is required");
        }
        if (carTypeName.length() > 100) {
            throw new BusinessException("Car Type cannot be longer than 100 characters");
        }

        // ---- Rates ----
        double daily = readPositiveRate(row, 2, "Daily Rate");
        double weekly = readPositiveRate(row, 3, "Weekly Rate");
        double monthly = readPositiveRate(row, 4, "Monthly Rate");

        // A weekly rate above 7 daily rates almost always means a typo.
        if (weekly > daily * 7) {
            throw new BusinessException(
                    "Weekly Rate is higher than 7 x Daily Rate - please check the figures");
        }
        if (monthly > daily * 30) {
            throw new BusinessException(
                    "Monthly Rate is higher than 30 x Daily Rate - please check the figures");
        }

        // ---- Dates ----
        LocalDate from = readDate(row, 5, "Effective From");
        LocalDate to = readDate(row, 6, "Effective To");

        if (from != null && to != null && to.isBefore(from)) {
            throw new BusinessException("Effective To cannot be before Effective From");
        }

        // ---- Optional image ----
        String imageUrl = readString(row, 7);

        // ---- Insert or update ----
        Optional<CarType> existing =
                carTypeRepository.findByHub_HubIdAndCarClass(hub.getHubId(), carClass);

        CarType carType = existing.orElseGet(CarType::new);
        boolean isNew = existing.isEmpty();

        carType.setHub(hub);
        carType.setCarClass(carClass);
        carType.setCarType(carTypeName.trim());
        carType.setDailyRate(daily);
        carType.setWeeklyRate(weekly);
        carType.setMonthlyRate(monthly);
        carType.setEffectiveFrom(from);
        carType.setEffectiveTo(to);

        // Keep the existing picture when the sheet leaves the column blank.
        if (imageUrl != null) {
            carType.setImageUrl(imageUrl.trim());
        }

        carTypeRepository.save(carType);

        return isNew;
    }

    // =====================================================
    // Template
    // =====================================================

    @Override
    public byte[] buildCarTypeRateTemplate() {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Vehicle Rates");

            // Bold, coloured header so it is obvious it must not be edited.
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Two example rows so the expected format is unambiguous.
            String[][] samples = {
                    { "SMALL", "Small Hatchback", "1800", "10800", "38000",
                      "2026-01-01", "2026-12-31", "" },
                    { "SUV", "Full Size SUV", "4800", "28800", "99000",
                      "2026-01-01", "2026-12-31", "" },
            };

            int rowNum = 1;
            for (String[] sample : samples) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < sample.length; i++) {
                    row.createCell(i).setCellValue(sample[i]);
                }
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Could not build the rate template", e);
            throw new BusinessException("Could not build the Excel template.");
        }
    }

    // =====================================================
    // Helpers
    // =====================================================

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException("Please choose a file to upload.");
        }

        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".xlsx")) {
            throw new BusinessException(
                    "Only .xlsx files are supported. Save your sheet as Excel Workbook (.xlsx) and try again.");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BusinessException("The file is too large. The limit is 2 MB.");
        }
    }

    /** The hub of the staff member doing the upload. */
    private Hub getLoggedInStaffHub() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedActionException("Staff is not authenticated");
        }

        Staff staff = staffRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        return staff.getHub();
    }

    /** True when the row is missing or every cell in it is empty. */
    private boolean isBlankRow(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < HEADERS.length; i++) {
            if (readString(row, i) != null) {
                return false;
            }
        }
        return true;
    }

    /** Cell as trimmed text, or null when empty. Handles numeric cells too. */
    private String readString(Row row, int column) {

        Cell cell = row.getCell(column);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        String text;
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                text = cell.getLocalDateTimeCellValue().toLocalDate().format(ISO_DATE);
            } else {
                // Avoid "1800.0" when the user typed 1800.
                double number = cell.getNumericCellValue();
                text = number == Math.floor(number)
                        ? String.valueOf((long) number)
                        : String.valueOf(number);
            }
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            text = String.valueOf(cell.getBooleanCellValue());
        } else {
            text = cell.getStringCellValue();
        }

        text = text == null ? null : text.trim();
        return (text == null || text.isEmpty()) ? null : text;
    }

    /** A rate that must be present and greater than zero. */
    private double readPositiveRate(Row row, int column, String fieldName) {

        String raw = readString(row, column);
        if (raw == null) {
            throw new BusinessException(fieldName + " is required");
        }

        double value;
        try {
            value = Double.parseDouble(raw.replace(",", ""));
        } catch (NumberFormatException e) {
            throw new BusinessException(fieldName + " must be a number, found '" + raw + "'");
        }

        if (value <= 0) {
            throw new BusinessException(fieldName + " must be greater than 0");
        }

        return value;
    }

    /** Accepts a real Excel date cell or plain yyyy-MM-dd text. Optional. */
    private LocalDate readDate(Row row, int column, String fieldName) {

        Cell cell = row.getCell(column);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }

        String raw = readString(row, column);
        if (raw == null) {
            return null;
        }

        try {
            return LocalDate.parse(raw, ISO_DATE);
        } catch (Exception e) {
            throw new BusinessException(
                    fieldName + " must be a date in yyyy-MM-dd format, found '" + raw + "'");
        }
    }
}
