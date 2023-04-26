package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.composite.CompositeContestProblemId;
import com.hust.baseweb.applications.programmingcontest.entity.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestProblemRepo extends JpaRepository<ContestProblem, CompositeContestProblemId> {
    List<ContestProblem> findAllByProblemId(String problemId);
    ContestProblem findByContestIdAndProblemId(String contestId, String problemId);
    ContestProblem findByContestIdAndProblemRecode(String contestId, String problemRecode);
}
