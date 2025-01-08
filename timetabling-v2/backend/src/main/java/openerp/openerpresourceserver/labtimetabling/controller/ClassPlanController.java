package openerp.openerpresourceserver.labtimetabling.controller;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.labtimetabling.entity.ClassPlan;
import openerp.openerpresourceserver.labtimetabling.service.ClassPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassPlanController {

    private ClassPlanService planningService;

    @GetMapping("/get-all-semesters")
    public ResponseEntity<?> getAllSemesters(){
        List<String> semesters = planningService.getAllSemesters();
        return ResponseEntity.status(HttpStatus.OK).body(semesters);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllClasses(){
        List<String> semesters = planningService.getAllSemesters();
        Map<String, Object> body = new HashMap<>();
        if(!semesters.isEmpty()) {
            Collections.sort(semesters);
            List<ClassPlan> classes = planningService.getClassesBySemester(semesters.get(semesters.size() - 1));
            body.put("semesters", semesters);
            body.put("classes", classes);
        }
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<?> batchInsert(@RequestBody List<ClassPlan> planningList){
        System.out.println(planningList);
        int insert_successful = planningService.batchInsert(planningList);
        System.out.println(insert_successful);
        return ResponseEntity.status(HttpStatus.OK).body(insert_successful);
    }

    @GetMapping("/semester/{semester}")
    public ResponseEntity<?> getClassesBySemester(@PathVariable String semester){
        List<ClassPlan> classes = planningService.getClassesBySemester(semester);
        return ResponseEntity.status(HttpStatus.OK).body(classes);
    }

}
