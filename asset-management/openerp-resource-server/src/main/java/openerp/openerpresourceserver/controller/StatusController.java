package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Status;
import openerp.openerpresourceserver.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/status")
public class StatusController {
    private StatusService statusService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllStatus(){
        List<Status> statusList = statusService.getAllStatus();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(statusList);
    }
}
