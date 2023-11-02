package openerp.openerpresourceserver.controller;

import openerp.openerpresourceserver.model.entity.ClassCode;
import openerp.openerpresourceserver.service.ClassCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/class-code")
public class ClassCodeController {

    @Autowired
    private ClassCodeService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<ClassCode>> getAllClassCode() {
        try {
            List<ClassCode> classCodeList = service.getClassCode();
            if (classCodeList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(classCodeList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/update")
    public ResponseEntity<Void> updateClassCode() {
        try {
            service.updateClassCode();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
