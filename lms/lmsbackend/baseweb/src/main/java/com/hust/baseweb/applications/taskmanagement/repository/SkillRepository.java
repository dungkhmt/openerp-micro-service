package com.hust.baseweb.applications.taskmanagement.repository;

import com.hust.baseweb.applications.taskmanagement.entity.Skill;
import com.hust.baseweb.applications.taskmanagement.entity.TaskAssignable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, String>, CrudRepository<Skill, String> {

}
