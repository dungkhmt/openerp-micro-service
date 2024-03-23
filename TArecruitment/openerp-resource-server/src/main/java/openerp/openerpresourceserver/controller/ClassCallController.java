package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.ClassCall;
import openerp.openerpresourceserver.service.ClassCallService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/class-call")
public class ClassCallController {
    private ClassCallService classCallService;

    @PostMapping("/create-class")
    public ResponseEntity<?> createClass(@RequestBody ClassCall classCall) {
        ClassCall newClassCall = classCallService.createNewClass(classCall);
        return ResponseEntity.ok().body(newClassCall);
    }

    @GetMapping("/get-all-class")
    public ResponseEntity<?> getAllClass() {
        List<ClassCall> classCalls = classCallService.getAllClass();
        return ResponseEntity.ok().body(classCalls);
    }

    @GetMapping("/get-class/{id}")
    public ResponseEntity<?> getClassById(@PathVariable int id) {
        try {
            Optional<ClassCall> classCall = classCallService.getClassById(id);
            return ResponseEntity.ok().body(classCall);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update-class/{id}")
    public ResponseEntity<?> updateClass(@PathVariable int id, @RequestBody ClassCall classCall) {
        try {
            ClassCall updatedClassCall = classCallService.updateClass(id, classCall);
            return ResponseEntity.ok().body(updatedClassCall);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-class/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable int id) {
        try {
            classCallService.deleteClass(id);
            return ResponseEntity.ok().body("Delete successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
