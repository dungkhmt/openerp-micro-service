package openerp.openerpresourceserver.controller;

import openerp.openerpresourceserver.model.entity.OpenBatch;
import openerp.openerpresourceserver.service.OpenBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/open-batch")
public class OpenBatchController {

    @Autowired
    private OpenBatchService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<OpenBatch>> getAllOpenBatch() {
        try {
            List<OpenBatch> openBatchList = service.getOpenBatch();
            if (openBatchList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(openBatchList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/update")
    public ResponseEntity<Void> updateOpenBatch() {
        try {
            service.updateOpenBatch();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
