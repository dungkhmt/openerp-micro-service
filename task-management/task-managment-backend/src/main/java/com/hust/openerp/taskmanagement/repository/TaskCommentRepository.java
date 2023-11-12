package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskCommentRepository extends JpaRepository<Comment, UUID> {

    @Query(value = "select e.* from task_management_task_comment e where e.task_id = :taskId order by e.created_stamp ASC", nativeQuery = true)
    List<Comment> getAllCommentsByTaskId(@Param("taskId") UUID taskId);
}
