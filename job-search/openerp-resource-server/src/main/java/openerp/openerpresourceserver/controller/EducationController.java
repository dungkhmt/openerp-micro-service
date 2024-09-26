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
@RequestMapping("/education")
public class EducationController {
    private EducationService educationService;

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getAll(@PathVariable String id) {
        List<Education> educationList = educationService.getAllByUserId(id);
        return ResponseEntity.ok(educationList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer educationId) {
        Education education = educationService.getById(educationId);
        return ResponseEntity.ok(education);
    }

    @PostMapping
    public ResponseEntity<?> addSkill(@RequestBody Education education) {
        Education education1 = educationService.save(education);
        return ResponseEntity.ok(education1);
    }

    @PutMapping
    public ResponseEntity<?> updateSkill(@RequestBody Education education) {
        Education education1 = educationService.save(education);
        return ResponseEntity.ok(education1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer educationId) {
        educationService.deleteById(educationId);
        return ResponseEntity.ok("deleted experience with id " + educationId);
    }

}
