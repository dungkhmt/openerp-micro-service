package com.hust.baseweb.applications.exam.repository;

import com.hust.baseweb.applications.exam.entity.ExamTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamTestRepository extends JpaRepository<ExamTestEntity, String> {

    Optional<ExamTestEntity> findByCode(String code);
}
