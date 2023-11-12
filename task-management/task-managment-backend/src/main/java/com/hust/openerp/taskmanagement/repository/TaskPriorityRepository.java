package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskPriorityRepository
    extends JpaRepository<TaskPriority, String> {
}
