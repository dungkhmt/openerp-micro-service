package openerp.openerpresourceserver.log.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.log.entity.LmsLog;
import openerp.openerpresourceserver.log.model.LmsLogModelCreate;
import openerp.openerpresourceserver.log.service.LmsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class LogController {
    private LmsLogService lmsLogService;

    @PostMapping("/log/create-log")
    public ResponseEntity<?> createLog(Principal principal, @RequestBody LmsLogModelCreate I){
        log.info("createLog body = {}",I);
        log.info("createLog, userId = " + I.getUserId() + " action = " + I.getActionType() + " description = " + I.getDescription());
        LmsLog alog = lmsLogService.save(I);
        log.info("createLog, userId = " + I.getUserId() + " action = " + I.getActionType() + " description = " + I.getDescription() + " save OK!!");

        return ResponseEntity.ok().body(alog);
    }
    //@Secured("LMS_LOG")
    @GetMapping("/log/get-logs")
    public ResponseEntity<?> getLmsLogs(Principal principal){
        log.info("getLmsLog user = " + principal.getName());
        List<LmsLog> logs = lmsLogService.getAllLogs();
        return ResponseEntity.ok().body(logs);
    }
}
