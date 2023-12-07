package openerp.openerpresourceserver.controller;

import openerp.openerpresourceserver.model.entity.Institute;
import openerp.openerpresourceserver.service.InstituteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/institute")
public class InstituteController {

    @Autowired
    private InstituteService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<Institute>> getAllInstitute() {
        try {
            List<Institute> instituteList = service.getInstitute();
            if (instituteList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(instituteList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/update")
    public ResponseEntity<Void> updateInstitute() {
        try {
            service.updateInstitute();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
