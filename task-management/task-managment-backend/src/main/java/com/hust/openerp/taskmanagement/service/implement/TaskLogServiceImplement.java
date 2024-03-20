package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.entity.TaskLog;
import com.hust.openerp.taskmanagement.repository.TaskLogDetailRepository;
import com.hust.openerp.taskmanagement.repository.TaskLogRepository;
import com.hust.openerp.taskmanagement.service.TaskLogService;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@lombok.RequiredArgsConstructor
public class TaskLogServiceImplement implements TaskLogService {
  private final TaskLogRepository taskLogRepository;
  private final TaskLogDetailRepository taskLogDetailRepository;

  @Override
  @Transactional
  public TaskLog addLog(TaskLog taskLog) {
    var details = taskLog.getDetails();

    if (details.isEmpty() && (taskLog.getComment() == null || taskLog.getComment().equals("")))
      return null;

    taskLog.setDetails(null);
    taskLog = taskLogRepository.save(taskLog);

    for (var detail : details) {
      detail.setLogId(taskLog.getId());
    }
    taskLogDetailRepository.saveAll(details);

    return taskLogRepository.save(taskLog);
  }

  @Override
  public List<TaskLog> getLogsByTaskId(UUID taskId) {
    return taskLogRepository.findByTaskId(taskId);
  }
}
