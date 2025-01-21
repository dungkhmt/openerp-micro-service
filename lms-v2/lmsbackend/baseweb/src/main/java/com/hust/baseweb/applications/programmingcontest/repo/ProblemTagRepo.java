package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ProblemTag;
import com.hust.baseweb.applications.programmingcontest.entity.ProblemTagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemTagRepo extends JpaRepository<ProblemTag, ProblemTagId> {

}
