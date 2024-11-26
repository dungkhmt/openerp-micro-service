package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import com.hust.baseweb.applications.programmingcontest.entity.ContestUserParticipantGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ContestSubmissionPagingAndSortingRepo
    extends PagingAndSortingRepository<ContestSubmissionEntity, UUID> {

    Page<ContestSubmissionEntity> findAllByContestId(Pageable pageable, String contestId);

    Page<ContestSubmissionEntity> findAllByContestIdAndStatus(Pageable pageable, String contestId, String status);

    Page<ContestSubmissionEntity> findAllByUserId(Pageable pageable, String userId);

    Page<ContestSubmissionEntity> findAllByUserIdAndContestId(Pageable pageable, String userId, String contestId);

    Page<ContestSubmissionEntity> findAllByUserIdAndContestIdAndProblemId(
        Pageable pageable,
        String userId,
        String contestId,
        String problemId
    );

    List<ContestSubmissionEntity> findAllByUserIdAndContestId(String userId, String contestId);

    @Query(value = "select * from contest_submission_new where user_submission_id = ?1 order by created_stamp desc "
        ,
           nativeQuery = true
    )
    List<ContestSubmissionEntity> findAllByUserId(String userId);

    @Query("SELECT s FROM ContestSubmissionEntity s " +
           "WHERE s.contestId = :contestId " +
           "AND (LOWER(s.userId) LIKE LOWER(concat('%', :userId, '%')) " +
           "    OR LOWER(s.problemId) LIKE LOWER(concat('%', :problemId, '%'))" +
           ")")

    Page<ContestSubmissionEntity> searchSubmissionInContestPaging(
        @Param("contestId") String contestId,
        @Param("userId") String userId,
        @Param("problemId") String problemId,
        Pageable pageable
    );


    @Query("SELECT s FROM ContestSubmissionEntity s " +
           "WHERE s.contestId = :contestId " +
           "AND s.userId in (select C.participantId from ContestUserParticipantGroup C where C.contestId = :contestId and C.userId = :userId) " +
           "AND (LOWER(s.userId) LIKE LOWER(concat('%', :participantId, '%')) " +
           "    OR LOWER(s.problemId) LIKE LOWER(concat('%', :problemId, '%'))" +
           ")")

    Page<ContestSubmissionEntity> searchSubmissionInContestGroupPaging(
        @Param("contestId") String contestId,
        @Param("userId") String userId,
        @Param("participantId") String participantId,
        @Param("problemId") String problemId,
        Pageable pageable
    );

}
