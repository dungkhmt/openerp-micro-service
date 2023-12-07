package openerp.openerpresourceserver.controller;

import openerp.openerpresourceserver.model.entity.ClassType;
import openerp.openerpresourceserver.service.ClassTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/class-type")
public class ClassTypeController {

    @Autowired
    private ClassTypeService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<ClassType>> getAllClassType() {
        try {
            List<ClassType> classTypeList = service.getClassType();
            if (classTypeList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(classTypeList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/update")
    public ResponseEntity<Void> updateClassType() {
        try {
            service.updateClassType();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
