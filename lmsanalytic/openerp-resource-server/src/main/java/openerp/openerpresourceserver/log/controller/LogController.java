package openerp.openerpresourceserver.log.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.log.entity.LmsLog;
import openerp.openerpresourceserver.log.model.LmsLogModelCreate;
import openerp.openerpresourceserver.log.service.LmsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Log4j2
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class LogController {
    private LmsLogService lmsLogService;

    @PostMapping("/log/create-log")
    public ResponseEntity<?> createLog(Principal principal, @RequestBody LmsLogModelCreate I){
        LmsLog alog = lmsLogService.save(I);
        log.info("create a log");
        return ResponseEntity.ok().body(alog);
    }
}
