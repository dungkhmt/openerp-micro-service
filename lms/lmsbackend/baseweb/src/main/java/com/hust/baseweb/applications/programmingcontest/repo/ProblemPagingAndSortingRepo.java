package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ProblemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.ArrayList;

public interface ProblemPagingAndSortingRepo extends PagingAndSortingRepository<ProblemEntity, String> {
    @Query("select p.problemName from ProblemEntity p")
    ArrayList<String> getProblemNamePaging(Pageable pageable);

    @Query(value = "select p from ProblemEntity p where p.isPublicProblem = true ")
    Page<ProblemEntity> findAllByPublicIs(Pageable pageable);


}
