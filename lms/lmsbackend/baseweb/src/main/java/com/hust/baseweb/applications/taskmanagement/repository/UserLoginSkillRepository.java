package com.hust.baseweb.applications.taskmanagement.repository;

import com.hust.baseweb.applications.taskmanagement.entity.UserloginSkill;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface UserLoginSkillRepository
    extends JpaRepository<UserloginSkill, UUID>, CrudRepository<UserloginSkill, UUID> {

    @Query(value = "select e.* from backlog_user_login_skill e where e.user_login_id = CAST(:userLoginId AS varchar)",
           nativeQuery = true)
    List<UserloginSkill> getListUserLoginSkill(@Param("userLoginId") String userLoginId);
}
