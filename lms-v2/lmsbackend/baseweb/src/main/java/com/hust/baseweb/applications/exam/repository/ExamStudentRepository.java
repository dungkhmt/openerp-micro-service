package com.hust.baseweb.applications.exam.repository;

import com.hust.baseweb.applications.exam.entity.ExamStudentEntity;
import com.hust.baseweb.applications.exam.model.response.ExamStudentResultDetailsRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamStudentRepository extends JpaRepository<ExamStudentEntity, String> {

    Optional<ExamStudentEntity> findByCode(String code);
    Optional<ExamStudentEntity> findByCodeAndExamId(String code, String examId);
    List<ExamStudentEntity> findALlByExamId(String examId);

    @Query(value = "select\n" +
                   "    es.id as id,\n" +
                   "    es.code as code,\n" +
                   "    es.name as name,\n" +
                   "    es.email as email,\n" +
                   "    es.phone as phone,\n" +
                   "    er.id as examResultId,\n" +
                   "    er.total_score as totalScore,\n" +
                   "    er.total_time as totalTime,\n" +
                   "    er.submited_at as submitedAt\n" +
                   "from\n" +
                   "    exam_student es\n" +
                   "left join exam_result er on\n" +
                   "    es.id = er.exam_student_id\n" +
                   "where\n" +
                   "    es.exam_id =:examId\n" +
                   "order by\n" +
                   "    es.name", nativeQuery = true)
    List<ExamStudentResultDetailsRes> findAllWithResult(@Param("examId") String examId);
}
