package openerp.openerpresourceserver.assetmanagement.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.assetmanagement.entity.RequestLog;
import openerp.openerpresourceserver.assetmanagement.service.RequestLogService;
import openerp.openerpresourceserver.assetmanagement.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/request-log")
public class RequestLogController {
    private RequestService requestService;
    private RequestLogService requestLogService;

    @GetMapping("/get-by-user")
    public ResponseEntity<?> getByUser(Principal principal){
        String user_id = principal.getName();
        System.out.println("user_id: " + user_id);
        List<RequestLog> requestLogs = requestLogService.getByUserId(user_id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestLogs);
    }
}
