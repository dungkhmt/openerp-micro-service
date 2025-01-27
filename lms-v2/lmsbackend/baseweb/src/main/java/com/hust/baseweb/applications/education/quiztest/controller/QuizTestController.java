package com.hust.baseweb.applications.education.quiztest.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hust.baseweb.applications.education.cache.QuizQuestionServiceCache;
import com.hust.baseweb.applications.education.classmanagement.entity.EduClassSession;
import com.hust.baseweb.applications.education.classmanagement.repo.EduClassSessionRepo;
import com.hust.baseweb.applications.education.classmanagement.service.ClassService;
import com.hust.baseweb.applications.education.classmanagement.service.EduClassSessionService;
import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.EduCourseSession;
import com.hust.baseweb.applications.education.entity.EduCourseSessionInteractiveQuiz;
import com.hust.baseweb.applications.education.entity.EduCourseSessionInteractiveQuizQuestion;
import com.hust.baseweb.applications.education.entity.QuizQuestion;
import com.hust.baseweb.applications.education.entity.QuizTag;
import com.hust.baseweb.applications.education.entity.compositeid.CompositeCourseSessionInteractiveQuizQuestionId;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.model.quiz.QuizTagCreateModel;
import com.hust.baseweb.applications.education.quiztest.UserQuestionQuizExecutionOM;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTest;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTestQuizQuestion;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroup;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizParticipant;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizRole;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuiz;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuizAnswer;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuizQuestion;
import com.hust.baseweb.applications.education.quiztest.model.*;
import com.hust.baseweb.applications.education.quiztest.model.edutestquizparticipation.GetQuizTestParticipationExecutionResultInputModel;
import com.hust.baseweb.applications.education.quiztest.model.edutestquizparticipation.ModelResponseImportExcelUsersToQuizTest;
import com.hust.baseweb.applications.education.quiztest.model.edutestquizparticipation.QuizTestParticipationExecutionResultOutputModel;
import com.hust.baseweb.applications.education.quiztest.model.quitestgroupquestion.AutoAssignQuestion2QuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroup.AutoAssignParticipants2QuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestquestion.CopyQuestionFromQuizTest2QuizTestInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestquestion.CreateQuizTestQuestionInputModel;
import com.hust.baseweb.applications.education.quiztest.repo.EduTestQuizParticipantRepo;
import com.hust.baseweb.applications.education.quiztest.repo.EduTestQuizRoleRepo;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizQuestionRepo;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizRepo;
import com.hust.baseweb.applications.education.quiztest.repo.QuizGroupQuestionAssignmentRepo;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizRepo.StudentResult;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizRepo.StudentSubmission;
import com.hust.baseweb.applications.education.quiztest.service.*;
import com.hust.baseweb.applications.education.repo.EduCourseSessionInteractiveQuizQuestionRepo;
import com.hust.baseweb.applications.education.repo.EduCourseSessionInteractiveQuizRepo;
import com.hust.baseweb.applications.education.repo.EduCourseSessionRepo;
import com.hust.baseweb.applications.education.service.EduCourseSessionInteractiveQuizQuestionService;
import com.hust.baseweb.applications.education.service.QuizQuestionService;
import com.hust.baseweb.applications.education.service.QuizTagService;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.Principal;
import java.util.*;


@Log4j2
@Controller
@Validated
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin
public class QuizTestController {

    private QuizTestService quizTestService;
    private UserService userService;
    private EduTestQuizParticipantRepo eduTestQuizParticipationRepo;
    private QuizQuestionService quizQuestionService;
    private ClassService classService;
    private EduQuizTestQuizQuestionService eduQuizTestQuizQuestionService;
    private EduQuizTestGroupService eduQuizTestGroupService;
    private EduTestQuizRoleRepo eduTestQuizRoleRepo;
    private EduQuizTestParticipantRoleService eduQuizTestParticipantRoleService;
    private EduTestQuizParticipantService eduTestQuizParticipantService;
    private QuizGroupQuestionAssignmentRepo quizGroupQuestionAssignmentRepo;
    private InteractiveQuizService interactiveQuizService;
    private InteractiveQuizQuestionService interactiveQuizQuestionService;
    private InteractiveQuizAnswerService interactiveQuizAnswerService;
    private InteractiveQuizRepo interactiveQuizRepo;
    private InteractiveQuizQuestionRepo interactiveQuizQuestionRepo;
    private EduClassSessionRepo eduClassSessionRepo;
    private EduCourseSessionRepo eduCourseSessionRepo;
    private EduCourseSessionInteractiveQuizRepo eduCourseSessionInteractiveQuizRepo;
    private EduCourseSessionInteractiveQuizQuestionRepo eduCourseSessionInteractiveQuizQuestionRepo;
    private EduCourseSessionInteractiveQuizQuestionService eduCourseSessionInteractiveQuizQuestionService;
    private QuizQuestionServiceCache cacheService;

    @Secured({"ROLE_TEACHER"})
    @PostMapping("/create-quiz-test")
    public ResponseEntity<?> createQuizCourseTopic(
        Principal principal,
        @RequestParam(required = false, name = "QuizTestCreateInputModel") String json
    ) {
        Gson g = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        QuizTestCreateInputModel input = g.fromJson(json, QuizTestCreateInputModel.class);

        UserLogin user = userService.findById(principal.getName());


        //System.out.println(input);
        return ResponseEntity.ok().body(quizTestService.save(input, user));
    }

    @Secured({"ROLE_TEACHER"})
    @PostMapping("/create-interactive-quiz")
    public ResponseEntity<?> createInteractiveQuiz(Principal principal,  @RequestBody InteractiveQuizInputModel input){

        return ResponseEntity.ok(interactiveQuizService.createInteractiveQuiz(input.getInteractiveQuizName(), input.getSessionId(), input.getStatusId()));
    }

    @Secured({"ROLE_TEACHER"})
    @PostMapping("/add-question-to-interactive-quiz")
    public ResponseEntity<?> addQuestionToInteractiveQuiz(
        Principal principal,
        @RequestBody InteractiveQuizQuestionInputModel input
    ) {
        InteractiveQuizQuestion interactiveQuizQuestion = new InteractiveQuizQuestion();
        interactiveQuizQuestion.setInteractiveQuizId(input.getInteractiveQuizId());
        interactiveQuizQuestion.setQuestionId(input.getQuestionId());
        interactiveQuizQuestion.setCreatedStamp(new Date());
        interactiveQuizQuestion.setLastUpdated(new Date());
        cacheService.flushCache();
        return ResponseEntity.ok().body(interactiveQuizQuestionRepo.save(interactiveQuizQuestion));
    }

    @Secured({"ROLE_TEACHER"})
    @PostMapping("/remove-question-from-interactive-quiz")
    public ResponseEntity<?> removeQuestionFromInteractiveQuiz(
        Principal principal,
        @RequestBody InteractiveQuizQuestionInputModel input
    ) {
        // InteractiveQuizQuestion interactiveQuizQuestion = new InteractiveQuizQuestion();
        // interactiveQuizQuestion.setInteractiveQuizId(input.getInteractiveQuizId());
        // interactiveQuizQuestion.setQuestionId(input.getQuestionId());
        // interactiveQuizQuestion.setCreatedStamp(new Date());
        // interactiveQuizQuestion.setLastUpdated(new Date());
        interactiveQuizQuestionService.removeFromInteractiveQuiz(input);
        cacheService.flushCache();
        return ResponseEntity.ok().build();
    }

    @Secured({"ROLE_TEACHER"})
    @PostMapping("/add-question-to-course-interactive-quiz")
    public ResponseEntity<?> addQuestionToCourseInteractiveQuiz(
        Principal principal,
        @RequestBody InteractiveQuizQuestionInputModel input
    ) {
        EduCourseSessionInteractiveQuizQuestion eduCourseSessionInteractiveQuizQuestion = new EduCourseSessionInteractiveQuizQuestion();
        eduCourseSessionInteractiveQuizQuestion.setInteractiveQuizId(input.getInteractiveQuizId());
        eduCourseSessionInteractiveQuizQuestion.setQuestionId(input.getQuestionId());
        eduCourseSessionInteractiveQuizQuestion.setCreatedStamp(new Date());
        eduCourseSessionInteractiveQuizQuestion.setLastUpdated(new Date());
        return ResponseEntity.ok().body(eduCourseSessionInteractiveQuizQuestionRepo.save(eduCourseSessionInteractiveQuizQuestion));
    }

    @Secured({"ROLE_TEACHER"})
    @PostMapping("/remove-question-from-course-interactive-quiz")
    public ResponseEntity<?> removeQuestionFromCourseInteractiveQuiz(
        Principal principal,
        @RequestBody InteractiveQuizQuestionInputModel input
    ) {
        // EduCourseSessionInteractiveQuizQuestion eduCourseSessionInteractiveQuizQuestion = new EduCourseSessionInteractiveQuizQuestion();
        // eduCourseSessionInteractiveQuizQuestion.setInteractiveQuizId(input.getInteractiveQuizId());
        // eduCourseSessionInteractiveQuizQuestion.setQuestionId(input.getQuestionId());
        // eduCourseSessionInteractiveQuizQuestion.setCreatedStamp(new Date());
        // eduCourseSessionInteractiveQuizQuestion.setLastUpdated(new Date());
        CompositeCourseSessionInteractiveQuizQuestionId id = new CompositeCourseSessionInteractiveQuizQuestionId();
        id.setInteractiveQuizId(input.getInteractiveQuizId());
        id.setQuestionId(input.getQuestionId());
        eduCourseSessionInteractiveQuizQuestionRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @Secured({"ROLE_TEACHER"})
    @GetMapping("/get-questions-of-interactive-quiz/{interactiveQuizId}")
    public ResponseEntity<?> getQuestionsOfInteractiveQuiz(Principal principal, @PathVariable UUID interactiveQuizId) {
        List<QuizQuestionDetailModel> quizQuestionList =
            interactiveQuizQuestionService.findAllByInteractiveQuizId(interactiveQuizId);
        return ResponseEntity.ok().body(quizQuestionList);
    }

    @GetMapping("/get-questions-of-interactive-quiz-student/{interactiveQuizId}")
    public ResponseEntity<?> getStudentQuestionsOfInteractiveQuiz(Principal principal, @PathVariable UUID interactiveQuizId) {
        InteractiveQuiz interactiveQuiz = interactiveQuizRepo.findById(interactiveQuizId).orElse(null);
        if (interactiveQuiz == null || !interactiveQuiz.getStatusId().equals("OPENED")) {
            return ResponseEntity.status(403).build();
        }
        List<QuizQuestionDetailModel> quizQuestionList =
            interactiveQuizQuestionService.findAllByInteractiveQuizId(interactiveQuizId);
        return ResponseEntity.ok().body(quizQuestionList);
    }    

    @GetMapping("/get-questions-of-course-interactive-quiz/{interactiveQuizId}")
    public ResponseEntity<?> getQuestionsOfCourseInteractiveQuiz(Principal principal, @PathVariable UUID interactiveQuizId) {
        List<QuizQuestionDetailModel> quizQuestionList = eduCourseSessionInteractiveQuizQuestionService.findAllByInteractiveQuizId(interactiveQuizId);
        return ResponseEntity.ok().body(quizQuestionList);
    }
    
    @Secured({"ROLE_TEACHER"})
    @PostMapping("/update-status-of-interactive-quiz")
    public ResponseEntity<?> updateStatusOfInteractiveQuiz(Principal principal, @RequestBody ModelUpdateStatusOfInteractiveQuiz input) {
        InteractiveQuiz interactiveQuiz = interactiveQuizRepo.findById(input.getInteractiveQuizId()).orElse(null);
        if (interactiveQuiz != null) {
            interactiveQuiz.setStatusId(input.getStatus());
            return ResponseEntity.ok().body(interactiveQuizRepo.save(interactiveQuiz));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/get-status-of-interactive-quiz/{interactiveQuizId}")
    public ResponseEntity<?> getStatusOfInteractiveQuiz(Principal principal, @PathVariable UUID interactiveQuizId) {
        InteractiveQuiz interactiveQuiz = interactiveQuizRepo.findById(interactiveQuizId).orElse(null);
        if (interactiveQuiz != null) {
            return ResponseEntity.ok().body(interactiveQuiz.getStatusId());
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/submit-interactive-quiz-answer-by-user")
    public ResponseEntity<?> submitInteractiveQuizAnswer(Principal principal, @RequestBody ModelInteractiveQuizAnswer input) {
        InteractiveQuiz interactiveQuiz = interactiveQuizRepo.findById(input.getInteractiveQuizId()).orElse(null);
        if (interactiveQuiz == null || interactiveQuiz.getStatusId().equals("HIDDEN") || interactiveQuiz.getStatusId().equals("CREATED")) {
            return ResponseEntity.badRequest().build();
        }
        List<InteractiveQuizAnswer> output = new ArrayList<>();
        for (UUID anAnswer : input.getChoiceAnswerId()) {
            InteractiveQuizAnswer answer = new InteractiveQuizAnswer();
            answer.setInteractiveQuizId(input.getInteractiveQuizId());
            answer.setQuestionId(input.getQuestionId());
            answer.setChoiceAnswerId(anAnswer);

            answer.setUserId(principal.getName());
            answer.setCreatedStamp(new Date());
            answer.setLastUpdated(new Date());
            output.add(answer);
        }
        if (interactiveQuizAnswerService.submitAnswer(output) == 1) {
            return ResponseEntity.ok().body(output);
        } 
        return ResponseEntity.badRequest().build();
    }

    @Secured({"ROLE_TEACHER"})
    @PostMapping("/update-quiz-test")
    public ResponseEntity<?> updateQuizTest(Principal principal, @RequestBody EditQuizTestInputModel input) {
        EduQuizTest eduQuizTest = quizTestService.update(input);
        return ResponseEntity.ok().body(eduQuizTest);
    }

    @Secured({"ROLE_TEACHER"})
    @GetMapping("/open-quiz-test/{testId}")
    public ResponseEntity<?> openQuizTest(Principal principal, @PathVariable String testId) {
        EduQuizTest eduQuizTest = quizTestService.openQuizTest(testId);
        return ResponseEntity.ok().body(eduQuizTest);
    }

    @Secured({"ROLE_TEACHER"})
    @GetMapping("/hide-quiz-test/{testId}")
    public ResponseEntity<?> hideQuizTest(Principal principal, @PathVariable String testId) {
        EduQuizTest eduQuizTest = quizTestService.hideQuizTest(testId);
        return ResponseEntity.ok().body(eduQuizTest);
    }

    @GetMapping("/get-users-role-of-quiz-test/{testId}")
    public ResponseEntity<?> getUserRolesOfQuizTest(@PathVariable String testId) {
        List<QuizTestParticipantRoleModel> res = eduQuizTestParticipantRoleService.getParticipantRolesOfQuizTest(testId);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-roles-user-not-granted-in-quiz-test/{testId}/{userId}")
    public ResponseEntity<?> getRolesUserNotGrantedInQuizTest(
        @PathVariable String testId,
        @PathVariable String userId
    ) {
        List<QuizTestParticipantRoleModel> rolesGranted = eduQuizTestParticipantRoleService.getParticipantRolesOfUserInQuizTest(
            userId,
            testId);
        List<String> res = new ArrayList();
        List<String> roles = EduTestQuizRole.getRoles();
        for (String r : roles) {
            boolean exist = false;
            for (QuizTestParticipantRoleModel ri : rolesGranted) {
                if (ri.getRoleId().equals(r)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                res.add(r);
            }
        }
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-all-quiz-test")
    public ResponseEntity<?> getAllQuizTests(Principal principal) {
        List<QuizTestParticipantRoleModel> res = eduQuizTestParticipantRoleService.getAllQuizTests();
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-quiz-tests-of-user-login")
    public ResponseEntity<?> getQuizTestsOfUserLogin(Principal principal) {
        log.info("getQuizTestsOfUserLogin, user = " + principal.getName());
        List<QuizTestParticipantRoleModel> res = eduQuizTestParticipantRoleService.getQuizTestsOfUser(principal.getName());
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/add-quiz-test-participant-role")
    public ResponseEntity<?> addQuizTestParticipantRole(
        Principal principal,
        @RequestBody ModelCreateEduQuizTestParticipantRole input
    ) {
        EduTestQuizRole eduTestQuizRole = eduQuizTestParticipantRoleService.save(input);
        return ResponseEntity.ok().body(eduTestQuizRole);
    }

    @DeleteMapping("/quiz-test-participant-role")
    public ResponseEntity<?> deleteQuizTestParticipantRole(
        @RequestParam String testId,
        @RequestParam String userId,
        @RequestParam String roleId
    ) {
        eduQuizTestParticipantRoleService.deleteParticipantRole(testId, userId, roleId);
        return ResponseEntity.ok().build();
    }

    @Secured({"ROLE_TEACHER"})
    @GetMapping("/get-all-quiz-test-by-user")
    public ResponseEntity<?> getAllQuizTestByUserLogin(
        Principal principal
    ) {
        log.info("getAllQuizTestByUserLogin, user = " + principal.getName());
        List<EduQuizTest> res = quizTestService.getAllTestByCreateUser(principal.getName());
        log.info("getAllQuizTestByUserLogin, user = " + principal.getName() + " res = " + res.size());
        return ResponseEntity.ok().body(res);
    }

    @Secured({"ROLE_TEACHER"})
    @GetMapping("/get-quiz-test")
    public ResponseEntity<?> getQuizTestByTestId(
        Principal principal,
        @RequestParam(required = false, name = "testId") String testId
    ) {
        return ResponseEntity.ok().body(quizTestService.getQuizTestById(testId));
    }

    @GetMapping("/get-list-question-statement-view-type-id")
    public ResponseEntity<?> getListQuestionStatementViewTypeId() {
        List<String> L = EduQuizTest.getListQuestionStatementViewType();
        return ResponseEntity.ok().body(L);
    }

    @GetMapping("/get-list-quiz-test-view-type-id")
    public ResponseEntity<?> getListQuizTestViewTypeId() {
        List<String> L = EduQuizTest.getListQuizTestViewTypes();
        return ResponseEntity.ok().body(L);
    }

    @GetMapping("/get-list-participant-quizgroup-assignment-mode")
    public ResponseEntity<?> getListParticipantQuizGroupAssignmentMode() {
        List<String> L = EduQuizTest.getListParticipantQuizGroupAssignmentModes();
        return ResponseEntity.ok().body(L);
    }

    @GetMapping("/get-all-quiz-test-user")
    public ResponseEntity<?> getAllQuizTestByUser(
        Principal principal
    ) {
        UserLogin user = userService.findById(principal.getName());
        List<EduQuizTestModel> listQuizTest = quizTestService.getListQuizByUserId(user.getUserLoginId());
        return ResponseEntity.ok().body(listQuizTest);
    }

    @GetMapping("/get-my-quiz-test-list")
    public ResponseEntity<?> getMyQuizTestListByUser(
        Principal principal
    ) {
        //UserLogin user = userService.findById(principal.getName());
        String userId = principal.getName();
        List<ModelResponseGetMyQuizTest> listQuizTest = quizTestService.getQuizTestListOfUser(userId);
        return ResponseEntity.ok().body(listQuizTest);
    }

    @GetMapping("/get-active-quiz-of-session-for-participant/{sessionId}")
    public ResponseEntity<?> getActiveQuizTestOfSession(
        Principal principal, @PathVariable UUID sessionId
    ) {
        UserLogin user = userService.findById(principal.getName());

        List<EduQuizTestModel> listQuizTest = quizTestService.getListOpenQuizTestOfSession(sessionId,
                                                                                           user.getUserLoginId());
        if (listQuizTest == null || listQuizTest.size() == 0) {
            log.info("getActiveQuizTestOfSession, listQuizTest null or size = 0 -> RETURN");
            return ResponseEntity.ok().body(new QuizGroupTestDetailModel());
        }

        QuizGroupTestDetailModel testDetail = null;
        //for(EduQuizTestModel qt: listQuizTest){
        // TO BE IMPROVED
        EduQuizTestModel qt = listQuizTest.get(0);
        String testID = qt.getTestId();
            /*
            EduQuizTest eduQuizTest = quizTestService.getQuizTestById(testID);
            Date startDateTime = eduQuizTest.getScheduleDatetime();
            Date currentDate = new Date();
            int timeTest = ((int) (currentDate.getTime() - startDateTime.getTime())) / (60 * 1000); //minutes
            log.info("getTestGroupQuestionByUser, current = " + currentDate.toString() +
                     " scheduleDate = " + startDateTime.toString() + " timeTest = " + timeTest);

            if (timeTest > eduQuizTest.getDuration() || timeTest < 0) {// out-of-allowed date-time
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
            }
            */
        log.info("getActiveQuizTestOfSession, get TestId = " + testID);
        EduTestQuizParticipant testParticipant = eduTestQuizParticipationRepo
            .findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(
                principal.getName(),
                testID);

        if (testParticipant == null ||
            (!testParticipant.getStatusId().equals(EduTestQuizParticipant.STATUS_APPROVED))) {
            log.info("getActiveQuizTestOfSession, participant to testID " + testID + " is NULL -> return");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }


        testDetail = eduQuizTestGroupService.getTestGroupQuestionDetail(principal, testID);

        // check if user has already done the quiz question, then return null
        // in this context, each user can only take quiz once
        Set<String> disableQuestionIds = new HashSet();
        for (String questionId : testDetail.getParticipationExecutionChoice().keySet()) {
            List<UUID> choiceAnswers = testDetail.getParticipationExecutionChoice().get(questionId);
            if (choiceAnswers.size() > 0) {
                // disable this question, do not return
                disableQuestionIds.add(questionId);
            }
        }
        for (String qid : disableQuestionIds) {
            testDetail.getParticipationExecutionChoice().remove(qid);
            //testDetail.getListQuestion().remove(qid);
            for (QuizQuestionDetailModel q : testDetail.getListQuestion()) {
                if (q.getQuestionId().toString().equals(qid)) {
                    testDetail.getListQuestion().remove(q);
                    break;
                }
            }
            log.info("getActiveQuizTestOfSession, question  " + qid + " has already been answered, remove this");
        }
        return ResponseEntity.ok().body(testDetail);
    }


    @Secured({"ROLE_TEACHER"})
    @GetMapping("/get-all-student-in-test")
    public ResponseEntity<?> getAllStudentInTest(
        Principal principal, @RequestParam(required = false, name = "testId") String testId
    ) {
        testId = testId.replaceAll("\'", "");
        /* System.out.println("============================================================================================================");
        System.out.println(testId); */
        List<StudentInTestQueryReturnModel> list = quizTestService.getAllStudentInTest(testId);
        /* for (StudentInTestQueryReturnModel studentInTestQueryReturn : list) {
            System.out.println(studentInTestQueryReturn);
        } */
        return ResponseEntity.ok().body(list);

    }

    @PostMapping("/auto-assign-participants-2-quiz-test-group")
    public ResponseEntity<?> autoAssignParticipants2QuizTestGroup(
        Principal principal, @RequestBody
    AutoAssignParticipants2QuizTestGroupInputModel input
    ) {
        boolean ok = quizTestService.autoAssignParticipants2QuizTestGroup(input);

        return ResponseEntity.ok().body(ok);
    }

    @PostMapping("auto-assign-question-2-quiz-group")
    public ResponseEntity<?> autoAssignQuestion2QuizTestGroup(
        Principal principal, @RequestBody
    AutoAssignQuestion2QuizTestGroupInputModel input
    ) {

        boolean ok = quizTestService.autoAssignQuestion2QuizTestGroup(input);

        return ResponseEntity.ok().body(ok);
    }

    @GetMapping("/get-list-quiz-for-assignment-of-test/{testId}")
    public ResponseEntity<?> getListQuizForAssignmentOfTest(Principal principal, @PathVariable String testId) {
        EduQuizTest eduQuizTest = quizTestService.getQuizTestById(testId);
        if (eduQuizTest == null) {
            return ResponseEntity.ok().body(new ArrayList());
        }
        UUID classId = eduQuizTest.getClassId();
        EduClass eduClass = classService.findById(classId);

        String courseId = null;
        if (eduClass != null) {
            courseId = eduClass.getEduCourse().getId();
        }


        List<QuizQuestion> quizQuestions = quizQuestionService.findQuizOfCourse(courseId);
        log.info("getListQuizForAssignmentOfTest, testId = " +
                 testId +
                 " courseId = " +
                 courseId +
                 " all questions = " +
                 quizQuestions.size());
        List<QuizQuestionDetailModel> quizQuestionDetailModels = new ArrayList<>();
        for (QuizQuestion q : quizQuestions) {
            if (q.getStatusId().equals(QuizQuestion.STATUS_PUBLIC)) {
                continue;
            }
            QuizQuestionDetailModel quizQuestionDetailModel = quizQuestionService.findQuizDetail(q.getQuestionId());
            quizQuestionDetailModels.add(quizQuestionDetailModel);
        }
        Collections.sort(quizQuestionDetailModels, new Comparator<QuizQuestionDetailModel>() {
            @Override
            public int compare(QuizQuestionDetailModel o1, QuizQuestionDetailModel o2) {
                String topic1 = o1.getQuizCourseTopic().getQuizCourseTopicId();
                String topic2 = o2.getQuizCourseTopic().getQuizCourseTopicId();
                String level1 = o1.getLevelId();
                String level2 = o2.getLevelId();
                int c1 = topic1.compareTo(topic2);
                if (c1 == 0) {
                    return level1.compareTo(level2);
                } else {
                    return c1;
                }
            }
        });
        log.info("getListQuizForAssignmentOfTest, testId = " + testId + " courseId = " + courseId
                 + " RETURN list.sz = " + quizQuestionDetailModels.size());

        return ResponseEntity.ok().body(quizQuestionDetailModels);

    }

    @GetMapping("/get-list-interactive-quiz-by-session/{sessionId}")
    public ResponseEntity<?> getListInteractiveQuizBySession(Principal principal, @PathVariable UUID sessionId) {
        List<InteractiveQuiz> interactiveQuizs = interactiveQuizRepo.findAllBySessionId(sessionId);
        return ResponseEntity.ok().body(interactiveQuizs);

    }

    @GetMapping("/get-list-course-interactive-quiz-by-session/{sessionId}")
    public ResponseEntity<?> getListCourseInteractiveQuizBySession(Principal principal, @PathVariable UUID sessionId) {
        List<InteractiveQuiz> interactiveQuizs = interactiveQuizRepo.findAllBySessionId(sessionId);
        return ResponseEntity.ok().body(interactiveQuizs);

    }

    @Secured({"ROLE_TEACHER"})
    @GetMapping("/get-list-quiz-questions-of-course-by-testId/{testId}")
    public ResponseEntity<?> getQuizQuestionsOfCourse(Principal principal, @PathVariable UUID testId) {
        EduCourseSessionInteractiveQuiz eduCourseSessionInteractiveQuiz = eduCourseSessionInteractiveQuizRepo.findById(testId).orElse(null);
        if (eduCourseSessionInteractiveQuiz == null) {
            return ResponseEntity.status(404).build();
        }
        EduCourseSession eduCourseSession = eduCourseSessionRepo.findById(eduCourseSessionInteractiveQuiz.getSessionId()).orElse(null);
        if (eduCourseSession == null) {
            return ResponseEntity.status(404).build();
        }
        List<QuizQuestion> quizQuestions = quizQuestionService.findQuizOfCourse(eduCourseSession.getCourseId());
        List<QuizQuestionDetailModel> quizQuestionDetailModels = new ArrayList<>();
        for (QuizQuestion q : quizQuestions) {
            if (q.getStatusId().equals(QuizQuestion.STATUS_PUBLIC)) {
                continue;
            }
            QuizQuestionDetailModel quizQuestionDetailModel = quizQuestionService.findQuizDetail(q.getQuestionId());
            quizQuestionDetailModels.add(quizQuestionDetailModel);
        }
        Collections.sort(quizQuestionDetailModels, new Comparator<QuizQuestionDetailModel>() {
            @Override
            public int compare(QuizQuestionDetailModel o1, QuizQuestionDetailModel o2) {
                String topic1 = o1.getQuizCourseTopic().getQuizCourseTopicId();
                String topic2 = o2.getQuizCourseTopic().getQuizCourseTopicId();
                String level1 = o1.getLevelId();
                String level2 = o2.getLevelId();
                int c1 = topic1.compareTo(topic2);
                if (c1 == 0) {
                    return level1.compareTo(level2);
                } else {
                    return c1;
                }
            }
        });

        return ResponseEntity.ok().body(quizQuestionDetailModels);
    }

    @GetMapping("/get-list-interactive-quiz-questions/{testId}")
    public ResponseEntity<?> getListQuestionsOfInteractiveQuiz(Principal principal, @PathVariable String testId) {
        InteractiveQuiz interactiveQuiz = interactiveQuizService.getInteractiveQuizById(testId);
        if (interactiveQuiz == null) {
            return ResponseEntity.ok().body(new ArrayList());
        }
        UUID sessionId = interactiveQuiz.getSessionId();
        EduClassSession eduClassSession = eduClassSessionRepo.findBySessionId(sessionId);
        if (eduClassSession == null) {
            return ResponseEntity.ok().body(new ArrayList());
        }
        EduClass eduClass = classService.findById(eduClassSession.getClassId());


        String courseId = null;
        if (eduClassSession != null) {
            courseId = eduClass.getEduCourse().getId();
        }


        List<QuizQuestion> quizQuestions = quizQuestionService.findQuizOfCourse(courseId);
        log.info("getListQuizForAssignmentOfTest, testId = " +
                 testId +
                 " courseId = " +
                 courseId +
                 " all questions = " +
                 quizQuestions.size());
        List<QuizQuestionDetailModel> quizQuestionDetailModels = new ArrayList<>();
        for (QuizQuestion q : quizQuestions) {
            if (q.getStatusId().equals(QuizQuestion.STATUS_PUBLIC)) {
                continue;
            }
            QuizQuestionDetailModel quizQuestionDetailModel = quizQuestionService.findQuizDetail(q.getQuestionId());
            quizQuestionDetailModels.add(quizQuestionDetailModel);
        }
        Collections.sort(quizQuestionDetailModels, new Comparator<QuizQuestionDetailModel>() {
            @Override
            public int compare(QuizQuestionDetailModel o1, QuizQuestionDetailModel o2) {
                String topic1 = o1.getQuizCourseTopic().getQuizCourseTopicId();
                String topic2 = o2.getQuizCourseTopic().getQuizCourseTopicId();
                String level1 = o1.getLevelId();
                String level2 = o2.getLevelId();
                int c1 = topic1.compareTo(topic2);
                if (c1 == 0) {
                    return level1.compareTo(level2);
                } else {
                    return c1;
                }
            }
        });
        log.info("getListQuizForAssignmentOfTest, testId = " + testId + " courseId = " + courseId
                 + " RETURN list.sz = " + quizQuestionDetailModels.size());

        return ResponseEntity.ok().body(quizQuestionDetailModels);

    }

    @Secured({"ROLE_TEACHER"})
    @GetMapping("/get-interactive-quiz-result/{interactiveQuizId}")
    public ResponseEntity<?> getInteractiveQuizResult(Principal principal, @PathVariable UUID interactiveQuizId) {
        List<StudentResult> results = interactiveQuizRepo.getResultOfInteractiveQuiz(interactiveQuizId);
        return ResponseEntity.ok().body(results);

    }

    @Secured({"ROLE_TEACHER"})
    @GetMapping("/get-result-of-interactive-quiz/{interactiveQuizId}")
    public ResponseEntity<?> getResultsOfInteractiveQuiz(Principal principal, @PathVariable UUID interactiveQuizId) {
        List<StudentResult> results = interactiveQuizService.getQuizResults(interactiveQuizId);
        return ResponseEntity.ok().body(results);

    }

    @Secured({"ROLE_TEACHER"})
    @GetMapping("/get-interactive-quiz-submission/{interactiveQuizId}")
    public ResponseEntity<?> getInteractiveQuizSubmission(Principal principal, @PathVariable UUID interactiveQuizId) {
        List<StudentSubmission> results = interactiveQuizRepo.getStudentSubmission(interactiveQuizId);
        return ResponseEntity.ok().body(results);
    }

    @Secured({"ROLE_TEACHER"})
    @PostMapping("/reject-students-in-test")
    public ResponseEntity<?> rejectStudentInTest(
        Principal principal,
        @RequestParam(required = false, name = "testId") String testId,
        @RequestParam(required = false, name = "studentList") String studentList
    ) {
        String[] students = studentList.split(";");
        return ResponseEntity.ok().body(quizTestService.rejectStudentsInTest(testId, students));
    }

    @Secured({"ROLE_TEACHER"})
    @PostMapping("/accept-students-in-test")
    public ResponseEntity<?> acceptStudentInTest(
        Principal principal,
        @RequestParam(required = false, name = "testId") String testId,
        @RequestParam(required = false, name = "studentList") String studentList
    ) {
        String[] students = studentList.split(";");
        return ResponseEntity.ok().body(quizTestService.acceptStudentsInTest(testId, students));
    }

    @Secured({"ROLE_TEACHER"})
    @GetMapping("/get-test-groups-info")
    public ResponseEntity<?> getTestGroups(
        @RequestParam(required = false, name = "testId") String testId
    ) {
        return ResponseEntity.ok().body(quizTestService.getQuizTestGroupsInfoByTestId(testId));
    }

    @Secured({"ROLE_TEACHER"})
    @PostMapping("/delete-quiz-test-groups")
    public ResponseEntity<?> deleteQuizTestGroups(
        Principal principal,
        @RequestParam(required = false, name = "testId") String testId,
        @RequestParam(required = false, name = "quizTestGroupList") String quizTestGroupList
    ) {
        String[] list = quizTestGroupList.split(";");
        return ResponseEntity.ok().body(quizTestService.deleteQuizTestGroups(testId, list));
    }

    @Secured({"ROLE_TEACHER"})
    @PostMapping("/get-quiz-test-participation-execution-result")
    public ResponseEntity<?> getQuizTestParticipationExecutionResult(
        Principal principal, @RequestBody
    GetQuizTestParticipationExecutionResultInputModel input
    ) {
        List<QuizTestParticipationExecutionResultOutputModel> quizTestParticipationExecutionResultOutputModels =
            quizTestService.getQuizTestParticipationExecutionResult(input.getTestId());
        //quizTestService.getQuizTestParticipationExecutionResultNewByPQD(input.getTestId());

        return ResponseEntity.ok().body(quizTestParticipationExecutionResultOutputModels);
    }

    @Secured({"ROLE_TEACHER"})
    @GetMapping("/get-quiz-test-participation-execution-result-of-user-login/{userLoginId}")
    public ResponseEntity<?> getQuizTestParticipationExecutionResultOfAStudent(
        Principal principal, @PathVariable String userLoginId
    ) {
        log.info("getQuizTestParticipationExecutionResultOfAStudent, studentId = " + userLoginId);
        List<UserQuestionQuizExecutionOM> userQuestionExecutions =
            quizTestService.getQuizTestParticipationExecutionResultOfAUserLogin(userLoginId);

        return ResponseEntity.ok().body(userQuestionExecutions);
    }

    @Secured({"ROLE_TEACHER"})
    @PostMapping("/copy-question-from-quiztest-to-quiztest")
    public ResponseEntity<?> copyQuestionFromQuizTestId2QuizTestId(
        Principal principal,
        @RequestBody CopyQuestionFromQuizTest2QuizTestInputModel input
    ) {
        UserLogin u = userService.findById(principal.getName());

        log.info("copyQuestionFromQuizTestId2QuizTestId from test " +
                 input.getFromTestId() +
                 " to test " +
                 input.getToTestId());
        int cnt = quizTestService.copyQuestionsFromQuizTest2QuizTest(u, input.getFromTestId(), input.getToTestId());
        return ResponseEntity.ok().body(cnt);
    }

    @PostMapping("/upload-solution-excel-quiz-of-student")
    public ResponseEntity<?> uploadSolutionExcelQuizTestOfStudent(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelUploadSolutionExcelQuizTestOfStudent modelUpload = gson.fromJson(
            inputJson, ModelUploadSolutionExcelQuizTestOfStudent.class);
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFSheet sheetInfo = wb.getSheetAt(1);
            Row r = sheetInfo.getRow(1);
            Cell c = r.getCell(1);
            String userId = c.getStringCellValue();
            r = sheetInfo.getRow(2);
            c = r.getCell(1);
            String testId = c.getStringCellValue();
            r = sheetInfo.getRow(3);
            c = r.getCell(1);
            String quizGroupCode = c.getStringCellValue();
            EduTestQuizGroup group = eduQuizTestGroupService.getQuizTestGroupFrom(quizGroupCode, testId);
            UUID groupId = null;
            if (group != null) {
                groupId = group.getQuizGroupId();
            }
            log.debug("uploadSolutionExcelQuizTestOfStudent, userId = " +
                      userId +
                      " testId = " +
                      testId +
                      " groupCode = " +
                      quizGroupCode
                      +
                      " groupId = " +
                      groupId);

            EduQuizTest eduQuizTest = quizTestService.getQuizTestById(testId);
            if (eduQuizTest.getJudgeMode() == null ||
                !eduQuizTest.getJudgeMode().equals(EduQuizTest.JUDGE_MODE_OFFLINE_VIA_EXCEL_UPLOAD)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Mode Not Allowed");
            }

            QuizGroupTestDetailModel res = eduQuizTestGroupService.getTestGroupQuestionDetailNotUsePermutationConfig(
                userId,
                testId);
            if (res == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found Quiz Test Group");
            }
            if (res.getListQuestion() == null || res.getListQuestion().size() == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Not Found Questions of the Quiz Test Group");
            }
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                r = sheet.getRow(i);
                c = r.getCell(0);
                String questionIdx = c.getStringCellValue();
                c = r.getCell(1);
                String[] choices = null;
                //if(c == null){
                //    continue;
                //}
                //if(!c.getCellType().equals(CellType.STRING)){
                //    continue;
                //}
                if (c != null) {
                    if (c.getCellType().equals(CellType.STRING)) {
                        if (c.getStringCellValue() != null && !c.getStringCellValue().equals("")) {
                            choices = c.getStringCellValue().split(",");
                        }
                    }
                }
                if (i - 1 >= res.getListQuestion().size()) {
                    log.debug("uploadSolutionExcelQuizTestOfStudent, question " +
                              questionIdx +
                              " i = " +
                              i +
                              " -> continue");
                    continue;
                }
                //if(choices == null || choices.length == 0) continue;
                QuizQuestionDetailModel question = res.getListQuestion().get(i - 1);
                UUID questionId = question.getQuestionId();
                List<UUID> chooseAnsIds = new ArrayList();
                if (choices != null && choices.length > 0) {
                    for (int j = 0; j < choices.length; j++) {
                        String choiceCode = choices[j].trim();
                        for (QuizChoiceAnswerHideCorrectAnswer a : question.getQuizChoiceAnswerList()) {
                            if (choiceCode.equals(a.getChoiceAnswerCode())) {
                                chooseAnsIds.add(a.getChoiceAnswerId());
                                break;
                            }
                        }
                        log.debug("uploadSolutionExcelQuizTestOfStudent, question " +
                                  questionIdx +
                                  " choiceCode = " +
                                  choiceCode);
                    }
                    log.debug("uploadSolutionExcelQuizTestOfStudent, question " +
                              questionIdx +
                              " chooseAnsIds = " +
                              chooseAnsIds.size());
                }
                log.debug("uploadSolutionExcelQuizTestOfStudent, question " +
                          questionIdx +
                          " questionId = " +
                          questionId +
                          " choices (len = " +
                          chooseAnsIds.size() +
                          ") ");

                quizTestService.submitSynchronousQuizTestExecutionChoice(questionId, groupId, userId, chooseAnsIds);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("OK");

    }

    @PostMapping("/upload-excel-student-list")
    public ResponseEntity<?> uploadExcelStudentListOfQuizTest(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelUploadExcelStudentListOfQuizTest modelUpload = gson.fromJson(
            inputJson,
            ModelUploadExcelStudentListOfQuizTest.class);
        String testId = modelUpload.getTestId();
        log.info("uploadExcelStudentListOfQuizTest, testId = " + testId);
        List<ModelResponseImportExcelUsersToQuizTest> res = new ArrayList<>();
        try{
            InputStream is = file.getInputStream();
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            Row r0 = sheet.getRow(0);
            int nbCol = r0.getLastCellNum();
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                //Cell c = row.getCell(0);
                String userId = "";
                String fullName = "";
                String refUserId = "";
                String email = "";
                String code = "";
                for(int j = 0; j < nbCol; j++){
                    Cell cj = row.getCell(j);
                    if(r0.getCell(j).getStringCellValue().equals("user_id")){
                        userId = cj.getStringCellValue();
                    }else if(r0.getCell(j).getStringCellValue().equals("full_name")){
                        fullName = cj.getStringCellValue();
                    }else if(r0.getCell(j).getStringCellValue().equals("ref_user_id")){
                        refUserId = cj.getStringCellValue();
                    }else if(r0.getCell(j).getStringCellValue().equals("email")){
                        email = cj.getStringCellValue();
                    }else if(r0.getCell(j).getStringCellValue().equals("code")){
                        code = cj.getStringCellValue();
                    }
                }
                res.add(new ModelResponseImportExcelUsersToQuizTest(userId,fullName,refUserId,email,code));
            }
            eduTestQuizParticipantService.addParticipants2QuizTest(testId,res);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(res);
        /*
        List<String> uploadedUsers = new ArrayList();
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);

            int lastRowNum = sheet.getLastRowNum();
            //System.out.println("uploadExcelStudentListOfQuizTest, lastRowNum = " + lastRowNum);
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                Cell c = row.getCell(0);

                String userId = c.getStringCellValue();
                boolean ok = eduTestQuizParticipantService.addParticipant2QuizTest(userId, testId);
                if (ok) {
                    uploadedUsers.add(userId);
                }
                //System.out.print("get user " + userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(uploadedUsers);
        */

    }

    @GetMapping("/get-list-quiz-test-status-ids")
    public ResponseEntity<?> getListQuizTestStatusIds() {
        List<String> statusIds = EduQuizTest.getListStatusIds();
        return ResponseEntity.ok().body(statusIds);
    }

    @GetMapping("/get-list-judge-modes")
    public ResponseEntity<?> getListJudgeModes() {
        List<String> judgeModes = EduQuizTest.getListJudgeModes();
        return ResponseEntity.ok().body(judgeModes);
    }

    @PostMapping("/analyze-do-quiz-test-in-class")
    public ResponseEntity<?> analyzeDoQuizTestInClass(Principal principal, @RequestBody ModelAnalyzeDoQuizTestInClassInput I){
        log.info("analyzeDoQuizTestInClass, classId = " + I.getClassId());
        //int res = quizTestService.summarizeQuizTestInClass(I.getClassId());
        int res = quizTestService.summarizeInteractiveQuizTestInClass(I.getClassId());


        return ResponseEntity.ok().body(res);
    }
    @GetMapping("/get-analyze-do-quiz-in-class/{classId}")
    public ResponseEntity<?> getAnalyzeDoQuizInClass(Principal principal, @PathVariable UUID classId){
        List<ModelResponseAnalyzeDoQuizInClass> res = quizTestService.getAnalyzeDoQuizInClass(classId);
        return ResponseEntity.ok().body(res);
    }
}
