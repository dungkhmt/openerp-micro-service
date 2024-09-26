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
@RequestMapping("/skill")
public class SkillController {
    private SkillService skillService;

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getAll(@PathVariable String id) {
        List<Skill> skillList = skillService.getAllByUserId(id);
        return ResponseEntity.ok(skillList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getByUserId(@PathVariable Integer skillId) {
        Skill skill = skillService.getById(skillId);
        return ResponseEntity.ok(skill);
    }

    @PostMapping
    public ResponseEntity<?> addSkill(@RequestBody Skill skill) {
        Skill skill1 = skillService.save(skill);
        return ResponseEntity.ok(skill1);
    }

    @PutMapping
    public ResponseEntity<?> updateSkill(@RequestBody Skill skill) {
        Skill skill1 = skillService.save(skill);
        return ResponseEntity.ok(skill1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer skillId) {
        skillService.deleteById(skillId);
        return ResponseEntity.ok("deleted skill with id " + skillId);
    }

}
