package com.hust.baseweb.service;

import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import com.hust.baseweb.repo.TestCaseRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TestCaseService {

    private TestCaseRepo testCaseRepo;

    private static final String HASH = "TEST_CASE";

    @Cacheable(value = HASH, key = "#problemId + '_' + #evaluatePrivateTestcase")
    public List<TestCaseEntity> findListTestCaseWithCache(String problemId, boolean evaluatePrivateTestcase) {
        return findListTestCase(problemId, evaluatePrivateTestcase);
    }

    public List<TestCaseEntity> findListTestCase(String problemId, boolean includePublicAndPrivateTest) {
        List<TestCaseEntity> testCaseEntityList;
        // IMPROVED: run the submission on both public and private test-cases
        // if includePublicAndPrivateTest = true, then both public and private are accounted to the grade of the submissoin
        // else (includePublicAndPrivateTest = false) then only private testcases are accounted to the grade of the submission

        testCaseEntityList = testCaseRepo.findAllByProblemId(problemId);

        /* NO LONGER USED
        if (includePublicAndPrivateTest) {
            testCaseEntityList = testCaseRepo.findAllByProblemId(problemId);
        } else {
            testCaseEntityList = testCaseRepo.findAllByProblemIdAndIsPublic(problemId, "Y");
        }
        */
        return testCaseEntityList;
    }

}
