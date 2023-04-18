package com.hust.baseweb.applications.education.thesisdefensejury.controller;

import com.hust.baseweb.applications.education.recourselink.entity.EducationResource;
import com.hust.baseweb.applications.education.recourselink.entity.EducationResourceDomain;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.DefenseJury;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.DefenseJuryTeacher;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.Thesis;
import com.hust.baseweb.applications.education.thesisdefensejury.models.*;
import com.hust.baseweb.applications.education.thesisdefensejury.repo.DefenseJuryTeacherRepo;
import com.hust.baseweb.applications.education.thesisdefensejury.repo.ThesisRepo;
import com.hust.baseweb.applications.education.thesisdefensejury.service.DefenseJuryService;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.*;

@Log4j2
@Controller
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DefenseJuryController {
    private final DefenseJuryService juryService;
    private final DefenseJuryTeacherRepo defenseJuryTeacherRepo;
    private final ThesisRepo thesisRepo;
    private UserService userService;
    private static Logger logger = LogManager.getLogger(DefenseJuryController.class);

    @PostMapping("/defense_jury")
    public ResponseEntity<?> createDefenseJury(
        Principal principal,
        @RequestBody DefenseJuryIM request
    ){
        logger.debug(request);
        log.info("Session Login , sessionName = " + principal.getName());
        UserLogin u = userService.findById(principal.getName());
        if (u == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid session login");
        }
        // TODO: check valid request
        if (request == null) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid body request");
        }

        request.setUserLoginID(u.getUserLoginId());
        DefenseJury jury = juryService.createDefenseJury(request);
        if (jury == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Internal server");
        }


        return ResponseEntity.status(HttpStatus.OK).body(jury);
    }

    @GetMapping("/defense_jurys")
    public ResponseEntity<?> getAllDefensseJurys(Pageable pageable){
        try {
            Map<String,Object> response = new HashMap<>();
            Page<DefenseJury> pageDF;
            pageDF = juryService.findAll(pageable);
            response.put("DefenseJurys",pageDF.getContent());
            response.put("currentPagge",pageDF.getNumber());
            response.put("totalItems",pageDF.getTotalElements());
            response.put("totalPages",pageDF.getTotalPages());

            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/defense_jury/{defenseJuryId}")
    public  ResponseEntity<?> getDefenseJury(@PathVariable("defenseJuryId")DefenseJury defenseJury){
        // check request
        if (defenseJury.getId()== null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid defense jury id");
        }
        DefenseJuryOM res = juryService.getDefenseJury(defenseJury);
        if (res == null){
            return ResponseEntity.ok().body("Not found defense jury");
        }
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/defense_jury/{defenseJuryId}/thesiss")
    public  ResponseEntity<?> getListThesisById(@PathVariable("defenseJuryId")UUID juryID){
        // check request
        if (juryID== null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid defense jury id");
        }
        List<Thesis> res = thesisRepo.findAllByJuryID(juryID);
        if (res == null){
            return ResponseEntity.ok().body("Not found thesis");
        }
        return ResponseEntity.ok().body(res);
    }
    @PostMapping("/jury/search")
    public ResponseEntity<?> searchByName(@RequestBody SearchDefenseJuryIM input){
        // check input
//        if (input == null){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid body request");
//        }
//        if (input.getName()=="") {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid defense jury name");
//        }
        List<DefenseJuryOM> res = juryService.searchByDefenseJury(input.getName());

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/defense_jury/{defenseJuryId}/teachers")
    public  ResponseEntity<?> getListDefenseJuryTeachers(@PathVariable("defenseJuryId")UUID juryID){
        // check request
        if (juryID== null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid defense jury id");
        }
        Response res = juryService.getListDefenseJuryTeachers(juryID);

        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/defense_jury/{defenseJuryId}/addTeacher")
    public ResponseEntity<?> addTeachertoDefenseJury(
        @RequestBody AddTeacherToDefenseJuryIM request,
        @PathVariable("defenseJuryId")UUID juryID
    ){
        logger.debug(request);
        // TODO: check valid request
        if (request == null || juryID == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid body request");
        }
        // save data to defense jury teacher
        DefenseJuryTeacher dt = new DefenseJuryTeacher();
        dt.setJuryId(juryID);
        dt.setTeacherId(request.getTeacherId());
        defenseJuryTeacherRepo.save(dt);


        Response res = new Response();
        res.setOk(true);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/defense_jury/{defenseJuryId}/deleteTeacher")
    public ResponseEntity<?> deleteTeachertoDefenseJury(
        @RequestBody AddTeacherToDefenseJuryIM request,
        @PathVariable("defenseJuryId")UUID juryID
    ){
        logger.debug("Inout Delete Teacher",request);
        // TODO: check valid request
        if (request.getTeacherId() == "" || juryID == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid body request");
        }
        // delete data at defense jury teacher
        DefenseJuryTeacher dt = new DefenseJuryTeacher();
        dt.setJuryId(juryID);
        dt.setTeacherId(request.getTeacherId());
        defenseJuryTeacherRepo.delete(dt);


        Response res = new Response();
        res.setOk(true);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/defense_jury/{defenseJuryId}/deleteJury")
    public ResponseEntity<?> deleteJurytoDefenseJury(
        @RequestBody ThesisWithDefenseJuryIM request,
        @PathVariable("defenseJuryId")UUID juryID
    ){
        logger.debug(request);
        // TODO: check valid request
        if (request == null || juryID == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid body request");
        }
        Response res = juryService.deleteTheisByIdAtIt(request,juryID);


        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    @PostMapping("/defense_jury/{defenseJuryId}/addJury")
    public ResponseEntity<?> addJurytoDefenseJury(
        @RequestBody ThesisWithDefenseJuryIM request,
        @PathVariable("defenseJuryId")UUID juryID
    ){
        logger.debug(request);
        // TODO: check valid request
        if (request == null || juryID == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid body request");
        }
        Response res = juryService.addTheisByIdAtIt(request,juryID);


        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/{planId}/defenseJurysBelongPlan")
    public ResponseEntity<?> getAllDefensseJurysBelongPlan(
        @PathVariable("planId")String planId
    ){
        // check input
        if(planId == ""){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid plan id");
        }
        // TODO handler
        Response res = juryService.findAllBelongPlanID(planId);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }



}
