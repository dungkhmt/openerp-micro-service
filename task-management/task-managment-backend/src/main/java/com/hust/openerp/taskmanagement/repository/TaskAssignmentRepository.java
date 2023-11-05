package com.hust.openerp.taskmanagement.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.TaskAssignment;

@Repository
public interface TaskAssignmentRepository
    extends JpaRepository<TaskAssignment, UUID> {

  @Query(value = "SELECT e.* FROM task_management_task_assignment e WHERE e.assigned_to_user_id = :assigneeId ORDER BY e.created_stamp DESC", nativeQuery = true)
  Page<TaskAssignment> getByAssigneeId(@Param("assigneeId") String assigneeId, Pageable pageable);

  @Query(value = "SELECT * FROM task_management_task_assignment e WHERE e.task_id = :taskId", nativeQuery = true)
  TaskAssignment getByTaskId(@Param("taskId") UUID taskId);
}
