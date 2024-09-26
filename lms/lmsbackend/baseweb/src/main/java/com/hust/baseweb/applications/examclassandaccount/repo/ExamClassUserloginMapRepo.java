package com.hust.baseweb.applications.examclassandaccount.repo;

import com.hust.baseweb.applications.examclassandaccount.entity.ExamClassUserloginMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExamClassUserloginMapRepo extends JpaRepository<ExamClassUserloginMap, UUID> {
    List<ExamClassUserloginMap> findByExamClassId(UUID examClassId);

    List<ExamClassUserloginMap> findAllByStatus(String status);
}
