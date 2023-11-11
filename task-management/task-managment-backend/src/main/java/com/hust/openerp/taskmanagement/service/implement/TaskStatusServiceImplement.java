package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.entity.StatusItem;
import com.hust.openerp.taskmanagement.repository.StatusItemRepository;
import com.hust.openerp.taskmanagement.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskStatusServiceImplement implements TaskStatusService {

    private final StatusItemRepository statusItemRepo;

    @Override
    public List<StatusItem> getAllTaskStatus() {
        return statusItemRepo.findAllByStatusTypeId("BACKLOG_STATUS");
    }
}
