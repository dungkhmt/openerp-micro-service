package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ContestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

public interface ContestPagingAndSortingRepo extends PagingAndSortingRepository<ContestEntity, String> {

    long count();

    Page<ContestEntity> findAll(Pageable pageable);

    List<ContestEntity> findAll();

    Page<ContestEntity> findAllByUserId(Pageable pageable, String userId);

    //Page<ContestEntity> findAllByUserIdAndRoleId(Pageable pageable, String userId, String roleId);
    List<ContestEntity> findAllByContestIdIn(Set<String> contestIds);
}
