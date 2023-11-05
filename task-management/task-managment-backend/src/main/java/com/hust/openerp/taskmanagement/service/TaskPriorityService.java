package com.hust.openerp.taskmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.TaskPriority;

@Service
public interface TaskPriorityService {

    TaskPriority getTaskPriorityById(String taskPriorityId);

    List<TaskPriority> getAll();

    TaskPriority create(TaskPriority taskPriority);

    void delete(String priorityId);
}
