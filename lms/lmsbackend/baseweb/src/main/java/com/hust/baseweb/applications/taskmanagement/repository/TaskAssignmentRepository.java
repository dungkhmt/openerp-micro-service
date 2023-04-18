package com.hust.baseweb.applications.taskmanagement.repository;

import com.hust.baseweb.applications.taskmanagement.entity.TaskAssignment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface TaskAssignmentRepository
    extends CrudRepository<TaskAssignment, UUID>, JpaRepository<TaskAssignment, UUID>, PagingAndSortingRepository<TaskAssignment, UUID> {

    @Query(value = "SELECT e.* FROM backlog_task_assignable e WHERE e.assigned_to_party_id = :partyId ORDER BY e.created_stamp DESC",
           nativeQuery = true)
    Page<TaskAssignment> getByPartyId(@Param("partyId") UUID partyId, Pageable pageable);

    @Query(value = "SELECT e.* FROM backlog_task_assignable e WHERE e.backlog_task_id = :taskId", nativeQuery = true)
    TaskAssignment getByTaskId(@Param("taskId") UUID taskId);
}
