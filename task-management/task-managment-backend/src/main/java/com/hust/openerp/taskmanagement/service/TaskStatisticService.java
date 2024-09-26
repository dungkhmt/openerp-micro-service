package com.hust.openerp.taskmanagement.service;

import java.util.List;

import com.hust.openerp.taskmanagement.dto.TaskStatisticByStatusDTO;
import com.hust.openerp.taskmanagement.dto.request.TaskStatisticByStatusRequest;
import com.hust.openerp.taskmanagement.dto.request.TaskStatisticByWorkloadRequest;

public interface TaskStatisticService {
    TaskStatisticByStatusDTO getTaskStatisticByStatus(String userId, TaskStatisticByStatusRequest request);

    List<TaskStatisticByStatusDTO> getTaskStatisticWorkloadByStatus(String userId,
            TaskStatisticByWorkloadRequest request);
}
