package com.hust.baseweb.applications.examclassandaccount.repo;

import com.hust.baseweb.applications.examclassandaccount.entity.ExamClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExamClassRepo extends JpaRepository<ExamClass, UUID> {


}
