package openerp.openerpresourceserver.examtimetabling.service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.examtimetabling.entity.ExamClass;
import openerp.openerpresourceserver.examtimetabling.repository.ExamClassRepository;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
public class ExamClassService {
    private final ExamClassRepository examClassRepository;
    @Autowired
    private DataSource dataSource;
    private static final int BATCH_SIZE = 1000;

    public List<ExamClass> getExamClassesByPlanId(UUID examPlanId) {
        return examClassRepository.findByExamPlanId(examPlanId);
    }

    @Transactional
    public void deleteClasses(List<UUID> ids) {
        for (int i = 0; i < ids.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, ids.size());
            List<UUID> batchIds = ids.subList(i, end);
            examClassRepository.deleteByExamClassIdIn(batchIds);
        }
    }

    public boolean validateExamClass(String examClassId, UUID examPlanId) {
        return examClassRepository.existsByExamClassIdAndExamPlanId(examClassId, examPlanId);
    }

    public ExamClass createExamClass(ExamClass examClass) {
        return examClassRepository.save(examClass);
    }

    public ExamClass updateExamClass(ExamClass examClass) {
        // Check if exists
        if (!examClassRepository.existsById(examClass.getId())) {
            throw new RuntimeException("Exam class not found with id: " + examClass.getExamClassId());
        }
        
        return examClassRepository.save(examClass);
    }

    @Transactional
    public List<ExamClass> bulkCreateFromExcel(MultipartFile file, UUID examPlanId) throws IOException, EncryptedDocumentException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        
        // For conflicts check
        Set<String> excelExamClassIds = new HashSet<>();
        
        // Prepare a batch insert
        StringBuilder insertValuesBuilder = new StringBuilder();
        List<Object[]> batchParams = new ArrayList<>();
        
        // Skip header row and process Excel data
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            try {
                String examClassId = getStringValue(row.getCell(14));
                
                // Skip if examClassId is null or empty
                if (examClassId == null || examClassId.trim().isEmpty()) continue;
                
                excelExamClassIds.add(examClassId);
                
                // Add parameters for this row
                UUID id = UUID.randomUUID();
                String classId = getStringValue(row.getCell(0));
                String courseId = getStringValue(row.getCell(2));
                String groupId = getStringValue(row.getCell(5));
                String courseName = getStringValue(row.getCell(3));
                String description = getStringValue(row.getCell(4));
                Integer numberOfStudents = getIntValue(row.getCell(8));
                String period = getStringValue(row.getCell(9));
                String managementCode = getStringValue(row.getCell(11));
                String school = getStringValue(row.getCell(13));
                
                batchParams.add(new Object[]{
                    id, examClassId, classId, courseId, groupId, courseName, 
                    description, numberOfStudents, period, managementCode, 
                    school, examPlanId
                });
            } catch (Exception e) {
                System.out.println("Error processing row " + i + ": " + e.getMessage());
            }
        }
        workbook.close();
        
        // Skip further processing if no valid records
        if (excelExamClassIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Check for conflicts in one database call
        List<ExamClass> conflictClasses = examClassRepository.findByExamPlanIdAndExamClassIdIn(
            examPlanId, new ArrayList<>(excelExamClassIds));
        
        // If conflicts found, return them
        if (!conflictClasses.isEmpty()) {
            return conflictClasses;
        }
        
        // No conflicts - perform batch insert using JDBC batch
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        String sql = "INSERT INTO exam_timetabling_class (id, exam_class_id, class_id, course_id, " +
            "group_id, course_name, description, number_students, period, management_code, " +
            "school, exam_plan_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Object[] params = batchParams.get(i);
                ps.setObject(1, params[0]);  // id
                ps.setString(2, (String)params[1]);  // exam_class_id
                ps.setString(3, (String)params[2]);  // class_id
                ps.setString(4, (String)params[3]);  // course_id
                ps.setString(5, (String)params[4]);  // group_id
                ps.setString(6, (String)params[5]);  // course_name
                ps.setString(7, (String)params[6]);  // description
                ps.setObject(8, params[7]);  // number_students
                ps.setString(9, (String)params[8]);  // period
                ps.setString(10, (String)params[9]); // management_code
                ps.setString(11, (String)params[10]); // school
                ps.setObject(12, params[11]); // exam_plan_id
            }

            @Override
            public int getBatchSize() {
                return batchParams.size();
            }
        });
        
        return new ArrayList<>();
    }
    
   public ByteArrayInputStream loadExamClasses(List<UUID> ids) {
        List<ExamClass> examClasses = examClassRepository.findAllById(ids);

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
