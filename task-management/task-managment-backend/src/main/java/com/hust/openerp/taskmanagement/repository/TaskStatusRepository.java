package com.hust.openerp.taskmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.google.common.base.Optional;
import com.hust.openerp.taskmanagement.entity.TaskStatus;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, String> {

	List<TaskStatus> findAllByOrderByCreatedStampDesc();
	
	Optional<TaskStatus> findByStatusCode(String code);

	Optional<TaskStatus> findByDescription(String description);

}
