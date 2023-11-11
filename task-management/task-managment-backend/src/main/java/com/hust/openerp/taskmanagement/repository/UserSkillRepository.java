package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSkillRepository
    extends JpaRepository<UserSkill, UUID> {

    @Query(value = "select e.* from task_management_user_skill e where e.use_id = CAST(:userId AS varchar)", nativeQuery = true)
    List<UserSkill> getListUserLoginSkill(@Param("userId") String userId);
}
