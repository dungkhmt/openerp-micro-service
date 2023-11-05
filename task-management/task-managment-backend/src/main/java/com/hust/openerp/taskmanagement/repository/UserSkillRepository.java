package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.UserSkill;

@Repository
public interface UserSkillRepository
    extends JpaRepository<UserSkill, UUID> {

  @Query(value = "select e.* from task_management_user_skill e where e.use_id = CAST(:userId AS varchar)", nativeQuery = true)
  List<UserSkill> getListUserLoginSkill(@Param("userId") String userId);
}
