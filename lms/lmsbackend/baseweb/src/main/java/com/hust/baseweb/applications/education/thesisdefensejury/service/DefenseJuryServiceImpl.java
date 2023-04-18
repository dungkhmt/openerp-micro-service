package com.hust.baseweb.applications.education.thesisdefensejury.service;

import com.hust.baseweb.applications.education.recourselink.entity.EducationResourceDomain;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.EduTeacher;
import com.hust.baseweb.applications.education.teacherclassassignment.repo.EduTeacherRepo;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.*;
import com.hust.baseweb.applications.education.thesisdefensejury.models.*;
import com.hust.baseweb.applications.education.thesisdefensejury.repo.*;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.repo.UserLoginRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Transactional
@Slf4j
public class DefenseJuryServiceImpl implements DefenseJuryService {

    private final DefenseJuryRepo defenseJuryRepo;
    private  final TranningProgramRepo tranningProgramRepo;
    private  final ThesisDefensePlanRepo thesisDefensePlanRepo;
    private final UserLoginRepo userLoginRepo;
    private final EduTeacherRepo eduTeacherRepo;
    private final DefenseJuryTeacherRepo defenseJuryTeacherRepo;
    private final ThesisRepo thesisRepo;

    @Override
    public DefenseJury createDefenseJury(DefenseJuryIM jury) {
        System.out.println(jury);        // TODO: check valid all fields
        if ((jury.getName()=="")||(jury.getDefenseDate()==null)
            ||(jury.getThesisPlanName()==null)||(jury.getUserLoginID())==null){
            return null;
        }
        String s1 = jury.getName().substring(0, 1).toUpperCase() + jury.getName().substring(1);
        System.out.println(s1);
        // check program name and thesis plan name
//        Optional<TraningProgram> tp = tranningProgramRepo.findByName(jury.getProgram_name());
        Optional<ThesisDefensePlan> tdp = thesisDefensePlanRepo.findByName(jury.getThesisPlanName());
        UserLogin user = userLoginRepo.findByUserLoginId(jury.getUserLoginID());
//        System.out.println(tp);
        if ((tdp==null)|| (user==null)){
            return null;
        }
        // check existed defense jury
        List<DefenseJury> df = defenseJuryRepo.findByName(jury.getName());
        if (df != null) {
            for (DefenseJury ele : df) {
                if (Objects.equals(jury.getName(), ele.getName())) {
                    return null;
                }
            }
        }
        System.out.println(jury.getName());
        DefenseJury dj = new DefenseJury();
        dj.setName(jury.getName());
//        dj.setProgramID(tp.get().getId());
        dj.setThesisDefensePlanID(tdp.get().getId());
        dj.setDefenseDate(jury.getDefenseDate());
        dj.setMaxThesis(jury.getMaxThesis());
        dj.setUserLoginId(jury.getUserLoginID());
        DefenseJury res = defenseJuryRepo.save(dj);

        return res;
    }

    @Override
    public Optional<DefenseJury> findById(UUID id) {
        return defenseJuryRepo.findById(id);
    }

    @Override
    public DefenseJuryOM getDefenseJury(DefenseJury jury) {
        Optional<DefenseJury> dj = defenseJuryRepo.findById(jury.getId());
        if (dj == null){
            return null;
        }
        System.out.println(dj);

        // get thesis plan name and tranning program
        Optional<ThesisDefensePlan> thesisDefensePlan = thesisDefensePlanRepo.findById(dj.get().getThesisDefensePlanID());
        Optional<TraningProgram> traningProgram = tranningProgramRepo.findById(dj.get().getProgramID());
//        if ((!thesisDefensePlan.isPresent())||(!traningProgram.isPresent())){
//            return null;
//        }
        System.out.println(traningProgram);

        DefenseJuryOM res = new DefenseJuryOM();

        res.setName(dj.get().getName());
        res.setUserLoginID(dj.get().getUserLoginId());
        res.setThesisPlanName(thesisDefensePlan.get().getName());
        res.setDefenseDate(dj.get().getDefenseDate());
        res.setMaxThesis(dj.get().getMaxThesis());
        res.setProgram_name(traningProgram.get().getName());
        return res;
    }

    @Override
    public Page<DefenseJury> findAll(Pageable pageable) {
        return defenseJuryRepo.findAll(pageable);
    }

    @Override
    public Response findAllBelongPlanID(String planId) {
        Response res = new Response();
        // check input
        if(planId == ""){
            res.setOk(false);
            res.setErr("Invalid plan id");
            return res;
        }
        // check planID existed
        Optional<ThesisDefensePlan> dp = thesisDefensePlanRepo.findById(planId);
        if(!dp.isPresent()){
            res.setOk(false);
            res.setErr("Plan Id isnt existed");
            return res;
        }
        // TODO: handler
        List<DefenseJury> dj = defenseJuryRepo.findAllByPlanId(planId);
        if (dj.size() == 0){
            res.setOk(true);
            res.setErr("Not found defense jury");
            return res;
        }
        res.setOk(true);
        res.setResult(dj);
        return res;
    }

    @Override
    public List<DefenseJuryOM> searchByDefenseJury(String name) {
        // check input
//        if (name == ""){
//
//        }
        List<DefenseJury> juryList = defenseJuryRepo.findAllByName(name);
//        System.out.println(juryList);
        // mapping defense jury to defense jury OM
        List<DefenseJuryOM> output = new ArrayList<DefenseJuryOM>();
        if(juryList.size()== 0){
            return  null;
        }
        for (int i=0;i<juryList.size();i++){
            DefenseJuryOM res = new DefenseJuryOM();
            // get thesis plan name and tranning program
            Optional<ThesisDefensePlan> thesisDefensePlan = thesisDefensePlanRepo.findById(juryList.get(i).getThesisDefensePlanID());
            Optional<TraningProgram> traningProgram = tranningProgramRepo.findById(juryList.get(i).getProgramID());
//            if ((!thesisDefensePlan.isPresent())||(!traningProgram.isPresent())){
//                return null;
//            }
            System.out.println(traningProgram);
            System.out.println(thesisDefensePlan);


            res.setName(juryList.get(i).getName());
            res.setUserLoginID(juryList.get(i).getUserLoginId());
            res.setThesisPlanName(thesisDefensePlan.get().getName());
            res.setDefenseDate(juryList.get(i).getDefenseDate());
            res.setMaxThesis(juryList.get(i).getMaxThesis());
            res.setProgram_name(traningProgram.get().getName());

            output.add(res);
        }
        return output;
    }

    @Override
    public Response getListDefenseJuryTeachers(UUID defenseJuryID) {
        Response res = new Response();
        if (defenseJuryID == null){
            res.setErr("Invalid defense jury id");
            res.setOk(false);
            return res;
        }

        List<EduTeacher> teachers = new ArrayList<EduTeacher>();
        List<DefenseJuryTeacher> dt = defenseJuryTeacherRepo.findAllByDefenseJuryID(defenseJuryID);
        if (dt.size() >0 ){
            for(int i=0;i<dt.size();i++){
                Optional<EduTeacher> teacher = eduTeacherRepo.findById(dt.get(i).getTeacherId());
                teachers.add(teacher.get());
            }
            res.setOk(true);
            res.setResult(teachers);
        }
        return res;
    }

    @Override
    public Response deleteTheisByIdAtIt(ThesisWithDefenseJuryIM request,UUID juryId) {
        Response res = new Response();
        if (request.getThesisId() == null || juryId == null){
            res.setOk(false);
            res.setErr("Invalid thesis ID or defense jury ID ");
            return res;
        }
        // check exist of defense juryid
        Optional<DefenseJury> dj = defenseJuryRepo.findById(juryId);
        if (!dj.isPresent()){
            res.setOk(false);
            res.setErr("Denfense Jury isnt existed");
            return res;
        }
        // check exist at thesis
        Optional<Thesis> thesis = thesisRepo.findByIdAndDefenseJury(request.getThesisId(),juryId);
        System.out.println(thesis);
        if (!thesis.isPresent()){
            res.setOk(false);
            res.setErr("Not any thesis is found satisfy the condition");
            return res;
        }

//        thesisSaved.setId(thesis.get().getId());
//        thesisSaved.setThesisName(thesis.get().getThesisName());
//        thesisSaved.setThesisAbstract(thesis.get().getThesisAbstract());
//        thesisSaved.setProgramId(thesis.get().getProgramId());
//        thesisSaved.setDefensePlanId(thesis.get().getDefensePlanId());
//        thesisSaved.setStudentName(thesis.get().getStudentName());
//        thesisSaved.setSupervisor(thesis.get().getSupervisor());
//        thesisSaved.setUserLogin(thesis.get().getUserLogin());
//        thesisSaved.setScheduled_reviewer_id(thesis.get().getScheduled_reviewer_id());
//        thesisSaved.setDefenseJury(null);
//        thesisSaved.setThesisKeyword(thesis.get().getThesisKeyword());
//        thesisSaved.setUpdatedDateTime(thesis.get().getUpdatedDateTime());
//        thesisSaved.setCreatedTime(thesis.get().getCreatedTime());
//        thesisRepo.save(thesisSaved);
        thesisRepo.updateDeleteThesisByDefenJuryId(request.getThesisId());
        res.setOk(true);
        // delete thesisId at defenseJury table;

        return res;
    }

    @Override
    public Response addTheisByIdAtIt(ThesisWithDefenseJuryIM request, UUID juryId) {
        Response res = new Response();
        if (request.getThesisId() == null || juryId == null){
            res.setOk(false);
            res.setErr("Invalid thesis ID or defense jury ID ");
            return res;
        }
        // check exist of defense juryid
        Optional<DefenseJury> dj = defenseJuryRepo.findById(juryId);
        if (!dj.isPresent()){
            res.setOk(false);
            res.setErr("Denfense Jury isnt existed");
            return res;
        }

        Long countThesisBelongJury = thesisRepo.getCountThesisByJuryId(juryId);
        System.out.println(countThesisBelongJury);
        if (dj.get().getMaxThesis() <= countThesisBelongJury) {
            res.setOk(false);
            res.setErr("Không thể thêm luận văn vào hội đồng này vì đã vượt quá số lượng đồ án cho phép đăng ký");
            return res;
        }
        // check exist at thesis
        System.out.println(request.getThesisId());
        Optional<Thesis> thesis = thesisRepo.findById(request.getThesisId());
        System.out.println(thesis);
        if (!thesis.isPresent()){
            res.setOk(false);
            res.setErr("Not any thesis is found satisfy the condition");
            return res;
        }

        thesisRepo.updateThesisByDefenJuryId(request.getThesisId(),juryId);
        res.setOk(true);
        // delete thesisId at defenseJury table;

        return res;
    }
}
