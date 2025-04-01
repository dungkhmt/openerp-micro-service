package openerp.openerpresourceserver.examtimetabling.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.examtimetabling.entity.ExamTimetableAssignment;

@Repository
public interface ExamTimetableAssignmentRepository extends JpaRepository<ExamTimetableAssignment, UUID> {
    long countByExamTimetableIdAndRoomIdIsNotNullAndExamSessionIdIsNotNull(UUID examTimetableId);

    @Query("SELECT a.id, c.id, c.examClassId, c.classId, c.courseId, c.groupId, c.courseName, " +
           "c.description, c.numberOfStudents, c.period, c.managementCode, c.school, " +
           "a.roomId, a.examSessionId, a.weekNumber, a.date " +
           "FROM ExamTimetableAssignment a JOIN ExamClass c ON a.examTimetablingClassId = c.id " +
           "WHERE a.examTimetableId = :timetableId")
    List<Object[]> findAssignmentsWithDetailsByTimetableId(UUID timetableId);
}
