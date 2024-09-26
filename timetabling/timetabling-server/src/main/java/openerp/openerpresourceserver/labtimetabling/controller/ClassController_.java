package openerp.openerpresourceserver.labtimetabling.controller;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.entity.Class;
import openerp.openerpresourceserver.labtimetabling.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/lab-timetabling/class")
public class ClassController_ {
    private ClassService classService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllClasses(){
        return ResponseEntity.status(HttpStatus.OK).body(classService.getAllClasses());
    }
    @GetMapping("/get-all-semesters")
    public ResponseEntity<?> getAllSemesters(){
        List<String> semesters = classService.getAllSemesters();
        return ResponseEntity.status(HttpStatus.OK).body(semesters);
    }
    @PostMapping
    public ResponseEntity<?> createClass(@RequestBody Class _class) {
        Class new_class = classService.createClass(_class);
        if(new_class != null){
            return ResponseEntity.status(HttpStatus.OK).body(new_class);
        }else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassById(@PathVariable Long id){
        Class lesson = classService.getClassById(id);
        return ResponseEntity.status(HttpStatus.OK).body(lesson);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchClass(@PathVariable Long id, @RequestBody Class updatedClass){
        Optional<Class> patchedClass = classService.patchClass(id, updatedClass);
        return patchedClass.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable Long id){
        if(classService.deleteClass(id)){
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<?> batchInsert(@RequestBody List<Class> classes){
        System.out.println(classes);
        int insert_successful = classService.batchInsert(classes);
        System.out.println(insert_successful);
        return ResponseEntity.status(HttpStatus.OK).body(insert_successful);
    }
    @GetMapping("/semester/{semester}")
    public ResponseEntity<?> getClassesBySemester(@PathVariable Long semester){
        List<Class> classes = classService.getClassesBySemester(semester);
        return ResponseEntity.status(HttpStatus.OK).body(classes);
    }
}
