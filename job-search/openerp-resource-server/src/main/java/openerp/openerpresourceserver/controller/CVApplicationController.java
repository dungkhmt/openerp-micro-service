package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.CVApplication;
import openerp.openerpresourceserver.entity.EmployeeCV;
import openerp.openerpresourceserver.entity.JobPost;
import openerp.openerpresourceserver.entity.User;
import openerp.openerpresourceserver.repo.UserRepo;
import openerp.openerpresourceserver.service.CVApplicationService;
import openerp.openerpresourceserver.service.EmployeeCVService;
import openerp.openerpresourceserver.service.JobPostService;
import openerp.openerpresourceserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/cv-application")
public class CVApplicationController {
    private CVApplicationService cvApplicationService;
    private UserService userService;
    private JobPostService jobPostService;
    private EmployeeCVService employeeCVService;
    @GetMapping
    public ResponseEntity<?> getAllCVApplication() {
        List<CVApplication> cvApplicationList = cvApplicationService.getAll();
        return ResponseEntity.ok(cvApplicationList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCvApplicationByJobId(@PathVariable Integer id) {
        List<CVApplication> cvApplication = cvApplicationService.getAllByJobId(jobPostService.getById(id));
        return ResponseEntity.ok(cvApplication);
    }

    @PostMapping("/user/{id}/{jobPostId}")
    public ResponseEntity<?> save(@RequestBody CVApplication cvApplication, @PathVariable String id, @PathVariable Integer jobPostId) {
        CVApplication cvApplication1 = cvApplicationService.save(cvApplication);
        User user = userService.getUserById(id);
        JobPost jobPost = jobPostService.getById(jobPostId);
        cvApplication1.setUser(user);
        cvApplication1.setJobId(jobPost);
        return ResponseEntity.ok(cvApplication1);
    }

    @PutMapping("/user/{id}/{jobPostId}")
    public ResponseEntity<?> update(@RequestBody CVApplication cvApplication, @PathVariable String id, @PathVariable Integer jobPostId) {
        
        CVApplication cvApplication1 = cvApplicationService.save(cvApplication);
        return ResponseEntity.ok(cvApplication1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        cvApplicationService.DeleteById(id);
        return ResponseEntity.ok("deleted job application with id " + id);
    }

}
