package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.entity.TaskLog;

import java.util.List;
import java.util.UUID;

public interface TaskLogService {
  TaskLog addLog(TaskLog taskHistory);

  List<TaskLog> getLogsByTaskId(UUID taskId);
}
