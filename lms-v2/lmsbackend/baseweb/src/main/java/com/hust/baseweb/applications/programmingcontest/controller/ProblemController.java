package com.hust.baseweb.applications.programmingcontest.controller;

import com.hust.baseweb.applications.chatgpt.ChatGPTService;
import com.hust.baseweb.applications.programmingcontest.callexternalapi.model.LmsLogModelCreate;
import com.hust.baseweb.applications.programmingcontest.callexternalapi.service.ApiService;
import com.hust.baseweb.applications.programmingcontest.entity.ProblemEntity;
import com.hust.baseweb.applications.programmingcontest.entity.TagEntity;
import com.hust.baseweb.applications.programmingcontest.entity.UserContestProblemRole;
import com.hust.baseweb.applications.programmingcontest.exception.MiniLeetCodeException;
import com.hust.baseweb.applications.programmingcontest.model.*;
import com.hust.baseweb.applications.programmingcontest.model.externalapi.ContestProblemModelResponse;
import com.hust.baseweb.applications.programmingcontest.model.externalapi.GetSubmissionsOfParticipantModelInput;
import com.hust.baseweb.applications.programmingcontest.model.externalapi.SubmissionModelResponse;
import com.hust.baseweb.applications.programmingcontest.repo.ProblemRepo;
import com.hust.baseweb.applications.programmingcontest.repo.TestCaseRepo;
import com.hust.baseweb.applications.programmingcontest.repo.UserContestProblemRoleRepo;
import com.hust.baseweb.applications.programmingcontest.service.ProblemTestCaseService;
import com.hust.baseweb.model.ProblemFilter;
import com.hust.baseweb.model.TestCaseFilter;
import com.hust.baseweb.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProblemController {

    TestCaseRepo testCaseRepo;

    ProblemTestCaseService problemTestCaseService;

    UserService userService;

    UserContestProblemRoleRepo userContestProblemRoleRepo;

    ProblemRepo problemRepo;

    ChatGPTService chatGPTService;

    ApiService apiService;

    @Secured("ROLE_TEACHER")
    @PostMapping(value = "/problems", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createProblem(
        Principal principal,
        @RequestPart("dto") ModelCreateContestProblem dto,
        @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        return ResponseEntity.ok().body(problemTestCaseService.createContestProblem(principal.getName(), dto, files));
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/problems/general-info")
    public ResponseEntity<?> getAllContestProblemsGeneralInfo() {
        return ResponseEntity.ok().body(problemTestCaseService.getAllProblemsGeneralInfo());
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/problems/generate-statement")
    public ResponseEntity<?> suggestProblemStatement(
        @RequestBody ProblemSuggestionRequest suggestion
    ) {
        String problemStatement = chatGPTService.getChatGPTAnswer(suggestion.generateRequest());
        return ResponseEntity.status(200).body(problemStatement);
    }

    @Async
    public void logManagerGetProblemDetail(String userId, String problemId){
        if(true)return;
        LmsLogModelCreate logM = new LmsLogModelCreate();
        logM.setUserId(userId);
        log.info("logManagerGetProblemDetail, userId = " + logM.getUserId());
        logM.setParam1(problemId);


        logM.setActionType("MANAGER_VIEW_PROBLEM_DETAIL");
        logM.setDescription("a manager view problem detail");
        apiService.callLogAPI("https://analytics.soict.ai/api/log/create-log",logM);
    }


    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/problems/{problemId}")
    public ResponseEntity<?> getProblemDetailViewByTeacher(
        @PathVariable("problemId") String problemId,
        Principal teacher
    ) throws Exception {
        try {

            logManagerGetProblemDetail(teacher.getName(),problemId);

            ModelCreateContestProblemResponse problemResponse = problemTestCaseService.getContestProblemDetailByIdAndTeacher(
                problemId,
                teacher.getName());
            return ResponseEntity.status(200).body(problemResponse);
        } catch (MiniLeetCodeException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        }
    }

    @Secured("ROLE_TEACHER")
    @PutMapping(value = "/problems/{problemId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateProblem(
        Principal principal,
        @PathVariable("problemId") String problemId,
        @RequestPart("dto") ModelUpdateContestProblem dto,
        @RequestPart("files") MultipartFile[] files
    ) throws Exception {
        return ResponseEntity.ok().body(problemTestCaseService.updateContestProblem(
            problemId,
            principal.getName(),
            dto,
            files));
    }

    @PostMapping("/check-compile")
    public ResponseEntity<?> checkCompile(@RequestBody ModelCheckCompile modelCheckCompile, Principal principal)
        throws Exception {
        ModelCheckCompileResponse resp = problemTestCaseService.checkCompile(modelCheckCompile, principal.getName());
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/tags")
    public ResponseEntity<?> getAllTags() {
        List<TagEntity> tags = problemTestCaseService.getAllTags();
        return ResponseEntity.ok().body(tags);
    }

    @PostMapping("/tags")
    public ResponseEntity<?> addNewTag(@RequestBody ModelTag tagInput) {

        TagEntity tag = problemTestCaseService.addNewTag(tagInput);

        return ResponseEntity.status(200).body(tag);
    }

    @GetMapping("/test-jmeter")
    public ResponseEntity<?> testJmeter(@RequestParam String s) {
        s = s.concat("Hello");
        return ResponseEntity.ok().body(s);
    }
    @Secured("ROLE_TEACHER")
    @GetMapping("/problems/{problemId}/testcases")
    public ResponseEntity<?> getTestCaseListByProblem(@PathVariable("problemId") String problemId, TestCaseFilter filter) {
        return ResponseEntity.ok().body(problemTestCaseService.getTestCaseByProblem(problemId, filter));
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/problems/{problemId}/contests")
    public ResponseEntity<?> getContestsUsingAProblem(@PathVariable String problemId) {
        List<ModelGetContestResponse> res = problemTestCaseService.getContestsUsingAProblem(problemId);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/problems/{problemId}/testcases/{testCaseId}/solution")
    public ResponseEntity<?> rerunCreateTestCaseSolution(
        @PathVariable String problemId,
        @PathVariable UUID testCaseId
    ) throws Exception {
        log.info("rerunCreateTestCaseSolution problem " + problemId + " testCaseId " + testCaseId);
        return ResponseEntity.ok().body(problemTestCaseService.reCreateTestcaseCorrectAnswer(problemId, testCaseId));
    }

    @GetMapping("/problems/{problemId}/users/role")
    public ResponseEntity<?> getUserContestProblemRoles(@PathVariable String problemId) {
        List<ModelResponseUserProblemRole> res = problemTestCaseService.getUserProblemRoles(problemId);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/problems/users/role")
    public ResponseEntity<?> addContestProblemRole(Principal principal, @RequestBody ModelUserProblemRole input) {
        try {
            boolean ok = problemTestCaseService.addUserProblemRole(principal.getName(), input);
            return ResponseEntity.ok().body(ok);
        } catch (Exception e) {
            if (e instanceof MiniLeetCodeException) {
                return ResponseEntity.status(((MiniLeetCodeException) e).getCode()).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @DeleteMapping("/problems/users/role")
    public ResponseEntity<?> removeContestProblemRole(Principal principal, @RequestBody ModelUserProblemRole input) {
        try {
            //log.info("removeContestProblemRole, remove user " + input.getUserId() + " with role " + input.getRoleId() + " from the problem " + input.getProblemId());
            if(principal.getName().equals(input.getUserId())){// current userlogin cannot remove himself from the problem
                return ResponseEntity.ok().body(false);
            }
            if(input.getRoleId().equals(UserContestProblemRole.ROLE_OWNER)){// cannot remove user who is the owner of the problem
                return ResponseEntity.ok().body(false);
            }

            boolean ok = problemTestCaseService.removeUserProblemRole(principal.getName(), input);
            return ResponseEntity.ok().body(ok);
        } catch (Exception e) {
            if (e instanceof MiniLeetCodeException) {
                return ResponseEntity.status(((MiniLeetCodeException) e).getCode()).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    //    @Secured("ROLE_TEACHER")
    @GetMapping(value = "/problems/{id}/export")
    public ResponseEntity<StreamingResponseBody> exportProblem(
        @PathVariable @NotBlank String id
    ) {
        StreamingResponseBody stream = outputStream -> problemTestCaseService.exportProblem(
            id,
            outputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + id + ".zip");

        return ResponseEntity.ok().headers(headers).body(stream);
    }

    @PostMapping(value = "/teachers/problems/clone")
    public ResponseEntity<?> cloneProblem(Principal principal, @RequestBody ModelCloneProblem cloneRequest) throws MiniLeetCodeException {
        return ResponseEntity.ok().body(problemTestCaseService.cloneProblem(principal.getName(), cloneRequest));
    }

    /**
     * @param owner
     * @param filter
     * @return
     */
    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/owned-problems")
    public ResponseEntity<?> getAllMyProblems(Principal owner, ProblemFilter filter) {
        return ResponseEntity.ok().body(this.problemTestCaseService.getProblems(owner.getName(), filter, null));
    }

    /**
     *
     * @param owner
     * @param filter
     * @return
     */
    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/shared-problems")
    public ResponseEntity<?> getAllSharedProblems(Principal owner, ProblemFilter filter) {
        return ResponseEntity.ok().body(this.problemTestCaseService.getSharedProblems(owner.getName(), filter));
    }

    /**
     *
     * @param owner
     * @param filter
     * @return
     */
    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/public-problems")
    public ResponseEntity<?> getPublicProblems(Principal owner, ProblemFilter filter) {
        return ResponseEntity.ok().body(this.problemTestCaseService.getPublicProblems(owner.getName(), filter));
    }

    //    @Secured("ROLE_TEACHER")
//    @GetMapping("/teacher/all-problems")
//    public List<ProblemEntity> getAllProblems(Principal owner) {
//        return this.problemTestCaseService.getAllProblems(owner.getName());
//    }
    //@Secured("ROLE_EXT_DATA_QUERY")
    @GetMapping("/extapi/all-problems")
    public ResponseEntity<?> extGetAllProblems(Principal principal){
        List<ContestProblemModelResponse> problems = problemTestCaseService.extApiGetAllProblems(principal.getName());
        return ResponseEntity.ok().body(problems);
    }
    @PostMapping("extapi/get-submissions-of-participant")
    public ResponseEntity<?> getSubmissionOf(Principal principal, @RequestBody GetSubmissionsOfParticipantModelInput I){
        String participantId = I.getParticipantId();
        List<SubmissionModelResponse> res = problemTestCaseService.extApiGetSubmissions(participantId);
        return ResponseEntity.ok().body(res);
    }
//    @GetMapping("/grant-owner-role-problem-to-admin")
//    public ResponseEntity<?> grantOwnerRoleProblemToAdmin(Principal principal){
//        List<ProblemEntity> probs = problemRepo.findAll();
//        for(ProblemEntity p: probs){
//            List<UserContestProblemRole> ucpr = userContestProblemRoleRepo
//                .findAllByProblemIdAndUserIdAndRoleId(p.getProblemId(),"admin", UserContestProblemRole.ROLE_OWNER);
//            if(ucpr == null || ucpr.size() == 0){
//                UserContestProblemRole r = new UserContestProblemRole();
//                r.setProblemId(p.getProblemId());
//                r.setUserId("admin");
//                r.setRoleId(UserContestProblemRole.ROLE_OWNER);
//                r.setCreatedStamp(new Date());
//
//                r = userContestProblemRoleRepo.save(r);
//                log.info("grantOwnerRoleProblemToAdmin grant OWNER role of problem " + p.getProblemId() + " to admin");
//            }
//        }
//
//        return ResponseEntity.ok().body("OK");
//    }
}
