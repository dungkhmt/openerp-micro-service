package com.hust.baseweb.applications.programmingcontest.controller;

import com.google.gson.Gson;
import com.hust.baseweb.applications.programmingcontest.entity.ContestEntity;
import com.hust.baseweb.applications.programmingcontest.entity.ContestProblem;
import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import com.hust.baseweb.applications.programmingcontest.entity.UserRegistrationContestEntity;
import com.hust.baseweb.applications.programmingcontest.model.*;
import com.hust.baseweb.applications.programmingcontest.repo.ContestProblemRepo;
import com.hust.baseweb.applications.programmingcontest.repo.ContestRepo;
import com.hust.baseweb.applications.programmingcontest.repo.ContestSubmissionRepo;
import com.hust.baseweb.applications.programmingcontest.repo.UserRegistrationContestRepo;
import com.hust.baseweb.applications.programmingcontest.service.ProblemTestCaseService;
import com.hust.baseweb.applications.programmingcontest.service.helper.cache.ProblemTestCaseServiceCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.Principal;
import java.util.*;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SubmissionController {

    ProblemTestCaseService problemTestCaseService;
    ContestRepo contestRepo;
    ContestSubmissionRepo contestSubmissionRepo;
    ContestProblemRepo contestProblemRepo;
    UserRegistrationContestRepo userRegistrationContestRepo;
    ProblemTestCaseServiceCache cacheService;

    @Secured("ROLE_TEACHER")
    @PostMapping("/teacher/submissions/{submissionId}/disable")
    public ResponseEntity<?> teacherDisableSubmission(Principal principal, @PathVariable UUID submissionId){
        ContestSubmissionEntity managementStatus = problemTestCaseService.teacherDisableSubmission(principal.getName(),submissionId);
        return ResponseEntity.ok().body(managementStatus);
    }
    @Secured("ROLE_TEACHER")
    @PostMapping("/teacher/submissions/{submissionId}/enable")
    public ResponseEntity<?> teacherEnableSubmission(Principal principal, @PathVariable UUID submissionId){
        ContestSubmissionEntity managementStatus = problemTestCaseService.teacherEnableSubmission(principal.getName(),submissionId);
        return ResponseEntity.ok().body(managementStatus);
    }


    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/submissions/{submissionId}")
    public ResponseEntity<?> getContestProblemSubmissionDetailByTestCaseOfASubmission(
        @PathVariable UUID submissionId
    ) {
        List<ModelProblemSubmissionDetailByTestCaseResponse> retLst = problemTestCaseService
            .getContestProblemSubmissionDetailByTestCaseOfASubmission(submissionId);
        return ResponseEntity.ok().body(retLst);
    }

    @GetMapping("/student/submissions/{submissionId}")
    public ResponseEntity<?> getContestProblemSubmissionDetailByTestCaseOfASubmissionViewedByParticipant(
        Principal principal, @PathVariable UUID submissionId
    ) {
        List<ModelProblemSubmissionDetailByTestCaseResponse> retLst;
        try {
            retLst = problemTestCaseService
                .getContestProblemSubmissionDetailByTestCaseOfASubmissionViewedByParticipant(
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
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }

        return ResponseEntity.status(200).body(contestSubmission);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/submissions/{submissionId}/general-info")
    public ResponseEntity<?> getContestSubmissionDetailViewedByManager(
        @PathVariable("submissionId") UUID submissionId
    ) {
        ContestSubmissionEntity contestSubmission = problemTestCaseService.getContestSubmissionDetailForTeacher(submissionId);
        return ResponseEntity.status(200).body(contestSubmission);
    }

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

    @PostMapping("/submissions/{submissionId}/evaluation")
    public ResponseEntity<?> evaluateSubmission(@PathVariable UUID submissionId) {
        problemTestCaseService.evaluateSubmission(submissionId);
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

    @PostMapping("/submissions/testcases/solution-output")
    public ResponseEntity<?> submitSolutionOutputOfATestCase(
        Principal principale,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelSubmitSolutionOutputOfATestCase model = gson.fromJson(
            inputJson,
            ModelSubmitSolutionOutputOfATestCase.class);
        try {
            StringBuilder solutionOutput = new StringBuilder();
            InputStream inputStream = file.getInputStream();
            Scanner in = new Scanner(inputStream);
            while (in.hasNext()) {
                String line = in.nextLine();
                solutionOutput.append(line).append("\n");
                //System.out.println("submitSolutionOutputOfATestCase: read line: " + line);
            }
            in.close();
            ModelContestSubmissionResponse resp = problemTestCaseService.submitSolutionOutputOfATestCase(
                principale.getName(),
                solutionOutput.toString(),
                model
            );
            log.info("resp {}", resp);
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("OK");

    }

    @PostMapping("/submissions/solution-output")
    public ResponseEntity<?> submitSolutionOutput(
        Principal principale,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        log.info("submitSolutionOutput, inputJson = " + inputJson);
        Gson gson = new Gson();
        ModelSubmitSolutionOutput model = gson.fromJson(inputJson, ModelSubmitSolutionOutput.class);
        try {
            StringBuilder solutionOutput = new StringBuilder();
            InputStream inputStream = file.getInputStream();
            Scanner in = new Scanner(inputStream);
            while (in.hasNext()) {
                String line = in.nextLine();
                solutionOutput.append(line).append("\n");
                System.out.println("submitSolutionOutput: read line: " + line);
            }
            in.close();
            ModelContestSubmissionResponse resp = problemTestCaseService.submitSolutionOutput(
                solutionOutput.toString(),
                model.getContestId(),
                model.getProblemId(),
                model.getTestCaseId(),
                principale.getName());
            log.info("resp {}", resp);
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("OK");
    }

    @PostMapping("/submissions/file-upload")
    public ResponseEntity<?> contestSubmitProblemViaUploadFileV3(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelContestSubmitProgramViaUploadFile model = gson.fromJson(
            inputJson,
            ModelContestSubmitProgramViaUploadFile.class);
        ContestEntity contestEntity = contestRepo.findContestByContestId(model.getContestId());
        if (contestEntity.getJudgeMode() != null &&
            contestEntity.getJudgeMode().equals(ContestEntity.ASYNCHRONOUS_JUDGE_MODE_QUEUE)) {
            return contestSubmitProblemViaUploadFileV2(principal, inputJson, file);
        }
        return contestSubmitProblemViaUploadFile(principal, inputJson, file);
    }

    public ResponseEntity<?> contestSubmitProblemViaUploadFile(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        //log.info("contestSubmitProblemViaUploadFile, inputJson = " + inputJson);
        Gson gson = new Gson();
        ModelContestSubmitProgramViaUploadFile model = gson.fromJson(
            inputJson,
            ModelContestSubmitProgramViaUploadFile.class);
        ContestEntity contestEntity = contestRepo.findContestByContestId(model.getContestId());
        ContestProblem cp = contestProblemRepo.findByContestIdAndProblemId(model.getContestId(), model.getProblemId());
        Date currentDate = new Date();
        int timeTest = ((int) (currentDate.getTime() - contestEntity.getStartedAt().getTime())) /
                       (60 * 1000); // minutes
        // System.out.println(currentDate);
        // System.out.println(testStartDate);
        // System.out.println(timeTest);
        // System.out.println(test.getDuration());
        //log.info("contestSubmitProblemViaUploadFile, currentDate = " + currentDate + ", contest started at"
        //        + contestEntity.getStartedAt()
        //        + " timeTest = " + timeTest + " contestSolvingTime = " + contestEntity.getContestSolvingTime());

        // if (timeTest > contestEntity.getContestSolvingTime()) {
        if (!contestEntity.getStatusId().equals(ContestEntity.CONTEST_STATUS_RUNNING)) {
            //log.info("contestSubmitProblemViaUploadFile, TIME OUT!!!!! currentDate = " + currentDate
            //        + ", contest started at" + contestEntity.getStartedAt()
            //        + " timeTest = " + timeTest + " contestSolvingTime = " +
            //       contestEntity.getContestSolvingTime());

            // return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);

            ModelContestSubmissionResponse resp = ModelContestSubmissionResponse.builder()
                                                                                .status("TIME_OUT")
                                                                                .testCasePass("0")
                                                                                .runtime(new Long(0))
                                                                                .memoryUsage(new Float(0))
                                                                                .problemName("")
                                                                                .contestSubmissionID(null)
                                                                                .submittedAt(null)
                                                                                .score(0L)
                                                                                .numberTestCasePassed(0)
                                                                                .totalNumberTestCase(0)
                                                                                .build();
            return ResponseEntity.ok().body(resp);
        }

        List<UserRegistrationContestEntity> userRegistrations = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndStatusAndRoleId(
                model.getContestId(),
                principal.getName(),
                UserRegistrationContestEntity.STATUS_SUCCESSFUL,
                UserRegistrationContestEntity.ROLE_PARTICIPANT);
        if (userRegistrations == null || userRegistrations.size() == 0) {
            ModelContestSubmissionResponse resp = ModelContestSubmissionResponse.builder()
                                                                                .status(
                                                                                    "PARTICIPANT_NOT_APPROVED_OR_REGISTERED")
                                                                                .message(
                                                                                    "Participant is not approved or not registered")
                                                                                .build();
            return ResponseEntity.ok().body(resp);

        }

        for (UserRegistrationContestEntity u : userRegistrations) {
            if (u.getPermissionId() != null
                && u.getPermissionId().equals(UserRegistrationContestEntity.PERMISSION_FORBIDDEN_SUBMIT)) {
                ModelContestSubmissionResponse resp = ModelContestSubmissionResponse.builder()
                                                                                    .status(
                                                                                        "PARTICIPANT_HAS_NOT_PERMISSION_TO_SUBMIT")
                                                                                    .message(
                                                                                        "Participant has not permission to submit")
                                                                                    .build();
                return ResponseEntity.ok().body(resp);

            }
        }

        List<ContestSubmissionEntity> submissions = contestSubmissionRepo
            .findAllByContestIdAndUserIdAndProblemId(model.getContestId(), principal.getName(),
                                                     model.getProblemId());

        if (cp != null &&
            cp.getSubmissionMode() != null &&
            cp.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_NOT_ALLOWED)) {
            ModelContestSubmissionResponse resp = ModelContestSubmissionResponse.builder()
                                                                                .status("SUBMISSION_NOT_ALLOWED")
                                                                                .message(
                                                                                    "This problem is not opened for submitting solution")
                                                                                .build();
            return ResponseEntity.ok().body(resp);
        }

        if (submissions.size() >= contestEntity.getMaxNumberSubmissions()) {
            ModelContestSubmissionResponse resp = ModelContestSubmissionResponse.builder()
                                                                                .status("MAX_NUMBER_SUBMISSIONS_REACHED")
                                                                                .message(
                                                                                    "Maximum Number of Submissions " +
                                                                                    contestEntity.getMaxNumberSubmissions()
                                                                                    + " Reached! Cannot submit more")
                                                                                .build();
            return ResponseEntity.ok().body(resp);
        }

        try {
            StringBuilder source = new StringBuilder();
            InputStream inputStream = file.getInputStream();
            Scanner in = new Scanner(inputStream);
            while (in.hasNext()) {
                String line = in.nextLine();
                source.append(line).append("\n");
            }
            in.close();

            if (source.length() > contestEntity.getMaxSourceCodeLength()) {
                ModelContestSubmissionResponse resp = ModelContestSubmissionResponse.builder()
                                                                                    .status(
                                                                                        "MAX_SOURCE_CODE_LENGTH_VIOLATIONS")
                                                                                    .message(
                                                                                        "Max source code length violations: " +
                                                                                        source.length() +
                                                                                        " exceeded")
                                                                                    .build();
                return ResponseEntity.ok().body(resp);
            }
            ModelContestSubmission request = new ModelContestSubmission(model.getContestId(), model.getProblemId(),
                                                                        source.toString(), model.getLanguage());
            ModelContestSubmissionResponse resp = null;
            if (contestEntity.getSubmissionActionType()
                             .equals(ContestEntity.CONTEST_SUBMISSION_ACTION_TYPE_STORE_AND_EXECUTE)) {
                if (cp != null &&
                    cp.getSubmissionMode() != null &&
                    cp.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_SOLUTION_OUTPUT)) {
                    //log.info("contestSubmitProblemViaUploadFile, mode submit output");
                    resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(
                        request,
                        principal.getName(),
                        principal.getName());
                } else {
                    resp = problemTestCaseService.submitContestProblemTestCaseByTestCase(
                        request,
                        principal.getName());

                }
            } else {
                resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(
                    request,
                    principal.getName(),
                    principal.getName());
            }
            //log.info("resp {}", resp);
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("OK");
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
        ContestEntity contestEntity = contestRepo.findContestByContestId(model.getContestId());
        ContestProblem cp = contestProblemRepo.findByContestIdAndProblemId(model.getContestId(), model.getProblemId());

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
        if (userRegistrations == null || userRegistrations.size() == 0) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseNotRegistered();
            return ResponseEntity.ok().body(resp);

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
            StringBuilder source = new StringBuilder();
            InputStream inputStream = file.getInputStream();
            Scanner in = new Scanner(inputStream);
            while (in.hasNext()) {
                String line = in.nextLine();
                source.append(line).append("\n");
            }
            in.close();

            if (source.length() > contestEntity.getMaxSourceCodeLength()) {
                ModelContestSubmissionResponse resp = buildSubmissionResponseReachMaxSourceLength(
                    source.length(),
                    contestEntity.getMaxSourceCodeLength());
                return ResponseEntity.ok().body(resp);
            }
            ModelContestSubmission request = new ModelContestSubmission(model.getContestId(), model.getProblemId(),
                                                                        source.toString(), model.getLanguage());
            ModelContestSubmissionResponse resp = null;
            if (contestEntity.getSubmissionActionType()
                             .equals(ContestEntity.CONTEST_SUBMISSION_ACTION_TYPE_STORE_AND_EXECUTE)) {
                if (cp != null &&
                    cp.getSubmissionMode() != null &&
                    cp.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_SOLUTION_OUTPUT)) {
                    resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(request, userId, userId);
                } else {
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

        List<UserRegistrationContestEntity> userRegistrations = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndStatusAndRoleId(
                model.getContestId(),
                userId,
                UserRegistrationContestEntity.STATUS_SUCCESSFUL,
                UserRegistrationContestEntity.ROLE_PARTICIPANT);
        if (userRegistrations == null || userRegistrations.size() == 0) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseNotRegistered();
            return ResponseEntity.ok().body(resp);

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
            StringBuilder source = new StringBuilder();
            InputStream inputStream = file.getInputStream();
            Scanner in = new Scanner(inputStream);
            while (in.hasNext()) {
                String line = in.nextLine();
                source.append(line).append("\n");
            }
            in.close();

            if (source.length() > contestEntity.getMaxSourceCodeLength()) {
                ModelContestSubmissionResponse resp = buildSubmissionResponseReachMaxSourceLength(
                    source.length(),
                    contestEntity.getMaxSourceCodeLength());
                return ResponseEntity.ok().body(resp);
            }
            ModelContestSubmission request = new ModelContestSubmission(model.getContestId(), problemId,
                                                                        source.toString(), language);
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

}
