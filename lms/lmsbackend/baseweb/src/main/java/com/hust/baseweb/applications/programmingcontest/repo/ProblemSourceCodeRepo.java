package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ProblemSourceCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemSourceCodeRepo extends JpaRepository<ProblemSourceCodeEntity, String> {
}
