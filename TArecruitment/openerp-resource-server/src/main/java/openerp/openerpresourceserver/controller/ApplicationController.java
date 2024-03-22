package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Application;
import openerp.openerpresourceserver.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/application")
public class ApplicationController {
    private ApplicationService applicationService;

    @PostMapping("/create-application")
    public ResponseEntity<?> createApplication(Principal principal, @RequestBody Application application) {
        String userId = principal.getName();
        application.getUser().setId(userId);
        try {
            Application newApplication = applicationService.createApplication(application);
            return ResponseEntity.ok().body(newApplication);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications(Principal principal) {
        String userId = principal.getName();
        List<Application> applications = applicationService.getMyApplications(userId);
        return ResponseEntity.ok().body(applications);
    }
}
