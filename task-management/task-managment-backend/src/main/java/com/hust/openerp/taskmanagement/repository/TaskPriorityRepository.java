package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.TaskPriority;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskPriorityRepository
    extends JpaRepository<TaskPriority, String> {

	Optional<TaskPriority> findByPriorityName(String name);
}
