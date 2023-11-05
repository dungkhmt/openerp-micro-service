package com.hust.openerp.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.TaskExecution;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskExecutionRepository
    extends JpaRepository<TaskExecution, UUID> {

  @Query(value = "SELECT e.* FROM task_management_task_execution e WHERE e.id = :taskExecutionId", nativeQuery = true)
  TaskExecution findByTaskExecutionId(@Param("taskExecutionId") UUID taskExecutionId);

  @Query(value = "SELECT e.* FROM task_management_task_execution e WHERE e.task_id = :taskId AND e.execution_tags = 'comment' AND e.comment <> ''", nativeQuery = true)
  List<TaskExecution> getAllCommentsByTaskId(@Param("taskId") UUID taskId);

  @Query(value = "SELECT DISTINCT DATE(e.created_stamp) FROM task_management_task_execution e WHERE e.project_id = :projectId ORDER BY DATE(e.created_stamp) DESC", nativeQuery = true)
  List<Object[]> getAllDistinctDay(@Param("projectId") UUID projectId);

  @Query(value = "SELECT e.* FROM task_management_task_execution e\n" +
      "WHERE e.project_id = :projectId AND ( e.created_stamp BETWEEN :startDate AND :endDate ) ORDER BY e.created_stamp DESC", nativeQuery = true)
  List<TaskExecution> getAllTaskExecutionByDate(
      @Param("startDate") Date startDate,
      @Param("endDate") Date endDate,
      @Param("projectId") UUID projectId);

  @Query(value = "SELECT e.* FROM task_management_task_execution e WHERE e.comment_id = :commentId", nativeQuery = true)
  TaskExecution findByCommentId(@Param("commentId") UUID commentId);
}
