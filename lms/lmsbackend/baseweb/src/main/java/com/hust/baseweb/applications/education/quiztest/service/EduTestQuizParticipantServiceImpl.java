package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizParticipant;
import com.hust.baseweb.applications.education.quiztest.model.edutestquizparticipation.EduTestQuizParticipationCreateInputModel;
import com.hust.baseweb.applications.education.quiztest.repo.EduTestQuizParticipantRepo;
import com.hust.baseweb.applications.education.quiztest.utils.Utils;
import com.hust.baseweb.entity.UserLogin;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hust.baseweb.repo.UserLoginRepo;
import java.util.List;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class EduTestQuizParticipantServiceImpl implements EduTestQuizParticipantService {

    private EduTestQuizParticipantRepo eduTestQuizParticipationRepo;
    private UserLoginRepo userLoginRepo;

    @Override
    public EduTestQuizParticipant register(UserLogin userLogin, EduTestQuizParticipationCreateInputModel input) {
        List<EduTestQuizParticipant> eduTestQuizParticipants = eduTestQuizParticipationRepo
            .findByTestIdAndParticipantUserLoginId(input.getTestQuizId(), userLogin.getUserLoginId());

        if (eduTestQuizParticipants != null && eduTestQuizParticipants.size() > 0) {
            log.info("register, record userLoginId = " +
                     userLogin.getUserLoginId() +
                     " testId " +
                     input.getTestQuizId() +
                     " EXISTS!!");
            return null;
        }

        EduTestQuizParticipant eduTestQuizParticipant = new EduTestQuizParticipant();
        eduTestQuizParticipant.setTestId(input.getTestQuizId());
        eduTestQuizParticipant.setParticipantUserLoginId(userLogin.getUserLoginId());

        eduTestQuizParticipant.setStatusId(eduTestQuizParticipant.STATUS_REGISTERED);

        // generate random permutation
        String p = Utils.genRandomPermutation(10);
        eduTestQuizParticipant.setPermutation(p);

        eduTestQuizParticipant = eduTestQuizParticipationRepo.save(eduTestQuizParticipant);

        return eduTestQuizParticipant;
    }

    @Override
    public EduTestQuizParticipant findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(
        String userId,
        String testId
    ) {
        return eduTestQuizParticipationRepo.findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(userId, testId);
    }

    @Override
    public boolean addParticipant2QuizTest(String userId, String testId){
        List<EduTestQuizParticipant> eduTestQuizParticipants = eduTestQuizParticipationRepo
            .findByTestIdAndParticipantUserLoginId(testId, userId);

        if (eduTestQuizParticipants != null && eduTestQuizParticipants.size() > 0) {
            log.info("addParticipant2QuizTest, record userLoginId = " +
                     userId +
                     " testId " +
                     testId +
                     " EXISTS!!");
            return false;
        }
        UserLogin u = userLoginRepo.findById(userId).orElse(null);
        if(u == null){
            log.info("addParticipant2QuizTest, userLoginId = " +
                     userId  +
                     " NOT EXISTS!!");
            return false;
        }

        EduTestQuizParticipant eduTestQuizParticipant = new EduTestQuizParticipant();
        eduTestQuizParticipant.setTestId(testId);
        eduTestQuizParticipant.setParticipantUserLoginId(userId);

        eduTestQuizParticipant.setStatusId(eduTestQuizParticipant.STATUS_APPROVED);

        // generate random permutation
        String p = Utils.genRandomPermutation(10);
        eduTestQuizParticipant.setPermutation(p);

        eduTestQuizParticipant = eduTestQuizParticipationRepo.save(eduTestQuizParticipant);

        return true;
    }
}
