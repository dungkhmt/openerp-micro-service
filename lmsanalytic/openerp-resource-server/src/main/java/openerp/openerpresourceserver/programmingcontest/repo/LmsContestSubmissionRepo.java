package openerp.openerpresourceserver.programmingcontest.repo;

import openerp.openerpresourceserver.programmingcontest.entity.LmsContestSubmission;
import openerp.openerpresourceserver.programmingcontest.model.ModelResponseGetSubmissionsWithStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface LmsContestSubmissionRepo extends JpaRepository<LmsContestSubmission, UUID> {
    List<LmsContestSubmission> findAllByCreatedStampBetween(Date s, Date e);
    @Query(value = "select * from lms_contest_submission order by submission_created_stamp asc limit 5", nativeQuery = true)
    List<LmsContestSubmission> findEarlestPage5Items();
    @Query(value = "select min(submission_created_stamp) from lms_contest_submission", nativeQuery = true)
    Date findMinSubmissionCreatedStamp();

    List<LmsContestSubmission> findAllByUserSubmissionId(String userSubmissionId);

    LmsContestSubmission findByContestSubmissionId(UUID contestSubmissionId);

    @Query(value = "select new openerp.openerpresourceserver.programmingcontest.model.ModelResponseGetSubmissionsWithStatus(s.id, s.contestSubmissionId, s.userSubmissionId, s.status, s.createdStamp) from LmsContestSubmission s")
    List<ModelResponseGetSubmissionsWithStatus> findAllSubmissionWithStatus();


    @Query(value = "select new openerp.openerpresourceserver.programmingcontest.model.ModelResponseGetSubmissionsWithStatus(" +
            "s.id, s.contestSubmissionId, s.userSubmissionId, s.status, s.createdStamp) from " +
            "LmsContestSubmission s where s.createdStamp >= :start and s.createdStamp < :end")

    List<ModelResponseGetSubmissionsWithStatus> findAllWithStatusByCreatedStampBetween(Date start, Date end);


}

