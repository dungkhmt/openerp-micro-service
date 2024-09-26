package openerp.openerpresourceserver.controller;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.repo.UserRepo;
import openerp.openerpresourceserver.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/experience")
public class ExperienceController {
    private ExperienceService experienceService;

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getAll(@PathVariable String id) {
        List<Experience> experienceList = experienceService.getAllByUserId(id);
        return ResponseEntity.ok(experienceList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer experienceId) {
        Experience experience = experienceService.getById(experienceId);
        return ResponseEntity.ok(experience);
    }

    @PostMapping
    public ResponseEntity<?> addSkill(@RequestBody Experience experience) {
        Experience experience1 = experienceService.save(experience);
        return ResponseEntity.ok(experience1);
    }

    @PutMapping
    public ResponseEntity<?> updateSkill(@RequestBody Experience experience) {
        Experience experience1 = experienceService.save(experience);
        return ResponseEntity.ok(experience1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer experienceId) {
        experienceService.deleteById(experienceId);
        return ResponseEntity.ok("deleted experience with id " + experienceId);
    }

}
