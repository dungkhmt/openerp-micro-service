package com.hust.baseweb.applications.programmingcontest.controller;

import com.hust.baseweb.applications.programmingcontest.entity.CodePlagiarism;
import com.hust.baseweb.applications.programmingcontest.model.*;
import com.hust.baseweb.applications.programmingcontest.service.ProblemTestCaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CodeSimilarityController {

    ProblemTestCaseService problemTestCaseService;

    @GetMapping("/similarity-check/{contestId}")
    public ResponseEntity<?> getCodeSimilaritySummaryOfParticipants(
        @PathVariable String contestId
    ) {
        List<ModelReponseCodeSimilaritySummaryParticipant> res = problemTestCaseService.getListModelReponseCodeSimilaritySummaryParticipant(
            contestId);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/code-similarity")
    public ResponseEntity<?> getCodeSimilarity(@RequestBody ModelGetCodeSimilarityParams input) {
        List<CodePlagiarism> codePlagiarism = problemTestCaseService.findAllBy(input);
        return ResponseEntity.ok().body(codePlagiarism);
    }

    @PostMapping("/code-similarity-cluster")
    public ResponseEntity<?> getCodeSimilarityCluster(
        @RequestBody ModelGetCodeSimilarityParams input
    ) {
        List<ModelSimilarityClusterOutput> res = problemTestCaseService.computeSimilarityClusters(input);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/check-code-similarity/{contestId}")
    public ResponseEntity<?> checkCodeSimilarity(
        @RequestBody ModelCheckSimilarityInput I,
        @PathVariable String contestId
    ) {
        log.info("checkCodeSimilarity, contestId = " + contestId);
        ModelCodeSimilarityOutput res = problemTestCaseService.checkSimilarity(contestId, I);
        return ResponseEntity.ok().body(res);
    }

}
