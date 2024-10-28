package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.Assignment;
import com.hust.baseweb.applications.education.model.GetSubmissionsOM;
import com.hust.baseweb.applications.education.model.getassignmentdetail.AssignmentDetailOM;
import com.hust.baseweb.applications.education.model.getassignmentdetail4teacher.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AssignmentRepo extends JpaRepository<Assignment, UUID> {

    @Query(value = "select\n" +
                   "\tassignment_name \"name\",\n" +
                   "\tsubject,\n" +
                   "\tclose_time closeTime,\n" +
                   "\topen_time openTime,\n" +
                   "\tdeleted\n" +
                   "from\n" +
                   "\tedu_assignment ea\n" +
                   "where\n" +
                   "\tea.id = ?1",
           nativeQuery = true)
    AssignmentDetailOM getAssignmentDetail(UUID id);

    @Query(value = "select\n" +
                   "\teas.student_id studentId,\n" +
                   "\tconcat(p.first_name, ' ', p.middle_name, ' ', p.last_name) \"name\",\n" +
                   "\teas.last_updated_stamp submissionDate,\n" +
                   "\teas.original_file_name originalFileName\n" +
                   "from\n" +
                   "\tedu_assignment_submission eas\n" +
                   "inner join user_login ul on\n" +
                   "\teas.student_id = ul.user_login_id\n" +
                   "inner join person p on\n" +
                   "\tul.party_id = p.party_id\n" +
                   "where\n" +
                   "\teas.assignment_id = ?1\n" +
                   "order by\n" +
                   "\teas.last_updated_stamp desc",
           nativeQuery = true)
    List<Submission> getStudentSubmissionsOf(UUID assignmentId);

    @Query(value = "select\n" +
                   "\tcast(class_id as varchar)\n" +
                   "from\n" +
                   "\tedu_assignment ea\n" +
                   "where\n" +
                   "\tea.id = ?1", nativeQuery = true)
    String getClassIdOf(UUID assignmentId);

    @Query(value = "select\n" +
                   "\teas.student_id studentId,\n" +
                   "\teas.original_file_name originalFileName\n" +
                   "from\n" +
                   "\tedu_assignment_submission eas\n" +
                   "where\n" +
                   "\teas.assignment_id = ?1\n" +
                   "\tand eas.student_id in ?2",
           nativeQuery = true)
    List<GetSubmissionsOM> getSubmissionsOf(UUID assignmentId, Set<String> studentIds);

    @Modifying
    @Transactional
    @Query(value = "update\n" +
                   "\tedu_assignment\n" +
                   "set\n" +
                   "\tdeleted = true\n" +
                   "where\n" +
                   "\tid = ?1",
           nativeQuery = true)
    void deleteAssignment(UUID id);

    @Query(value = "select\n" +
                   "\tcount(1)\n" +
                   "from\n" +
                   "\tedu_assignment ea\n" +
                   "where\n" +
                   "\tid = ?1",
           nativeQuery = true)
    int isAssignExist(UUID id);

    @Query(value = "select\n" +
                   "\tclose_time\n" +
                   "from\n" +
                   "\tedu_assignment\n" +
                   "where\n" +
                   "\tid = ?1\n" +
                   "\tand deleted = false ",
           nativeQuery = true)
    Date getCloseTime(UUID assignmentId);

    Assignment findByIdAndDeletedFalse(UUID id);

    @Query(value = "select\n" +
                   "\tcount(1)\n" +
                   "from\n" +
                   "\tedu_assignment ea\n" +
                   "inner join edu_class ec on\n" +
                   "\tea.class_id = ec.id\n" +
                   "where\n" +
                   "\tea.id = ?2\n" +
                   "\tand ec.teacher_id = ?1",
           nativeQuery = true)
    int hasDownloadingPermission(String teacherId, UUID assignmentId);
}
