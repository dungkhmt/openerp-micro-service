package openerp.openerpresourceserver.controller;

import openerp.openerpresourceserver.model.entity.ManagementCode;
import openerp.openerpresourceserver.service.ManagementCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/management-code")
public class ManagementCodeController {

    @Autowired
    private ManagementCodeService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<ManagementCode>> getAllManagementCode() {
        try {
            List<ManagementCode> managementCodeList = service.getManagementCode();
            if (managementCodeList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(managementCodeList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/update")
    public ResponseEntity<Void> updateManagementCode() {
        try {
            service.updateManagementCode();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
