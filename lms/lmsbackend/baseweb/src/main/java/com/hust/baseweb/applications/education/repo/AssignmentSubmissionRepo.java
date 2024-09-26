package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface AssignmentSubmissionRepo extends JpaRepository<AssignmentSubmission, UUID> {

    AssignmentSubmission findByAssignmentIdAndStudentUserLoginId(UUID assignmentId, String studentId);

    @Query(value = "select\n" +
                   "\teas.original_file_name\n" +
                   "from\n" +
                   "\tedu_assignment_submission eas\n" +
                   "where\n" +
                   "\teas.assignment_id = ?1\n" +
                   "\tand eas.student_id = ?2",
           nativeQuery = true)
    String getSubmitedFilenameOf(UUID assignmentId, String studentId);

    @Query(value = "select\n" +
                   "\tcount(1)\n" +
                   "from\n" +
                   "\tedu_assignment_submission eas\n" +
                   "where\n" +
                   "\teas.assignment_id = ?1\n" +
                   "limit 1",
           nativeQuery = true)
    Integer checkSubmission(UUID assignmentId);
}
