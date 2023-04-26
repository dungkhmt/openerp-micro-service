package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.composite.UserSubmissionContestResultID;
import com.hust.baseweb.applications.programmingcontest.entity.UserSubmissionContestResultNativeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserSubmissionContestResultNativePagingRepo extends PagingAndSortingRepository<UserSubmissionContestResultNativeEntity, UserSubmissionContestResultID> {
    Page<UserSubmissionContestResultNativeEntity> findAllByContestId(Pageable pageable, String contestId);
}
