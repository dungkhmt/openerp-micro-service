package com.hust.openerp.taskmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.EntityAuthorization;

@Repository
public interface EntityAuthorizationRepository extends JpaRepository<EntityAuthorization, String> {

  List<EntityAuthorization> findAllByIdStartingWithAndRoleIdIn(String prefix, List<String> roleIds);
}
