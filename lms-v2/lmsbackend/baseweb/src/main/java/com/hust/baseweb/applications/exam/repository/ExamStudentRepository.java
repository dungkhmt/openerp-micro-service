package com.hust.baseweb.applications.exam.repository;

import com.hust.baseweb.applications.exam.entity.ExamStudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamStudentRepository extends JpaRepository<ExamStudentEntity, String> {

    Optional<ExamStudentEntity> findByCode(String code);
    Optional<ExamStudentEntity> findByCodeAndExamId(String code, String examId);
    List<ExamStudentEntity> findALlByExamId(String examId);
}
