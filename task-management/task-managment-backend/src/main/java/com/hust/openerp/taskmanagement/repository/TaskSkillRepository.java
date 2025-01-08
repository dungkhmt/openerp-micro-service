package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.TaskSkill;

import jakarta.transaction.Transactional;

@Repository
public interface TaskSkillRepository extends JpaRepository<TaskSkill, UUID> {
	
	List<TaskSkill> findByTaskId(UUID taskId);
	
	@Transactional
	void deleteByTaskId(UUID taskId);
}
