package com.hust.baseweb.applications.education.classmanagement.service;

import com.hust.baseweb.applications.education.classmanagement.entity.EduClassSession;
import com.hust.baseweb.applications.education.classmanagement.enumeration.RegistStatus;
import com.hust.baseweb.applications.education.classmanagement.model.EduClassSessionDetailOM;
import com.hust.baseweb.applications.education.classmanagement.repo.EduClassSessionRepo;
import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.model.GetStudentsOfClassOM;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTest;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroup;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizParticipant;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroup.GenerateQuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestRepo;
import com.hust.baseweb.applications.education.quiztest.repo.EduTestQuizParticipantRepo;
import com.hust.baseweb.applications.education.quiztest.service.EduQuizTestGroupService;
import com.hust.baseweb.applications.education.quiztest.utils.Utils;
import com.hust.baseweb.applications.education.repo.ClassRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Date;
import java.util.UUID;

@Service
@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))

public class EduClassSessionServiceImpl implements EduClassSessionService{
    private EduClassSessionRepo eduClassSessionRepo;
    private EduQuizTestRepo eduQuizTestRepo;
    private ClassRepo  classRepo;
    private EduTestQuizParticipantRepo eduTestQuizParticipationRepo;
    private EduQuizTestGroupService eduQuizTestGroupService;

    @Override
    public EduClassSession save(UUID classId, String sessionName, String description, String userLoginId) {
        EduClassSession o = new EduClassSession();
        o.setClassId(classId);
        o.setSessionName(sessionName);
        o.setCreatedByUserLoginId(userLoginId);
        o.setDescription(description);
        o.setStatusId(EduClassSession.STATUS_CREATED);
        o = eduClassSessionRepo.save(o);
        return o;
    }

    @Override
    public List<EduClassSession> findAllByClassId(UUID classId) {
        List<EduClassSession> lst = eduClassSessionRepo.findAllByClassId(classId);
        return lst;
    }

    @Transactional
    @Override
    public EduQuizTest createQuizTestOfClassSession(UUID sessionId, String testId, String testName, int duration) {
        EduClassSession eduClassSession = eduClassSessionRepo.findById(sessionId).orElse(null);
        UUID classId = null;
        String courseId = null;
        if(eduClassSession != null){
            classId = eduClassSession.getClassId();
            EduClass eduClass = classRepo.findById(classId).orElse(null);
            if(eduClass != null){
                EduCourse eduCourse = eduClass.getEduCourse();
                if(eduCourse != null) courseId = eduCourse.getId();
            }
        }
        EduQuizTest eduQuizTest = new EduQuizTest();
        eduQuizTest.setSessionId(sessionId);
        eduQuizTest.setTestId(testId);
        eduQuizTest.setTestName(testName);
        eduQuizTest.setClassId(classId);
        eduQuizTest.setCourseId(courseId);
        eduQuizTest.setScheduleDatetime(new Date());
        eduQuizTest.setCreatedStamp(new Date());
        eduQuizTest.setDuration(duration);// default
        eduQuizTest.setStatusId(EduQuizTest.QUIZ_TEST_STATUS_CREATED);
        eduQuizTest.setJudgeMode(EduQuizTest.JUDGE_MODE_SYNCHRONOUS);
        eduQuizTest.setQuestionStatementViewTypeId(EduQuizTest.QUESTION_STATEMENT_VIEW_TYPE_VISIBLE);
        eduQuizTest.setParticipantQuizGroupAssignmentMode(EduQuizTest.PARTICIPANT_QUIZ_GROUP_ASSIGNMENT_MODE_ASSIGN_GROUP_BEFORE_HANDOUT);

        eduQuizTest = eduQuizTestRepo.save(eduQuizTest);

        // update automatically participants by participants of classId
        List<GetStudentsOfClassOM> participants = classRepo.getStudentsOfClass(classId, RegistStatus.APPROVED.toString());
        for(GetStudentsOfClassOM p: participants){
            EduTestQuizParticipant eduTestQuizParticipant = new EduTestQuizParticipant();
            eduTestQuizParticipant.setTestId(testId);
            eduTestQuizParticipant.setParticipantUserLoginId(p.getId());

            eduTestQuizParticipant.setStatusId(eduTestQuizParticipant.STATUS_APPROVED);

            // generate random permutation
            String perm = Utils.genRandomPermutation(10);
            eduTestQuizParticipant.setPermutation(perm);

            eduTestQuizParticipant = eduTestQuizParticipationRepo.save(eduTestQuizParticipant);

        }

        // gen only one EduQuizTestGroup
        GenerateQuizTestGroupInputModel input = new GenerateQuizTestGroupInputModel(testId,1);
        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupService.generateQuizTestGroups(input);
        return eduQuizTest;
    }

    @Override
    public List<EduQuizTest> findAllBySession(UUID sessionId) {
        List<EduQuizTest> lst = eduQuizTestRepo.findAllBySessionId(sessionId);

        return lst;
    }

    @Override
    public EduClassSessionDetailOM getSessionDetail(UUID sessionId) {
        EduClassSession s = eduClassSessionRepo.findById(sessionId).orElse(null);
        EduClassSessionDetailOM m = new EduClassSessionDetailOM();
        if(s != null){
            m.setSessionId(s.getSessionId()) ;
            m.setSessionName(s.getSessionName());
            m.setClassId(s.getClassId());
        }
        return m;


    }
}
