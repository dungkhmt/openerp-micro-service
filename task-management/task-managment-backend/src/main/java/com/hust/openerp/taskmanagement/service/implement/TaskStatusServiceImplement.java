package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.TaskStatus;
import com.hust.openerp.taskmanagement.repository.TaskStatusRepository;
import com.hust.openerp.taskmanagement.service.TaskStatusService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskStatusServiceImplement implements TaskStatusService {

    private final TaskStatusRepository statusItemRepo;

    @Override
    public List<TaskStatus> getAllTaskStatus() {
        return statusItemRepo.findAll();
    }
}
