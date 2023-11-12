package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.TaskAssignable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskAssignableRepository
    extends JpaRepository<TaskAssignable, UUID> {

    @Query(value = "SELECT e.* FROM task_management_task_assignable e WHERE e.assigned_to_user_id = :userId", nativeQuery = true)
    Page<TaskAssignable> getByPartyId(@Param("userId") String userId, Pageable pageable);

    @Query(value = "SELECT e.* FROM task_management_task_assignable e WHERE e.task_id = :taskId", nativeQuery = true)
    TaskAssignable getByTaskId(@Param("taskId") UUID taskId);
}
