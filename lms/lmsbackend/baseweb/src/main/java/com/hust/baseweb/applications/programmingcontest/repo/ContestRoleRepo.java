package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.composite.ContestUserLoginRoleFromDateId;
import com.hust.baseweb.applications.programmingcontest.entity.ContestRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;

public interface ContestRoleRepo extends JpaRepository<ContestRole, ContestUserLoginRoleFromDateId> {
    List<ContestRole> findAllByUserLoginIdAndThruDate(String userLoginId, Date thruDate);
}
