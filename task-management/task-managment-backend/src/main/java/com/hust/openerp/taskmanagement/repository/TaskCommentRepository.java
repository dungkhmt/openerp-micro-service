package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskCommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByTaskIdOrderByCreatedStampDesc(UUID taskId);
}
