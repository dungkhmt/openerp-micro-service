package openerp.openerpresourceserver.timetablefirstyearstandard.controller;


import openerp.openerpresourceserver.timetablefirstyearstandard.entity.ClassFirstYearStandardOpened;
import openerp.openerpresourceserver.timetablefirstyearstandard.helper.*;
import openerp.openerpresourceserver.timetablefirstyearstandard.repo.ClassFirstYearStandardOpenedRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ClassController {
    @Autowired
    ClassFirstYearStandardOpenedRepo classFirstYearStandardOpenedRepo;

    @PostMapping(value = "/upload-class-first-year-standard-opened")
    public ResponseEntity<?> uploadFileFirstYearClassOpened(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("semester") String semester) {
        String message = "";
        System.out.println("uploadFileFirstYearClassOpened, semester = " + semester);
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                List<ClassFirstYearStandardOpened> cls =
                        ExcelHelper.excelToClassOpened(file.getInputStream());
                for(ClassFirstYearStandardOpened c: cls){
                    c = classFirstYearStandardOpenedRepo.save(c);
                    System.out.println("uploadFileFirstYearClassOpened SAVE: " + c.getId() + "," + c.getModuleName());

                }
                return ResponseEntity.ok().body(cls);

            } catch (Exception e) {
//                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
            }
        }
        return null;
    }
}
