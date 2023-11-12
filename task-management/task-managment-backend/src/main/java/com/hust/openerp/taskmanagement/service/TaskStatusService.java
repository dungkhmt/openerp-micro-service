package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.entity.StatusItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskStatusService {
    List<StatusItem> getAllTaskStatus();
}
