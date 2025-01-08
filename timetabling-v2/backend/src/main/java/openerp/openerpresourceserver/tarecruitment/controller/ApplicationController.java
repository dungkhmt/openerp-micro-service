package openerp.openerpresourceserver.tarecruitment.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.tarecruitment.entity.dto.ChartDTO;
import openerp.openerpresourceserver.tarecruitment.entity.dto.PaginationDTO;
import openerp.openerpresourceserver.tarecruitment.entity.Application;
import openerp.openerpresourceserver.tarecruitment.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/application")
@Configuration
@EnableAsync
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

    @PutMapping("/update-application/{id}")
    public ResponseEntity<?> updateApplication(@PathVariable int id, @RequestBody Application application) {
        try {
            Application updateApplication = applicationService.updateApplication(id, application);
            return ResponseEntity.ok().body(updateApplication);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update-multiple-application-status/{status}")
    public ResponseEntity<?> updateMultipleApplicationStatus(@PathVariable String status, @RequestBody List<Integer> idList) {
        try {
            String result = applicationService.updateMultipleApplicationStatus(idList, status);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-application/{id}")
    public ResponseEntity<?> deleteApplication(@PathVariable int id) {
        try {
            applicationService.deleteApplication(id);
            return ResponseEntity.ok().body("Delete successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-multiple-application")
    public ResponseEntity<?> deleteMultipleApplication(@RequestBody List<Integer> idList) {
        try {
            applicationService.deleteMultiApplication(idList);
            return ResponseEntity.ok().body("Delete successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/get-application-by-id/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable int id) {
        try {
            Application application = applicationService.getApplicationById(id);
            return ResponseEntity.ok().body(application);
        } catch (Exception e) {
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
        applicationService.autoAssignApplication(semester);
        return ResponseEntity.ok().body("Success");
    }

    @GetMapping("/old-auto-assign-class/{semester}")
    public ResponseEntity<?> oldAutoAssignClass(@PathVariable String semester) {
        applicationService.oldAutoAssignApplication(semester);
        return ResponseEntity.ok().body("Success");
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

    @GetMapping("/get-ta-by-semester/{semester}")
    public ResponseEntity<?> getTABySemester(@PathVariable String semester,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int limit,
                                             @RequestParam(defaultValue = "") String search) {
        PaginationDTO<Application> applications = applicationService.getTABySemester(semester, search, page, limit);
        return ResponseEntity.ok().body(applications);
    }

    @GetMapping("/get-applicator-data")
    public ResponseEntity<?> getApplicatorData() {
        List<ChartDTO> chart = applicationService.getApplicatorEachSemesterData();
        return ResponseEntity.ok().body(chart);
    }

    @GetMapping("/get-application-data")
    public ResponseEntity<?> getApplicationData() {
        List<ChartDTO> chart = applicationService.getNumbApplicationEachSemesterData();
        return ResponseEntity.ok().body(chart);
    }

    @GetMapping("/get-ta-data")
    public ResponseEntity<?> getTaData() {
        List<ChartDTO> chart = applicationService.getNumbApplicationApproveEachSemesterData();
        return ResponseEntity.ok().body(chart);
    }

    @GetMapping("/get-course-data")
    public ResponseEntity<?> getCourseData() {
        List<ChartDTO> chart = applicationService.dataApplicationEachCourseThisSemester();
        return ResponseEntity.ok().body(chart);
    }
}
