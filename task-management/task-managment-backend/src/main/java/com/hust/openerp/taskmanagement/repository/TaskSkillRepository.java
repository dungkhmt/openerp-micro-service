package com.hust.openerp.taskmanagement.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.TaskSkill;

@Repository
public interface TaskSkillRepository extends JpaRepository<TaskSkill, UUID> {
}
