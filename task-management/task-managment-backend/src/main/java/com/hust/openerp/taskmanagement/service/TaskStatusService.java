package com.hust.openerp.taskmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.TaskStatus;

@Service
public interface TaskStatusService {
    List<TaskStatus> getAllTaskStatus();
    
    TaskStatus create(TaskStatus taskStatus);

    void delete(String statusId);
}
