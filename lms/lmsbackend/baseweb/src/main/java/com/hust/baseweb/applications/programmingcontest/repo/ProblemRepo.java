package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ProblemEntity;
import com.hust.baseweb.applications.programmingcontest.model.ModelProblemGeneralInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemRepo extends JpaRepository<ProblemEntity, String> {

    ProblemEntity findByProblemId(String problemId);

    @Query("SELECT p FROM ProblemEntity p LEFT JOIN FETCH p.tags WHERE p.problemId = (:problemId)")
    ProblemEntity findByProblemIdWithTagFetched(@Param("problemId") String problemId);

    @Query("select p from ProblemEntity p where p.problemId in :problemIds")
    List<ProblemEntity> getAllProblemWithArray(@Param("problemIds") List<String> problemIds);

    // @Query(
    //     nativeQuery = true,
    //     value = 
    //     "select p.problem_id as problemId, p.problem_name as problemName, p.problem_description as problemDescription from contest_problem_new p"
    // )
    @Query("select new com.hust.baseweb.applications.programmingcontest.model.ModelProblemGeneralInfo("
            + "p.problemId, p.problemName, p.problemDescription"
            + ") from ProblemEntity p")
    List<ModelProblemGeneralInfo> getAllProblemGeneralInformation();

}
