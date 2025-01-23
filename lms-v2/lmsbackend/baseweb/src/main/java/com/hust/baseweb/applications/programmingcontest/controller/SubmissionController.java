package com.hust.baseweb.applications.programmingcontest.controller;

import com.google.gson.Gson;
import com.hust.baseweb.applications.programmingcontest.callexternalapi.model.LmsLogModelCreate;
import com.hust.baseweb.applications.programmingcontest.callexternalapi.service.ApiService;
import com.hust.baseweb.applications.programmingcontest.entity.*;
import com.hust.baseweb.applications.programmingcontest.model.*;
import com.hust.baseweb.applications.programmingcontest.model.externalapi.ModelInputGetContestSubmissionPageOfPeriod;
import com.hust.baseweb.applications.programmingcontest.model.externalapi.ModelResponseGetContestSubmissionOfPeriod;
import com.hust.baseweb.applications.programmingcontest.model.externalapi.ModelResponseGetContestSubmissionPage;
import com.hust.baseweb.applications.programmingcontest.repo.*;
import com.hust.baseweb.applications.programmingcontest.service.ContestService;
import com.hust.baseweb.applications.programmingcontest.service.ContestSubmissionCommentService;
import com.hust.baseweb.applications.programmingcontest.service.ContestSubmissionService;
import com.hust.baseweb.applications.programmingcontest.service.ProblemTestCaseService;
import com.hust.baseweb.applications.programmingcontest.service.helper.cache.ProblemTestCaseServiceCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SubmissionController {

    @Autowired
    private ContestSubmissionService contestSubmissionService;
    private final ContestService contestService;
    private ContestSubmissionCommentService commentService;
    private final ContestSubmissionCommentService contestSubmissionCommentService;
    ProblemTestCaseService problemTestCaseService;
    ContestRepo contestRepo;
    ContestSubmissionRepo contestSubmissionRepo;
    ContestProblemRepo contestProblemRepo;
    UserRegistrationContestRepo userRegistrationContestRepo;
    ProblemTestCaseServiceCache cacheService;
    ContestSubmissionCommentRepository contestSubmissionCommentRepo;
    ApiService apiService;

    //@Autowired
    //HttpRequest request;

    @Secured("ROLE_TEACHER")
    @PostMapping("/teacher/submissions/{submissionId}/disable")
    public ResponseEntity<?> teacherDisableSubmission(Principal principal, @PathVariable UUID submissionId) {
        ContestSubmissionEntity managementStatus = problemTestCaseService.teacherDisableSubmission(principal.getName(),
                                                                                                   submissionId);
        return ResponseEntity.ok().body(managementStatus);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/teacher/submissions/{submissionId}/enable")
    public ResponseEntity<?> teacherEnableSubmission(Principal principal, @PathVariable UUID submissionId) {
        ContestSubmissionEntity managementStatus = problemTestCaseService.teacherEnableSubmission(principal.getName(),
                                                                                                  submissionId);
        return ResponseEntity.ok().body(managementStatus);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/submissions/{submissionId}")
    public ResponseEntity<?> getTestCasesResult(
        @PathVariable UUID submissionId
    ) {
        List<SubmissionDetailByTestcaseOM> result = problemTestCaseService
            .getSubmissionDetailByTestcase(submissionId, null);
        return ResponseEntity.ok().body(result);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/submissions/{submissionId}/testcases/{testcaseId}")
    public ResponseEntity<?> getTestCasesResultDetail(
        @PathVariable UUID submissionId, @PathVariable UUID testcaseId
    ) {
        List<SubmissionDetailByTestcaseOM> result = problemTestCaseService
            .getSubmissionDetailByTestcase(submissionId, testcaseId);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/student/submissions/{submissionId}")
    public ResponseEntity<?> getContestProblemSubmissionDetailByTestCaseOfASubmissionViewedByParticipant(
        Principal principal, @PathVariable UUID submissionId
    ) {
        List<SubmissionDetailByTestcaseOM> retLst;
        try {
            retLst = problemTestCaseService
                .getParticipantSubmissionDetailByTestCase(
                    principal.getName(),
                    submissionId);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
        return ResponseEntity.ok().body(retLst);
    }

    @GetMapping("/student/submissions/{submissionId}/general-info")
    public ResponseEntity<?> getContestSubmissionDetailViewedByParticipant(
        Principal principal,
        @PathVariable("submissionId") UUID submissionId
    ) {
        ContestSubmissionEntity contestSubmission;
        try {
            contestSubmission = problemTestCaseService.getContestSubmissionDetailForStudent(
                principal.getName(), submissionId);
            if(contestSubmission != null){
                String contestId = contestSubmission.getContestId();
                if(contestId != null) {
                    ContestEntity contest = contestRepo.findContestByContestId(contestId);
                    if(contest.getParticipantViewSubmissionMode() != null)
                        if(contest.getParticipantViewSubmissionMode().equals(ContestEntity.PARTICIPANT_VIEW_SUBMISSION_MODE_DISABLED)){
                            contestSubmission.setSourceCode("HIDDEN");
                        }
                }
            }
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }

        return ResponseEntity.status(200).body(contestSubmission);
    }

    @Async
    public void logTeacherViewDetailSubmissionOfStudentContest(String userId, String contestId, String problemId, String studentId, UUID submissionId){
        if(true)return;
        LmsLogModelCreate logM = new LmsLogModelCreate();
        logM.setUserId(userId);
        log.info("logTeacherViewDetailSubmissionOfStudentContest, userId = " + logM.getUserId());
        logM.setParam1(contestId);
        logM.setParam2(problemId);
        logM.setParam3(submissionId.toString());
        logM.setParam4(studentId);

        logM.setActionType("MANAGER_VIEW_DETAIL_A_SUBMISSION_OF_STUDENT_CONTEST");
        logM.setDescription("an user manager views detail of a submission of a student in a contest");
        apiService.callLogAPI("https://analytics.soict.ai/api/log/create-log",logM);
    }


    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/submissions/{submissionId}/general-info")
    public ResponseEntity<?> getContestSubmissionDetailViewedByManager(
        Principal principal,
        @PathVariable("submissionId") UUID submissionId
    ) {
        ContestSubmissionEntity contestSubmission = problemTestCaseService.getContestSubmissionDetailForTeacher(
            submissionId);

        logTeacherViewDetailSubmissionOfStudentContest(principal.getName(),
                                                       contestSubmission.getContestId(),
                                                       contestSubmission.getProblemId(),
                                                       contestSubmission.getUserId(),
                                                       contestSubmission.getContestSubmissionId());

        return ResponseEntity.status(200).body(contestSubmission);
    }
//    @Secured("ROLE_TEACHER")
//    @GetMapping("/teacher/submissions/{submissionId}/general-info")
//    public ResponseEntity<?> getContestSubmissionDetailViewedByManager(
//        @PathVariable("submissionId") UUID submissionId
//    ) {
//        ContestSubmissionEntity contestSubmission = problemTestCaseService.getContestSubmissionDetailForTeacher(
//            submissionId);
//        return ResponseEntity.status(200).body(contestSubmission);
//    }


    @GetMapping("/submissions/{submissionId}/contest")
    public ResponseEntity<?> getContestInfosOfASubmission(@PathVariable("submissionId") UUID submissionId) {
        ModelGetContestInfosOfSubmissionOutput res = problemTestCaseService.getContestInfosOfASubmission(submissionId);
        return ResponseEntity.ok().body(res);
    }

    @Secured("ROLE_TEACHER")
    @PutMapping("/submissions/source-code")
    public ResponseEntity<?> updateContestSubmissionSourceCode(
        @RequestBody ModelUpdateContestSubmission input
    ) {
        ContestSubmissionEntity sub = problemTestCaseService.updateContestSubmissionSourceCode(input);
        return ResponseEntity.ok().body(sub);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/submissions/{submissionId}/evaluation")
    public ResponseEntity<?> evaluateSubmission(@PathVariable UUID submissionId) {
        problemTestCaseService.evaluateSubmission(submissionId);
        return ResponseEntity.ok().body("ok");
    }
    @Secured("ROLE_TEACHER")
    //@PostMapping("/submissions/{contestId}/{problemId}/evaluation")
    //public ResponseEntity<?> evaluateSubmissionOfAProblemInContest(@PathVariable String contestId, @PathVariable String problemId) {
    @PostMapping("/submissions-of-a-problem-in-contest/rejudge")
    public ResponseEntity<?> evaluateSubmissionOfAProblemInContest(Principal principal, @RequestBody ModelInputRejudgeSubmissionsOfAProblemInContest m){
        log.info("evaluateSubmissionOfAProblemInContest, contestId = " + m.getContestId() + " and problemId = " + m.getProblemId());
        problemTestCaseService.evaluateSubmissions(m.getContestId(), m.getProblemId());

        return ResponseEntity.ok().body("ok");
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/submissions/{contestId}/batch-evaluation")
    public ResponseEntity<?> evaluateBatchSubmissionContest(@PathVariable String contestId) {
        log.info("evaluateBatchSubmissionContest, contestId = " + contestId);
        ModelEvaluateBatchSubmissionResponse res = problemTestCaseService.reJudgeAllSubmissionsOfContest(contestId);
        return ResponseEntity.ok().body(res);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/submissions/{contestId}/batch-non-evaluated-evaluation")
    public ResponseEntity<?> evaluateBatchNotEvaluatedSubmissionContest(
        @PathVariable String contestId
    ) {
        log.info("evaluateBatchNotEvaluatedSubmissionContest, contestId = " + contestId);
        ModelEvaluateBatchSubmissionResponse res = problemTestCaseService.judgeAllSubmissionsOfContest(contestId);
        return ResponseEntity.ok().body(res);
    }

//    @PostMapping("/submissions/testcases/solution-output")
//    public ResponseEntity<?> submitSolutionOutputOfATestCase(
//        Principal principale,
//        @RequestParam("inputJson") String inputJson,
//        @RequestParam("file") MultipartFile file
//    ) {
//        Gson gson = new Gson();
//        ModelSubmitSolutionOutputOfATestCase model = gson.fromJson(
//            inputJson,
//            ModelSubmitSolutionOutputOfATestCase.class);
//        try {
//            ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
//            String solutionOutput = IOUtils.toString(stream, StandardCharsets.UTF_8);
//            stream.close();
//
//            ModelContestSubmissionResponse resp = problemTestCaseService.submitSolutionOutputOfATestCase(
//                principale.getName(),
//                solutionOutput,
//                model
//            );
//            log.info("resp {}", resp);
//            return ResponseEntity.status(200).body(resp);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ResponseEntity.ok().body("OK");
//
//    }
//
//    @PostMapping("/submissions/solution-output")
//    public ResponseEntity<?> submitSolutionOutput(
//        Principal principale,
//        @RequestParam("inputJson") String inputJson,
//        @RequestParam("file") MultipartFile file
//    ) {
//        log.info("submitSolutionOutput, inputJson = " + inputJson);
//        Gson gson = new Gson();
//        ModelSubmitSolutionOutput model = gson.fromJson(inputJson, ModelSubmitSolutionOutput.class);
//        try {
//            ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
//            String solutionOutput = IOUtils.toString(stream, StandardCharsets.UTF_8);
//            stream.close();
//
//            ModelContestSubmissionResponse resp = problemTestCaseService.submitSolutionOutput(
//                solutionOutput,
//                model.getContestId(),
//                model.getProblemId(),
//                model.getTestCaseId(),
//                principale.getName());
//            return ResponseEntity.status(200).body(resp);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ResponseEntity.ok().body("OK");
//    }

    @Async
    public void logStudentSubmitToAContest(String userId, String contestId,
                                           ModelContestSubmitProgramViaUploadFile model){
        if(true)return;
        LmsLogModelCreate logM = new LmsLogModelCreate();

        logM.setUserId(userId);
        log.info("logStudentSubmitToAContest, userId = " + logM.getUserId());
        logM.setParam1(contestId);
        logM.setParam2(model.getProblemId());
        logM.setParam3(model.getLanguage());

        logM.setActionType("PARTICIPANT_SUBMIT_SOLUTION_CODE_TO_CONTEST");
        logM.setDescription("a participant submit solution code to a contest");
        apiService.callLogAPI("https://analytics.soict.ai/api/log/create-log",logM);
    }



    @PostMapping("/submissions/file-upload")
    public ResponseEntity<?> contestSubmitProblemViaUploadFileV3(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {


        return contestSubmitProblemViaUploadFileV2(principal, inputJson, file);
    }

    public ResponseEntity<?> contestSubmitProblemViaUploadFileV2(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {

        String userId = principal.getName();
        Gson gson = new Gson();
        ModelContestSubmitProgramViaUploadFile model = gson.fromJson(
            inputJson,
            ModelContestSubmitProgramViaUploadFile.class);

        logStudentSubmitToAContest(principal.getName(), model.getContestId(), model);

        ContestEntity contestEntity = contestRepo.findContestByContestId(model.getContestId());
        ContestProblem cp = contestProblemRepo.findByContestIdAndProblemId(model.getContestId(), model.getProblemId());
        List<String> languagesAllowed = contestEntity.getListLanguagesAllowedInContest();
        boolean languageOK = false;
        for(String l: languagesAllowed){
            if(l.equals(model.getLanguage())){
                languageOK = true; break;
            }
        }
        if(!languageOK){
            ModelContestSubmissionResponse resp = buildSubmissionNotLegalLanguage(model.getLanguage());
            //log.info("contestSubmitProblemViaUploadFileV2, not legal language " + model.getLanguage());
            return ResponseEntity.ok().body(resp);
        }else{
            //log.info("contestSubmitProblemViaUploadFileV2, legal language " + model.getLanguage());

        }
        if (!contestEntity.getStatusId().equals(ContestEntity.CONTEST_STATUS_RUNNING)) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseTimeOut();
            return ResponseEntity.ok().body(resp);
        }

        List<UserRegistrationContestEntity> userRegistrations = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndStatusAndRoleId(
                model.getContestId(),
                userId,
                UserRegistrationContestEntity.STATUS_SUCCESSFUL,
                UserRegistrationContestEntity.ROLE_PARTICIPANT);

        Boolean contestPublic = contestEntity.getContestPublic();
        if (!Boolean.TRUE.equals(contestPublic) || userRegistrations == null ) {
            if(userRegistrations.isEmpty()){
                ModelContestSubmissionResponse resp = buildSubmissionResponseNotRegistered();
                return ResponseEntity.ok().body(resp);
            }

        }

        for (UserRegistrationContestEntity u : userRegistrations) {
            if (u.getPermissionId() != null
                && u.getPermissionId().equals(UserRegistrationContestEntity.PERMISSION_FORBIDDEN_SUBMIT)) {
                ModelContestSubmissionResponse resp = buildSubmissionResponseNoPermission();
                return ResponseEntity.ok().body(resp);
            }
        }

        if (cp != null &&
            cp.getSubmissionMode() != null &&
            cp.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_NOT_ALLOWED)) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseSubmissionNotAllowed();
            return ResponseEntity.ok().body(resp);


        }


        int numOfSubmissions = contestSubmissionRepo
            .countAllByContestIdAndUserIdAndProblemId(model.getContestId(), userId, model.getProblemId());
        if (numOfSubmissions >= contestEntity.getMaxNumberSubmissions()) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseReachMaxSubmission(contestEntity.getMaxNumberSubmissions());
            return ResponseEntity.ok().body(resp);
        }

        long submissionInterval = contestEntity.getMinTimeBetweenTwoSubmissions();
        if (submissionInterval > 0) {
            Date now = new Date();
            Long lastSubmitTime = cacheService.findUserLastProblemSubmissionTimeInCache(model.getProblemId(), userId);
            if (lastSubmitTime != null) {
                long diffBetweenNowAndLastSubmit = now.getTime() - lastSubmitTime;
                if (diffBetweenNowAndLastSubmit < submissionInterval * 1000) {
                    ModelContestSubmissionResponse resp = buildSubmissionResponseNotEnoughTimeBetweenSubmissions(
                        submissionInterval);
                    return ResponseEntity.ok().body(resp);
                }
            }
            cacheService.addUserLastProblemSubmissionTimeToCache(model.getProblemId(), userId);
        }

        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
            String source = IOUtils.toString(stream, StandardCharsets.UTF_8);
            stream.close();

            if (source.length() > contestEntity.getMaxSourceCodeLength()) {
                ModelContestSubmissionResponse resp = buildSubmissionResponseReachMaxSourceLength(
                    source.length(),
                    contestEntity.getMaxSourceCodeLength());
                return ResponseEntity.ok().body(resp);
            }


            ModelContestSubmission request = new ModelContestSubmission(model.getContestId(), model.getProblemId(),
                                                                        source, model.getLanguage());
            ModelContestSubmissionResponse resp = null;

            if(cp != null && cp.getForbiddenInstructions() != null){
                log.info("contestSubmitProblemViaUploadFileV2, forbidden instructions = " + cp.getForbiddenInstructions());
                String[] fis = cp.getForbiddenInstructions().split(",");
                boolean ok = true;
                if(fis != null)for(String fi: fis){
                    String i = fi.trim();
                    log.info("contestSubmitProblemViaUploadFileV2, forbidden instructions i = " + i + " source = " + source);
                    if(i != null){
                        if(!i.equals("") && i.length() > 0 && source.contains(i)){
                            log.info("contestSubmitProblemViaUploadFileV2, has forbidden instructions i = " + i + " source = " + source);

                            ok = false; break;
                        }
                    }
                }
                if(!ok){
                    resp = problemTestCaseService.submitContestProblemNotExecuteDueToForbiddenInstructions(request, userId, userId);

                    return ResponseEntity.ok().body(resp);
                }
            }
            if (contestEntity.getSubmissionActionType()
                             .equals(ContestEntity.CONTEST_SUBMISSION_ACTION_TYPE_STORE_AND_EXECUTE)) {
                if (cp != null &&
                    cp.getSubmissionMode() != null &&
                    cp.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_SOLUTION_OUTPUT)) {
                    resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(request, userId, userId);
                } else {
                    log.info("contestSubmitProblemViaUploadFileV2, SUBMIT NORMAL");
                    resp = problemTestCaseService.submitContestProblemTestCaseByTestCaseWithFile(
                        request,
                        userId,
                        userId);
                }
            } else {
                resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(request, userId, userId);
            }

            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("OK");
    }

    private ModelContestSubmissionResponse buildSubmissionResponseTimeOut() {
        return ModelContestSubmissionResponse.builder()
                                             .status("TIME_OUT")
                                             .testCasePass("0")
                                             .runtime(0L)
                                             .memoryUsage((float) 0)
                                             .problemName("")
                                             .contestSubmissionID(null)
                                             .submittedAt(null)
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }
    private ModelContestSubmissionResponse buildSubmissionNotLegalLanguage(String lang) {
        return ModelContestSubmissionResponse.builder()
                                             //.status("ILLEGAL LANGUAGE " + lang)
                                             .status("ILLEGAL_LANGUAGE")
                                             .message("Illegal language " + lang)
                                             .testCasePass("0")
                                             .runtime(0L)
                                             .memoryUsage((float) 0)
                                             .problemName("")
                                             .contestSubmissionID(null)
                                             .submittedAt(null)
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseProblemNotFound() {
        return ModelContestSubmissionResponse.builder()
                                             .status("PROBLEM_NO_FOUND")
                                             .testCasePass("0")
                                             .runtime(0L)
                                             .memoryUsage((float) 0)
                                             .problemName("")
                                             .contestSubmissionID(null)
                                             .submittedAt(null)
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseInvalidFilename(String fn) {
        return ModelContestSubmissionResponse.builder()
                                             .status("Invalid filename " + fn)
                                             .testCasePass("0")
                                             .runtime(0L)
                                             .memoryUsage((float) 0)
                                             .problemName("")
                                             .contestSubmissionID(null)
                                             .submittedAt(null)
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseNotRegistered() {
        return ModelContestSubmissionResponse.builder()
                                             .status("PARTICIPANT_NOT_APPROVED_OR_REGISTERED")
                                             .message("Participant is not approved or not registered")
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseNoPermission() {
        return ModelContestSubmissionResponse.builder()
                                             .status("PARTICIPANT_HAS_NOT_PERMISSION_TO_SUBMIT")
                                             .message("Participant has no permission to submit")
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseSubmissionNotAllowed() {
        return ModelContestSubmissionResponse.builder()
                                             .status("SUBMISSION_NOT_ALLOWED")
                                             .message(
                                                 "This problem is not opened for submitting solution")
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseReachMaxSubmission(int maxNumberSubmission) {
        return ModelContestSubmissionResponse.builder()
                                             .status("MAX_NUMBER_SUBMISSIONS_REACHED")
                                             .message("Maximum Number of Submissions " + maxNumberSubmission
                                                      + " Reached! Cannot submit more")
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseReachMaxSourceLength(
        int sourceLength,
        int maxLength
    ) {
        return ModelContestSubmissionResponse.builder()
                                             .status("MAX_SOURCE_CODE_LENGTH_VIOLATIONS")
                                             .message("Max source code length violations " + sourceLength + " exceeded "
                                                      + maxLength + " ")
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseNotEnoughTimeBetweenSubmissions(long interval) {
        return ModelContestSubmissionResponse.builder()
                                             .status("SUBMISSION_INTERVAL_VIOLATIONS")
                                             .message("Not enough time between 2 submissions (" + interval + "s) ")
                                             .build();
    }

    @PostMapping("/teacher/submissions/participant-code")
    public ResponseEntity<?> ManagerSubmitCodeOfParticipant(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelInputManagerSubmitCodeOfParticipant model = gson.fromJson(
            inputJson,
            ModelInputManagerSubmitCodeOfParticipant.class);
        ContestEntity contestEntity = contestRepo.findContestByContestId(model.getContestId());
        String filename = file.getOriginalFilename();
        log.info("ManagerSubmitCodeOfParticipant, filename = " + file.getOriginalFilename());
        String[] s = filename.split("\\.");
        log.info("ManagerSubmitCodeOfParticipant, extract from filename, s.length = " + s.length);
        if (s.length < 2) {
            return ResponseEntity.ok().body("Filename " + filename + " Invalid");
        }
        String language = s[1].trim();
        if (language.equals("cpp")) {
            language = ContestSubmissionEntity.LANGUAGE_CPP;
        } else if (language.equals("java")) {
            language = ContestSubmissionEntity.LANGUAGE_JAVA;
        } else if (language.equals("py")) {
            language = ContestSubmissionEntity.LANGUAGE_PYTHON;
        }

        String[] s1 = s[0].split("_");
        log.info("ManagerSubmitCodeOfParticipant, extract from filename, s[0] = " + s[0] + " s1 = " + s1.length);
        if (s1.length < 2) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseInvalidFilename(filename);
            return ResponseEntity.ok().body(resp);
        }
        String userId = s1[0].trim();
        String problemCode = s1[1].trim();
        String contestId = model.getContestId();
        String problemId = null;
        ContestProblem cp = contestProblemRepo.findByContestIdAndProblemRecode(contestId, problemCode);

        if (cp != null) {
            problemId = cp.getProblemId();
        } else {
            log.info("ManagerSubmitCodeOfParticipant, not found problem of code " + problemCode);
            ModelContestSubmissionResponse resp = buildSubmissionResponseProblemNotFound();
            return ResponseEntity.ok().body(resp);
        }
        if (!contestEntity.getStatusId().equals(ContestEntity.CONTEST_STATUS_RUNNING)) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseTimeOut();
            return ResponseEntity.ok().body(resp);
        }

        boolean contestPublic = contestEntity.getContestPublic();
        List<UserRegistrationContestEntity> userRegistrations = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndStatusAndRoleId(
                model.getContestId(),
                userId,
                UserRegistrationContestEntity.STATUS_SUCCESSFUL,
                UserRegistrationContestEntity.ROLE_PARTICIPANT);
        if (!Boolean.TRUE.equals(contestPublic) || userRegistrations == null ) {
            if(userRegistrations.isEmpty()){
                ModelContestSubmissionResponse resp = buildSubmissionResponseNotRegistered();
                return ResponseEntity.ok().body(resp);
            }

        }

        for (UserRegistrationContestEntity u : userRegistrations) {
            if (u.getPermissionId() != null
                && u.getPermissionId().equals(UserRegistrationContestEntity.PERMISSION_FORBIDDEN_SUBMIT)) {
                ModelContestSubmissionResponse resp = buildSubmissionResponseNoPermission();
                return ResponseEntity.ok().body(resp);
            }
        }

        int numOfSubmissions = contestSubmissionRepo
            .countAllByContestIdAndUserIdAndProblemId(model.getContestId(), userId, problemId);
        if (numOfSubmissions >= contestEntity.getMaxNumberSubmissions()) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseReachMaxSubmission(contestEntity.getMaxNumberSubmissions());
            return ResponseEntity.ok().body(resp);
        }

        long submissionInterval = contestEntity.getMinTimeBetweenTwoSubmissions();
        if (submissionInterval > 0) {
            Date now = new Date();
            Long lastSubmitTime = cacheService.findUserLastProblemSubmissionTimeInCache(problemId, userId);
            if (lastSubmitTime != null) {
                long diffBetweenNowAndLastSubmit = now.getTime() - lastSubmitTime;
                if (diffBetweenNowAndLastSubmit < submissionInterval * 1000) {
                    ModelContestSubmissionResponse resp = buildSubmissionResponseNotEnoughTimeBetweenSubmissions(
                        submissionInterval);
                    return ResponseEntity.ok().body(resp);
                }
            }
            cacheService.addUserLastProblemSubmissionTimeToCache(problemId, userId);
        }

        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
            String source = IOUtils.toString(stream, StandardCharsets.UTF_8);
            stream.close();

            if (source.length() > contestEntity.getMaxSourceCodeLength()) {
                ModelContestSubmissionResponse resp = buildSubmissionResponseReachMaxSourceLength(
                    source.length(),
                    contestEntity.getMaxSourceCodeLength());
                return ResponseEntity.ok().body(resp);
            }
            ModelContestSubmission request = new ModelContestSubmission(model.getContestId(), problemId,
                                                                        source, language);
            ModelContestSubmissionResponse resp = null;
            if (contestEntity.getSubmissionActionType()
                             .equals(ContestEntity.CONTEST_SUBMISSION_ACTION_TYPE_STORE_AND_EXECUTE)) {
                if (cp.getSubmissionMode() != null &&
                    cp.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_SOLUTION_OUTPUT)) {
                    resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(
                        request,
                        userId,
                        principal.getName());
                } else {
                    resp = problemTestCaseService.submitContestProblemTestCaseByTestCaseWithFile(
                        request,
                        userId,
                        principal.getName());
                }
            } else {
                resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(
                    request,
                    userId,
                    principal.getName());
            }
            log.info("ManagerSubmitCodeOfParticipant, submitted successfully");
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/submissions/users/{userLoginId}")
    public ResponseEntity<?> getContestSubmissionPagingOfAUser(
        @PathVariable("userLoginId") String userLoginId,
        Pageable pageable
    ) {
        log.info("getContestSubmissionPagingOfAUser, user = " + userLoginId);
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        Page<ContestSubmission> page = problemTestCaseService.findContestSubmissionByUserLoginIdPaging(
            pageable,
            userLoginId);
        log.info("page {}", page);
        return ResponseEntity.status(200).body(page);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/teacher/submissions/{submissionId}/comments")
    public ResponseEntity<?> postComment(
        @PathVariable UUID submissionId,
        @RequestBody @Valid ModelContestSubmissionComment modelContestSubmissionComment,
        Principal principal
    ) throws Exception {
        log.info("postComment for submissionId {}: {}", submissionId, modelContestSubmissionComment);

        ContestSubmissionComment comment = contestSubmissionCommentService.postComment(
            submissionId,
            modelContestSubmissionComment,
            principal.getName()
        );

        return ResponseEntity.status(200).body(comment);
    }

    @GetMapping("/submissions/{submissionId}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable UUID submissionId) {
        ContestSubmissionEntity submission = contestSubmissionService.getSubmissionById(submissionId);

        String contestId = submission.getContestId();

        ContestEntity contest = contestService.findContest(contestId);

        if (!"Y".equals(contest.getContestShowComment())) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<CommentDTO> comments = commentService.getAllCommentsBySubmissionId(submissionId);

        Collections.reverse(comments);

        return ResponseEntity.ok(comments);
    }
    @PostMapping("/get-contest-submissions-date-between")
    public ResponseEntity<?> getContestSubmissionDateBetween(Principal principal, @RequestBody
    ModelResponseGetContestSubmissionOfPeriod model
    ){
        List<ContestSubmissionEntity> res = contestSubmissionRepo.findAllByCreatedAtBetween(model.getFromDateTime(), model.getToDateTime());
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/get-contest-submissions-page-date-between")
    public ResponseEntity<?> getContestSubmissionPageDateBetween(Principal principal, @RequestBody
                                                                 ModelInputGetContestSubmissionPageOfPeriod m
    ){
        List<ContestSubmissionEntity> L = contestSubmissionRepo.findPageByCreatedAtBetween(m.getFromDate(),m.getToDate(),m.getOffset(),m.getLimit());
        log.info("getContestSubmissionPageDateBetween, from {} to {}, Limit {} offset = {}, GOT sz = {}",m.getFromDate(),m.getToDate(),m.getLimit(),m.getOffset(),L.size());
        ModelResponseGetContestSubmissionPage res = new ModelResponseGetContestSubmissionPage(L);
         return ResponseEntity.ok().body(res);
    }


//    @Secured("ROLE_TEACHER")
//    @PutMapping("/teacher/submissions/{submissionId}/comments/{commentId}")
//    public ResponseEntity<?> updateComment(
//        @PathVariable UUID submissionId,
//        @PathVariable UUID commentId,
//        @RequestBody ContestSubmissionComment updatedComment
//    ) {
//        ContestSubmissionComment existingComment = contestSubmissionCommentRepo.findById(commentId)
//                                                                               .orElseThrow(() -> new RuntimeException("Comment not found"));
//
//        existingComment.setComment(updatedComment.getComment());
//        existingComment.setLastUpdatedStamp(new Date());
//
//        ContestSubmissionComment savedComment = contestSubmissionCommentRepo.save(existingComment);
//        return ResponseEntity.ok().body(savedComment);
//    }
}
