package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.JobPost;
import openerp.openerpresourceserver.service.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/job-post")

public class JobPostController {
    private JobPostService jobPostService;

    @GetMapping
    public ResponseEntity<?> getAllJobPost() {
        List<JobPost> jobPosts = jobPostService.getAll();
        return ResponseEntity.ok().body(jobPosts);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllByUserId(@PathVariable String userId) {

        List<JobPost> jobPosts = jobPostService.getAllByUserId(userId);
        return ResponseEntity.ok().body(jobPosts);
    }

    @PostMapping
    public  ResponseEntity<?> save(@RequestBody JobPost jobPost) {
        JobPost jobPost1 =  jobPostService.save(jobPost);
        return ResponseEntity.ok().body(jobPost1);
    }

    @PutMapping
    public  ResponseEntity<?> update(@RequestBody JobPost jobPost) {
        JobPost jobPost1 =  jobPostService.save(jobPost);
        return ResponseEntity.ok().body(jobPost1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) throws Exception {
        try {
            JobPost jobPost = jobPostService.getById(id);
        }
        catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        jobPostService.deleteById(id);
        return ResponseEntity.ok("deleted job post with id " + id);
    }
}
