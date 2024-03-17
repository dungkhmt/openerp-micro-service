package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.dao.TaskExecutionDao;
import com.hust.openerp.taskmanagement.dto.form.CommentForm;
import com.hust.openerp.taskmanagement.entity.Comment;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.TaskExecution;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public interface TaskExecutionService {

    TaskExecution create(TaskExecution taskExecution);

    boolean delete(UUID taskExecutionId);

    void deleteComment(UUID commentId);

    TaskExecution findById(UUID taskExecutionId);

    TaskExecution save(TaskExecution taskExecution);

    List<Object[]> getAllDistinctDay(UUID projectId);

    List<TaskExecutionDao> getAllTaskExecutionByDate(Date date, UUID projectId);

    TaskExecution createTaskComment(Task task, CommentForm commentForm, String userLoginId);

    void updateTaskComment(UUID commentId, CommentForm commentForm);
}
