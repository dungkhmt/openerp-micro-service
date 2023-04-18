package com.hust.baseweb.applications.taskmanagement.service.implement;

import com.hust.baseweb.applications.taskmanagement.entity.Task;
import com.hust.baseweb.applications.taskmanagement.repository.TaskRepository;
import com.hust.baseweb.applications.taskmanagement.service.TaskService;
import com.hust.baseweb.applications.taskmanagement.service.TaskStatusService;
import com.hust.baseweb.entity.StatusItem;
import com.hust.baseweb.repo.StatusItemRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskStatusServiceImplement implements TaskStatusService {

    private final StatusItemRepo statusItemRepo;

    private final TaskRepository taskRepository;

    @Override
    public List<StatusItem> getAllTaskStatus() {
        return statusItemRepo.findAllByStatusTypeId("BACKLOG_STATUS");
    }
}
