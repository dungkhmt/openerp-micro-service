package com.hust.baseweb.applications.taskmanagement.repository;

import com.hust.baseweb.applications.taskmanagement.entity.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface TaskCommentRepository extends CrudRepository<Comment, UUID>, JpaRepository<Comment, UUID> {

    @Query(value = "select e.* from backlog_task_comment e where e.task_id = :taskId order by e.created_stamp ASC", nativeQuery = true)
    List<Comment> getAllCommentsByTaskId(@Param("taskId") UUID taskId);
}
