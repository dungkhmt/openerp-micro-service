package com.hust.baseweb.applications.taskmanagement.service.implement;

import com.hust.baseweb.applications.taskmanagement.dto.dao.TaskExecutionDao;
import com.hust.baseweb.applications.taskmanagement.dto.form.CommentForm;
import com.hust.baseweb.applications.taskmanagement.entity.Comment;
import com.hust.baseweb.applications.taskmanagement.entity.Task;
import com.hust.baseweb.applications.taskmanagement.entity.TaskExecution;
import com.hust.baseweb.applications.taskmanagement.repository.TaskCommentRepository;
import com.hust.baseweb.applications.taskmanagement.repository.TaskExecutionRepository;
import com.hust.baseweb.applications.taskmanagement.service.TaskExecutionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskExecutionServiceImplement implements TaskExecutionService {

    private final TaskExecutionRepository taskExecutionRepository;

    private final TaskCommentRepository taskCommentRepository;

    @Override
    public TaskExecution create(TaskExecution taskExecution) {
        return taskExecutionRepository.save(taskExecution);
    }

    @Override
    public boolean delete(UUID taskExecutionId) {
        taskExecutionRepository.deleteById(taskExecutionId);
        return true;
    }

    @Override
    public void deleteComment(UUID commentId) {
        TaskExecution taskExecution = taskExecutionRepository.findByCommentId(commentId);
        taskExecution.setComment("");
        taskExecution.setCommentId(null);
        taskExecutionRepository.save(taskExecution);
        //delete comment
        taskCommentRepository.deleteById(commentId);
    }

    @Override
    public TaskExecution findById(UUID taskExecutionId) {
        return taskExecutionRepository.findByTaskExecutionId(taskExecutionId);
    }

    @Override
    public TaskExecution save(TaskExecution taskExecution) {
        return taskExecutionRepository.save(taskExecution);
    }

    @Override
    public List<Comment> getAllCommentsByTaskId(UUID taskId) {
        return taskCommentRepository.getAllCommentsByTaskId(taskId);
    }

    @Override
    public List<Object[]> getAllDistinctDay(UUID projectId) {
        return taskExecutionRepository.getAllDistinctDay(projectId);
    }

    public List<TaskExecutionDao> getAllTaskExecutionByDate(Date date, UUID projectId) {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(dateStr + " 00:00:00.00");
            endDate = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(dateStr + " 23:59:59.99");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<TaskExecution> taskExecutionList = taskExecutionRepository.getAllTaskExecutionByDate(
            startDate,
            endDate,
            projectId);
        List<TaskExecutionDao> taskExecutionDaoList = new ArrayList<>();
        for (TaskExecution taskExecution : taskExecutionList) {
            taskExecutionDaoList.add(new TaskExecutionDao(taskExecution));
        }

        return taskExecutionDaoList;
    }

    @Override
    public TaskExecution createTaskComment(Task task, CommentForm commentForm, String userLoginId) {
        Comment comment = new Comment();
        comment.setComment(commentForm.getComment());
        comment.setTaskId(task.getId());
        comment.setStatus(false);
        comment.setCreatedByUserLoginId(userLoginId);
        Comment comment1 = taskCommentRepository.save(comment);

        TaskExecution taskExecution = new TaskExecution();
        taskExecution.setTask(task);
        taskExecution.setExecutionTags("comment");
        taskExecution.setCreatedByUserLoginId(userLoginId);
        taskExecution.setCommentId(comment1.getCommentId());
        taskExecution.setComment(commentForm.getComment());
        taskExecution.setProjectId(commentForm.getProjectId());

        return taskExecutionRepository.save(taskExecution);
    }

    @Override
    public void updateTaskComment(UUID commentId, CommentForm commentForm) {
        Comment comment = taskCommentRepository.getOne(commentId);
        comment.setComment(commentForm.getComment());
        comment.setStatus(true);
        taskCommentRepository.save(comment);
    }
}
