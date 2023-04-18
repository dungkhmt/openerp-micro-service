package com.hust.baseweb.applications.taskmanagement.service.implement;

import com.hust.baseweb.applications.taskmanagement.dto.dao.AssignedTaskPagination;
import com.hust.baseweb.applications.taskmanagement.dto.dao.PersonDao;
import com.hust.baseweb.applications.taskmanagement.dto.dao.TaskDao;
import com.hust.baseweb.applications.taskmanagement.entity.TaskAssignable;
import com.hust.baseweb.applications.taskmanagement.entity.TaskAssignment;
import com.hust.baseweb.applications.taskmanagement.repository.TaskAssignmentRepository;
import com.hust.baseweb.applications.taskmanagement.service.ProjectMemberService;
import com.hust.baseweb.applications.taskmanagement.service.TaskAssignableService;
import com.hust.baseweb.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskAssignableServiceImplement implements TaskAssignableService {

    private final TaskAssignmentRepository taskAssignmentRepository;

    private final ProjectMemberService projectMemberService;

    private final PersonService personService;

    @Override
    public TaskAssignment create(TaskAssignment TaskAssignment) {
        return taskAssignmentRepository.save(TaskAssignment);
    }

    @Override
    public TaskAssignment getByTaskId(UUID taskId) {
        return taskAssignmentRepository.getByTaskId(taskId);
    }

    @Override
    public AssignedTaskPagination getAssignedTaskPaginated(UUID partyId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<TaskAssignment> TaskAssignmentList =  taskAssignmentRepository.getByPartyId(partyId, pageable);
        List<TaskAssignment> TaskAssignments = TaskAssignmentList.toList();
        List<TaskDao> taskDaoList = new ArrayList<>();
        for (TaskAssignment TaskAssignment : TaskAssignments) {
            String userLoginIdTemp = projectMemberService
                .getUserLoginByPartyId(TaskAssignment.getPartyId())
                .getUserLoginId();
            PersonDao personDao = new PersonDao(
                personService.findByPartyId(TaskAssignment.getPartyId()),
                userLoginIdTemp);
            taskDaoList.add(
                new TaskDao(
                    TaskAssignment.getTask(),
                    personDao.getUserLoginId() + " (" + personDao.getFullName() + ")",
                    partyId)
            );
        }

        AssignedTaskPagination assignedTaskPagination = new AssignedTaskPagination();
        assignedTaskPagination.setData(taskDaoList);
        assignedTaskPagination.setTotalPage(TaskAssignmentList.getTotalPages());
        return assignedTaskPagination;
    }
}
