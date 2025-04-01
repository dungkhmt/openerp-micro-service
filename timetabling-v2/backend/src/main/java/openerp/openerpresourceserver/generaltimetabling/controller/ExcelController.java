package openerp.openerpresourceserver.generaltimetabling.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.exception.InvalidFileInputException;
import openerp.openerpresourceserver.generaltimetabling.helper.ExcelHelper;
import openerp.openerpresourceserver.generaltimetabling.message.ResponseMessage;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.FilterClassOpenedDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassOpened;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Schedule;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.PlanGeneralClass;
import openerp.openerpresourceserver.generaltimetabling.service.ExcelService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/excel")
@Log4j2
public class ExcelController {

    private ExcelService fileService;

    // @GetMapping(value = "/download-template")
    // public ResponseEntity<Resource> getFile() {
    // String filename = "schedules_template.xlsx";
    // InputStreamResource file = new InputStreamResource(fileService.load());
    // return ResponseEntity.ok()
    // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
    // .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
    // .body(file);
    // }

    // @PostMapping(value = "/upload")
    // public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file")
    // MultipartFile file) {
    // String message = "";
    // if (ExcelHelper.hasExcelFormat(file)) {
    // try {
    // fileService.save(file);
    // message = "Uploaded the file successfully: " + file.getOriginalFilename();
    // return ResponseEntity.status(HttpStatus.OK).body(new
    // ResponseMessage(message));
    // } catch (Exception e) {
    // message = "Could not upload the file: " + file.getOriginalFilename() + "!";
    // return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new
    // ResponseMessage(message));
    // }
    // }
    // message = "Please upload an excel file!";
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new
    // ResponseMessage(message));
    // }

    @PostMapping(value = "/upload-general")
    public ResponseEntity uploadFileGeneralCLass(@RequestParam("file") MultipartFile file,
            @RequestParam("semester") String semester) {
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                List<GeneralClass> classOpenedConflict = fileService.saveGeneralClasses(file, semester);
                return ResponseEntity.status(HttpStatus.OK).body(classOpenedConflict);
            } catch (Exception e) {
                System.err.println("\n\n\nERRROR: " + e + "\n\n\n");
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage().toString());
            }
        }
        // message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @ExceptionHandler(InvalidFileInputException.class)
    public ResponseEntity resolveInvalidFileException(InvalidFileInputException e) {
        return ResponseEntity.status(410).body(e.getErrorMessage());
    }


    @PostMapping(value = "/upload-plan")
    public ResponseEntity requestUploadFilePlanCLass(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("semester") String semester,
                                                     @RequestParam("createclass") String createclass) {
        log.info("requestUploadFilePlanCLass, createclass = " + createclass);
        boolean createClass = true;
        if(createclass.equals("F")) createClass = false;
        if (ExcelHelper.hasExcelFormat(file)) {
            List<PlanGeneralClass> classOpenedConflict = fileService.savePlanClasses(file, semester,createClass);
            return ResponseEntity.status(HttpStatus.OK).body(classOpenedConflict);
        } else {
            throw new InvalidFileInputException("File không đúng định dạng! (Yêu cầu: .xlsx)");
        }
    }

    @PostMapping(value = "/upload-class-opened")
    public ResponseEntity<List<ClassOpened>> uploadFileCLassOpened(@RequestParam("file") MultipartFile file,
            @RequestParam("semester") String semester) {
        String message = "";
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                List<ClassOpened> classOpenedConflict = fileService.saveClassOpened(file, semester);
                // message = "Uploaded the file successfully: " + file.getOriginalFilename();
                // return ResponseEntity.status(HttpStatus.OK).body(new
                // ResponseMessage(message));
                return ResponseEntity.status(HttpStatus.OK).body(classOpenedConflict);
            } catch (Exception e) {
                // message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
            }
        }
        // message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @PostMapping(value = "/export/class-opened-conflict")
    public ResponseEntity<Resource> getFileClassConflict(@Valid @RequestBody List<ClassOpened> classOpenedList) {
        String filename = "Class_Conflict_List.xlsx";
        InputStreamResource file = new InputStreamResource(fileService.loadClassConflict(classOpenedList));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        try {
            List<Schedule> schedules = fileService.getAllSchedules();
            if (schedules.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(schedules, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/export-schedules")
    public ResponseEntity<Resource> getFileSchedule(@Valid @RequestBody FilterClassOpenedDto requestDto) {
        String filename = "Schedules.xlsx";
        InputStreamResource file = new InputStreamResource(fileService.loadExport(requestDto));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    // -------------Classroom---------------
    @PostMapping(value = "/upload-classroom")
    public ResponseEntity<ResponseMessage> uploadFileClassroom(@RequestParam("file") MultipartFile file) {
        String message = "";
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                fileService.saveClassroom(file);
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }
        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }
}
