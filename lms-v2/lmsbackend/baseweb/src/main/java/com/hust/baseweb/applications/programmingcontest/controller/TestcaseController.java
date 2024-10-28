package com.hust.baseweb.applications.programmingcontest.controller;

import com.google.gson.Gson;
import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import com.hust.baseweb.applications.programmingcontest.exception.MiniLeetCodeException;
import com.hust.baseweb.applications.programmingcontest.model.*;
import com.hust.baseweb.applications.programmingcontest.service.ProblemTestCaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.UUID;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TestcaseController {

    ProblemTestCaseService problemTestCaseService;

    @Secured("ROLE_TEACHER")
    @PostMapping("/testcases/{problemId}")
    public ResponseEntity<?> saveTestCase(
        @PathVariable("problemId") String problemId,
        @RequestBody ModelSaveTestcase modelSaveTestcase
    ) {
        TestCaseEntity testCaseEntity = problemTestCaseService.saveTestCase(problemId, modelSaveTestcase);
        return ResponseEntity.status(200).body(testCaseEntity);
    }

    @PostMapping("/testcases")
    public ResponseEntity<?> uploadTestCase(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelProgrammingContestUploadTestCase modelUploadTestCase = gson.fromJson(
            inputJson,
            ModelProgrammingContestUploadTestCase.class);

        ModelUploadTestCaseOutput res = new ModelUploadTestCaseOutput();
        String testCase;
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
            testCase = IOUtils.toString(stream, StandardCharsets.UTF_8);
            stream.close();

            res = problemTestCaseService.addTestCase(testCase, modelUploadTestCase, principal.getName());
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        res.setStatus("FAILURE");
        res.setMessage("Exception!!");
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/testcases/{testCaseId}")
    public ResponseEntity<?> getTestCaseDetail(@PathVariable("testCaseId") UUID testCaseId)
        throws MiniLeetCodeException {
        ModelGetTestCaseDetail resp = problemTestCaseService.getTestCaseDetail(testCaseId);
        return ResponseEntity.status(200).body(resp);
    }

    @PutMapping("/testcases/{testCaseId}/file-upload")
    public ResponseEntity<?> uploadUpdateTestCase(
        Principal principal,
        @PathVariable String testCaseId,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {

        Gson gson = new Gson();
        ModelProgrammingContestUploadTestCase modelUploadTestCase = gson.fromJson(
            inputJson,
            ModelProgrammingContestUploadTestCase.class);

        UUID testCaseUUID = UUID.fromString(testCaseId);
        String testCase = "";
        if (file != null) {
            try {
                ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
                testCase = IOUtils.toString(stream, StandardCharsets.UTF_8);
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.info("uploadUpdateTestCase, multipart file is null");
        }

        ModelUploadTestCaseOutput res = problemTestCaseService.uploadUpdateTestCase(
            testCaseUUID,
            testCase,
            modelUploadTestCase,
            principal.getName());
        return ResponseEntity.ok().body(res);
    }

    @PutMapping("/testcases/{testCaseId}")
    public ResponseEntity<?> uploadUpdateTestCaseWithoutFile(
        Principal principal,
        @PathVariable String testCaseId,
        @RequestParam("inputJson") String inputJson
    ) {

        Gson gson = new Gson();
        ModelProgrammingContestUploadTestCase modelUploadTestCase = gson.fromJson(
            inputJson,
            ModelProgrammingContestUploadTestCase.class);
        String problemId = modelUploadTestCase.getProblemId();
        UUID testCaseUUID = UUID.fromString(testCaseId);
        log.info("uploadUpdateTestCaseWithoutFile, problemId = " + problemId + " tesCaseId = " + testCaseId
                 + " testCaseUUID = " + testCaseUUID);
        ModelUploadTestCaseOutput res = problemTestCaseService.uploadUpdateTestCase(
            testCaseUUID,
            null,
            modelUploadTestCase,
            principal.getName());
        return ResponseEntity.ok().body(res);

    }

    @Secured("ROLE_TEACHER")
    @DeleteMapping("/testcases/{testCaseId}")
    public ResponseEntity<?> deleteTestCase(@PathVariable("testCaseId") UUID testCaseId, Principal principal)
        throws MiniLeetCodeException {
        problemTestCaseService.deleteTestcase(testCaseId, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/testcases/{problemId}/result")
    public ResponseEntity<?> getTestCaseResult(
        @PathVariable("problemId") String problemId,
        @RequestBody ModelGetTestCaseResult testCaseResult, Principal principal
    ) throws Exception {
        ModelGetTestCaseResultResponse resp = problemTestCaseService.getTestCaseResult(problemId, principal.getName(),
                                                                                       testCaseResult);
        return ResponseEntity.status(200).body(resp);
    }


}
