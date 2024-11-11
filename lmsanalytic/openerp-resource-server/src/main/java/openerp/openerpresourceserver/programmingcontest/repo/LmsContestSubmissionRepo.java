package openerp.openerpresourceserver.programmingcontest.repo;

import openerp.openerpresourceserver.programmingcontest.entity.LmsContestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface LmsContestSubmissionRepo extends JpaRepository<LmsContestSubmission, UUID> {
    List<LmsContestSubmission> findAllByCreatedStampBetween(Date s, Date e);
    @Query(value = "select * from lms_contest_submission order by submission_created_stamp asc limit 5", nativeQuery = true)
    List<LmsContestSubmission> findEarlestPage5Items();
    @Query(value = "select min(submission_created_stamp) from lms_contest_submission", nativeQuery = true)
    Date findMinSubmissionCreatedStamp();
}

