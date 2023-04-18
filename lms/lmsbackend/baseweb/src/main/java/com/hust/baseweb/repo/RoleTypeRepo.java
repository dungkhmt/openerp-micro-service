package com.hust.baseweb.repo;

import com.hust.baseweb.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleTypeRepo extends JpaRepository<RoleType, String> {

    RoleType findByRoleTypeId(String roleTypeId);
}
