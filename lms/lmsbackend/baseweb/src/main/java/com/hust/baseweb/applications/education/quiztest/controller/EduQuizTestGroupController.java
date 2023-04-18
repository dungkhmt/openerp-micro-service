package com.hust.baseweb.applications.education.quiztest.controller;

import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTest;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroup;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizParticipant;
import com.hust.baseweb.applications.education.quiztest.model.ParticipantAndQuestionsModel;
import com.hust.baseweb.applications.education.quiztest.model.QuizGroupTestDetailModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroup.GenerateQuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroup.QuizTestGroupParticipantAssignmentOutputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroupparticipant.ModelResponseGetQuizTestGroup;
import com.hust.baseweb.applications.education.quiztest.repo.EduTestQuizParticipantRepo;
import com.hust.baseweb.applications.education.quiztest.service.EduQuizTestGroupService;
import com.hust.baseweb.applications.education.quiztest.service.EduTestQuizGroupParticipationAssignmentService;
import com.hust.baseweb.applications.education.quiztest.service.QuizTestService;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
@Controller
@Validated
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin
public class EduQuizTestGroupController {

    private EduQuizTestGroupService eduQuizTestGroupService;
    private UserService userService;
    private EduTestQuizParticipantRepo eduTestQuizParticipationRepo;
    private EduTestQuizGroupParticipationAssignmentService eduTestQuizGroupParticipationAssignmentService;
    private QuizTestService quizTestService;

    @PostMapping("/generate-quiz-test-group")
    public ResponseEntity<?> generateQuizTestGroup(
        Principal principal, @RequestBody
        GenerateQuizTestGroupInputModel input
    ) {

        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupService.generateQuizTestGroups(input);

        return ResponseEntity.ok().body(eduTestQuizGroups);
    }

    @GetMapping("/get-all-quiz-test-participation-group-question/{testID}")
    public ResponseEntity<?> getAllTestGroupQuestionByUser(Principal principal, @PathVariable String testID) {
        List<QuizTestGroupParticipantAssignmentOutputModel> quizTestGroupParticipantAssignmentOutputModels
            = eduTestQuizGroupParticipationAssignmentService.getQuizTestGroupParticipant(testID);
        List<ParticipantAndQuestionsModel> retList = new ArrayList();
        for(QuizTestGroupParticipantAssignmentOutputModel p : quizTestGroupParticipantAssignmentOutputModels){
            String userId = p.getParticipantUserLoginId();
            QuizGroupTestDetailModel questions = eduQuizTestGroupService.getTestGroupQuestionDetail(userId, testID);
            ParticipantAndQuestionsModel participantAndQuestionsModel = new ParticipantAndQuestionsModel();
            participantAndQuestionsModel.setParticipantUserLoginId(userId);
            participantAndQuestionsModel.setQuizGroupTestDetailModel(questions);

            retList.add(participantAndQuestionsModel);

        }
        return ResponseEntity.ok().body(retList);
    }
    @GetMapping("/get-all-quiz-test-group-with-questions-detail/{testID}")
    public ResponseEntity<?> getAllTestGroupWithQuestionsDetail(Principal principal, @PathVariable String testID) {
        List<QuizGroupTestDetailModel> res = eduQuizTestGroupService.getQuizTestGroupWithQuestionsDetail(testID);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-quiz-questions-assigned-to-participant/{testID}/{participantId}")
    public ResponseEntity<?> getQuizQuestionsAssignedToParticipant(Principal principal, @PathVariable String testID, @PathVariable String participantId) {
        EduQuizTest eduQuizTest = quizTestService.getQuizTestById(testID);
        log.info("getQuizQuestionsAssignedToParticipant, testId = " + testID + " participantId = " + participantId);

        /*
        Date startDateTime = eduQuizTest.getScheduleDatetime();
        Date currentDate = new Date();
        int timeTest = ((int) (currentDate.getTime() - startDateTime.getTime())) / (60 * 1000); //minutes
        log.info("getTestGroupQuestionByUser, current = " + currentDate.toString() +
                 " scheduleDate = " + startDateTime.toString() + " timeTest = " + timeTest);

        if (timeTest > eduQuizTest.getDuration() || timeTest < 0) {// out-of-allowed date-time
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        */
        EduTestQuizParticipant testParticipant = eduTestQuizParticipationRepo
            .findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(
                participantId,
            testID);

        if (testParticipant == null ||
            (!testParticipant.getStatusId().equals(EduTestQuizParticipant.STATUS_APPROVED))) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.ok().body(eduQuizTestGroupService.getTestGroupQuestionDetail(participantId, testID));
    }

    @GetMapping("/get-quiz-test-participation-group-question/{testID}")
    public ResponseEntity<?> getTestGroupQuestionByUser(Principal principal, @PathVariable String testID) {
        EduQuizTest eduQuizTest = quizTestService.getQuizTestById(testID);
        Date startDateTime = eduQuizTest.getScheduleDatetime();
        Date currentDate = new Date();
        int timeTest = ((int) (currentDate.getTime() - startDateTime.getTime())) / (60 * 1000); //minutes
        log.info("getTestGroupQuestionByUser, current = " + currentDate.toString() +
                 " scheduleDate = " + startDateTime.toString() + " timeTest = " + timeTest);

        if (timeTest > eduQuizTest.getDuration() || timeTest < 0) {// out-of-allowed date-time
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        EduTestQuizParticipant testParticipant = eduTestQuizParticipationRepo.findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(
            principal.getName(),
            testID);

        if (testParticipant == null ||
            (!testParticipant.getStatusId().equals(EduTestQuizParticipant.STATUS_APPROVED))) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        QuizGroupTestDetailModel res = null;
        if(eduQuizTest.getParticipantQuizGroupAssignmentMode() != null &&
           eduQuizTest.getParticipantQuizGroupAssignmentMode().equals(EduQuizTest.PARTICIPANT_QUIZ_GROUP_ASSIGNMENT_MODE_ASSIGN_GROUP_BEFORE_HANDOUT)) {
            res = eduQuizTestGroupService.getTestGroupQuestionDetail(principal, testID);
        }else{
            res = eduQuizTestGroupService.getTestGroupQuestionDetailNotUsePermutationConfig(principal.getName(),testID);
        }

        if(eduQuizTest.getQuestionStatementViewTypeId() != null &&
           eduQuizTest.getQuestionStatementViewTypeId().equals(EduQuizTest.QUESTION_STATEMENT_VIEW_TYPE_HIDDEN)) {
            //ìf(res != null && res.getListQuestion() != null){
            if(res != null) if(res.getListQuestion() != null){
                for (QuizQuestionDetailModel q : res.getListQuestion()) {
                    q.setStatement("");
                }
            }
        }
        res.setParticipantUserId(principal.getName());
        res.setJudgeMode(eduQuizTest.getJudgeMode());
        return ResponseEntity.ok().body(res);
    }
    @GetMapping("/get-quiz-test-participation-group-question-reload-heavy/{testID}")
    public ResponseEntity<?> getTestGroupQuestionByUserReloadHeavy(Principal principal, @PathVariable String testID) {
        EduQuizTest eduQuizTest = quizTestService.getQuizTestById(testID);
        Date startDateTime = eduQuizTest.getScheduleDatetime();
        Date currentDate = new Date();
        int timeTest = ((int) (currentDate.getTime() - startDateTime.getTime())) / (60 * 1000); //minutes
        log.info("getTestGroupQuestionByUserReloadHeavy, current = " + currentDate.toString() +
                 " scheduleDate = " + startDateTime.toString() + " timeTest = " + timeTest);

        if (timeTest > eduQuizTest.getDuration() || timeTest < 0) {// out-of-allowed date-time
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        EduTestQuizParticipant testParticipant = eduTestQuizParticipationRepo.findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(
            principal.getName(),
            testID);

        if (testParticipant == null ||
            (!testParticipant.getStatusId().equals(EduTestQuizParticipant.STATUS_APPROVED))) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        QuizGroupTestDetailModel res = null;
        if(eduQuizTest.getParticipantQuizGroupAssignmentMode() != null &&
           eduQuizTest.getParticipantQuizGroupAssignmentMode().equals(EduQuizTest.PARTICIPANT_QUIZ_GROUP_ASSIGNMENT_MODE_ASSIGN_GROUP_BEFORE_HANDOUT)) {
            res = eduQuizTestGroupService.getTestGroupQuestionDetailHeavyReload(principal, testID);
        }else{
            res = eduQuizTestGroupService.getTestGroupQuestionDetailNotUsePermutationConfigHeavyReload(principal.getName(),testID);
        }

        if(eduQuizTest.getQuestionStatementViewTypeId() != null &&
           eduQuizTest.getQuestionStatementViewTypeId().equals(EduQuizTest.QUESTION_STATEMENT_VIEW_TYPE_HIDDEN)) {
            //ìf(res != null && res.getListQuestion() != null){
            if(res != null) if(res.getListQuestion() != null){
                for (QuizQuestionDetailModel q : res.getListQuestion()) {
                    q.setStatement("");
                }
            }
        }
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/confirm-update-group-code-quiz-test/{testID}/{groupCode}")
    public ResponseEntity<?> confirmAndUpdateGroupCodeInQuizTest(Principal principal,
                                                     @PathVariable String testID, @PathVariable String groupCode) {
        boolean ok = quizTestService.confirmUpdateGroupInQuizTest(principal.getName(), groupCode, testID);

        return ResponseEntity.ok().body(ok);

    }

        @GetMapping("/check-questions-of-group/{testID}/{groupCode}")
    public ResponseEntity<?> getCheckQuestionOfGroup(Principal principal,
                                                        @PathVariable String testID, @PathVariable String groupCode) {
        EduQuizTest eduQuizTest = quizTestService.getQuizTestById(testID);
        Date startDateTime = eduQuizTest.getScheduleDatetime();
        Date currentDate = new Date();
        int timeTest = ((int) (currentDate.getTime() - startDateTime.getTime())) / (60 * 1000); //minutes
        log.info("getCheckQuestionOfGroup, current = " + currentDate.toString() +
                 " groupCode = " + groupCode + " testID = " + testID +
                 " scheduleDate = " + startDateTime.toString() + " timeTest = " + timeTest);

        if (timeTest > eduQuizTest.getDuration() || timeTest < 0) {// out-of-allowed date-time
            log.info("getCheckQuestionOfGroup, timeTest = " + timeTest + " > duration = " + eduQuizTest.getDuration() + " return");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        EduTestQuizParticipant testParticipant = eduTestQuizParticipationRepo.findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(
            principal.getName(),
            testID);

        if (testParticipant == null ||
            (!testParticipant.getStatusId().equals(EduTestQuizParticipant.STATUS_APPROVED))) {
            log.info("getCheckQuestionOfGroup, testParticipant = null -> return");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        //QuizGroupTestDetailModel res = eduQuizTestGroupService.getQuestionsDetailOfQuizGroup(groupCode, testID);
        QuizGroupTestDetailModel res = eduQuizTestGroupService.getQuestionsDetailWithUserExecutionChoideOfQuizGroupNotUsePermutationConfig(
            principal.getName(), groupCode, testID);

        log.info("getCheckQuestionOfGroup, GOT " + res.getListQuestion().size());
        if(eduQuizTest.getQuestionStatementViewTypeId() != null &&
           eduQuizTest.getQuestionStatementViewTypeId().equals(EduQuizTest.QUESTION_STATEMENT_VIEW_TYPE_HIDDEN)) {
            for (QuizQuestionDetailModel q : res.getListQuestion()) {
                q.setStatement("");
            }
        }
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-all-quiz-test-group-participants/{testId}")
    public ResponseEntity<?> getQuizTestGroupParticipants(Principal principal, @PathVariable String testId) {
        log.info("getQuizTestGroupParticipants, testId = " + testId);
        List<QuizTestGroupParticipantAssignmentOutputModel> quizTestGroupParticipantAssignmentOutputModels
            = eduTestQuizGroupParticipationAssignmentService.getQuizTestGroupParticipant(testId);
        return ResponseEntity.ok().body(quizTestGroupParticipantAssignmentOutputModels);
    }

    @GetMapping("/get-my-quiz-test-group/{testId}")
    public ResponseEntity<?> getMyQuizTestGroup(Principal principal, @PathVariable String testId){
        ModelResponseGetQuizTestGroup res = eduTestQuizGroupParticipationAssignmentService
            .getQuizTestGroupOfUser(principal.getName(),testId);
        return ResponseEntity.ok().body(res);
    }

}
