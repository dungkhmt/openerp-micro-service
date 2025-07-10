package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.EntityAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EntityAuthorizationRepo extends JpaRepository<EntityAuthorization, String> {

    List<EntityAuthorization> findAllByIdStartingWithAndRoleIdIn(String prefix, List<String> roleIds);
}
