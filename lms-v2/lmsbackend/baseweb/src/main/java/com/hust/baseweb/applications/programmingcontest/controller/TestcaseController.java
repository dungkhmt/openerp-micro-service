package com.hust.baseweb.applications.programmingcontest.controller;

import com.hust.baseweb.applications.programmingcontest.exception.MiniLeetCodeException;
import com.hust.baseweb.applications.programmingcontest.model.ModelGetTestCaseDetail;
import com.hust.baseweb.applications.programmingcontest.model.ModelProgrammingContestUploadTestCase;
import com.hust.baseweb.applications.programmingcontest.service.ProblemTestCaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.UUID;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TestcaseController {

    ProblemTestCaseService problemTestCaseService;

//    @Secured("ROLE_TEACHER")
//    @PostMapping("/testcases/{problemId}")
//    public ResponseEntity<?> saveTestCase(
//        @PathVariable("problemId") String problemId,
//        @RequestBody ModelSaveTestcase modelSaveTestcase
//    ) {
//        TestCaseEntity testCaseEntity = problemTestCaseService.saveTestCase(problemId, modelSaveTestcase);
//        return ResponseEntity.status(200).body(testCaseEntity);
//    }

    @Secured("ROLE_TEACHER")
    @PostMapping(value = "/testcases",
                 produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addTestcase(
        @RequestPart("dto") ModelProgrammingContestUploadTestCase dto,
        @RequestPart(value = "file") MultipartFile file
    ) throws Exception {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes())) {
            String testCase = IOUtils.toString(stream, StandardCharsets.UTF_8);
            return ResponseEntity.ok().body(problemTestCaseService.addTestcase(testCase, dto));
        }
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/testcases/{testCaseId}")
    public ResponseEntity<?> getTestCaseDetail(@PathVariable("testCaseId") UUID testCaseId) {
        return ResponseEntity.ok().body(problemTestCaseService.getTestCaseDetail(testCaseId));
    }

    /**
     * @param testCaseId
     * @param dto
     * @param file
     * @return
     * @throws IOException
     */
    @Secured("ROLE_TEACHER")
    @PutMapping(value = "/testcases/{testCaseId}",
                produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> editTestcase(
        @PathVariable UUID testCaseId,
        @RequestPart("dto") ModelProgrammingContestUploadTestCase dto,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {
        String testCase = null;
        if (file != null) {
            try (ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes())) {
                testCase = IOUtils.toString(stream, StandardCharsets.UTF_8);
            }
        }

        return ResponseEntity.ok().body(problemTestCaseService.editTestcase(testCaseId, testCase, dto));
    }

    @Secured("ROLE_TEACHER")
    @DeleteMapping("/testcases/{testCaseId}")
    public ResponseEntity<?> deleteTestCase(@PathVariable("testCaseId") UUID testCaseId, Principal principal)
        throws MiniLeetCodeException {
        problemTestCaseService.deleteTestcase(testCaseId, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

//    @PostMapping("/testcases/{problemId}/result")
//    public ResponseEntity<?> getTestCaseResult(
//        @PathVariable("problemId") String problemId,
//        @RequestBody ModelGetTestCaseResult testCaseResult, Principal principal
//    ) throws Exception {
//        ModelGetTestCaseResultResponse resp = problemTestCaseService.getTestCaseResult(problemId, principal.getName(),
//                                                                                       testCaseResult);
//        return ResponseEntity.status(200).body(resp);
//    }


}
