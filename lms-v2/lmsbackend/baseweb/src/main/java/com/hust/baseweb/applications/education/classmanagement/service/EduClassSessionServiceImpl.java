package com.hust.baseweb.applications.education.classmanagement.service;

import com.hust.baseweb.applications.education.classmanagement.entity.EduClassSession;
import com.hust.baseweb.applications.education.classmanagement.enumeration.RegistStatus;
import com.hust.baseweb.applications.education.classmanagement.model.EduClassSessionDetailOM;
import com.hust.baseweb.applications.education.classmanagement.repo.EduClassSessionRepo;
import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.entity.EduCourseSession;
import com.hust.baseweb.applications.education.entity.EduCourseSessionInteractiveQuiz;
import com.hust.baseweb.applications.education.entity.EduCourseSessionInteractiveQuizQuestion;
import com.hust.baseweb.applications.education.model.GetStudentsOfClassOM;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTest;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroup;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizParticipant;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuiz;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuizQuestion;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroup.GenerateQuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestRepo;
import com.hust.baseweb.applications.education.quiztest.repo.EduTestQuizParticipantRepo;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizQuestionRepo;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizRepo;
import com.hust.baseweb.applications.education.quiztest.service.EduQuizTestGroupService;
import com.hust.baseweb.applications.education.quiztest.utils.Utils;
import com.hust.baseweb.applications.education.repo.ClassRepo;
import com.hust.baseweb.applications.education.repo.EduCourseSessionInteractiveQuizQuestionRepo;
import com.hust.baseweb.applications.education.repo.EduCourseSessionInteractiveQuizRepo;
import com.hust.baseweb.applications.education.repo.EduCourseSessionRepo;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))

public class EduClassSessionServiceImpl implements EduClassSessionService {

    private EduClassSessionRepo eduClassSessionRepo;
    private EduQuizTestRepo eduQuizTestRepo;
    private ClassRepo classRepo;
    private EduTestQuizParticipantRepo eduTestQuizParticipationRepo;
    private EduQuizTestGroupService eduQuizTestGroupService;
    private EduCourseSessionRepo eduCourseSessionRepo;
    private EduCourseSessionInteractiveQuizRepo eduCourseSessionInteractiveQuizRepo;
    private EduCourseSessionInteractiveQuizQuestionRepo eduCourseSessionInteractiveQuizQuestionRepo;
    private InteractiveQuizRepo interactiveQuizRepo;
    private InteractiveQuizQuestionRepo interactiveQuizQuestionRepo;

    @Override
    public EduClassSession save(UUID classId, String sessionName, String description, String userLoginId) {
        EduClassSession o = new EduClassSession();
        o.setClassId(classId);
        o.setSessionName(sessionName);
        o.setCreatedByUserLoginId(userLoginId);
        o.setDescription(description);
        o.setStatusId(EduClassSession.STATUS_CREATED);
        o.setCreatedStamp(new Date());
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
        if (eduClassSession != null) {
            classId = eduClassSession.getClassId();
            EduClass eduClass = classRepo.findById(classId).orElse(null);
            if (eduClass != null) {
                EduCourse eduCourse = eduClass.getEduCourse();
                if (eduCourse != null) {
                    courseId = eduCourse.getId();
                }
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
        List<GetStudentsOfClassOM> participants = classRepo.getStudentsOfClass(
            classId,
            RegistStatus.APPROVED.toString());
        for (GetStudentsOfClassOM p : participants) {
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
        GenerateQuizTestGroupInputModel input = new GenerateQuizTestGroupInputModel(testId, 1);
        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupService.generateQuizTestGroups(input);
        return eduQuizTest;
    }


    @Override
    public List<EduQuizTest> createQuizTestsOfClassSession(UUID sessionId, int numberOfTests, int duration) {
        List<EduQuizTest> res = new ArrayList<>();
        String testId = "";
        String testName = "";

        EduClassSession eduClassSession = eduClassSessionRepo.findById(sessionId).orElse(null);
        List<EduQuizTest> tests = eduQuizTestRepo.findAllBySessionId(sessionId);
        int startQuizIndex = 0;
        if(tests != null) startQuizIndex = tests.size();
        int sessionIndex = 1;
        UUID classId = null;
        String classCode = "";
        String courseId = null;
        String courseName = "";
        if (eduClassSession != null) {
            classId = eduClassSession.getClassId();
            List<EduClassSession> sessions = eduClassSessionRepo.findAllByClassId(classId);
            Collections.sort(sessions, new Comparator<EduClassSession>() {
                @Override
                public int compare(EduClassSession o1, EduClassSession o2) {
                    int res = 1;
                    //if(o1.getStartDatetime() == null) return -1;
                    //if(o2.getStartDatetime() == null) return 1;
                    if(o1.getCreatedStamp() == null) return -1;
                    if(o2.getCreatedStamp() == null) return 1;
                    //if(o1.getStartDatetime().equals(o2.getStartDatetime())) res = 0;
                    //else if(o1.getStartDatetime().before(o2.getStartDatetime())) res = -1;
                    if(o1.getCreatedStamp().equals(o2.getCreatedStamp())) res = 0;
                    else if(o1.getCreatedStamp().before(o2.getCreatedStamp())) res = -1;
                    else if(o2.getCreatedStamp().before(o1.getCreatedStamp())) res = 1;
                    return res;
                }
            });
            for(int i = 0; i < sessions.size(); i++){
                EduClassSession s = sessions.get(i);
                log.info("createQuizTestsOfClassSession, sorted sessions " + s.getSessionId() + " date "
                         + s.getStartDatetime() + " created at " + s.getCreatedStamp());
                if(s.getSessionId() == sessionId){
                    sessionIndex = i+1;
                }
            }

            EduClass eduClass = classRepo.findById(classId).orElse(null);
            if (eduClass != null) {
                classCode = eduClass.getClassCode();

                EduCourse eduCourse = eduClass.getEduCourse();
                if (eduCourse != null) {
                    courseId = eduCourse.getId();
                    courseName = eduCourse.getName();
                }
            }

        }

        for(int t = 1; t <= numberOfTests; t++){
            testId = classCode + "_session" + sessionIndex + "_quiz" + (startQuizIndex + t);
            testName = classCode + " " + "Quiz " + (startQuizIndex + t) + " session " + sessionIndex + " "
                       + courseName;

            EduQuizTest test = createQuizTestOfClassSession(sessionId, testId, testName, duration);
            log.info("createQuizTestsOfClassSession created quiz-test " + testId + " name " + testName);
            res.add(test);
        }
        return res;
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
        if (s != null) {
            m.setSessionId(s.getSessionId());
            m.setSessionName(s.getSessionName());
            m.setClassId(s.getClassId());
        }
        return m;


    }

    @Override
    public List<EduClassSession> addCourseSessionToClass(EduClass eduClass){
        List<EduCourseSession> eduCourseSessions = eduCourseSessionRepo.findByCourseId(eduClass.getEduCourse().getId());
        List<EduCourseSessionInteractiveQuiz> eduCourseSessionInteractiveQuizs = new ArrayList<>();
        List<EduClassSession> eduClassSessions = new ArrayList<>();
        List<InteractiveQuiz> interactiveQuizList = new ArrayList<>();

        for (EduCourseSession eduCourseSession : eduCourseSessions) {

            EduClassSession eduClassSession = new EduClassSession();
            eduClassSession.setClassId(eduClass.getId());
            eduClassSession.setSessionName(eduCourseSession.getSessionName());
            eduClassSession.setCreatedByUserLoginId(eduCourseSession.getCreatedByUserLoginId());
            eduClassSession.setStatusId(eduCourseSession.getStatusId());
            eduClassSession.setDescription(eduCourseSession.getDescription());
            eduClassSession.setCreatedStamp(eduCourseSession.getCreatedStamp());
            EduClassSession addedEduClassSession = eduClassSessionRepo.save(eduClassSession);
            eduClassSessions.add(addedEduClassSession); 

            List<EduCourseSessionInteractiveQuiz> courseInteractiveQuizs = eduCourseSessionInteractiveQuizRepo.findBySessionId(eduCourseSession.getId());
            for (EduCourseSessionInteractiveQuiz eduCourseSessionInteractiveQuiz : courseInteractiveQuizs) {
                eduCourseSessionInteractiveQuizs.add(eduCourseSessionInteractiveQuiz);

                InteractiveQuiz interactiveQuiz = new InteractiveQuiz();
                interactiveQuiz.setInteractive_quiz_name(eduCourseSessionInteractiveQuiz.getInteractiveQuizName());
                interactiveQuiz.setCreatedStamp(new Date());
                interactiveQuiz.setSessionId(addedEduClassSession.getSessionId());
                interactiveQuiz.setStatusId(InteractiveQuiz.STATUS_CREATED);
                InteractiveQuiz addedInteractiveQuiz = interactiveQuizRepo.save(interactiveQuiz);
                interactiveQuizList.add(addedInteractiveQuiz);

                List<EduCourseSessionInteractiveQuizQuestion> courseSessionInteractiveQuizQuestions = eduCourseSessionInteractiveQuizQuestionRepo.findByInteractiveQuizId(eduCourseSessionInteractiveQuiz.getId());

                for (EduCourseSessionInteractiveQuizQuestion courseSessionInteractiveQuizQuestion : courseSessionInteractiveQuizQuestions) {
                    InteractiveQuizQuestion interactiveQuizQuestion = new InteractiveQuizQuestion();
                    interactiveQuizQuestion.setInteractiveQuizId(addedInteractiveQuiz.getInteractive_quiz_id());
                    interactiveQuizQuestion.setQuestionId(courseSessionInteractiveQuizQuestion.getQuestionId());
                    interactiveQuizQuestion.setCreatedStamp(new Date());
                    interactiveQuizQuestion.setLastUpdated(new Date());
                    interactiveQuizQuestionRepo.save(interactiveQuizQuestion);
                }
            //eduCourseSessionInteractiveQuizs.add();
            }

        }
        return eduClassSessions;
    }
}
