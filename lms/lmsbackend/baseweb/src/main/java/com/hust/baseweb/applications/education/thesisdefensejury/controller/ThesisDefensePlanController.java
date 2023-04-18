package com.hust.baseweb.applications.education.thesisdefensejury.controller;

import com.hust.baseweb.applications.education.entity.mongodb.Teacher;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.EduTeacher;
import com.hust.baseweb.applications.education.teacherclassassignment.repo.EduTeacherRepo;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.*;
import com.hust.baseweb.applications.education.thesisdefensejury.models.*;
import com.hust.baseweb.applications.education.thesisdefensejury.repo.TeacherKeywordRepo;
import com.hust.baseweb.applications.education.thesisdefensejury.repo.TeacherThesisDefensePlanRepo;
import com.hust.baseweb.applications.education.thesisdefensejury.service.ThesisDefensePlanService;
import com.hust.baseweb.applications.education.thesisdefensejury.service.TranningProgramService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Controller
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ThesisDefensePlanController {
    private final ThesisDefensePlanService thesisDefensePlanService;
    private final EduTeacherRepo eduTeacherRepo;
    private final TeacherKeywordRepo teacherKeywordRepo;
    private final TeacherThesisDefensePlanRepo teacherThesisDefensePlanRepo;
    @GetMapping("/thesis_defense_plan")
    public ResponseEntity<?> getAll(Pageable pageable){
        try {
            List<ThesisDefensePlan> tdp;
            tdp = thesisDefensePlanService.getAllThesisDefensePlan();

            return new ResponseEntity<>(tdp, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/thesis_defense_plan/{defensePlanId}")
    public ResponseEntity<?> getDetailThesis(@PathVariable("defensePlanId") String defensePlanId){
        System.out.println(defensePlanId);
        if (defensePlanId == ""){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid defense plan id");
        }

        Response res = thesisDefensePlanService.findById(defensePlanId);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/thesis_defense_plan")
    public ResponseEntity<?> createThesisDefensePlan(
        @RequestBody ThesisDefensePlanIM request
    ){

        // TODO: check valid request
        if (request == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid body request");
        }
        if(request.getName() == ""){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid body request");
        }

        Response res = thesisDefensePlanService.createThesisDefensePlan(request);



        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/thesis_defense_plan/teachers")
    public ResponseEntity<?> getAllTeacher(){
        List<EduTeacher> res = eduTeacherRepo.findAll();
        if (res.size() == 0){
            return ResponseEntity.ok().body("Not found any teacher");
        }
        List<TeacherWithKeyword> tws = new ArrayList<TeacherWithKeyword>();
        for(int i=0;i<res.size();i++){
            TeacherWithKeyword tw = new TeacherWithKeyword();
            // get  list keyword for eachteacher
            List<TeacherKeyword> tk = teacherKeywordRepo.findAllByTeacherId(res.get(i).getId());
            List<String> keywords = new ArrayList<String>();
            for (int j=0;j<tk.size();j++){
                keywords.add(tk.get(j).getKeyword());
            }
            // add to  tws
            tw.setTeacherId(res.get(i).getId());
            tw.setTeacherName(res.get(i).getTeacherName());
            tw.setKeywords(keywords);
            tws.add(tw);
        }

        return ResponseEntity.ok().body(tws);
    }

    @GetMapping("/thesis_defense_plan/{defensePlanId}/teachers")
    public ResponseEntity<?> getAllTeacherBelongToPlan(@PathVariable("defensePlanId") String defensePlanId){
        List<TeacherThesisDefensePlan> res = teacherThesisDefensePlanRepo.findAllByDefensePlanID(defensePlanId);
        if (res.size() == 0){
            return ResponseEntity.ok().body("Not found any thesis defense plan");
        }
        List<TeacherWithKeyword> tws = new ArrayList<TeacherWithKeyword>();
        for(int i=0;i<res.size();i++){
            Optional<EduTeacher> teacher = eduTeacherRepo.findById(res.get(i).getTeacherId());
            TeacherWithKeyword tw = new TeacherWithKeyword();
            // get  list keyword for eachteacher
            List<TeacherKeyword> tk = teacherKeywordRepo.findAllByTeacherId(res.get(i).getTeacherId());
            List<String> keywords = new ArrayList<String>();
            for (int j=0;j<tk.size();j++){
                keywords.add(tk.get(j).getKeyword());
            }
            // add to  tws
            tw.setTeacherId(res.get(i).getTeacherId());
            tw.setTeacherName(teacher.get().getTeacherName());
            tw.setKeywords(keywords);
            tws.add(tw);
        }

        return ResponseEntity.ok().body(tws);
    }


    @PostMapping("/thesis_defense_plan/{defensePlanId}/addTeacher")
    public ResponseEntity<?> addTeachertoDefenseJury(
        @RequestBody AddTeacherToPlanIM request,
        @PathVariable("defensePlanId")String planID
    ){
        // TODO: check valid request
        if (request == null || planID == "") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid body request");
        }
        // save data to defense plan teacher
        TeacherThesisDefensePlan td = new TeacherThesisDefensePlan();
        td.setThesisDefensePlanId(planID);
        td.setTeacherId(request.getTeacherId());
        teacherThesisDefensePlanRepo.save(td);


        Response res = new Response();
        res.setOk(true);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/thesis_defense_plan/{defensePlanId}/deleteTeacher")
    public ResponseEntity<?> deleteTeachertoDefenseJury(
        @RequestBody AddTeacherToPlanIM request,
        @PathVariable("defensePlanId")String planID
    ){
        // TODO: check valid request
        if (request.getTeacherId() == "" || planID == "") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid body request");
        }
        // delete data at defense jury teacher
        TeacherThesisDefensePlan dt = new TeacherThesisDefensePlan();
        dt.setThesisDefensePlanId(planID);
        dt.setTeacherId(request.getTeacherId());
        teacherThesisDefensePlanRepo.delete(dt);


        Response res = new Response();
        res.setOk(true);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/thesis_defense_plan/{defensePlanId}/teacher/edit")
    public ResponseEntity<?> editTeachertoDefensePlan(
        @RequestBody TeacherWithKeyword request,
        @PathVariable("defensePlanId")String planID
    ){
        // TODO: check valid request
        if (request == null || request.getTeacherId()=="" || request.getTeacherName()=="" || planID == "") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid body request");
        }
        // check teacher belong to plan
        TeacherThesisDefensePlan ttp = teacherThesisDefensePlanRepo.findByDefensePlanIDAndTeacherId(planID, request.getTeacherId());
        if (ttp == null) {
            log.info("1");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request invalid");
        }
        // check teacher valid
        EduTeacher et = eduTeacherRepo.findByTeacherID(request.getTeacherId());
        if (et == null) {
            log.info("2");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request invalid");
        }
//        if (et.getTeacherName() != request.getTeacherName()) {
//            log.info("3");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request invalid");
//        }

        // update keyword
        teacherKeywordRepo.deleteByTeacherID(request.getTeacherId());
//        List<TeacherKeyword> tkDao = teacherKeywordRepo.findAllByTeacherId(request.getTeacherId());
        for(int i=0;i<request.getKeywords().size();i++){

            teacherKeywordRepo.insertByTeacherIdAndKeyword(request.getTeacherId(),request.getKeywords().get(i));

        }

        Response res = new Response();
        res.setOk(true);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}




