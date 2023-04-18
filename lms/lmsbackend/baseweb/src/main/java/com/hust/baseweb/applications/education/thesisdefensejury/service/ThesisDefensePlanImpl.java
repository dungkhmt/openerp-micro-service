package com.hust.baseweb.applications.education.thesisdefensejury.service;

import com.hust.baseweb.applications.education.repo.mongodb.TeacherRepo;
import com.hust.baseweb.applications.education.teacherclassassignment.repo.EduTeacherRepo;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.ThesisDefensePlan;
import com.hust.baseweb.applications.education.thesisdefensejury.models.Response;
import com.hust.baseweb.applications.education.thesisdefensejury.models.ThesisDefensePlanIM;
import com.hust.baseweb.applications.education.thesisdefensejury.repo.ThesisDefensePlanRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Transactional
@Slf4j
public class ThesisDefensePlanImpl implements ThesisDefensePlanService{
    private final ThesisDefensePlanRepo thesisDefensePlanRepo;
    private final EduTeacherRepo eduTeacherRepo;
    @Override
    public List<ThesisDefensePlan> getAllThesisDefensePlan() {
        return thesisDefensePlanRepo.findAll();
    }

    @Override
    public Response createThesisDefensePlan(ThesisDefensePlanIM request) {
        Response res = new Response();
        // check request
        if(request.getName() == "" || request.getId() == ""){
            res.setOk(false);
            res.setErr("Invalid defense plan name or id");
            return res;
        }
        // TODO
        // check name and id were existed?
        Optional<ThesisDefensePlan> findbyID = thesisDefensePlanRepo.findById(request.getId());
        if(findbyID.isPresent()){
            res.setOk(false);
            res.setErr("Id of thesis defense plan are existed");
            return res;
        }
        Optional<ThesisDefensePlan> findedPlan = thesisDefensePlanRepo.findByNameAndAndId(request.getName(),
                                                                                          request.getId());
        if (findedPlan.isPresent()){
            res.setOk(false);
            res.setErr("Name or Id of thesis defense plan are existed");
            return res;
        }
        // TODO: handler
        ThesisDefensePlan dp = new ThesisDefensePlan();
        dp.setId(request.getId());
        dp.setName(request.getName());
        thesisDefensePlanRepo.save(dp);
        res.setOk(true);
        return res;
    }

    @Override
    public Response findById(String id) {
        Response res = new Response();
        // check input
        if (id == ""){
            res.setOk(false);
            res.setErr("Invalid id");
            return res;
        }
        // TODO: handler
        Optional<ThesisDefensePlan> dp = thesisDefensePlanRepo.findById(id);
        if(!dp.isPresent()){
            res.setOk(false);
            res.setErr("Not found thesis plan");
            return res;
        }
        res.setOk(true);
        res.setResult(dp.get());
        return res;
    }
}
