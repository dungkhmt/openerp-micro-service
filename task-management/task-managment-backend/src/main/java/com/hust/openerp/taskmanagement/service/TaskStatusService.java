package com.hust.openerp.taskmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.StatusItem;

@Service
public interface TaskStatusService {
    List<StatusItem> getAllTaskStatus();
}
