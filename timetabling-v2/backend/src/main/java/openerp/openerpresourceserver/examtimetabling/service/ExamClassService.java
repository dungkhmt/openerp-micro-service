package openerp.openerpresourceserver.examtimetabling.service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.examtimetabling.entity.ExamClass;
import openerp.openerpresourceserver.examtimetabling.repository.ExamClassRepository;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamClassService {
    private final ExamClassRepository examClassRepository;

    public List<ExamClass> getAllClasses() {
        return examClassRepository.findAll();
    }

    public List<String> validateExamClasses(List<String> examClassIds) {
        return examClassIds.stream()
            .filter(id -> !examClassRepository.existsById(id))
            .collect(Collectors.toList());
    }

    public void deleteClasses(List<String> examClassIds) {
        examClassRepository.deleteAllById(examClassIds);
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
        
        List<ExamClass> newClasses = new ArrayList<>();
        List<ExamClass> conflictClasses = new ArrayList<>();
        
        // Skip header row
        for (int i = 1; i <= 5; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                try {
                    String examClassId = getStringValue(row.getCell(14)); // Mã lớp thi
                    
                    // Skip if examClassId is null
                    if (examClassId == null || examClassId.trim().isEmpty()) continue;
                    
                    // Check if this exam class already exists
                    boolean exists = examClassRepository.existsById(examClassId);
                    
                    ExamClass examClass = ExamClass.builder()
                        .examClassId(examClassId)
                        .classId(getStringValue(row.getCell(0)))      // Mã lớp QT
                        .courseId(getStringValue(row.getCell(2)))     // Mã học phần
                        .groupId(getStringValue(row.getCell(5)))      // studyGroupID
                        .courseName(getStringValue(row.getCell(3)))   // Tên học phần
                        .description(getStringValue(row.getCell(4)))  // Ghi chú
                        .numberOfStudents(getIntValue(row.getCell(8))) // SL
                        .period(getStringValue(row.getCell(9)))       // Đợt mở
                        .managementCode(getStringValue(row.getCell(11))) // Mã_QL
                        .school(getStringValue(row.getCell(13)))      // Tên trường/khoa
                        .build();
                    
                    if (exists) {
                        conflictClasses.add(examClass);
                    } else {
                        newClasses.add(examClass);
                    }
                } catch (Exception e) {
                    System.out.println("Error processing row " + i + ": " + e.getMessage());
                }
            }
        }
        
        workbook.close();

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
