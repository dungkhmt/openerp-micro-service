package com.hust.baseweb.applications.exam.repository;

import com.hust.baseweb.applications.exam.entity.ExamTestQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamTestQuestionRepository extends JpaRepository<ExamTestQuestionEntity, String> {

    List<ExamTestQuestionEntity> findAllByExamTestId(String examTestId);
    List<ExamTestQuestionEntity> findAllByExamQuestionId(String examQuestionId);
}
