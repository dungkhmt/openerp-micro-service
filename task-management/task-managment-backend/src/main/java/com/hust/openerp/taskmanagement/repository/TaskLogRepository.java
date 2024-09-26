package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.TaskLog;

@Repository
public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {
  List<TaskLog> findByTaskId(UUID taskId);
}
