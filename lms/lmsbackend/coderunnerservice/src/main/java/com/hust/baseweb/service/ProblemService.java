package com.hust.baseweb.service;

import lombok.AllArgsConstructor;
import com.hust.baseweb.applications.programmingcontest.entity.ProblemEntity;
import com.hust.baseweb.repo.ProblemRepo;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ProblemEntity findProblem(String problemId) {
        return problemRepo.findByProblemId(problemId);
    }

}
