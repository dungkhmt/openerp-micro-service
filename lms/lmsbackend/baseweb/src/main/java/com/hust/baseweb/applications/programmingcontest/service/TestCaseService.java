package com.hust.baseweb.applications.programmingcontest.service;

import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import com.hust.baseweb.applications.programmingcontest.repo.TestCaseRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    @Caching(evict = {
        @CacheEvict(value = HASH, key = "#testCase.problemId + '_true'"),
        @CacheEvict(value = HASH, key = "#testCase.problemId + '_false'")
    })
    public TestCaseEntity saveTestCaseWithCache(TestCaseEntity testCase) {
        return saveTestCase(testCase);
    }

    public List<TestCaseEntity> findListTestCase(String problemId, boolean evaluatePrivateTestcase) {
        List<TestCaseEntity> testCaseEntityList;
        if (evaluatePrivateTestcase) {
            testCaseEntityList = testCaseRepo.findAllByProblemId(problemId);
        } else {
            testCaseEntityList = testCaseRepo.findAllByProblemIdAndIsPublic(problemId, "Y");
        }
        return testCaseEntityList;
    }

    public TestCaseEntity saveTestCase(TestCaseEntity testCase) {
        return testCaseRepo.save(testCase);
    }

}
