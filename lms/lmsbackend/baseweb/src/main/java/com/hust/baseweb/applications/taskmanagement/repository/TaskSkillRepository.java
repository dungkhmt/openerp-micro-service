package com.hust.baseweb.applications.taskmanagement.repository;

import com.hust.baseweb.applications.taskmanagement.entity.TaskSkill;
import com.hust.baseweb.applications.taskmanagement.entity.UserloginSkill;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaskSkillRepository extends JpaRepository<TaskSkill, Integer>, CrudRepository<TaskSkill, Integer> {
}
