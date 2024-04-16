package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.PaginationDTO;
import openerp.openerpresourceserver.entity.Application;
import openerp.openerpresourceserver.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public ResponseEntity<?> getMyApplications(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit) {
        String userId = principal.getName();
        PaginationDTO<Application> applications = applicationService.getMyApplications(userId, page, limit);
        return ResponseEntity.ok().body(applications);
    }

    @GetMapping("/get-application-by-class/{classCallId}")
    public ResponseEntity<?> getApplicationByClassId(
            @PathVariable int classCallId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit) {
        PaginationDTO<Application> applications = applicationService.getApplicationByClassId(classCallId, page, limit);
        return ResponseEntity.ok().body(applications);
    }

    /**
     * @TODO: Search by semester
     */
    @GetMapping("/get-unique-applicator")
    public ResponseEntity<?> getUniqueApplicator() {
        List<Application> applicators = applicationService.getUniqueApplicator();
        return ResponseEntity.ok().body(applicators);
    }

    @GetMapping("/get-application-by-semester/{semester}")
    public ResponseEntity<?> getApplicationBySemester(
            @PathVariable String semester,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String appStatus) {
        PaginationDTO<Application> applications = applicationService.getApplicationBySemester(semester, search, appStatus, page, limit);
        return ResponseEntity.ok().body(applications);
    }

    @PutMapping("/update-application-status/{applicationId}")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable int applicationId, @RequestBody Application updateApplication) {
        try {
            Application application = applicationService.updateApplicationStatus(applicationId, updateApplication.getApplicationStatus());
            return ResponseEntity.ok().body(application);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update-assign-status/{applicationId}")
    public ResponseEntity<?> updateAssignStatus(@PathVariable int applicationId, @RequestBody Application updateApplication) {
        try {
            Application application = applicationService.updateAssignStatus(applicationId, updateApplication.getAssignStatus());
            return ResponseEntity.ok().body(application);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Time conflict with existing approved application")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
    }

    @GetMapping("/get-application-by-status-and-semester/{semester}/{applicationStatus}")
    public ResponseEntity<?> getApplicationByApplyStatusAnsSemester(
            @PathVariable String semester,
            @PathVariable String applicationStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String assignStatus) {
        PaginationDTO<Application> applications =
                applicationService.getApplicationByApplicationStatusAndSemester(applicationStatus, semester, search, assignStatus, page, limit);
        return ResponseEntity.ok().body(applications);
    }

    @GetMapping("/auto-assign-class/{semester}")
    public ResponseEntity<?> autoAssignClass(@PathVariable String semester) {
        int[][] graph = applicationService.autoAssignApplication(semester);
        return ResponseEntity.ok().body(graph);
    }

    @GetMapping("/get-assign-list-file/{semester}")
    public ResponseEntity<?> getAssignListFile(@PathVariable String semester) {
        try {
            byte[] excelBytes = applicationService.generateExcelFile(semester);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", semester + "list.xlsx");
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD REQUEST");
        }
    }

}
