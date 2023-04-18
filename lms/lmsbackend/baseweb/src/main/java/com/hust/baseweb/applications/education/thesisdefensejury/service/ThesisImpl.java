package com.hust.baseweb.applications.education.thesisdefensejury.service;


import com.hust.baseweb.applications.education.teacherclassassignment.entity.EduTeacher;
import com.hust.baseweb.applications.education.teacherclassassignment.repo.EduTeacherRepo;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.*;
import com.hust.baseweb.applications.education.thesisdefensejury.models.Response;
import com.hust.baseweb.applications.education.thesisdefensejury.models.ThesisFilter;
import com.hust.baseweb.applications.education.thesisdefensejury.models.ThesisIM;
import com.hust.baseweb.applications.education.thesisdefensejury.models.ThesisOM;
import com.hust.baseweb.applications.education.thesisdefensejury.repo.*;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.repo.UserLoginRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Transactional
@Slf4j
public class ThesisImpl implements ThesisService {
    private final ThesisRepo thesisRepo;
    private final TranningProgramRepo tranningProgramRepo;
    private final ThesisDefensePlanRepo thesisDefensePlanRepo;
    private final EduTeacherRepo eduTeacherRepo;
    private final DefenseJuryRepo defenseJuryRepo;
    private final UserLoginRepo userLoginRepo;
    private final AcademicKeywordRepo academicKeywordRepo;
    private final ThesisKeywordRepo thesisKeywordRepo;

    @Override
    public Thesis createThesis(ThesisIM thesis) {
        Thesis rs = new Thesis();
        // check request

        if( thesis.getName()==""||thesis.getProgram_name()==""||thesis.getThesisPlanName()==""
            || thesis.getStudent_name()==""||thesis.getSupervisor_name()==""
            ||thesis.getUserLoginID()==""||thesis.getKeyword().size() == 0 ){
            log.info("invalid request");
            return null;
        }
        // TODO: program, thesis_defense_plan_name,supervisor,jury,reviewer
        Optional<TraningProgram> traningProgram = tranningProgramRepo.findByName(thesis.getProgram_name());
        if(!traningProgram.isPresent()){
            log.info("not found tranning program name");
            return null;
        }
        Optional<ThesisDefensePlan> thesisDefensePlan = thesisDefensePlanRepo.findByName(thesis.getThesisPlanName());
        if (!thesisDefensePlan.isPresent()){
            log.info("not found thesis defense plan name");
            return null;
        }
        Optional<EduTeacher> eduTeacher = eduTeacherRepo.findById(thesis.getSupervisor_name());
        if (!eduTeacher.isPresent()){
            log.info("not found supervisor teacher name");
            return null;
        }
        List<DefenseJury> defenseJury = new ArrayList<DefenseJury>();
        if (thesis.getDefense_jury_name() != ""){
            defenseJury  = defenseJuryRepo.findByName(thesis.getDefense_jury_name());
            if (defenseJury.size() == 0) {
                log.info("not found defense jury name");
                return null;
            }
            rs.setDefenseJury(defenseJury.get(0).getId());
        }
        // if reviewer name exist
        Optional<EduTeacher> reviewer = Optional.of(new EduTeacher());
        if (thesis.getReviewer_name() != ""){
            reviewer = eduTeacherRepo.findById(thesis.getReviewer_name());
            if (!reviewer.isPresent()){
                log.info("not found reviewer teacher name");
                return null;
            }
            rs.setScheduled_reviewer_id(reviewer.get().getId());
        }
        // check UserId exist
        UserLogin userLogin = userLoginRepo.findByUserLoginId(thesis.getUserLoginID());
        if (userLogin == null){
            log.info("not found user login");
            return null;
        }
        // check Thesis name is existed ?
        List<Thesis> t = thesisRepo.findAllByThesisName(thesis.getName());
        if (t.size() > 0){
            log.info("thesis name is existed");
            return null;
        }


        // maping fields and
        // insert or update thesis to db

        rs.setThesisName(thesis.getName());
        rs.setThesisAbstract(thesis.getThesis_abstract());
        rs.setProgramId(traningProgram.get().getId());
        rs.setDefensePlanId(thesisDefensePlan.get().getId());
        rs.setStudentName(thesis.getStudent_name());
        rs.setSupervisor(eduTeacher.get().getId());
        rs.setUserLogin(thesis.getUserLoginID());


//        rs.setThesisKeyword(thesis.getKeyword());
        thesisRepo.save(rs);

        // get just created thesis detail
        Optional<Thesis> createdThesis = thesisRepo.findByThesisName(thesis.getName());
        UUID id = createdThesis.get().getId();
        // check list keyword in academic_keyword
        for (int i=0;i< thesis.getKeyword().size();i++){
            log.info("KeyWords:",thesis.getKeyword().get(i));
            Optional<AcademicKeyword> ak = academicKeywordRepo.findById(thesis.getKeyword().get(i));
            if (ak.isPresent()) {
                // insert to thesis_keyword
                ThesisKeyword tk = new ThesisKeyword();
                tk.setThesis(id);
                tk.setKeyword(thesis.getKeyword().get(i));
                thesisKeywordRepo.save(tk);
            }
        }
        return createdThesis.get();
    }

    @Override
    public ThesisOM findById(UUID id) {
        Optional<Thesis> thesis = thesisRepo.findById(id);
        ThesisOM ele = new ThesisOM();
        ele.setId(thesis.get().getId());
        ele.setName(thesis.get().getThesisName());
        ele.setThesis_abstract(thesis.get().getThesisAbstract());
        // TODO: program, thesis_defense_plan_name,supervisor,jury,reviewer
        Optional<TraningProgram> program = tranningProgramRepo.findById(thesis.get().getProgramId());
        Optional<ThesisDefensePlan> defensePlan = thesisDefensePlanRepo.findById(thesis.get().getDefensePlanId());
        Optional<EduTeacher> supervisor = eduTeacherRepo.findById(thesis.get().getSupervisor());
        Optional<EduTeacher> reviewer = eduTeacherRepo.findById(thesis.get().getScheduled_reviewer_id());
        String juryName = "";
        if (thesis.get().getDefenseJury() != null){
            Optional<DefenseJury> jury = defenseJuryRepo.findById(thesis.get().getDefenseJury());
            juryName = jury.get().getName();
        }
        ele.setProgram_name(program.get().getName());
        ele.setThesisPlanName(defensePlan.get().getName());
        ele.setSupervisor_name(supervisor.get().getTeacherName());
        ele.setDefense_jury_name( juryName);
        ele.setReviewer_name(reviewer.get().getTeacherName());
        ele.setStudent_name(thesis.get().getStudentName());
        ele.setUserLoginID(thesis.get().getUserLogin());
        ele.setName(thesis.get().getThesisName());
        ele.setKeyword(thesis.get().getThesisKeyword());
        ele.setUpdatedDateTime(thesis.get().getUpdatedDateTime());
        ele.setCreatedTime(thesis.get().getCreatedTime());
        return ele;
    }


    @Override
    public Page<ThesisOM> findAll(Pageable pageable) {
        Page<Thesis> thesiss =  thesisRepo.findAll(pageable);
        List<Thesis> thesisList = thesiss.getContent();
        System.out.println(thesisList);
        List<ThesisOM> output = new ArrayList<ThesisOM>();
        for (int i=0;i<thesisList.size();i++) {
            ThesisOM ele = new ThesisOM();
            String juryName = "";
            ele.setId(thesisList.get(i).getId());
            ele.setName(thesisList.get(i).getThesisName());
            ele.setThesis_abstract(thesisList.get(i).getThesisAbstract());
            // TODO: program, thesis_defense_plan_name,supervisor,jury,reviewer
            Optional<TraningProgram> program = tranningProgramRepo.findById(thesisList.get(i).getProgramId());
            Optional<ThesisDefensePlan> defensePlan = thesisDefensePlanRepo.findById(thesisList.get(i).getDefensePlanId());
            Optional<EduTeacher> supervisor = eduTeacherRepo.findById(thesisList.get(i).getSupervisor());
            Optional<EduTeacher> reviewer = eduTeacherRepo.findById(thesisList.get(i).getScheduled_reviewer_id());
            if (thesisList.get(i).getDefenseJury() != null){
                Optional<DefenseJury> jury = defenseJuryRepo.findById(thesisList.get(i).getDefenseJury());
                juryName = jury.get().getName();
            }


            ele.setProgram_name(program.get().getName());
            ele.setThesisPlanName(defensePlan.get().getName());
            ele.setSupervisor_name(supervisor.get().getTeacherName());
            ele.setDefense_jury_name(juryName);
            ele.setReviewer_name(reviewer.get().getTeacherName());
            ele.setStudent_name(thesisList.get(i).getStudentName());
            ele.setUserLoginID(thesisList.get(i).getUserLogin());
            ele.setName(thesisList.get(i).getThesisName());
            ele.setKeyword(thesisList.get(i).getThesisKeyword());
            ele.setUpdatedDateTime(thesisList.get(i).getUpdatedDateTime());
            ele.setCreatedTime(thesisList.get(i).getCreatedTime());

            output.add(ele);

        }
        Page<ThesisOM> pagesThesiss = new PageImpl<ThesisOM>(output, pageable, output.size());
        return pagesThesiss;
    }

    @Override
    public List<ThesisOM> searchByThesisName(String name) {
        // check name
//        if (name == ""){
//            return null;
//        }
        // get list theis
        List<Thesis> thesisList = thesisRepo.findAllByThesisName(name);
        if (thesisList.size() == 0){
            return null;
        }
        // mapping thesis to thesisOM
        List<ThesisOM> output = new ArrayList<ThesisOM>();
        for (int i=0;i<thesisList.size();i++){
            ThesisOM ele = new ThesisOM();
            ele.setId(thesisList.get(i).getId());
            ele.setName(thesisList.get(i).getThesisName());
            ele.setThesis_abstract(thesisList.get(i).getThesisAbstract());
            // TODO: program, thesis_defense_plan_name,supervisor,jury,reviewer
            Optional<TraningProgram> program = tranningProgramRepo.findById(thesisList.get(i).getProgramId());
            Optional<ThesisDefensePlan> defensePlan = thesisDefensePlanRepo.findById(thesisList.get(i).getDefensePlanId());
            Optional<EduTeacher> supervisor = eduTeacherRepo.findById(thesisList.get(i).getSupervisor());
            Optional<EduTeacher> reviewer = eduTeacherRepo.findById(thesisList.get(i).getScheduled_reviewer_id());
            Optional<DefenseJury> jury = defenseJuryRepo.findById(thesisList.get(i).getDefenseJury());

            ele.setProgram_name(program.get().getName());
            ele.setThesisPlanName(defensePlan.get().getName());
            ele.setSupervisor_name(supervisor.get().getTeacherName());
            ele.setDefense_jury_name(jury.get().getName());
            ele.setReviewer_name(reviewer.get().getTeacherName());
            ele.setStudent_name(thesisList.get(i).getStudentName());
            ele.setUserLoginID(thesisList.get(i).getUserLogin());
            ele.setName(thesisList.get(i).getThesisName());
            ele.setKeyword(thesisList.get(i).getThesisKeyword());
            ele.setUpdatedDateTime(thesisList.get(i).getUpdatedDateTime());
            ele.setCreatedTime(thesisList.get(i).getCreatedTime());

            output.add(ele);
        }
        return output;
    }

    @Override
    public Response deleteThesis(UUID id, String UserId) {
        Response res = new Response();
        if (id == null || UserId == ""){
            res.setOk(false);
            res.setErr("TheisId or UserLogin ID invalid");
            return res;
        }
        // check UserId exist
        UserLogin userLogin = userLoginRepo.findByUserLoginId(UserId);
        if (userLogin == null){
            res.setOk(false);
            res.setErr("User Login Id isnt existed");
            return  res;
        }

        // delete records which mapping in thesis_keyword table
        thesisKeywordRepo.deleteByThesisId(id);

        thesisRepo.deleteByIdAndUserLogin(id,UserId);
        res.setOk(true);
        return res;
    }

    @Override
    public Response editThesis(ThesisIM thesis) {
        Response res = new Response();
        // check request

        if( thesis.getName()==""||thesis.getProgram_name()==""||thesis.getThesisPlanName()==""
            || thesis.getStudent_name()==""||thesis.getSupervisor_name()==""||thesis.getDefense_jury_name()==""
            ||thesis.getUserLoginID()=="" ){
            log.info("invalid request");
            res.setErr("invalid request");
            res.setOk(false);
            return res;
        }
        // TODO: program, thesis_defense_plan_name,supervisor,jury,reviewer
        Optional<TraningProgram> traningProgram = tranningProgramRepo.findByName(thesis.getProgram_name());
        if(!traningProgram.isPresent()){
            log.info("not found tranning program name");
            res.setOk(false);
            res.setErr("not found tranning program name");
            return res;
        }
        Optional<ThesisDefensePlan> thesisDefensePlan = thesisDefensePlanRepo.findByName(thesis.getThesisPlanName());
        if (!thesisDefensePlan.isPresent()){
            log.info("not found thesis defense plan name");
            res.setOk(false);
            res.setErr("not found thesis defense plan name");
            return res;
        }
        Optional<EduTeacher> eduTeacher = eduTeacherRepo.findByTeacherName(thesis.getSupervisor_name());
        if (!eduTeacher.isPresent()){
            log.info("not found supervisor teacher name");
            res.setOk(false);
            res.setErr("not found supervisor teacher name");
            return null;
        }
        List<DefenseJury> defenseJury  = defenseJuryRepo.findByName(thesis.getDefense_jury_name());
        if (defenseJury.size() == 0) {
            log.info("not found defense jury name");
            res.setOk(false);
            res.setErr("not found defense jury name");
            return res;
        }
        // if reviewer name exist
        Optional<EduTeacher> reviewer = Optional.of(new EduTeacher());
        if (thesis.getReviewer_name() != ""){
            reviewer = eduTeacherRepo.findByTeacherName(thesis.getReviewer_name());
            if (!reviewer.isPresent()){
                log.info("not found reviewer teacher name");
                res.setOk(false);
                res.setErr("not found reviewer teacher name");
                return res;
            }
        }

        // maping fields and
        // insert or update thesis to db
        Optional<Thesis> found = thesisRepo.findById(thesis.getId());
        Thesis rs = found.get();
        rs.setThesisName(thesis.getName());
        rs.setThesisAbstract(thesis.getThesis_abstract());
        rs.setProgramId(traningProgram.get().getId());
        rs.setDefensePlanId(thesisDefensePlan.get().getId());
        rs.setStudentName(thesis.getStudent_name());
        rs.setSupervisor(eduTeacher.get().getId());
        rs.setUserLogin(thesis.getUserLoginID());
        rs.setDefenseJury(defenseJury.get(0).getId());
        rs.setScheduled_reviewer_id(reviewer.get().getId());
//        rs.setThesisKeyword(thesis.getKeyword());
        thesisRepo.save(rs);

        // get just created thesis detail
        Optional<Thesis> editedThesis = thesisRepo.findById(thesis.getId());
        res.setOk(true);
        res.setResult(editedThesis.get());
        return res;
    }

    @Override
    public Response disableThesisWithDefenseJury(UUID id, UUID defenseJuryId) {
        Response res = new Response();
        if(id == null || defenseJuryId == null){
            res.setOk(false);
            res.setErr("Invalid thesisId or DefenseJuryId");
            return res;
        }
        // check defense jury id exist ?
        Optional<DefenseJury> df = defenseJuryRepo.findById(defenseJuryId);
        if (!df.isPresent()){
            res.setOk(false);
            res.setErr("DefenseJuryId isnt existed");
            return  res;
        }
        // disable

        return null;
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
        List<Thesis> t = thesisRepo.findAllByPlanId(planId);
        if (t.size() == 0){
            res.setOk(true);
            res.setErr("Not found thesis");
            return res;
        }
        res.setOk(true);
        res.setResult(t);
        return res;
    }

    @Override
    public Response filterThesis(ThesisFilter filter) {
        log.info("Request: ",filter);
        Response res = new Response();
        List<Thesis> t = new ArrayList<Thesis>();
        // custom query
        if (filter.getKey() == null) {
            filter.setKey("");
        }
        if (filter.getThesisPlanId() == ""){
            filter.setThesisPlanId(null);
        }

        if (filter.getThesisPlanId() != null && filter.getJuryId() != null){
            log.info("Case 1");
            t = thesisRepo.findByDefensePlanIdAndAndDefenseJuryAndThesisName(filter.getThesisPlanId(),filter.getJuryId(),filter.getKey());

        }else if(filter.getThesisPlanId() == null && filter.getJuryId() != null){
            log.info("Case 2");
            t = thesisRepo.findAllByJuryIDAAndThesisName(filter.getJuryId(),filter.getKey());
        }else if (filter.getThesisPlanId() != null && filter.getJuryId() == null){
            log.info("Case 3");
            log.info("Case 3: ",filter.getThesisPlanId());
            t = thesisRepo.findAllByPlanIdAndThesisName(filter.getThesisPlanId(),filter.getKey());
        }else {
            log.info("Case 4");
            t = thesisRepo.findAllByThesisName(filter.getKey());
        }
        log.info("Result: ",t);
        List<ThesisOM> output = new ArrayList<ThesisOM>();
        for (int i=0;i<t.size();i++) {
            ThesisOM ele = new ThesisOM();
            String juryName = "";
            ele.setId(t.get(i).getId());
            ele.setName(t.get(i).getThesisName());
            ele.setThesis_abstract(t.get(i).getThesisAbstract());
            // TODO: program, thesis_defense_plan_name,supervisor,jury,reviewer
            Optional<TraningProgram> program = tranningProgramRepo.findById(t.get(i).getProgramId());
            Optional<ThesisDefensePlan> defensePlan = thesisDefensePlanRepo.findById(t.get(i).getDefensePlanId());
            Optional<EduTeacher> supervisor = eduTeacherRepo.findById(t.get(i).getSupervisor());
            Optional<EduTeacher> reviewer = eduTeacherRepo.findById(t.get(i).getScheduled_reviewer_id());
            if (t.get(i).getDefenseJury() != null){
                Optional<DefenseJury> jury = defenseJuryRepo.findById(t.get(i).getDefenseJury());
                juryName = jury.get().getName();
            }


            ele.setProgram_name(program.get().getName());
            ele.setThesisPlanName(defensePlan.get().getName());
            ele.setSupervisor_name(supervisor.get().getTeacherName());
            ele.setDefense_jury_name(juryName);
            ele.setReviewer_name(reviewer.get().getTeacherName());
            ele.setStudent_name(t.get(i).getStudentName());
            ele.setUserLoginID(t.get(i).getUserLogin());
            ele.setName(t.get(i).getThesisName());
            ele.setKeyword(t.get(i).getThesisKeyword());
            ele.setUpdatedDateTime(t.get(i).getUpdatedDateTime());
            ele.setCreatedTime(t.get(i).getCreatedTime());

            output.add(ele);

        }
        res.setOk(true);
        res.setResult(output);
        return res;
    }
}
