package com.hust.baseweb.applications.programmingcontest.service.helper.cache;

import com.hust.baseweb.applications.programmingcontest.cache.RedisCacheService;
import com.hust.baseweb.applications.programmingcontest.entity.ContestEntity;
import com.hust.baseweb.applications.programmingcontest.entity.ProblemEntity;
import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import com.hust.baseweb.applications.programmingcontest.model.ModelGetContestDetailResponse;
import com.hust.baseweb.applications.programmingcontest.repo.ContestRepo;
import com.hust.baseweb.applications.programmingcontest.repo.ProblemRepo;
import com.hust.baseweb.applications.programmingcontest.repo.TestCaseRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProblemTestCaseServiceCache {

    private RedisCacheService cacheService;
    private ProblemRepo problemRepo;
    private TestCaseRepo testCaseRepo;
    private ContestRepo contestRepo;

    public enum RedisHashPrefix {
        PROBLEM("PROBLEM"),
        PROBLEM_SUBMISSION("PROBLEM_SUBMISSION"),
        TEST_CASE("TEST_CASE"),
        CONTEST("CONTEST"),
        CONTEST_DETAIL_SOLVING("CONTEST_DETAIL_SOLVING");

        private final String value;

        RedisHashPrefix(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public void flushCache(RedisHashPrefix hash) {
        cacheService.flushCache(hash.getValue());
    }

    public void flushAllCache() {
        for (RedisHashPrefix hash : RedisHashPrefix.values()) {
            cacheService.flushCache(hash.getValue());
        }
    }

//    public ProblemEntity findProblemAndUpdateCache(String problemId) {
//        ProblemEntity problem = findProblemInCache(problemId);
//        if (problem == null) {
//            problem = problemRepo.findByProblemIdWithTagFetched(problemId);
//            addProblemToCache(problem, 60 * 60);
//        }
//        return problem;
//    }
//
//    public ProblemEntity findProblemInCache(String problemId) {
//        return cacheService.getCachedObject(RedisHashPrefix.PROBLEM.getValue(), problemId, ProblemEntity.class);
//    }
//
//    public void addProblemToCache(ProblemEntity problem, int expireTime) {
//        cacheService.pushCachedWithExpire(
//            RedisHashPrefix.PROBLEM.getValue(),
//            problem.getProblemId(),
//            problem,
//            expireTime * 1000);
//    }


//    public ContestEntity findContestAndUpdateCache(String contestId) {
//        ContestEntity contest = findContestInCache(contestId);
//        if (contest == null) {
//            contest = contestRepo.findContestByContestId(contestId);
//            addContestToCache(contest, 60 * 60);
//        }
//        return contest;
//    }
//
//    public ContestEntity findContestInCache(String contestId) {
//        return cacheService.getCachedObject(RedisHashPrefix.CONTEST.getValue(), contestId, ContestEntity.class);
//    }
//
//    public void addContestToCache(ContestEntity contest, int expireTime) {
//        cacheService.pushCachedWithExpire(
//            RedisHashPrefix.CONTEST.getValue(),
//            contest.getContestId(),
//            contest,
//            expireTime * 1000);
//    }


//    public List<TestCaseEntity> findListTestCaseAndUpdateCache(String problemId, boolean isPublicTestCase) {
//        List<TestCaseEntity> testCaseEntityList = findListTestCaseInCache(problemId, isPublicTestCase);
//        if (testCaseEntityList == null || testCaseEntityList.isEmpty()) {
//            if (isPublicTestCase) {
//                testCaseEntityList = testCaseRepo.findAllByProblemId(problemId);
//            } else {
//                testCaseEntityList = testCaseRepo.findAllByProblemIdAndIsPublic(problemId, "N");
//            }
//            addListTestCaseToCache(problemId, testCaseEntityList, isPublicTestCase, 60 * 60);
//        }
//        return testCaseEntityList;
//    }
//
//    public List<TestCaseEntity> findListTestCaseInCache(String problemId, boolean isPublicTestCase) {
//        String key = generateKeyTestCase(problemId, isPublicTestCase);
//        return cacheService.getCachedSpecialListObject(RedisHashPrefix.TEST_CASE.getValue(), key);
//    }

    public Long findUserLastProblemSubmissionTimeInCache(String problemId, String userId) {
        String key = generateKeySubmissionInterval(problemId, userId);
        return cacheService.getCachedObject(RedisHashPrefix.PROBLEM_SUBMISSION.getValue(), key, Long.class);
    }

    public void addUserLastProblemSubmissionTimeToCache(String problemId, String userId) {
        String key = generateKeySubmissionInterval(problemId, userId);
        Date now = new Date();
        cacheService.pushCachedWithExpire(
            RedisHashPrefix.PROBLEM_SUBMISSION.getValue(),
            key,
            now.getTime(),
            24 * 60 * 60 * 1000);
    }

    private String generateKeySubmissionInterval(String problemId, String userId) {
        return problemId + "__" + userId;
    }

//    public void addListTestCaseToCache(
//        String problemId,
//        List<TestCaseEntity> testCaseEntityList,
//        boolean isPublicTestCase,
//        int expireTime
//    ) {
//        String key = generateKeyTestCase(problemId, isPublicTestCase);
//        cacheService.pushCachedWithExpire(
//            RedisHashPrefix.TEST_CASE.getValue(),
//            key,
//            testCaseEntityList,
//            expireTime * 1000);
//    }
//
//    private String generateKeyTestCase(String problemId, boolean isPublicTestCase) {
//        if (isPublicTestCase) {
//            return problemId + "_Y";
//        }
//        return problemId + "_N";
//    }
//
//
//    public ModelGetContestDetailResponse findContestDetailResponseInCache(String contestId) {
//        return cacheService.getCachedObject(
//            RedisHashPrefix.CONTEST_DETAIL_SOLVING.getValue(),
//            contestId,
//            ModelGetContestDetailResponse.class);
//    }
//
//    public void addContestDetailResponseToCache(ModelGetContestDetailResponse contest, int expireTime) {
//        cacheService.pushCachedWithExpire(
//            RedisHashPrefix.CONTEST_DETAIL_SOLVING.getValue(),
//            contest.getContestId(),
//            contest,
//            expireTime * 1000);
//    }
}
