package com.hust.baseweb.applications.exam.repository;

import com.hust.baseweb.applications.exam.entity.ExamQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestionEntity, String> {
    Optional<ExamQuestionEntity> findByCode(String code);

    List<ExamQuestionEntity> findAllByExamSubjectId(String examSubjectId);
}
