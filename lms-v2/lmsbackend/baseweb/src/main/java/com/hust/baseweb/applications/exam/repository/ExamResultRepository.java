package com.hust.baseweb.applications.exam.repository;

import com.hust.baseweb.applications.exam.entity.ExamResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResultEntity, String> {

    List<ExamResultEntity> findAllByExamId(String examId);
}
