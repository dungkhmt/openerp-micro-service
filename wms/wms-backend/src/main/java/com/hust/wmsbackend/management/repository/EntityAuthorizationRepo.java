package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.EntityAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntityAuthorizationRepo extends JpaRepository<EntityAuthorization, String> {

    List<EntityAuthorization> findAllByIdStartingWithAndRoleIdIn(String prefix, List<String> roleIds);
}
