package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.entity.TaskPriority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskPriorityService {

    TaskPriority getTaskPriorityById(String taskPriorityId);

    List<TaskPriority> getAll();

    TaskPriority create(TaskPriority taskPriority);

    void delete(String priorityId);
}
