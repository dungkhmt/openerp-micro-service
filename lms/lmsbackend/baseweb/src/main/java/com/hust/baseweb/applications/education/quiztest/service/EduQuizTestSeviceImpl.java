package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.classmanagement.entity.EduClassSession;
import com.hust.baseweb.applications.education.classmanagement.repo.EduClassSessionRepo;
import com.hust.baseweb.applications.education.classmanagement.service.ClassService;
import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.QuizChoiceAnswer;
import com.hust.baseweb.applications.education.entity.QuizQuestion;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.quiztest.UserQuestionQuizExecutionOM;
import com.hust.baseweb.applications.education.quiztest.entity.*;
import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeEduTestQuizGroupParticipationAssignmentId;
import com.hust.baseweb.applications.education.quiztest.model.EditQuizTestInputModel;
import com.hust.baseweb.applications.education.quiztest.model.EduQuizTestModel;
import com.hust.baseweb.applications.education.quiztest.model.ModelResponseGetMyQuizTest;
import com.hust.baseweb.applications.education.quiztest.model.QuizTestCreateInputModel;
import com.hust.baseweb.applications.education.quiztest.model.StudentInTestQueryReturnModel;
import com.hust.baseweb.applications.education.quiztest.model.edutestquizparticipation.QuizTestParticipationExecutionResultOutputModel;
import com.hust.baseweb.applications.education.quiztest.model.quitestgroupquestion.AutoAssignQuestion2QuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroup.AutoAssignParticipants2QuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroup.QuizTestGroupInfoModel;
import com.hust.baseweb.applications.education.quiztest.repo.*;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestGroupRepo.QuizTestGroupInfo;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestRepo.StudentInfo;
import com.hust.baseweb.applications.education.repo.ClassRepo;
import com.hust.baseweb.applications.education.repo.QuizChoiceAnswerRepo;
import com.hust.baseweb.applications.education.repo.QuizQuestionRepo;
import com.hust.baseweb.applications.education.service.QuizQuestionService;
import com.hust.baseweb.applications.notifications.service.NotificationsService;
import com.hust.baseweb.config.rabbitmq.QuizRoutingKey;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.repo.UserLoginRepo;
import com.hust.baseweb.repo.UserRegisterRepo;
import com.hust.baseweb.service.UserService;
import com.hust.baseweb.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestConfig.QUIZ_EXCHANGE;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EduQuizTestSeviceImpl implements QuizTestService {

    UserLoginRepo userLoginRepo;
    UserRegisterRepo userRegisterRepo;
    EduQuizTestRepo repo;
    EduTestQuizParticipantRepo eduTestQuizParticipantRepo;
    EduQuizTestGroupRepo eduQuizTestGroupRepo;
    EduTestQuizGroupParticipationAssignmentRepo eduTestQuizGroupParticipationAssignmentRepo;
    QuizQuestionService quizQuestionService;
    ClassService classService;
    ClassRepo classRepo;
    QuizGroupQuestionAssignmentRepo quizGroupQuestionAssignmentRepo;
    QuizGroupQuestionParticipationExecutionChoiceRepo quizGroupQuestionParticipationExecutionChoiceRepo;
    EduQuizTestQuizQuestionService eduQuizTestQuizQuestionService;
    QuizQuestionRepo quizQuestionRepo;
    QuizChoiceAnswerRepo quizChoiceAnswerRepo;
    EduClassSessionRepo eduClassSessionRepo;
    UserService userService;
    QuizTestExecutionSubmissionRepo quizTestExecutionSubmissionRepo;
    HistoryLogQuizGroupQuestionParticipationExecutionChoiceRepo historyLogQuizGroupQuestionParticipationExecutionChoiceRepo;
    private RabbitTemplate rabbitTemplate;
    private NotificationsService notificationsService;

    private EduTestQuizRoleRepo eduTestQuizRoleRepo;

    @Transactional
    @Override
    public EduQuizTest save(QuizTestCreateInputModel input, UserLogin user) {
        EduQuizTest newRecord = new EduQuizTest();

        newRecord.setTestName(input.getTestName());
        newRecord.setCourseId(input.getCourseId());
        newRecord.setCreatedByUserLoginId(user.getUserLoginId());
        newRecord.setDuration(input.getDuration());
        newRecord.setScheduleDatetime(input.getScheduleDatetime());
        newRecord.setStatusId(EduQuizTest.QUIZ_TEST_STATUS_CREATED);
        newRecord.setTestId(input.getTestId());
        newRecord.setClassId(input.getClassId());
        newRecord.setCreatedStamp(new Date());
        newRecord.setLastUpdatedStamp(new Date());
        newRecord.setQuestionStatementViewTypeId(EduQuizTest.QUESTION_STATEMENT_VIEW_TYPE_VISIBLE);
        newRecord.setParticipantQuizGroupAssignmentMode(EduQuizTest.PARTICIPANT_QUIZ_GROUP_ASSIGNMENT_MODE_HANDOUT_THEN_UPDATE_GROUP);
        newRecord.setViewTypeId(EduQuizTest.QUIZ_TEST_VIEW_TYPE_LIST);
        newRecord.setJudgeMode(EduQuizTest.JUDGE_MODE_SYNCHRONOUS);

        newRecord = repo.save(newRecord);

        EduTestQuizRole role = new EduTestQuizRole();
        role.setRoleId(EduTestQuizRole.ROLE_OWNER);
        role.setParticipantUserLoginId(user.getUserLoginId());
        role.setTestId(input.getTestId());
        role.setStatusId(EduTestQuizRole.STATUS_APPROVED);
        role = eduTestQuizRoleRepo.save(role);

        // grant manager role to user admin
        UserLogin admin = userLoginRepo.findByUserLoginId("admin");
        if(admin != null) {
            role = new EduTestQuizRole();
            role.setRoleId(EduTestQuizRole.ROLE_MANAGER);
            role.setParticipantUserLoginId(admin.getUserLoginId());
            role.setTestId(input.getTestId());
            role.setStatusId(EduTestQuizRole.STATUS_APPROVED);
            role = eduTestQuizRoleRepo.save(role);

            // push notification to admin
            notificationsService.create(user.getUserLoginId(), admin.getUserLoginId(),
                                        user.getUserLoginId() + " has created a quiz-test " +
                                        input.getTestId() + " of course " + input.getCourseId()
                , "");

        }


        return newRecord;
    }

    @Override
    public EduQuizTest update(EditQuizTestInputModel input) {
        log.info("update, testId = " +
                 input.getTestId() +
                 ", duration = " +
                 input.getDuration() +
                 " date = " +
                 input.getScheduleDate());

        EduQuizTest eduQuizTest = repo.findById(input.getTestId()).orElse(null);

        if (eduQuizTest != null) {
            eduQuizTest.setDuration(input.getDuration());
            eduQuizTest.setScheduleDatetime(input.getScheduleDate());
            eduQuizTest.setQuestionStatementViewTypeId(input.getQuestionStatementViewTypeId());
            eduQuizTest.setParticipantQuizGroupAssignmentMode(input.getParticipantQuizGroupAssignmentMode());
            eduQuizTest.setViewTypeId(input.getViewTypeId());
            eduQuizTest.setStatusId(input.getStatusId());
            eduQuizTest.setJudgeMode(input.getJudgeMode());
            eduQuizTest = repo.save(eduQuizTest);
            log.info("update, testId = " +
                     input.getTestId() +
                     ", duration = " +
                     input.getDuration() +
                     " date = " +
                     input.getScheduleDate() +
                     " OK updated");
        }
        return null;
    }

    @Override
    public EduQuizTest openQuizTest(String testId) {
        EduQuizTest qt = repo.findById(testId).orElse(null);
        if (qt != null) {
            qt.setStatusId(EduQuizTest.QUIZ_TEST_STATUS_OPEN);
            qt = repo.save(qt);
        }
        return qt;
    }

    @Override
    public EduQuizTest hideQuizTest(String testId) {
        EduQuizTest qt = repo.findById(testId).orElse(null);
        if (qt != null) {
            qt.setStatusId(EduQuizTest.QUIZ_TEST_STATUS_HIDDEN);
            qt = repo.save(qt);
        }
        return qt;
    }

    @Override
    public List<EduQuizTest> getAllTestByCreateUser(String userLoginId) {
        log.info("getAllTestByCreateUser, user = " + userLoginId);
        if (userLoginId.equals("admin")) {

            List<EduQuizTest> res = repo.findAll();
            log.info("getAllTestByCreateUser, user_login_id = admin -> findAll, res = " + res.size());
            return res;
        }
        return repo.findByCreateUser(userLoginId);
    }

    @Override
    public List<StudentInTestQueryReturnModel> getAllStudentInTest(String testId) {
        List<StudentInfo> list = repo.findAllStudentInTest(testId);
        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupRepo.findByTestId(testId);
        List<UUID> groupIds = new ArrayList<UUID>();
        HashMap<UUID, EduTestQuizGroup> mId2Group = new HashMap();
        for (EduTestQuizGroup g : eduTestQuizGroups) {
            groupIds.add(g.getQuizGroupId());
            mId2Group.put(g.getQuizGroupId(), g);
        }
        List<EduTestQuizGroupParticipationAssignment> eduTestQuizGroupParticipationAssignments =
            eduTestQuizGroupParticipationAssignmentRepo.findAllByQuizGroupIdIn(groupIds);

        HashMap<String, EduTestQuizGroupParticipationAssignment> mUserLoginId2Assignment = new HashMap();
        for (EduTestQuizGroupParticipationAssignment a : eduTestQuizGroupParticipationAssignments) {
            mUserLoginId2Assignment.put(a.getParticipationUserLoginId(), a);
        }

        List<StudentInTestQueryReturnModel> re = new ArrayList<>();

        for (StudentInfo studentInfo : list) {
            StudentInTestQueryReturnModel temp = new StudentInTestQueryReturnModel();
            temp.setFullName(studentInfo.getFull_name());
            temp.setTestId(studentInfo.getTest_id());
            temp.setEmail(studentInfo.getEmail());
            temp.setUserLoginId(studentInfo.getUser_login_id());
            temp.setStatusId(studentInfo.getStatus_id());

            EduTestQuizGroupParticipationAssignment a = mUserLoginId2Assignment.get(studentInfo.getUser_login_id());
            temp.setTestGroupCode("-");
            if (a != null) {
                EduTestQuizGroup g = mId2Group.get(a.getQuizGroupId());
                if (g != null) {
                    temp.setTestGroupCode(g.getGroupCode());
                }
            }
            re.add(temp);
        }

        return re;
    }

    @Override
    public List<EduQuizTestModel> getListQuizByUserId(String userLoginId) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        List<EduQuizTestModel> listModel = new ArrayList<>();
        // or find by user id??
        List<EduQuizTest> listEdu = repo.findAll();
        for (EduQuizTest eduEntity :
            listEdu) {
            if (eduEntity.getStatusId() == null ||
                !eduEntity.getStatusId().equals(EduQuizTest.QUIZ_TEST_STATUS_OPEN)) {
                continue;
            }
            if (eduEntity.getSessionId() != null) {
                continue;// do not list quiz test belonging to particilar session
            }

            EduQuizTestModel eduModel = new EduQuizTestModel();
            eduModel.setTestId(eduEntity.getTestId());
            eduModel.setTestName(eduEntity.getTestName());
            eduModel.setCourseId(eduEntity.getCourseId());
            eduModel.setViewTypeId(eduEntity.getViewTypeId());

            String strDate = null;
            if (eduEntity.getScheduleDatetime() != null) {
                strDate = formatter.format(eduEntity.getScheduleDatetime());
            }

            eduModel.setScheduleDatetime(strDate);
            //eduModel.setStatusId(eduEntity.getStatusId());
            List<EduTestQuizParticipant> eduTestQuizParticipants = eduTestQuizParticipantRepo
                .findByTestIdAndParticipantUserLoginId(eduEntity.getTestId(), userLoginId);
            if (eduTestQuizParticipants != null && eduTestQuizParticipants.size() > 0) {
                eduModel.setStatusId(eduTestQuizParticipants.get(0).getStatusId());
            } else {
                eduModel.setStatusId(null);
            }

            listModel.add(eduModel);
        }
        return listModel;
    }

    @Override
    public List<EduQuizTestModel> getListOpenQuizTestOfSession(UUID sessionId, String userLoginId) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        List<EduQuizTestModel> listModel = new ArrayList<>();
        // or find by user id??
        List<EduQuizTest> listEdu = repo.findAllBySessionId(sessionId);
        for (EduQuizTest eduEntity :
            listEdu) {
            if (eduEntity.getStatusId() == null ||
                (!eduEntity.getStatusId().equals(EduQuizTest.QUIZ_TEST_STATUS_OPEN) &&
                !eduEntity.getStatusId().equals(EduQuizTest.QUIZ_TEST_STATUS_RUNNING))) {
                continue;
            }

            EduQuizTestModel eduModel = new EduQuizTestModel();
            eduModel.setTestId(eduEntity.getTestId());
            eduModel.setTestName(eduEntity.getTestName());
            eduModel.setCourseId(eduEntity.getCourseId());

            String strDate = null;
            if (eduEntity.getScheduleDatetime() != null) {
                strDate = formatter.format(eduEntity.getScheduleDatetime());
            }

            eduModel.setScheduleDatetime(strDate);
            //eduModel.setStatusId(eduEntity.getStatusId());
            List<EduTestQuizParticipant> eduTestQuizParticipants = eduTestQuizParticipantRepo
                .findByTestIdAndParticipantUserLoginId(eduEntity.getTestId(), userLoginId);
            if (eduTestQuizParticipants != null && eduTestQuizParticipants.size() > 0) {
                eduModel.setStatusId(eduTestQuizParticipants.get(0).getStatusId());
                listModel.add(eduModel);
            } else {
                eduModel.setStatusId(null);
            }

            //listModel.add(eduModel);
        }
        return listModel;
    }

    private boolean checkCanBeReassignQuizGroup(List<UUID> quizGroupIds) {
        // if there exist participants of one of this group doing quiz, then cannot CHANGE
        List<QuizGroupQuestionParticipationExecutionChoice> quizGroupQuestionParticipationExecutionChoices
            = quizGroupQuestionParticipationExecutionChoiceRepo.findByQuizGroupIdIn(quizGroupIds);

        if (quizGroupQuestionParticipationExecutionChoices == null
            || quizGroupQuestionParticipationExecutionChoices.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    @Override
    public boolean autoAssignParticipants2QuizTestGroup(AutoAssignParticipants2QuizTestGroupInputModel input) {
        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupRepo.findByTestId(input.getQuizTestId());
        if (eduTestQuizGroups.size() <= 0) {
            return false;
        }

        // remove existing records related to eduTestQizGroups
        List<UUID> quizGroupIds = new ArrayList();
        for (EduTestQuizGroup g : eduTestQuizGroups) {
            quizGroupIds.add(g.getQuizGroupId());
        }

        if (checkCanBeReassignQuizGroup(quizGroupIds) == false) {
            log.info("autoAssignParticipants2QuizTestGroup, quiz groups are executed, cannot be CHANGED");
            return false;
        }

        List<EduTestQuizGroupParticipationAssignment> eduTestQuizGroupParticipationAssignments =
            eduTestQuizGroupParticipationAssignmentRepo.findAllByQuizGroupIdIn(quizGroupIds);

        for (EduTestQuizGroupParticipationAssignment a : eduTestQuizGroupParticipationAssignments) {
            eduTestQuizGroupParticipationAssignmentRepo.delete(a);
        }

        // balanced and random assignment algorithms
        List<EduTestQuizParticipant> eduTestQuizParticipants = eduTestQuizParticipantRepo
            .findByTestIdAndStatusId(input.getQuizTestId(), EduTestQuizParticipant.STATUS_APPROVED);

        Random R = new Random();

        HashMap<EduTestQuizGroup, Integer> mGroup2Qty = new HashMap();
        for (EduTestQuizGroup g : eduTestQuizGroups) {
            mGroup2Qty.put(g, 0);
        }
        List<EduTestQuizGroup> cand = new ArrayList();

        for (EduTestQuizParticipant p : eduTestQuizParticipants) {
            int minQty = Integer.MAX_VALUE;
            for (EduTestQuizGroup g : eduTestQuizGroups) {
                if (minQty > mGroup2Qty.get(g)) {
                    minQty = mGroup2Qty.get(g);
                }
                log.info("autoAssignParticipants2QuizTestGroup, Qty = " + mGroup2Qty.get(g) + " minQty = " + minQty);
            }
            cand.clear();
            for (EduTestQuizGroup g : eduTestQuizGroups) {
                if (mGroup2Qty.get(g) == minQty) {
                    cand.add(g);
                }
            }

            //int idx  = R.nextInt(eduTestQuizGroups.size());
            //EduTestQuizGroup g = eduTestQuizGroups.get(idx);
            int idx = R.nextInt(cand.size());
            EduTestQuizGroup g = cand.get(idx);

            EduTestQuizGroupParticipationAssignment a = eduTestQuizGroupParticipationAssignmentRepo
                .findByQuizGroupIdAndParticipationUserLoginId(g.getQuizGroupId(), p.getParticipantUserLoginId());
            if (a == null) {
                log.info("autoAssignParticipants2QuizTestGroup, assignment " +
                         g.getQuizGroupId() +
                         "," +
                         p.getParticipantUserLoginId() +
                         " not exists -> insert new");
                a = new EduTestQuizGroupParticipationAssignment();
                a.setQuizGroupId(g.getQuizGroupId());
                a.setParticipationUserLoginId(p.getParticipantUserLoginId());
                a = eduTestQuizGroupParticipationAssignmentRepo.save(a);
                mGroup2Qty.put(g, minQty + 1);
            }
        }

        return true;
    }

    private List<QuizQuestion> getRandomQuiz(
        List<QuizQuestion> quizQuestions,
        int sz,
        HashMap<QuizQuestion, Integer> mQuiz2QtyUsed
    ) {
        List<QuizQuestion> retList = new ArrayList();
        int maxUsed = 0;
        for (QuizQuestion q : quizQuestions) {
            if (maxUsed < mQuiz2QtyUsed.get(q)) {
                maxUsed = mQuiz2QtyUsed.get(q);
            }
        }
        List<QuizQuestion>[] usedLevelList = new List[maxUsed + 1];
        for (int i = 0; i <= maxUsed; i++) {
            usedLevelList[i] = new ArrayList();
        }
        for (QuizQuestion q : quizQuestions) {
            int qty = mQuiz2QtyUsed.get(q);
            usedLevelList[qty].add(q);
        }
        Random R = new Random();
        int curLevel = 0;
        while (sz > 0) {
            if (sz >= usedLevelList[curLevel].size()) {
                for (QuizQuestion q : usedLevelList[curLevel]) {
                    retList.add(q);
                }
                sz = sz - usedLevelList[curLevel].size();
                curLevel++;
            } else {
                int[] idx = CommonUtils.genRandom(sz, usedLevelList[curLevel].size(), R);
                for (int i = 0; i < idx.length; i++) {
                    retList.add(usedLevelList[curLevel].get(idx[i]));
                }
                sz = 0;
            }
        }
        return retList;
    }

    private List<QuizQuestionDetailModel> getRandomQuizQuestionDetailModel(
        List<QuizQuestionDetailModel> quizQuestions,
        int sz,
        HashMap<QuizQuestionDetailModel, Integer> mQuiz2QtyUsed
    ) {
        List<QuizQuestionDetailModel> retList = new ArrayList();
        int maxUsed = 0;
        for (QuizQuestionDetailModel q : quizQuestions) {
            if (maxUsed < mQuiz2QtyUsed.get(q)) {
                maxUsed = mQuiz2QtyUsed.get(q);
            }
        }
        List<QuizQuestionDetailModel>[] usedLevelList = new List[maxUsed + 1];
        for (int i = 0; i <= maxUsed; i++) {
            usedLevelList[i] = new ArrayList();
        }
        for (QuizQuestionDetailModel q : quizQuestions) {
            int qty = mQuiz2QtyUsed.get(q);
            usedLevelList[qty].add(q);
        }
        Random R = new Random();
        int curLevel = 0;
        while (sz > 0) {
            if (sz >= usedLevelList[curLevel].size()) {
                for (QuizQuestionDetailModel q : usedLevelList[curLevel]) {
                    retList.add(q);
                }
                sz = sz - usedLevelList[curLevel].size();
                curLevel++;
            } else {
                int[] idx = CommonUtils.genRandom(sz, usedLevelList[curLevel].size(), R);
                for (int i = 0; i < idx.length; i++) {
                    retList.add(usedLevelList[curLevel].get(idx[i]));
                }
                sz = 0;
            }
        }
        return retList;
    }

    @Transactional
    @Override
    public boolean autoAssignQuestion2QuizTestGroup(AutoAssignQuestion2QuizTestGroupInputModel input) {
        log.info("autoAssignQuestion2QuizTestGroup, testId = " + input.getQuizTestId() +
                 " number questions = " + input.getNumberQuestions());
        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupRepo.findByTestId(input.getQuizTestId());
        if (eduTestQuizGroups.size() <= 0) {
            return false;
        }
        EduQuizTest eduQuizTest = repo.findById(input.getQuizTestId()).orElse(null);
        if (eduQuizTest == null) {
            log.info("autoAssignQuestion2QuizTestGroup, cannot find quizTest " + input.getQuizTestId());
            return false;
        }

        // remove existing records related to eduTestQizGroups
        List<UUID> quizGroupIds = new ArrayList();
        for (EduTestQuizGroup g : eduTestQuizGroups) {
            quizGroupIds.add(g.getQuizGroupId());
        }

        if (checkCanBeReassignQuizGroup(quizGroupIds) == false) {
            log.info("autoAssignQuestion2QuizTestGroup, quiz groups are executed, cannot be CHANGED");
            return false;
        }


        UUID classId = eduQuizTest.getClassId();
        EduClass eduClass = classService.findById(classId);
        if (eduClass == null) {
            log.info("autoAssignQuestion2QuizTestGroup, cannot find class " + classId);
            return false;
        }

        String courseId = eduClass.getEduCourse().getId();

        //List<QuizQuestion> quizQuestions = quizQuestionService.findQuizOfCourse(courseId);
        List<QuizQuestionDetailModel> quizQuestions = eduQuizTestQuizQuestionService.findAllByTestId(input.getQuizTestId());


        List<QuizQuestionDetailModel> usedQuizQuestions = new ArrayList<>();
        HashMap<QuizQuestionDetailModel, Integer> mQuiz2Qty = new HashMap();// map quiz -> amount used
        HashMap<QuizQuestionDetailModel, Integer> mQuiz2Index = new HashMap();
        HashMap<String, List<QuizQuestionDetailModel>> mapTopicLevel2Question = new HashMap();

        for (QuizQuestionDetailModel q : quizQuestions) {

            if (q.getStatusId().equals(QuizQuestion.STATUS_PUBLIC)) {
                continue;
            }

            String key = q.getQuizCourseTopic().getQuizCourseTopicId() + q.getLevelId();
            if (mapTopicLevel2Question.get(key) == null) {
                mapTopicLevel2Question.put(key, new ArrayList());
            }
            mapTopicLevel2Question.get(key).add(q);
            usedQuizQuestions.add(q);
            mQuiz2Qty.put(q, 0);
        }

        for (int i = 0; i < usedQuizQuestions.size(); i++) {
            mQuiz2Index.put(usedQuizQuestions.get(i), i);
        }
        int activeKeys = 0;
        for (String k : mapTopicLevel2Question.keySet()) {
            if (mapTopicLevel2Question.get(k) != null) {
                activeKeys++;
            }
        }
        String[] sortedTopicLevel = new String[activeKeys];
        int I = 0;
        for (String k : mapTopicLevel2Question.keySet()) {
            if (mapTopicLevel2Question.get(k) != null) {
                sortedTopicLevel[I] = k;
                I++;
            }
        }

        for (int i = 0; i < sortedTopicLevel.length; i++) {
            for (int j = i + 1; j < sortedTopicLevel.length; j++) {
                if (mapTopicLevel2Question.get(sortedTopicLevel[i]).size() <
                    mapTopicLevel2Question.get(sortedTopicLevel[j]).size()) {
                    String t = sortedTopicLevel[i];
                    sortedTopicLevel[i] = sortedTopicLevel[j];
                    sortedTopicLevel[j] = t;
                }
            }
        }

/*
        for(int i = 0; i < sortedTopicLevel.length; i++){
            log.info("autoAssignQuestion2QuizTestGroup, topicLevel " + sortedTopicLevel[i] + " has "
                     + mapTopicLevel2Question.get(sortedTopicLevel[i]).size() + " quizs");

        }
*/

        HashMap<String, Integer> mTopicId2Num = new HashMap<>();
        for (String k : mapTopicLevel2Question.keySet()) {
            mTopicId2Num.put(k, 0);
        }
        int amount = input.getNumberQuestions();
        int cnt = 0;
        //for(String k: mapTopicId2QUestion.keySet()){
        for (int i = 0; i < sortedTopicLevel.length; i++) {
            String k = sortedTopicLevel[i];
            cnt += mapTopicLevel2Question.get(k).size();
        }
        // neu user-input amount > number of availables questions cnt then amount = cnt
        if (amount > cnt) {
            amount = cnt;
        }
        amount = sortedTopicLevel.length;// TEMPORARY USED: each category pick a quiz

        int sel_idx = 0;
        while (amount > 0) {
            int a = mTopicId2Num.get(sortedTopicLevel[sel_idx]);
            if (a < mapTopicLevel2Question.get(sortedTopicLevel[sel_idx]).size()) {
                mTopicId2Num.put(sortedTopicLevel[sel_idx], a + 1);
                amount--;
            }
            sel_idx++;
            if (sel_idx >= sortedTopicLevel.length) {
                sel_idx = 0;
            }
        }
        for (int i = 0; i < sortedTopicLevel.length; i++) {
            log.info("autoAssignQuestion2QuizTestGroup, topic " + sortedTopicLevel[i] + " has "
                     + mapTopicLevel2Question.get(sortedTopicLevel[i]).size() + " questions" +
                     " select " + mTopicId2Num.get(sortedTopicLevel[i]));
        }

        Random R = new Random();

        // delete existing assignment of quiz groups
        for (EduTestQuizGroup g : eduTestQuizGroups) {
            List<QuizGroupQuestionAssignment> list = quizGroupQuestionAssignmentRepo
                .findQuizGroupQuestionAssignmentsByQuizGroupId(g.getQuizGroupId());
            for (QuizGroupQuestionAssignment qq : list) {
                quizGroupQuestionAssignmentRepo.delete(qq);
            }
        }


        for (EduTestQuizGroup g : eduTestQuizGroups) {
            for (int i = 0; i < sortedTopicLevel.length; i++) {
                String topicId = sortedTopicLevel[i];
                int sz = mTopicId2Num.get(topicId);
                List<QuizQuestionDetailModel> questions = mapTopicLevel2Question.get(topicId);
                // select randomly sz questions from questions
                List<QuizQuestionDetailModel> selectedQuiz = getRandomQuizQuestionDetailModel(questions, sz, mQuiz2Qty);
                for (QuizQuestionDetailModel q : selectedQuiz) {
                    log.info("autoAssignQuestion2QuizTestGroup, group " + g.getGroupCode()
                             + " topic " + topicId + " -> select quiz " + mQuiz2Index.get(q));

                    mQuiz2Qty.put(q, mQuiz2Qty.get(q) + 1);// augment the occurrences of q

                    QuizGroupQuestionAssignment qq = quizGroupQuestionAssignmentRepo
                        .findByQuestionIdAndQuizGroupId(q.getQuestionId(), g.getQuizGroupId());

                    if (qq == null) {
                        //log.info("autoAssignQuestion2QuizTestGroup, record " + q.getQuestionId() + "," + g.getQuizGroupId() + " not exists -> insert new");
                        qq = new QuizGroupQuestionAssignment();
                        qq.setQuestionId(q.getQuestionId());
                        qq.setQuizGroupId(g.getQuizGroupId());
                        qq = quizGroupQuestionAssignmentRepo.save(qq);
                    }

                }
                /*
                int[] idx = CommonUtils.genRandom(sz,questions.size(),R);
                if(idx != null){
                    for(int j = 0; j < idx.length; j++){
                        QuizQuestion q = questions.get(idx[j]);
                        QuizGroupQuestionAssignment qq = quizGroupQuestionAssignmentRepo
                            .findByQuestionIdAndQuizGroupId(q.getQuestionId(),g.getQuizGroupId());

                        if(qq == null){
                            log.info("autoAssignQuestion2QuizTestGroup, record " + q.getQuestionId() + "," + g.getQuizGroupId() + " not exists -> insert new");
                            qq = new QuizGroupQuestionAssignment();
                            qq.setQuestionId(q.getQuestionId());
                            qq.setQuizGroupId(g.getQuizGroupId());
                            qq = quizGroupQuestionAssignmentRepo.save(qq);
                        }
                    }
                }

                 */
            }
        }
        //Random R = new Random();
        // assign sequence (seq) for questions in each quiz_group
        for (EduTestQuizGroup g : eduTestQuizGroups) {
            List<QuizGroupQuestionAssignment> questions = quizGroupQuestionAssignmentRepo
                .findQuizGroupQuestionAssignmentsByQuizGroupId(g.getQuizGroupId());

            int[] s = new int[questions.size()];
            for(int i = 0; i < s.length; i++) s[i] = i;
            for(int i = 0; i < s.length; i++){
                int j = R.nextInt(s.length);
                int k = R.nextInt(s.length);
                int tmp = s[j]; s[j] = s[k]; s[k] = tmp;
            }
            for(int i = 0; i < questions.size(); i++){
                QuizGroupQuestionAssignment qqa = questions.get(i);
                qqa.setSeq(s[i]);
                qqa = quizGroupQuestionAssignmentRepo.save(qqa);
            }
        }
        return true;

    }

    @Transactional
    //@Override
    public boolean autoAssignQuestion2QuizTestGroupTMP(AutoAssignQuestion2QuizTestGroupInputModel input) {
        log.info("autoAssignQuestion2QuizTestGroup, testId = " + input.getQuizTestId() +
                 " number questions = " + input.getNumberQuestions());
        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupRepo.findByTestId(input.getQuizTestId());
        if (eduTestQuizGroups.size() <= 0) {
            return false;
        }
        EduQuizTest eduQuizTest = repo.findById(input.getQuizTestId()).orElse(null);
        if (eduQuizTest == null) {
            log.info("autoAssignQuestion2QuizTestGroup, cannot find quizTest " + input.getQuizTestId());
            return false;
        }

        // remove existing records related to eduTestQizGroups
        List<UUID> quizGroupIds = new ArrayList();
        for (EduTestQuizGroup g : eduTestQuizGroups) {
            quizGroupIds.add(g.getQuizGroupId());
        }

        if (checkCanBeReassignQuizGroup(quizGroupIds) == false) {
            log.info("autoAssignQuestion2QuizTestGroup, quiz groups are executed, cannot be CHANGED");
            return false;
        }


        UUID classId = eduQuizTest.getClassId();
        EduClass eduClass = classService.findById(classId);
        if (eduClass == null) {
            log.info("autoAssignQuestion2QuizTestGroup, cannot find class " + classId);
            return false;
        }

        String courseId = eduClass.getEduCourse().getId();

        List<QuizQuestion> quizQuestions = quizQuestionService.findQuizOfCourse(courseId);


        List<QuizQuestion> usedQuizQuestions = new ArrayList<>();
        HashMap<QuizQuestion, Integer> mQuiz2Qty = new HashMap();// map quiz -> amount used
        HashMap<QuizQuestion, Integer> mQuiz2Index = new HashMap();
        HashMap<String, List<QuizQuestion>> mapTopicLevel2Question = new HashMap();

        for (QuizQuestion q : quizQuestions) {

            if (q.getStatusId().equals(QuizQuestion.STATUS_PUBLIC)) {
                continue;
            }

            String key = q.getQuizCourseTopic().getQuizCourseTopicId() + q.getLevelId();
            if (mapTopicLevel2Question.get(key) == null) {
                mapTopicLevel2Question.put(key, new ArrayList<QuizQuestion>());
            }
            mapTopicLevel2Question.get(key).add(q);
            usedQuizQuestions.add(q);
            mQuiz2Qty.put(q, 0);
        }

        for (int i = 0; i < usedQuizQuestions.size(); i++) {
            mQuiz2Index.put(usedQuizQuestions.get(i), i);
        }
        int activeKeys = 0;
        for (String k : mapTopicLevel2Question.keySet()) {
            if (mapTopicLevel2Question.get(k) != null) {
                activeKeys++;
            }
        }
        String[] sortedTopicLevel = new String[activeKeys];
        int I = 0;
        for (String k : mapTopicLevel2Question.keySet()) {
            if (mapTopicLevel2Question.get(k) != null) {
                sortedTopicLevel[I] = k;
                I++;
            }
        }

        for (int i = 0; i < sortedTopicLevel.length; i++) {
            for (int j = i + 1; j < sortedTopicLevel.length; j++) {
                if (mapTopicLevel2Question.get(sortedTopicLevel[i]).size() <
                    mapTopicLevel2Question.get(sortedTopicLevel[j]).size()) {
                    String t = sortedTopicLevel[i];
                    sortedTopicLevel[i] = sortedTopicLevel[j];
                    sortedTopicLevel[j] = t;
                }
            }
        }

/*
        for(int i = 0; i < sortedTopicLevel.length; i++){
            log.info("autoAssignQuestion2QuizTestGroup, topicLevel " + sortedTopicLevel[i] + " has "
                     + mapTopicLevel2Question.get(sortedTopicLevel[i]).size() + " quizs");

        }
*/

        HashMap<String, Integer> mTopicId2Num = new HashMap<>();
        for (String k : mapTopicLevel2Question.keySet()) {
            mTopicId2Num.put(k, 0);
        }
        int amount = input.getNumberQuestions();
        int cnt = 0;
        //for(String k: mapTopicId2QUestion.keySet()){
        for (int i = 0; i < sortedTopicLevel.length; i++) {
            String k = sortedTopicLevel[i];
            cnt += mapTopicLevel2Question.get(k).size();
        }
        // neu user-input amount > number of availables questions cnt then amount = cnt
        if (amount > cnt) {
            amount = cnt;
        }
        amount = sortedTopicLevel.length;// TEMPORARY USED: each category pick a quiz

        int sel_idx = 0;
        while (amount > 0) {
            int a = mTopicId2Num.get(sortedTopicLevel[sel_idx]);
            if (a < mapTopicLevel2Question.get(sortedTopicLevel[sel_idx]).size()) {
                mTopicId2Num.put(sortedTopicLevel[sel_idx], a + 1);
                amount--;
            }
            sel_idx++;
            if (sel_idx >= sortedTopicLevel.length) {
                sel_idx = 0;
            }
        }
        for (int i = 0; i < sortedTopicLevel.length; i++) {
            log.info("autoAssignQuestion2QuizTestGroup, topic " + sortedTopicLevel[i] + " has "
                     + mapTopicLevel2Question.get(sortedTopicLevel[i]).size() + " questions" +
                     " select " + mTopicId2Num.get(sortedTopicLevel[i]));
        }

        Random R = new Random();

        // delete existing assignment of quiz groups
        for (EduTestQuizGroup g : eduTestQuizGroups) {
            List<QuizGroupQuestionAssignment> list = quizGroupQuestionAssignmentRepo
                .findQuizGroupQuestionAssignmentsByQuizGroupId(g.getQuizGroupId());
            for (QuizGroupQuestionAssignment qq : list) {
                quizGroupQuestionAssignmentRepo.delete(qq);
            }
        }


        for (EduTestQuizGroup g : eduTestQuizGroups) {
            for (int i = 0; i < sortedTopicLevel.length; i++) {
                String topicId = sortedTopicLevel[i];
                int sz = mTopicId2Num.get(topicId);
                List<QuizQuestion> questions = mapTopicLevel2Question.get(topicId);
                // select randomly sz questions from questions
                List<QuizQuestion> selectedQuiz = getRandomQuiz(questions, sz, mQuiz2Qty);
                for (QuizQuestion q : selectedQuiz) {
                    log.info("autoAssignQuestion2QuizTestGroup, group " + g.getGroupCode()
                             + " topic " + topicId + " -> select quiz " + mQuiz2Index.get(q));

                    mQuiz2Qty.put(q, mQuiz2Qty.get(q) + 1);// augment the occurrences of q

                    QuizGroupQuestionAssignment qq = quizGroupQuestionAssignmentRepo
                        .findByQuestionIdAndQuizGroupId(q.getQuestionId(), g.getQuizGroupId());

                    if (qq == null) {
                        //log.info("autoAssignQuestion2QuizTestGroup, record " + q.getQuestionId() + "," + g.getQuizGroupId() + " not exists -> insert new");
                        qq = new QuizGroupQuestionAssignment();
                        qq.setQuestionId(q.getQuestionId());
                        qq.setQuizGroupId(g.getQuizGroupId());
                        qq = quizGroupQuestionAssignmentRepo.save(qq);
                    }

                }
                /*
                int[] idx = CommonUtils.genRandom(sz,questions.size(),R);
                if(idx != null){
                    for(int j = 0; j < idx.length; j++){
                        QuizQuestion q = questions.get(idx[j]);
                        QuizGroupQuestionAssignment qq = quizGroupQuestionAssignmentRepo
                            .findByQuestionIdAndQuizGroupId(q.getQuestionId(),g.getQuizGroupId());

                        if(qq == null){
                            log.info("autoAssignQuestion2QuizTestGroup, record " + q.getQuestionId() + "," + g.getQuizGroupId() + " not exists -> insert new");
                            qq = new QuizGroupQuestionAssignment();
                            qq.setQuestionId(q.getQuestionId());
                            qq.setQuizGroupId(g.getQuizGroupId());
                            qq = quizGroupQuestionAssignmentRepo.save(qq);
                        }
                    }
                }

                 */
            }
        }
        return true;

    }


    @Override
    public Integer rejectStudentsInTest(String testId, String[] userLoginId) {
        Integer re = 0;
        for (String student : userLoginId) {
            re += repo.rejectStudentInTest(testId, student);
        }
        return re;
    }

    @Override
    public EduQuizTest getQuizTestById(String testId) {
        Optional<EduQuizTest> re = repo.findById(testId);

        if (re.isPresent()) {
            return re.get();
        } else {
            return null;
        }
    }

    @Override
    public Integer acceptStudentsInTest(String testId, String[] userLoginId) {
        Integer re = 0;
        for (String student : userLoginId) {
            re += repo.acceptStudentInTest(testId, student);
        }
        return re;
    }

    @Override
    public List<QuizTestGroupInfoModel> getQuizTestGroupsInfoByTestId(String testId) {
        List<QuizTestGroupInfo> info = eduQuizTestGroupRepo.findQuizTestGroupsInfo(testId);

        List<QuizTestGroupInfoModel> re = new ArrayList<>();

        for (QuizTestGroupInfo quizTestGroupInfo : info) {
            QuizTestGroupInfoModel temp = new QuizTestGroupInfoModel();
            temp.setQuizGroupId(quizTestGroupInfo.getQuiz_group_id());
            temp.setGroupCode(quizTestGroupInfo.getGroup_code());
            temp.setNote(quizTestGroupInfo.getNote());
            temp.setNumQuestion(quizTestGroupInfo.getNum_question());
            temp.setNumStudent(quizTestGroupInfo.getNum_student());
            re.add(temp);
        }

        return re;
    }

    private boolean checkQuizGroupCanbeDeleted(UUID quizGroupId) {
        List<QuizGroupQuestionParticipationExecutionChoice> lst = quizGroupQuestionParticipationExecutionChoiceRepo
            .findByQuizGroupId(quizGroupId);
        if (lst == null || lst.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public Integer deleteQuizTestGroups(String testId, String[] listQuizTestGroupId) {
        for (String id : listQuizTestGroupId) {
            UUID quizGroupId = UUID.fromString(id);
            if (checkQuizGroupCanbeDeleted(quizGroupId) == false) {
                log.info("deleteQuizTestGroups, quizGroup " + quizGroupId + " being executed, cannot be deleted!");
                return 0;
            }
        }

        Integer re = 0;
        for (String id : listQuizTestGroupId) {
            UUID quizGroupId = UUID.fromString(id);
            re += eduQuizTestGroupRepo.deleteQuizTestGroup(testId, quizGroupId);
        }
        return re;
    }

    /*
    heavy computation
     */
    @Override
    public List<UserQuestionQuizExecutionOM> getQuizTestParticipationExecutionResultOfAUserLogin(String userLoginId) {
        // todo by PQD
        List<QuizGroupQuestionParticipationExecutionChoice> choices = quizGroupQuestionParticipationExecutionChoiceRepo
            .findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginId(userLoginId);
        //log.info("getQuizTestParticipationExecutionResultOfAUserLogin, sz = " + choices.size());

        HashSet<UUID> quizGroupIds = new HashSet();
        HashSet<UUID> questionIds = new HashSet();
        for (QuizGroupQuestionParticipationExecutionChoice i : choices) {
            quizGroupIds.add(i.getQuizGroupId());
            questionIds.add(i.getQuestionId());
        }

        //List<EduQuizTest> eduQuizTests = repo.findAll();


        List<UserQuestionQuizExecutionOM> userQuestionExecutions = new ArrayList<UserQuestionQuizExecutionOM>();


        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupRepo.findByQuizGroupIdIn(quizGroupIds);
        for (EduTestQuizGroup g : eduTestQuizGroups) {
            for (UUID qid : questionIds) {
                List<UUID> chooseAnsIds = new ArrayList();
                Date date = null;
                for (QuizGroupQuestionParticipationExecutionChoice c : choices) {
                    if (c.getQuizGroupId().equals(g.getQuizGroupId()) && c.getQuestionId().equals(qid)) {
                        chooseAnsIds.add(c.getChoiceAnswerId());
                        date = c.getCreatedStamp();
                    }
                }
                //log.info("getQuizTestParticipationExecutionResultOfAUserLogin, group " + g.getGroupCode()
                //         + " question " + qid + " ans = " + chooseAnsIds.size());

                QuizQuestion q = quizQuestionRepo.findById(qid).orElse(null);
                List<QuizChoiceAnswer> ans = quizChoiceAnswerRepo.findAllByQuizQuestion(q);
                boolean ques_ans = true;
                List<UUID> correctAns =
                    //quizQuestionDetail.getQuizChoiceAnswerList()
                    ans
                        .stream()
                        .filter(answer -> answer.getIsCorrectAnswer() == 'Y')
                        .map(QuizChoiceAnswer::getChoiceAnswerId)
                        .collect(Collectors.toList());

                // TRUE if and only if correctAns = chooseAnsIds
                //if (!correctAns.containsAll(chooseAnsIds)) {
                //    ques_ans = false;
                //}
                ques_ans = correctAns.containsAll(chooseAnsIds) && chooseAnsIds.containsAll(correctAns)
                           && chooseAnsIds.size() > 0;

                char result = ques_ans ? 'Y' : 'N';
                int grade = ques_ans ? 1 : 0;

                EduQuizTest eduQuizTest = repo.findById(g.getTestId()).orElse(null);
                UserQuestionQuizExecutionOM uq = new UserQuestionQuizExecutionOM();
                uq.setUserLoginId(userLoginId);
                PersonModel personModel = userService.findPersonByUserLoginId(userLoginId);
                uq.setFullName(personModel.getLastName() +
                               " " +
                               personModel.getFirstName() +
                               " " +
                               personModel.getFirstName());

                uq.setQuestionId(qid);
                uq.setTestId(g.getTestId());
                uq.setGrade(grade);
                if (eduQuizTest != null) {
                    uq.setCourseId(eduQuizTest.getCourseId());
                    EduClass cls = classRepo.findById(eduQuizTest.getClassId()).orElse(null);
                    if (cls != null) {
                        uq.setClassId(cls.getClassCode());
                    }

                    EduClassSession eduClassSession = eduClassSessionRepo
                        .findById(eduQuizTest.getSessionId())
                        .orElse(null);
                    if (eduClassSession != null) {
                        uq.setSessionId(eduClassSession.getSessionId());
                        uq.setSessionName(eduClassSession.getSessionName());
                    }
                }

                uq.setDate(date);
                userQuestionExecutions.add(uq);
            }
        }
        return userQuestionExecutions;
    }

    public List<QuizTestParticipationExecutionResultOutputModel> getQuizTestParticipationExecutionResult(String testId) {
        //TODO by HUY HUY

        //create list
        List<QuizTestParticipationExecutionResultOutputModel> listResult = new ArrayList<>();


        //find user + group id
//        List<EduTestQuizParticipant> eduTestQuizParticipants = eduTestQuizParticipantRepo.findByTestIdAndStatusId(
//            testId,
//            "STATUS_APPROVED");

        List<StudentInfo> list = repo.findAllStudentInTest(testId);

        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupRepo.findByTestId(testId);

        for (StudentInfo studentInfo : list) {
            //log.info("getQuizTestParticipationExecutionResult, consider user " + studentInfo.getUser_login_id() + "");

            for (EduTestQuizGroup eduTestQuizGroup : eduTestQuizGroups) {

                if (eduTestQuizGroupParticipationAssignmentRepo.existsById(new CompositeEduTestQuizGroupParticipationAssignmentId(
                    eduTestQuizGroup.getQuizGroupId(),
                    studentInfo.getUser_login_id()))) {
                    List<QuizGroupQuestionAssignment> quizGroupQuestionAssignments = quizGroupQuestionAssignmentRepo.findQuizGroupQuestionAssignmentsByQuizGroupId(
                        eduTestQuizGroup.getQuizGroupId());
                    quizGroupQuestionAssignments.forEach(question -> {

                        //create model
                        QuizTestParticipationExecutionResultOutputModel om =
                            new QuizTestParticipationExecutionResultOutputModel();

                        // get list choice
                        List<UUID> chooseAnsIds = new ArrayList<>();
                        List<QuizGroupQuestionParticipationExecutionChoice> choices =
                            quizGroupQuestionParticipationExecutionChoiceRepo
                                .findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupIdAndQuestionId(
                                    studentInfo.getUser_login_id(),
                                    eduTestQuizGroup.getQuizGroupId(),
                                    question.getQuestionId());

                        //choices.forEach(choice -> {
                        //    chooseAnsIds.add(choice.getChoiceAnswerId());
                        //});
                        Date createdStamp = null;
                        for (QuizGroupQuestionParticipationExecutionChoice choice : choices) {
                            createdStamp = choice.getCreatedStamp();
                            chooseAnsIds.add(choice.getChoiceAnswerId());
                        }
                        om.setChooseAnsIds(chooseAnsIds);
                        om.setCreatedStamp(createdStamp);

                        //get question
//                        QuizQuestionDetailModel quizQuestionDetail = quizQuestionService.findQuizDetail(question.getQuestionId());
//                        om.setQuizChoiceAnswerList(quizQuestionDetail.getQuizChoiceAnswerList());
//                        om.setQuestionContent(quizQuestionDetail.getStatement());

                        QuizQuestion q = quizQuestionRepo.findById(question.getQuestionId()).orElse(null);
                        om.setQuestionContent(q.getQuestionContent());

                        List<QuizChoiceAnswer> ans = quizChoiceAnswerRepo.findAllByQuizQuestion(q);
                        om.setQuizChoiceAnswerList(ans.stream()
                                                      .map(answer -> new QuizTestParticipationExecutionResultOutputModel.QuizChoiceAnswerDTO(
                                                          answer.getChoiceAnswerId(),
                                                          answer.getChoiceAnswerContent(),
                                                          answer.getIsCorrectAnswer()))
                                                      .collect(Collectors.toList()));

                        //check choice in question
                        boolean ques_ans;
                        List<UUID> correctAns =
                            //quizQuestionDetail.getQuizChoiceAnswerList()
                            ans
                                .stream()
                                .filter(answer -> answer.getIsCorrectAnswer() == 'Y')
                                .map(QuizChoiceAnswer::getChoiceAnswerId)
                                .collect(Collectors.toList());

                        // TRUE if and only if correctAns = chooseAnsIds
                        //if (!correctAns.containsAll(chooseAnsIds)) {
                        //    ques_ans = false;
                        //}
                        ques_ans = correctAns.containsAll(chooseAnsIds) && chooseAnsIds.containsAll(correctAns)
                                   && chooseAnsIds.size() > 0;

                        // log.info("getQuizTestParticipationExecutionResult, correctAns = ");
                        //for (UUID u : correctAns) {
                        // log.info("getQuizTestParticipationExecutionResult, correctAns = " + u);
                        //}
                        //for (UUID u : chooseAnsIds) {
                        //log.info("getQuizTestParticipationExecutionResult, chooseAns = " + u);
                        //}
                        //log.info("getQuizTestParticipationExecutionResult, user " + studentInfo.getUser_login_id() +
                        //         " quiz " + question.getQuestionId() + " -> ques_ans = " + ques_ans);

                        char result = ques_ans ? 'Y' : 'N';
                        int grade = ques_ans ? 1 : 0;

                        om.setParticipationUserLoginId(studentInfo.getUser_login_id());
                        om.setTestId(testId);
                        om.setQuizGroupId(eduTestQuizGroup.getQuizGroupId());
                        om.setQuizGroupCode(eduTestQuizGroup.getGroupCode());
                        om.setQuestionId(question.getQuestionId());
                        om.setResult(result);
                        om.setGrade(grade);
                        om.setParticipationFullName(studentInfo.getFull_name());

                        listResult.add(om);
                        //log.info("getQuizTestParticipationExecutionResult, add result with user " + om.getParticipationUserLoginId()
                        //+ " group " + eduTestQuizGroup.getGroupCode() + " question " + question.getQuestionId() + " time = " + om.getCreatedStamp());
                    });
                }

            }
        }

        return listResult;
    }

    public List<QuizTestParticipationExecutionResultOutputModel> getQuizTestParticipationExecutionResultNewByPQD(String testId) {
//create list
        List<QuizTestParticipationExecutionResultOutputModel> listResult = new ArrayList<>();


        //find user + group id
        List<EduTestQuizParticipant> eduTestQuizParticipants = eduTestQuizParticipantRepo.findByTestIdAndStatusId(
            testId,
            "STATUS_APPROVED");

        List<StudentInfo> list = repo.findAllStudentInTest(testId);

        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupRepo.findByTestId(testId);
        List<String> userLoginIds = eduTestQuizParticipants.stream().map(p -> p.getParticipantUserLoginId()).collect(
            Collectors.toList());
        List<UUID> groupIds = eduTestQuizGroups.stream().map(g -> g.getQuizGroupId()).collect(Collectors.toList());

        List<QuizGroupQuestionParticipationExecutionChoice> lst = quizGroupQuestionParticipationExecutionChoiceRepo
            .findAllByParticipationUserLoginIdInAndQuizGroupIdIn(userLoginIds, groupIds);

        for (String u : userLoginIds) {
            log.info("getQuizTestParticipationExecutionResultNewByPQD, collect users in test " + u);
        }
        for (UUID g : groupIds) {
            log.info("getQuizTestParticipationExecutionResultNewByPQD, collect group in test " + g);
        }

        class QuestionAndAnswer {

            public UUID questionId;
            List<UUID> chooseAnswerIds;
        }
        Map<String, List<QuestionAndAnswer>> m = new HashMap<String, List<QuestionAndAnswer>>();

        for (QuizGroupQuestionParticipationExecutionChoice ec : lst) {
            log.info("getQuizTestParticipationExecutionResultNewByPQD executionChoice user = "
                     + ec.getParticipationUserLoginId() + " group " + ec.getQuizGroupId() + " question " +
                     ec.getQuestionId() + "datetime = " + ec.getCreatedStamp());
            if (m.get(ec.getParticipationUserLoginId()) == null) {
                m.put(ec.getParticipationUserLoginId(), new ArrayList<QuestionAndAnswer>());
            }


        }

        return listResult;
    }

    @Override
    public boolean userAnsweredAQuizQuestion(String userLoginId, UUID questionId, UUID quizGroupId) {
        List<QuizGroupQuestionParticipationExecutionChoice> choices =
            quizGroupQuestionParticipationExecutionChoiceRepo
                .findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupIdAndQuestionId(
                    userLoginId,
                    quizGroupId,
                    questionId);

        return choices != null && choices.size() > 0;
    }

    @Override
    public int copyQuestionsFromQuizTest2QuizTest(UserLogin u, String fromQuizTestId, String toQuizTestId) {
        List<QuizQuestionDetailModel> quizQuestions =
            eduQuizTestQuizQuestionService.findAllByTestId(fromQuizTestId);
        int cnt = 0;
        for (QuizQuestionDetailModel q : quizQuestions) {

            cnt += eduQuizTestQuizQuestionService.createQuizTestQuestion(u, toQuizTestId, q.getQuestionId());
        }
        return cnt;
    }

    @Override
    public List<ModelResponseGetMyQuizTest> getQuizTestListOfUser(String userId){
        List<EduTestQuizParticipant> eduTestQuizParticipants = eduTestQuizParticipantRepo
            .findByParticipantUserLoginIdAndStatusId(userId,EduTestQuizParticipant.STATUS_APPROVED);

        List<ModelResponseGetMyQuizTest> res = new ArrayList();

        for(EduTestQuizParticipant e: eduTestQuizParticipants){
            EduQuizTest quizTest = repo.findById(e.getTestId()).orElse(null);
            if(quizTest != null && quizTest.getStatusId().equals(EduQuizTest.QUIZ_TEST_STATUS_OPEN)||quizTest.getStatusId().equals(EduQuizTest.QUIZ_TEST_STATUS_RUNNING)){
                ModelResponseGetMyQuizTest resItem = new ModelResponseGetMyQuizTest();
                resItem.setTestId(e.getTestId());
                resItem.setViewTypeId(quizTest.getViewTypeId());
                resItem.setTestName(quizTest.getTestName());
                resItem.setStatusId(e.getStatusId());
                res.add(resItem);
            }


        }

        return res;
    }

    @Override
    public boolean confirmUpdateGroupInQuizTest(String userId, String groupCode, String testId){
        List<EduTestQuizGroup> groups = eduQuizTestGroupRepo.findByTestId(testId);
        EduTestQuizGroup g = null;
        for(EduTestQuizGroup gr: groups){
            if(gr.getGroupCode() != null && gr.getGroupCode().equals(groupCode)){
                g = gr; break;
            }
        }
        if(g == null){
            return false;
        }
        // remove assignment of groups to current user in testId
        for(EduTestQuizGroup gr: groups) {
            List<EduTestQuizGroupParticipationAssignment> L = eduTestQuizGroupParticipationAssignmentRepo
                .findAllByQuizGroupIdAndParticipationUserLoginId(
                    gr.getQuizGroupId(),
                    userId);
            //log.info("confirmUpdateGroupInQuizTest, GOT " +
            //         L.size() +
            //         " items for group-participant assignments group " +
            //         gr.getQuizGroupId() +
            //         " user " +
            //         userId);
            // remove existing info
            if (L != null && L.size() > 0) {
                for (EduTestQuizGroupParticipationAssignment a : L) {
                    eduTestQuizGroupParticipationAssignmentRepo.delete(a);
                }
            }
        }
        // insert new assignment
        EduTestQuizGroupParticipationAssignment a = new EduTestQuizGroupParticipationAssignment();
        a.setQuizGroupId(g.getQuizGroupId());
        a.setParticipationUserLoginId(userId);
        a.setStatusId("OK");
        a = eduTestQuizGroupParticipationAssignmentRepo.save(a);

        /*
        if (L == null || L.size() == 0) {
            log.info("confirmUpdateGroupInQuizTest, assignment " +
                     g.getQuizGroupId() +
                     "," +
                     userId +
                     " not exists -> insert new");
            EduTestQuizGroupParticipationAssignment a = new EduTestQuizGroupParticipationAssignment();
            a.setQuizGroupId(g.getQuizGroupId());
            a.setParticipationUserLoginId(userId);
            a.setStatusId("OK");
            a = eduTestQuizGroupParticipationAssignmentRepo.save(a);

        }else{
            log.info("confirmUpdateGroupInQuizTest, assignment " +
                     g.getQuizGroupId() +
                     "," +
                     userId +
                     " GOT sz = " + L.size() + " remove existing and insert new");

            for(EduTestQuizGroupParticipationAssignment a: L){
                eduTestQuizGroupParticipationAssignmentRepo.delete(a);
            }
        }
        */
        return true;

    }

    @Override
    public QuizTestExecutionSubmission submitSynchronousQuizTestExecutionChoice(
        UUID questionId,
        UUID groupId,
        String userId,
        List<UUID> chooseAnsIds
    ) {
        Date createdStamp = new Date();

        // create a quiz-test submission
        String choiceAnsIds = "";
        for (int i = 0; i < chooseAnsIds.size(); i++) {
            UUID choiceId = chooseAnsIds.get(i);
            choiceAnsIds = choiceAnsIds + choiceId.toString();
            if(i < chooseAnsIds.size()-1)
                choiceAnsIds += ",";
        }
        QuizTestExecutionSubmission sub = new QuizTestExecutionSubmission();
        sub.setQuestionId(questionId);
        sub.setParticipationUserLoginId(userId);
        sub.setChoiceAnswerIds(choiceAnsIds);
        sub.setQuizGroupId(groupId);
        sub.setStatusId(QuizTestExecutionSubmission.STATUS_SOLVED);
        sub.setCreatedStamp(createdStamp);
        sub =  quizTestExecutionSubmissionRepo.save(sub);

        List<QuizGroupQuestionParticipationExecutionChoice> a = quizGroupQuestionParticipationExecutionChoiceRepo.findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupIdAndQuestionId(
            userId,
            groupId,
            questionId);


        a.forEach(quizGroupQuestionParticipationExecutionChoice -> {
            quizGroupQuestionParticipationExecutionChoiceRepo.delete(quizGroupQuestionParticipationExecutionChoice);
            //log.info("quizChooseAnswer, chooseAnsIds, delete previous choice answer for question " +
            //         questionId +
            //         " of groupId " +
            //         groupId +
            //         " of user " +
            //         userId);
        });

        List<QuizGroupQuestionParticipationExecutionChoice> res = new ArrayList();
        for (UUID choiceId :
            chooseAnsIds) {
            QuizGroupQuestionParticipationExecutionChoice tmp = new QuizGroupQuestionParticipationExecutionChoice();
            tmp.setQuestionId(questionId);
            tmp.setQuizGroupId(groupId);
            tmp.setParticipationUserLoginId(userId);
            tmp.setChoiceAnswerId(choiceId);
            tmp.setCreatedStamp(createdStamp);
            tmp.setSubmissionId(sub.getSubmissionId());
            tmp = quizGroupQuestionParticipationExecutionChoiceRepo.save(tmp);
            res.add(tmp);
            /*
            if(userId.equals("20210293")) {
                log.info("quizChooseAnswer, saved record user " +
                         tmp.getParticipationUserLoginId() +
                         " chose " +
                         tmp.getChoiceAnswerId());
            }
            */


            // create history log
            HistoryLogQuizGroupQuestionParticipationExecutionChoice historyLogQuizGroupQuestionParticipationExecutionChoice
                = new HistoryLogQuizGroupQuestionParticipationExecutionChoice();
            historyLogQuizGroupQuestionParticipationExecutionChoice.setChoiceAnswerId(choiceId);
            historyLogQuizGroupQuestionParticipationExecutionChoice.setParticipationUserLoginId(userId);
            historyLogQuizGroupQuestionParticipationExecutionChoice.setQuestionId(questionId);
            historyLogQuizGroupQuestionParticipationExecutionChoice.setQuizGroupId(groupId);
            historyLogQuizGroupQuestionParticipationExecutionChoice.setCreatedStamp(createdStamp);
            historyLogQuizGroupQuestionParticipationExecutionChoice = historyLogQuizGroupQuestionParticipationExecutionChoiceRepo
                .save(historyLogQuizGroupQuestionParticipationExecutionChoice);
        }



        return sub;
    }

    @Override
    public QuizTestExecutionSubmission submitAsynchronousQuizTestExecutionChoiceUsingRabbitMQ(
        UUID questionId,
        UUID groupId,
        String userId,
        List<UUID> chooseAnsIds
    ) {
        Date createdStamp = new Date();
        String choiceAnsIds = "";
        for (int i = 0; i < chooseAnsIds.size(); i++) {
            UUID choiceId = chooseAnsIds.get(i);
            choiceAnsIds = choiceAnsIds + choiceId.toString();
            if(i < chooseAnsIds.size()-1)
                choiceAnsIds += ",";
        }
        QuizTestExecutionSubmission sub = new QuizTestExecutionSubmission();
        sub.setQuestionId(questionId);
        sub.setParticipationUserLoginId(userId);
        sub.setChoiceAnswerIds(choiceAnsIds);
        sub.setQuizGroupId(groupId);
        sub.setStatusId(QuizTestExecutionSubmission.STATUS_IN_PROGRESS);
        sub.setCreatedStamp(createdStamp);
        sub =  quizTestExecutionSubmissionRepo.save(sub);

        // create a message and send to rabbitMQ HERE
        // after the message is process: update QuizGroupQuestionParticipationExecutionChoice
        // then QuizTestExecutionSubmission.statusId is changed to SOLVED
        rabbitTemplate.convertAndSend(
            QUIZ_EXCHANGE,
            QuizRoutingKey.QUIZ,
            sub.getSubmissionId()
        );


        return sub;


    }

    @Override
    public QuizTestExecutionSubmission submitQuizTestExecutionChoiceBatchLazyEvaluation(
        UUID questionId,
        UUID groupId,
        String userId,
        List<UUID> chooseAnsIds
    ) {
        Date createdStamp = new Date();
        String choiceAnsIds = "";
        for (int i = 0; i < chooseAnsIds.size(); i++) {
            UUID choiceId = chooseAnsIds.get(i);
            choiceAnsIds = choiceAnsIds + choiceId.toString();
            if(i < chooseAnsIds.size()-1)
                choiceAnsIds += ",";
        }
        QuizTestExecutionSubmission sub = new QuizTestExecutionSubmission();
        sub.setQuestionId(questionId);
        sub.setParticipationUserLoginId(userId);
        sub.setChoiceAnswerIds(choiceAnsIds);
        sub.setQuizGroupId(groupId);
        sub.setStatusId(QuizTestExecutionSubmission.STATUS_IN_PROGRESS);
        sub.setCreatedStamp(createdStamp);
        sub =  quizTestExecutionSubmissionRepo.save(sub);

        return sub;
    }

    private boolean updateFromQuizTestExecutionSubmission(QuizTestExecutionSubmission sub){
        if(sub == null){
            return false;
        }

        Date createdStamp = new Date();

        if(sub.getChoiceAnswerIds() != null && !sub.getChoiceAnswerIds().equals("")) {
            String[] choiceAnsIds = sub.getChoiceAnswerIds().split(",");
            if(choiceAnsIds != null && choiceAnsIds.length > 0){
                List<UUID> chooseAnsIds = new ArrayList();
                for(int i = 0; i < choiceAnsIds.length; i++){
                    UUID choiceId = UUID.fromString(choiceAnsIds[i]);
                    chooseAnsIds.add(choiceId);
                }
                String userId = sub.getParticipationUserLoginId();
                UUID questionId = sub.getQuestionId();
                UUID groupId = sub.getQuizGroupId();
                List<QuizGroupQuestionParticipationExecutionChoice> a = quizGroupQuestionParticipationExecutionChoiceRepo.findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupIdAndQuestionId(
                    userId,
                    groupId,
                    questionId);
                a.forEach(quizGroupQuestionParticipationExecutionChoice -> {
                    quizGroupQuestionParticipationExecutionChoiceRepo.delete(quizGroupQuestionParticipationExecutionChoice);
                    //log.info("quizChooseAnswer, chooseAnsIds, delete previous choice answer for question " +
                    //         questionId +
                    //         " of groupId " +
                    //         groupId +
                    //         " of user " +
                    //         userId);
                });

                for (UUID choiceId :
                    chooseAnsIds) {
                    QuizGroupQuestionParticipationExecutionChoice tmp = new QuizGroupQuestionParticipationExecutionChoice();
                    tmp.setQuestionId(questionId);
                    tmp.setQuizGroupId(groupId);
                    tmp.setParticipationUserLoginId(userId);
                    tmp.setChoiceAnswerId(choiceId);
                    tmp.setCreatedStamp(createdStamp);
                    tmp.setSubmissionId(sub.getSubmissionId());
                    tmp = quizGroupQuestionParticipationExecutionChoiceRepo.save(tmp);
                    log.info("updateFromQuizTestExecutionSubmission, transactional subID = " + sub.getSubmissionId() + " save choice " + choiceId);

                    // create history log
                    HistoryLogQuizGroupQuestionParticipationExecutionChoice historyLogQuizGroupQuestionParticipationExecutionChoice
                        = new HistoryLogQuizGroupQuestionParticipationExecutionChoice();
                    historyLogQuizGroupQuestionParticipationExecutionChoice.setChoiceAnswerId(choiceId);
                    historyLogQuizGroupQuestionParticipationExecutionChoice.setParticipationUserLoginId(userId);
                    historyLogQuizGroupQuestionParticipationExecutionChoice.setQuestionId(questionId);
                    historyLogQuizGroupQuestionParticipationExecutionChoice.setQuizGroupId(groupId);
                    historyLogQuizGroupQuestionParticipationExecutionChoice.setCreatedStamp(createdStamp);
                    historyLogQuizGroupQuestionParticipationExecutionChoice = historyLogQuizGroupQuestionParticipationExecutionChoiceRepo
                        .save(historyLogQuizGroupQuestionParticipationExecutionChoice);
                }
                sub.setStatusId(QuizTestExecutionSubmission.STATUS_SOLVED);
                sub.setLastUpdatedStamp(createdStamp);
                sub = quizTestExecutionSubmissionRepo.save(sub);

                return true;
            }
            sub.setStatusId(QuizTestExecutionSubmission.STATUS_SOLVED);
            sub.setLastUpdatedStamp(createdStamp);
            sub = quizTestExecutionSubmissionRepo.save(sub);
        }

        return true;
    }
    @Override
    @Transactional
    public boolean updateFromQuizTestExecutionSubmission(UUID submissionId) {
        QuizTestExecutionSubmission sub = quizTestExecutionSubmissionRepo.findById(submissionId).orElse(null);
        if(sub == null){
            return false;
        }
        return updateFromQuizTestExecutionSubmission(sub);
        /*
        log.info("updateFromQuizTestExecutionSubmission, subID = " + submissionId);
        Date createdStamp = new Date();

        if(sub.getChoiceAnswerIds() != null && !sub.getChoiceAnswerIds().equals("")) {
            String[] choiceAnsIds = sub.getChoiceAnswerIds().split(",");
            if(choiceAnsIds != null && choiceAnsIds.length > 0){
                List<UUID> chooseAnsIds = new ArrayList();
                for(int i = 0; i < choiceAnsIds.length; i++){
                    UUID choiceId = UUID.fromString(choiceAnsIds[i]);
                    chooseAnsIds.add(choiceId);
                }
                String userId = sub.getParticipationUserLoginId();
                UUID questionId = sub.getQuestionId();
                UUID groupId = sub.getQuizGroupId();
                List<QuizGroupQuestionParticipationExecutionChoice> a = quizGroupQuestionParticipationExecutionChoiceRepo.findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupIdAndQuestionId(
                    userId,
                    groupId,
                    questionId);
                a.forEach(quizGroupQuestionParticipationExecutionChoice -> {
                    quizGroupQuestionParticipationExecutionChoiceRepo.delete(quizGroupQuestionParticipationExecutionChoice);
                    //log.info("quizChooseAnswer, chooseAnsIds, delete previous choice answer for question " +
                    //         questionId +
                    //         " of groupId " +
                    //         groupId +
                    //         " of user " +
                    //         userId);
                });

                for (UUID choiceId :
                    chooseAnsIds) {
                    QuizGroupQuestionParticipationExecutionChoice tmp = new QuizGroupQuestionParticipationExecutionChoice();
                    tmp.setQuestionId(questionId);
                    tmp.setQuizGroupId(groupId);
                    tmp.setParticipationUserLoginId(userId);
                    tmp.setChoiceAnswerId(choiceId);
                    tmp.setCreatedStamp(createdStamp);
                    tmp = quizGroupQuestionParticipationExecutionChoiceRepo.save(tmp);
                    log.info("updateFromQuizTestExecutionSubmission, transactional subID = " + submissionId + " save choice " + choiceId);

                    // create history log
                    HistoryLogQuizGroupQuestionParticipationExecutionChoice historyLogQuizGroupQuestionParticipationExecutionChoice
                        = new HistoryLogQuizGroupQuestionParticipationExecutionChoice();
                    historyLogQuizGroupQuestionParticipationExecutionChoice.setChoiceAnswerId(choiceId);
                    historyLogQuizGroupQuestionParticipationExecutionChoice.setParticipationUserLoginId(userId);
                    historyLogQuizGroupQuestionParticipationExecutionChoice.setQuestionId(questionId);
                    historyLogQuizGroupQuestionParticipationExecutionChoice.setQuizGroupId(groupId);
                    historyLogQuizGroupQuestionParticipationExecutionChoice.setCreatedStamp(createdStamp);
                    historyLogQuizGroupQuestionParticipationExecutionChoice = historyLogQuizGroupQuestionParticipationExecutionChoiceRepo
                        .save(historyLogQuizGroupQuestionParticipationExecutionChoice);
                }
                sub.setStatusId(QuizTestExecutionSubmission.STATUS_SOLVED);
                sub.setLastUpdatedStamp(createdStamp);
                sub = quizTestExecutionSubmissionRepo.save(sub);

                return true;
            }
            sub.setStatusId(QuizTestExecutionSubmission.STATUS_SOLVED);
            sub.setLastUpdatedStamp(createdStamp);
            sub = quizTestExecutionSubmissionRepo.save(sub);
        }
        return false;
        */
    }

    @Transactional
    @Override
    public int summarizeQuizTestExecutionChoice(String testId) {
        // warning: heavy batch processing,
        int cnt = 0;
        List<EduTestQuizGroup> groups = eduQuizTestGroupRepo.findByTestId(testId);
        List<UUID> groupIds = new ArrayList<UUID>();
        Map<UUID, List<UUID>> mGroupID2QuestionIds = new HashMap();
        for(EduTestQuizGroup g: groups){
            groupIds.add(g.getQuizGroupId());
            mGroupID2QuestionIds.put(g.getQuizGroupId(), new ArrayList());
        }
        List<QuizGroupQuestionAssignment> qgAss = quizGroupQuestionAssignmentRepo.findAllByQuizGroupIdIn(groupIds);

        for(QuizGroupQuestionAssignment e: qgAss){
            UUID gid = e.getQuizGroupId();
            UUID questionId = e.getQuestionId();
            mGroupID2QuestionIds.get(gid).add(questionId);
        }
        List<EduTestQuizGroupParticipationAssignment> ugAss = eduTestQuizGroupParticipationAssignmentRepo.findAllByQuizGroupIdIn(groupIds);
        for(EduTestQuizGroupParticipationAssignment a: ugAss){
            UUID groupId = a.getQuizGroupId();
            String userId = a.getParticipationUserLoginId();
            List<UUID> questionIds = mGroupID2QuestionIds.get(groupId);
            if(questionIds != null){
                for(UUID qId: questionIds){
                    // get most recently submission of userId for question qId in group groupId
                    QuizTestExecutionSubmission sub = null;
                    List<QuizTestExecutionSubmission> subs = quizTestExecutionSubmissionRepo
                        .findAllByQuestionIdAndQuizGroupIdAndParticipationUserLoginIdOrderByCreatedStampDesc(qId, groupId, userId);
                    if(subs != null && subs.size() > 0){
                        sub = subs.get(0); // get most recently submitted record
                    }
                    if(sub != null){
                        log.debug("summarizeQuizTestExecutionChoice, get most recently record " + sub.getSubmissionId() + " time = " + sub.getCreatedStamp());
                        boolean ok = updateFromQuizTestExecutionSubmission(sub);
                        if(ok){
                            cnt += 1;
                        }
                    }
                }
            }
        }
        return cnt;
    }
}
