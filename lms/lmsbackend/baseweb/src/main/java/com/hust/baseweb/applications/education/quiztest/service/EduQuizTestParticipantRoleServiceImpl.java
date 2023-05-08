package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTest;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizRole;
import com.hust.baseweb.applications.education.quiztest.model.ModelCreateEduQuizTestParticipantRole;
import com.hust.baseweb.applications.education.quiztest.model.QuizTestParticipantRoleModel;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestRepo;
import com.hust.baseweb.applications.education.quiztest.repo.EduTestQuizRoleRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class EduQuizTestParticipantRoleServiceImpl implements EduQuizTestParticipantRoleService{
    private EduTestQuizRoleRepo eduTestQuizRoleRepo;
    private EduQuizTestRepo eduQuizTestRepo;
    @Override
    public EduTestQuizRole save(ModelCreateEduQuizTestParticipantRole input) {
        EduTestQuizRole eduQuizTestRole = new EduTestQuizRole();
        eduQuizTestRole.setTestId(input.getTestId());
        eduQuizTestRole.setParticipantUserLoginId(input.getUserId());
        eduQuizTestRole.setRoleId(input.getRoleId());
        eduQuizTestRole.setStatusId(EduTestQuizRole.STATUS_APPROVED);
        eduQuizTestRole = eduTestQuizRoleRepo.save(eduQuizTestRole);

        return eduQuizTestRole;
    }

    @Override
    public List<QuizTestParticipantRoleModel> getParticipantRolesOfQuizTest(String testId) {
        //List<EduTestQuizRole> eduTestQuizRoles = eduTestQuizRoleRepo.findAllByTestId(testId);
        List<EduTestQuizRole> eduTestQuizRoles = eduTestQuizRoleRepo.findByTestId(testId);

        List<QuizTestParticipantRoleModel> res = new ArrayList();
        //log.info("getParticipantRolesOfQuizTest, testId = " + testId + " list  = " + eduTestQuizRoles.size() + "");
        for(EduTestQuizRole r: eduTestQuizRoles){
            //log.info("getParticipantRolesOfQuizTest, role  = {}",r);
            QuizTestParticipantRoleModel m = new QuizTestParticipantRoleModel();
            m.setTestId(testId);
            m.setUserId(r.getParticipantUserLoginId());
            m.setRoleId(r.getRoleId());
            res.add(m);
        }
        return res;
    }
    @Override
    public List<QuizTestParticipantRoleModel> getParticipantRolesOfUserInQuizTest(String userId, String testId) {
        //List<EduTestQuizRole> eduTestQuizRoles = eduTestQuizRoleRepo.findAllByTestId(testId);
        List<EduTestQuizRole> eduTestQuizRoles = eduTestQuizRoleRepo.findAllByTestIdAndParticipantUserLoginId(testId, userId);

        List<QuizTestParticipantRoleModel> res = new ArrayList();
        //log.info("getParticipantRolesOfQuizTest, testId = " + testId + " list  = " + eduTestQuizRoles.size() + "");
        for(EduTestQuizRole r: eduTestQuizRoles){
            //log.info("getParticipantRolesOfQuizTest, role  = {}",r);
            QuizTestParticipantRoleModel m = new QuizTestParticipantRoleModel();
            m.setTestId(testId);
            m.setUserId(r.getParticipantUserLoginId());
            m.setRoleId(r.getRoleId());
            res.add(m);
        }
        return res;
    }

    @Override
    @Transactional
    public void deleteParticipantRole(String testId, String participantId, String role) {
        eduTestQuizRoleRepo.deleteByTestIdAndParticipantUserLoginIdAndRoleId(testId, participantId, role);
    }

    @Override
    public List<QuizTestParticipantRoleModel> getQuizTestsOfUser(String userId) {
        List<EduTestQuizRole> eduTestQuizRoles = eduTestQuizRoleRepo.findByParticipantUserLoginId(userId);
        log.info("getQuizTestsOfUser, user = " + userId + " res.sz = " + eduTestQuizRoles.size());
        List<QuizTestParticipantRoleModel> res = new ArrayList();
        for(EduTestQuizRole r: eduTestQuizRoles){
            //log.info("getParticipantRolesOfQuizTest, role  = {}",r);
            QuizTestParticipantRoleModel m = new QuizTestParticipantRoleModel();
            m.setTestId(r.getTestId());
            m.setUserId(r.getParticipantUserLoginId());
            m.setRoleId(r.getRoleId());
            res.add(m);
        }
        return res;
    }

    @Override
    public List<QuizTestParticipantRoleModel> getAllQuizTests() {
        //List<EduTestQuizRole> eduTestQuizRoles = eduTestQuizRoleRepo.findAll();
        List<EduQuizTest> eduQuizTests = eduQuizTestRepo.findAll();
        List<QuizTestParticipantRoleModel> res = new ArrayList();
        for(EduQuizTest r: eduQuizTests){
            //log.info("getParticipantRolesOfQuizTest, role  = {}",r);
            QuizTestParticipantRoleModel m = new QuizTestParticipantRoleModel();
            m.setTestId(r.getTestId());
            m.setUserId(r.getCreatedByUserLoginId());
            m.setRoleId("");
            res.add(m);
        }
        return res;
    }
}
