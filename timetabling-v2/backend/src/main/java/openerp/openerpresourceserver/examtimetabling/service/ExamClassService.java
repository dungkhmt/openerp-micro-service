package openerp.openerpresourceserver.examtimetabling.service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.examtimetabling.entity.ExamClass;
import openerp.openerpresourceserver.examtimetabling.repository.ExamClassRepository;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamClassService {
    private final ExamClassRepository examClassRepository;
    private static final int BATCH_SIZE = 1000;

    public List<ExamClass> getAllClasses() {
        return examClassRepository.findAll();
    }

    public List<String> validateExamClasses(List<String> examClassIds) {
        return examClassIds.stream()
            .filter(id -> !examClassRepository.existsById(id))
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteClasses(List<String> examClassIds) {
        for (int i = 0; i < examClassIds.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, examClassIds.size());
            List<String> batchIds = examClassIds.subList(i, end);
            examClassRepository.deleteByExamClassIdIn(batchIds);
        }
    }

    public boolean validateExamClass(String examClassId) {
        return examClassRepository.existsById(examClassId);
    }

    public ExamClass createExamClass(ExamClass examClass) {
        return examClassRepository.save(examClass);
    }

    public ExamClass updateExamClass(ExamClass examClass) {
        // Check if exists
        if (!examClassRepository.existsById(examClass.getExamClassId())) {
            throw new RuntimeException("Exam class not found with id: " + examClass.getExamClassId());
        }
        
        return examClassRepository.save(examClass);
    }

    
    public List<ExamClass> bulkCreateFromExcel(MultipartFile file) throws IOException, EncryptedDocumentException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        
        // First pass: collect all examClassIds from Excel
        Set<String> excelExamClassIds = new HashSet<>();
        List<ExamClass> allExcelClasses = new ArrayList<>();
        
        // Skip header row
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                try {
                    String examClassId = getStringValue(row.getCell(14));
                    if (examClassId == null || examClassId.trim().isEmpty()) continue;
                    
                    ExamClass examClass = ExamClass.builder()
                        .examClassId(examClassId)
                        .classId(getStringValue(row.getCell(0)))
                        .courseId(getStringValue(row.getCell(2)))
                        .groupId(getStringValue(row.getCell(5)))
                        .courseName(getStringValue(row.getCell(3)))
                        .description(getStringValue(row.getCell(4)))
                        .numberOfStudents(getIntValue(row.getCell(8)))
                        .period(getStringValue(row.getCell(9)))
                        .managementCode(getStringValue(row.getCell(11)))
                        .school(getStringValue(row.getCell(13)))
                        .build();
                    
                    excelExamClassIds.add(examClassId);
                    allExcelClasses.add(examClass);
                } catch (Exception e) {
                    System.out.println("Error processing row " + i + ": " + e.getMessage());
                }
            }
        }
        workbook.close();

        // Get existing IDs from database in one query
        List<String> existingIds = examClassRepository.findExamClassIdsByExamClassIdIn(excelExamClassIds);
        Set<String> existingIdSet = new HashSet<>(existingIds);

        // Separate new and conflict classes
        List<ExamClass> conflictClasses = new ArrayList<>();
        List<ExamClass> newClasses = new ArrayList<>();

        for (ExamClass examClass : allExcelClasses) {
            if (existingIdSet.contains(examClass.getExamClassId())) {
                conflictClasses.add(examClass);
            } else {
                newClasses.add(examClass);
            }
        }

        // If there are conflicts, return them
        if (!conflictClasses.isEmpty()) {
            return conflictClasses;
        }

        // Otherwise save new classes and return empty list
        examClassRepository.saveAll(newClasses);
        return new ArrayList<>();
    }

   public ByteArrayInputStream loadExamClasses(List<String> examClassIds) {
        List<ExamClass> examClasses = examClassRepository.findAllById(examClassIds);

        try (Workbook workbook = new XSSFWorkbook()) { // Using XLSX format
            Sheet sheet = workbook.createSheet("Exam Classes");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Mã lớp QT", "Mã lớp LT", "Mã học phần", "Tên học phần", "Ghi chú",
                "studyGroupID", "Nhóm", "sessionid", "SL", "Đợt mở",
                "ManagerID", "Mã_QL", "TeachUnitID", "Tên trường/khoa", "Mã lớp thi"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Create data rows
            int rowNum = 1;
            for (ExamClass examClass : examClasses) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(examClass.getClassId());           // Mã lớp QT
                row.createCell(1).setCellValue("");                               // Mã lớp LT  
                row.createCell(2).setCellValue(examClass.getCourseId());         // Mã học phần
                row.createCell(3).setCellValue(examClass.getCourseName());       // Tên học phần
                row.createCell(4).setCellValue(examClass.getDescription());      // Ghi chú
                row.createCell(5).setCellValue(examClass.getGroupId());          // studyGroupID
                row.createCell(6).setCellValue("");                              // Nhóm
                row.createCell(7).setCellValue("");                              // sessionid
                row.createCell(8).setCellValue(examClass.getNumberOfStudents()); // SL
                row.createCell(9).setCellValue(examClass.getPeriod());           // Đợt mở
                row.createCell(10).setCellValue("");                             // ManagerID
                row.createCell(11).setCellValue(examClass.getManagementCode());  // Mã_QL
                row.createCell(12).setCellValue("");                             // TeachUnitID
                row.createCell(13).setCellValue(examClass.getSchool());          // Tên trường/khoa
                row.createCell(14).setCellValue(examClass.getExamClassId());     // Mã lớp thi
            }

            // Autosize columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to export exam classes data to Excel file: " + e.getMessage());
        }
    }

    public ByteArrayInputStream generateTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Exam Classes Template");
    
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Mã lớp QT", "Mã lớp LT", "Mã học phần", "Tên học phần", "Ghi chú",
                "studyGroupID", "Nhóm", "sessionid", "SL", "Đợt mở",
                "ManagerID", "Mã_QL", "TeachUnitID", "Tên trường/khoa", "Mã lớp thi"
            };
    
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
    
            // Set headers
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
    
            // Add sample data - Row 1
            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("158783");      // Mã lớp QT
            row1.createCell(1).setCellValue("158783");      // Mã lớp LT
            row1.createCell(2).setCellValue("AC2030");      // Mã học phần
            row1.createCell(3).setCellValue("Khai thác thông tin đa phương tiện"); // Tên học phần
            row1.createCell(4).setCellValue("CN giáo dục"); // Ghi chú
            row1.createCell(5).setCellValue("01-K68S");     // studyGroupID
            row1.createCell(6).setCellValue("365");         // Nhóm
            row1.createCell(7).setCellValue("TC");          // sessionid
            row1.createCell(8).setCellValue(650);           // SL
            row1.createCell(9).setCellValue("51");          // Đợt mở
            row1.createCell(10).setCellValue("AB");         // ManagerID
            row1.createCell(11).setCellValue("671");        // Mã_QL
            row1.createCell(12).setCellValue("CT CHUẨN");   // TeachUnitID
            row1.createCell(13).setCellValue("Trường Điện - Điện tử"); // Tên trường/khoa
            row1.createCell(14).setCellValue("182568");     // Mã lớp thi
    
            // Add sample data - Row 2
            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("158784");
            row2.createCell(1).setCellValue("158784");
            row2.createCell(2).setCellValue("AC2031");
            row2.createCell(3).setCellValue("Lập trình web nâng cao");
            row2.createCell(4).setCellValue("CN giáo dục");
            row2.createCell(5).setCellValue("02-K68S");
            row2.createCell(6).setCellValue("366");
            row2.createCell(7).setCellValue("TC");
            row2.createCell(8).setCellValue(45);
            row2.createCell(9).setCellValue("51");
            row2.createCell(10).setCellValue("CD");
            row2.createCell(11).setCellValue("672");
            row2.createCell(12).setCellValue("CT CHUẨN");
            row2.createCell(13).setCellValue("Trường Điện - Điện tử");
            row2.createCell(14).setCellValue("182569");
    
            // Autosize columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
    
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
    
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel template", e);
        }
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return null;
        }
    }

    private Integer getIntValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return (int) cell.getNumericCellValue();
            case Cell.CELL_TYPE_STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }
}
