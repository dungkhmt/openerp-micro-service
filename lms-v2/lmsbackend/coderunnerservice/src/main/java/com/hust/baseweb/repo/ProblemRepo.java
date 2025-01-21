package com.hust.baseweb.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepo extends JpaRepository<ProblemEntity, String> {

    ProblemEntity findByProblemId(String problemId);

}
