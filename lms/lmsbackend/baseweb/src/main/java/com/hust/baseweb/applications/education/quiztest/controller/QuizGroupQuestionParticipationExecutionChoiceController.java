package com.hust.baseweb.applications.education.quiztest.controller;

import com.hust.baseweb.applications.education.classmanagement.service.ClassService;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTest;
import com.hust.baseweb.applications.education.quiztest.entity.HistoryLogQuizGroupQuestionParticipationExecutionChoice;
import com.hust.baseweb.applications.education.quiztest.entity.QuizGroupQuestionParticipationExecutionChoice;
import com.hust.baseweb.applications.education.quiztest.entity.QuizTestExecutionSubmission;
import com.hust.baseweb.applications.education.quiztest.model.HistoryLogQuizGroupQuestionParticipationExecutionChoiceDetailModel;
import com.hust.baseweb.applications.education.quiztest.model.ModelResponseSubmitQuizTestExecutionChoice;
import com.hust.baseweb.applications.education.quiztest.model.QuizGroupQuestionParticipationExecutionChoiceInputModel;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestRepo;
import com.hust.baseweb.applications.education.quiztest.repo.HistoryLogQuizGroupQuestionParticipationExecutionChoiceRepo;
import com.hust.baseweb.applications.education.quiztest.repo.QuizGroupQuestionParticipationExecutionChoiceRepo;
import com.hust.baseweb.applications.education.quiztest.repo.QuizTestExecutionSubmissionRepo;
import com.hust.baseweb.applications.education.quiztest.service.QuizTestService;
import com.hust.baseweb.applications.education.service.CourseService;
import com.hust.baseweb.config.rabbitmq.QuizRoutingKey;
import com.hust.baseweb.service.PersonService;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.hust.baseweb.config.rabbitmq.ProblemContestRoutingKey.JUDGE_PROBLEM;
import static com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestConfig.EXCHANGE;
import static com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestConfig.QUIZ_EXCHANGE;

@Log4j2
@Controller
@Validated
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin

public class QuizGroupQuestionParticipationExecutionChoiceController {

    QuizTestExecutionSubmissionRepo quizTestExecutionSubmissionRepo;
    QuizGroupQuestionParticipationExecutionChoiceRepo quizGroupQuestionParticipationExecutionChoiceRepo;
    QuizTestService quizTestService;
    EduQuizTestRepo eduQuizTestRepo;
    HistoryLogQuizGroupQuestionParticipationExecutionChoiceRepo historyLogQuizGroupQuestionParticipationExecutionChoiceRepo;
    UserService userService;
    PersonService personService;
    ClassService classService;
    CourseService courseService;
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/summarize-quiz-test-execution-choice/{testId}")
    public ResponseEntity<?> summarizeQuizTestExecutionChoice(Principal principal, @PathVariable String testId){
        int cnt = quizTestService.summarizeQuizTestExecutionChoice(testId);
        return ResponseEntity.ok().body(cnt);
    }
    @PostMapping("/submit-quiz-test-choose_answer-by-user")
    public ResponseEntity<?> submitQuizChooseAnswer(
        Principal principal,
        @RequestBody @Valid QuizGroupQuestionParticipationExecutionChoiceInputModel input
    ) {
        EduQuizTest test = eduQuizTestRepo.findById(input.getTestId()).get();
        Date currentDate = new Date();
        Date testStartDate = test.getScheduleDatetime();
        int timeTest = ((int) (currentDate.getTime() - testStartDate.getTime())) / (60 * 1000); //minutes
        //System.out.println(currentDate);
        //System.out.println(testStartDate);
        //System.out.println(timeTest);
        //System.out.println(test.getDuration());

        if (timeTest > test.getDuration()) {
            log.info("submitQuizChooseAnswer, user " + principal.getName() + " try to submit BUT out time~!");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        if(!test.getStatusId().equals(EduQuizTest.QUIZ_TEST_STATUS_RUNNING)){
            log.info("submitQuizChooseAnswer, user " + principal.getName() + " try to submit BUT quiz test is NOT running!");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        UUID questionId = input.getQuestionId();
        UUID groupId = input.getQuizGroupId();
        String userId = principal.getName();
        List<UUID> chooseAnsIds = input.getChooseAnsIds();

        ModelResponseSubmitQuizTestExecutionChoice res = new ModelResponseSubmitQuizTestExecutionChoice();
        res.setChoiceAnswerIds(chooseAnsIds);

        if(test.getJudgeMode() != null){
            if(test.getJudgeMode().equals(EduQuizTest.JUDGE_MODE_SYNCHRONOUS)){
                QuizTestExecutionSubmission response = quizTestService
                    .submitSynchronousQuizTestExecutionChoice(questionId, groupId, userId, chooseAnsIds);
                res.setSubmissionId(response.getSubmissionId());

                //return ResponseEntity.ok().body(res);
            }else if(test.getJudgeMode().equals(EduQuizTest.JUDGE_MODE_ASYNCHRONOUS_QUEUE)){
                QuizTestExecutionSubmission sub = quizTestService.submitAsynchronousQuizTestExecutionChoiceUsingRabbitMQ(questionId, groupId, userId, chooseAnsIds);
                res.setSubmissionId(sub.getSubmissionId());

                //return ResponseEntity.ok().body(res);
            }else if(test.getJudgeMode().equals(EduQuizTest.JUDGE_MODE_BATCH_LAZY_EVALUATION)){
                QuizTestExecutionSubmission sub = quizTestService.submitQuizTestExecutionChoiceBatchLazyEvaluation(questionId, groupId, userId, chooseAnsIds);
                res.setSubmissionId(sub.getSubmissionId());

                //return ResponseEntity.ok().body(res);
            }
        }else{// by default: SYNCHRONOUS
            QuizTestExecutionSubmission response = quizTestService
                .submitSynchronousQuizTestExecutionChoice(questionId, groupId, userId, chooseAnsIds);

            res.setSubmissionId(response.getSubmissionId());
            //return ResponseEntity.ok().body(res);
        }
        return ResponseEntity.ok().body(res);
    }

    /*
    can be replaced by the API submit-quiz-test-choose_answer-by-user with judge_mode option of quiz-test
     */
    @PostMapping("/quiz-test-choose_answer-by-user-v2-asynchronous")
    public ResponseEntity<?> quizChooseAnswerVersion2Asynchronous(
        Principal principal,
        @RequestBody @Valid QuizGroupQuestionParticipationExecutionChoiceInputModel input
    ) {
        EduQuizTest test = eduQuizTestRepo.findById(input.getTestId()).get();
        Date currentDate = new Date();
        Date testStartDate = test.getScheduleDatetime();
        int timeTest = ((int) (currentDate.getTime() - testStartDate.getTime())) / (60 * 1000); //minutes
        //System.out.println(currentDate);
        //System.out.println(testStartDate);
        //System.out.println(timeTest);
        //System.out.println(test.getDuration());

        if (timeTest > test.getDuration()) {
            log.info("quizChooseAnswerVersion2Asynchronous, user " + principal.getName() + " try to submit BUT out time~!");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        if(!test.getStatusId().equals(EduQuizTest.QUIZ_TEST_STATUS_RUNNING)){
            log.info("quizChooseAnswerVersion2Asynchronous, user " + principal.getName() + " try to submit BUT quiz test is NOT running!");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        UUID questionId = input.getQuestionId();
        UUID groupId = input.getQuizGroupId();
        String userId = principal.getName();
        List<UUID> chooseAnsIds = input.getChooseAnsIds();

        /*
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
        */
        QuizTestExecutionSubmission sub = quizTestService.submitAsynchronousQuizTestExecutionChoiceUsingRabbitMQ(questionId, groupId, userId, chooseAnsIds);
        ModelResponseSubmitQuizTestExecutionChoice res = new ModelResponseSubmitQuizTestExecutionChoice();
        res.setSubmissionId(sub.getSubmissionId());
        res.setChoiceAnswerIds(chooseAnsIds);

        return ResponseEntity.ok().body(res);
    }

    /*
        can be replaced by the API submit-quiz-test-choose_answer-by-user with judge_mode option of quiz-test
    */
    @PostMapping("/quiz-test-choose_answer-by-user")
    public ResponseEntity<?> quizChooseAnswer(
        Principal principal,
        @RequestBody @Valid QuizGroupQuestionParticipationExecutionChoiceInputModel input
    ) {
        EduQuizTest test = eduQuizTestRepo.findById(input.getTestId()).get();
        Date currentDate = new Date();
        Date testStartDate = test.getScheduleDatetime();
        int timeTest = ((int) (currentDate.getTime() - testStartDate.getTime())) / (60 * 1000); //minutes
        //System.out.println(currentDate);
        //System.out.println(testStartDate);
        //System.out.println(timeTest);
        //System.out.println(test.getDuration());

        if (timeTest > test.getDuration()) {
            log.info("quizChooseAnswer, user " + principal.getName() + " try to submit BUT out time~!");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        if(!test.getStatusId().equals(EduQuizTest.QUIZ_TEST_STATUS_RUNNING)){
            log.info("quizChooseAnswer, user " + principal.getName() + " try to submit BUT quiz test is NOT running!");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        UUID questionId = input.getQuestionId();
        UUID groupId = input.getQuizGroupId();
        String userId = principal.getName();
        List<UUID> chooseAnsIds = input.getChooseAnsIds();

        //List<QuizGroupQuestionParticipationExecutionChoice> res = quizTestService
        QuizTestExecutionSubmission sub = quizTestService
            .submitSynchronousQuizTestExecutionChoice(questionId, groupId, userId, chooseAnsIds);

        ModelResponseSubmitQuizTestExecutionChoice res = new ModelResponseSubmitQuizTestExecutionChoice();
        res.setSubmissionId(sub.getSubmissionId());
        res.setChoiceAnswerIds(chooseAnsIds);

        //return ResponseEntity.ok().body(chooseAnsIds);
        return ResponseEntity.ok().body(res);
    }
    @PostMapping("/quiz-test-session-choose_answer-by-user")
    public ResponseEntity<?> quizTestSessionChooseAnswer(
        Principal principal,
        @RequestBody @Valid QuizGroupQuestionParticipationExecutionChoiceInputModel input
    ) {
        EduQuizTest test = eduQuizTestRepo.findById(input.getTestId()).get();
        //Date currentDate = new Date();
        //Date testStartDate = test.getScheduleDatetime();
        //int timeTest = ((int) (currentDate.getTime() - testStartDate.getTime())) / (60 * 1000); //minutes
        //System.out.println(currentDate);
        //System.out.println(testStartDate);
        //System.out.println(timeTest);
        //System.out.println(test.getDuration());

        //if (timeTest > test.getDuration()) {
            //System.out.println("out time~!");
        //    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        //}
        if(!test.getStatusId().equals(EduQuizTest.QUIZ_TEST_STATUS_OPEN)){
            log.info("quizTestSessionChooseAnswer, quizTestSession is not Open, but user try to submit answer");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        UUID questionId = input.getQuestionId();
        UUID groupId = input.getQuizGroupId();
        String userId = principal.getName();
        List<UUID> chooseAnsIds = input.getChooseAnsIds();

        //if (chooseAnsIds == null) {
        //    log.info("quizChooseAnswer, chooseAnsIds = null");
        //} else {
        //    log.info("quizChooseAnswer, chooseAnsIds = " + chooseAnsIds.size());
        //}

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

        Date createdStamp = new Date();
        for (UUID choiceId :
            chooseAnsIds) {
            QuizGroupQuestionParticipationExecutionChoice tmp = new QuizGroupQuestionParticipationExecutionChoice();
            tmp.setQuestionId(questionId);
            tmp.setQuizGroupId(groupId);
            tmp.setParticipationUserLoginId(userId);
            tmp.setChoiceAnswerId(choiceId);
            tmp.setCreatedStamp(createdStamp);
            quizGroupQuestionParticipationExecutionChoiceRepo.save(tmp);


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


        return ResponseEntity.ok().body(chooseAnsIds);
    }

    @GetMapping("/get-history-log-quiz_group_question_participation_execution_choice/{testId}")
    public ResponseEntity<?> getHistoryLogQuizGroupQuestionParticipationExecutionChoice(
        Principal principal,
        @PathVariable String testId
    ) {
        log.info("getHistoryLogQuizGroupQuestionParticipationExecutionChoice, testId = " + testId);

        List<HistoryLogQuizGroupQuestionParticipationExecutionChoice> list =
            historyLogQuizGroupQuestionParticipationExecutionChoiceRepo.findAll();
        List<HistoryLogQuizGroupQuestionParticipationExecutionChoiceDetailModel> modelList
            = new ArrayList();
        DateFormat formetter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (HistoryLogQuizGroupQuestionParticipationExecutionChoice e : list) {
            HistoryLogQuizGroupQuestionParticipationExecutionChoiceDetailModel m =
                new HistoryLogQuizGroupQuestionParticipationExecutionChoiceDetailModel();
            m.setChoiceAnswerId(e.getChoiceAnswerId());
            String sDate = "";
            if (e.getCreatedStamp() != null) {
                try {
                    sDate = formetter.format(e.getCreatedStamp());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            m.setDate(sDate);
            m.setUserLoginId(e.getParticipationUserLoginId());
            m.setQuestionId(e.getQuestionId());
            m.setQuizGroupId(e.getQuizGroupId());
            modelList.add(m);
        }
        return ResponseEntity.ok().body(modelList);
    }
}
