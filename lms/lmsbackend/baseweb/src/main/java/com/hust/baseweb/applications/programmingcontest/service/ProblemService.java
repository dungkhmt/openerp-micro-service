package com.hust.baseweb.applications.programmingcontest.service;

import com.hust.baseweb.applications.programmingcontest.entity.ProblemEntity;
import com.hust.baseweb.applications.programmingcontest.repo.ProblemRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProblemService {

    private ProblemRepo problemRepo;

    private static final String HASH = "PROBLEM";

    @Cacheable(value = HASH, key = "#problemId")
    public ProblemEntity findProblemWithCache(String problemId) {
        return findProblem(problemId);
    }

    @CachePut(value = HASH, key = "#result.problemId")
    public ProblemEntity saveProblemWithCache(ProblemEntity problem) {
        return saveProblem(problem);
    }

    public ProblemEntity findProblem(String problemId) {
        return problemRepo.findByProblemIdWithTagFetched(problemId);
    }

    public ProblemEntity saveProblem(ProblemEntity problem) {
        return problemRepo.save(problem);
    }
}
