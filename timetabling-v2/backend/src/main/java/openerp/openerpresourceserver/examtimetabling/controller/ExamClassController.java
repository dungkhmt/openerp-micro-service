package openerp.openerpresourceserver.examtimetabling.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.examtimetabling.entity.ExamClass;
import openerp.openerpresourceserver.examtimetabling.service.ExamClassService;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/exam-class")
@RequiredArgsConstructor
public class ExamClassController {
    private final ExamClassService examClassService;

    @GetMapping
    public ResponseEntity<List<ExamClass>> getAllExamClasses(
        @RequestParam(value = "examPlanId", required = false) UUID examPlanId
    ) {
        List<ExamClass> examClasses;
        
        if (examPlanId != null) {
            // Filter by exam plan ID
            examClasses = examClassService.getExamClassesByPlanId(examPlanId);
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(examClasses);
    }

    @PostMapping("/delete-classes")
    public ResponseEntity<?> deleteClasses(@RequestBody List<UUID> examClassIds) {
        try {
            examClassService.deleteClasses(examClassIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error deleting exam classes"));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcelFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "examPlanId", required = true) UUID examPlanId) throws EncryptedDocumentException, InvalidFormatException {
        try {
            List<ExamClass> result = examClassService.bulkCreateFromExcel(file, examPlanId);

            System.err.println(result);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/export")
    public ResponseEntity<Resource> exportToExcel(@Valid @RequestBody List<UUID> ids) {
        String filename = "Exam_Classes.xlsx";
        InputStreamResource file = new InputStreamResource(examClassService.loadExamClasses(ids));
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @PostMapping("/update")
    public ResponseEntity<ExamClass> updateExamClass(
        @RequestBody ExamClass examClass
    ) {
        try {
            ExamClass updatedClass = examClassService.updateExamClass(examClass);
            return ResponseEntity.ok(updatedClass);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createExamClass(@Valid @RequestBody ExamClass examClass) {
        try {
            // Check if exam class already exists
            if (examClassService.validateExamClass(examClass.getExamClassId(), examClass.getExamPlanId())) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                        "message", "Exam class already exists",
                        "examClassId", examClass.getExamClassId()
                    ));
            }

            examClass.setId(UUID.randomUUID());
            ExamClass createdClass = examClassService.createExamClass(examClass);
            return ResponseEntity.ok(createdClass);
            
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error creating exam class"));
        }
    }

    @GetMapping("/download-template")
        public ResponseEntity<Resource> downloadTemplate() {
        try {
            String filename = "exam_class_template.xlsx";
            InputStreamResource file = new InputStreamResource(examClassService.generateTemplate());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
