package com.hust.baseweb.applications.programmingcontest.controller;

import com.hust.baseweb.applications.chatgpt.ChatGPTService;
import com.hust.baseweb.applications.programmingcontest.entity.ProblemEntity;
import com.hust.baseweb.applications.programmingcontest.entity.TagEntity;
import com.hust.baseweb.applications.programmingcontest.entity.UserContestProblemRole;
import com.hust.baseweb.applications.programmingcontest.exception.MiniLeetCodeException;
import com.hust.baseweb.applications.programmingcontest.model.*;
import com.hust.baseweb.applications.programmingcontest.repo.UserContestProblemRoleRepo;
import com.hust.baseweb.applications.programmingcontest.service.ProblemTestCaseService;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ProblemController {

    ProblemTestCaseService problemTestCaseService;
    UserService userService;
    UserContestProblemRoleRepo userContestProblemRoleRepo;
    ChatGPTService chatGPTService;

    @Secured("ROLE_TEACHER")
    @PostMapping("/problems")
    public ResponseEntity<?> createProblem(
        Principal principal,
        @RequestParam("ModelCreateContestProblem") String json,
        @RequestParam("files") MultipartFile[] files
    ) throws MiniLeetCodeException {
        ProblemEntity resp = problemTestCaseService.createContestProblem(principal.getName(), json, files);
        return ResponseEntity.status(200).body(resp);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/problems/general-info")
    public ResponseEntity<?> getAllContestProblemsGeneralInfo() {
        List<ModelProblemGeneralInfo> problems = problemTestCaseService.getAllProblemsGeneralInfo();
        return ResponseEntity.ok().body(problems);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/problems/generate-statement")
    public ResponseEntity<?> suggestProblemStatement(
        @RequestBody ProblemSuggestionRequest suggestion
    ) {
        String problemStatement = chatGPTService.getChatGPTAnswer(suggestion.generateRequest());
        return ResponseEntity.status(200).body(problemStatement);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/problems/{problemId}")
    public ResponseEntity<?> getProblemDetailViewByTeacher(
        @PathVariable("problemId") String problemId,
        Principal teacher
    ) throws Exception {
        try {
            ModelCreateContestProblemResponse problemResponse = problemTestCaseService.getContestProblemDetailByIdAndTeacher(
                problemId,
                teacher.getName());
            return ResponseEntity.status(200).body(problemResponse);
        } catch (MiniLeetCodeException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        }
    }

    @Secured("ROLE_TEACHER")
    @PutMapping("/problems/{problemId}")
    public ResponseEntity<?> updateProblem(
        @PathVariable("problemId") String problemId, Principal principal,
        @RequestParam("ModelUpdateContestProblem") String json, @RequestParam("files") MultipartFile[] files
    )
        throws Exception {
        List<UserContestProblemRole> L = userContestProblemRoleRepo.findAllByProblemIdAndUserId(
            problemId,
            principal.getName());
        boolean hasPermission = false;
        for (UserContestProblemRole e : L) {
            if (e.getRoleId().equals(UserContestProblemRole.ROLE_EDITOR) ||
                e.getRoleId().equals(UserContestProblemRole.ROLE_OWNER)) {
                hasPermission = true;
                break;
            }
        }
        if (!hasPermission) {
            return ResponseEntity.status(403).body("No permission");
        }
        ProblemEntity problemResponse = problemTestCaseService.updateContestProblem(
            problemId,
            principal.getName(),
            json,
            files);
        return ResponseEntity.status(HttpStatus.OK).body(problemResponse);
    }

    @PostMapping("/check-compile")
    public ResponseEntity<?> checkCompile(@RequestBody ModelCheckCompile modelCheckCompile, Principal principal)
        throws Exception {
        ModelCheckCompileResponse resp = problemTestCaseService.checkCompile(modelCheckCompile, principal.getName());
        return ResponseEntity.status(200).body(resp);
    }

    @GetMapping("/tags")
    public ResponseEntity<?> getAllTags() {

        List<TagEntity> listTag = problemTestCaseService.getAllTags();
        return ResponseEntity.status(200).body(listTag);
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

    @GetMapping("/problems/{problemId}/testcases")
    public ResponseEntity<?> getTestCaseListByProblem(@PathVariable("problemId") String problemId) {
        List<ModelGetTestCase> list = problemTestCaseService.getTestCaseByProblem(problemId);
        return ResponseEntity.status(200).body(list);
    }

    @GetMapping("/problems/{problemId}/contests")
    public ResponseEntity<?> getContestsUsingAProblem(@PathVariable String problemId) {
        List<ModelGetContestResponse> res = problemTestCaseService.getContestsUsingAProblem(problemId);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/problems/{problemId}/testcases/{testCaseId}/solution")
    public ResponseEntity<?> rerunCreateTestCaseSolution(
        Principal principal, @PathVariable String problemId,
        @PathVariable UUID testCaseId
    ) {
        log.info("rerunCreateTestCaseSolution problem " + problemId + " testCaseId " + testCaseId);
        ModelUploadTestCaseOutput res = problemTestCaseService.rerunCreateTestCaseSolution(problemId, testCaseId,
                                                                                           principal.getName());
        return ResponseEntity.ok().body(res);
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
            log.info("removeContestProblemRole, remove user " + input.getUserId() + " with role " + input.getRoleId() + " from the problem " + input.getProblemId());
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
    @PostMapping(value = "/problems/{id}/export", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
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

    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/owned-problems")
    public List<ProblemEntity> getAllMyProblems(Principal owner) {
        return this.problemTestCaseService.getOwnerProblems(owner.getName());
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/shared-problems")
    public List<ProblemEntity> getAllSharedProblems(Principal owner) {
        return this.problemTestCaseService.getSharedProblems(owner.getName());
    }
    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/all-problems")
    public List<ProblemEntity> getAllProblems(Principal owner) {
        return this.problemTestCaseService.getAllProblems(owner.getName());
    }

}
