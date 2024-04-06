package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Request;
import openerp.openerpresourceserver.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/request")
public class RequestController {
    private RequestService requestService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllRequests(){
        List<Request> requests = requestService.getAllRequests();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(requests);
    }
}
