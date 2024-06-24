package com.hust.openerp.taskmanagement.service.implement;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.TaskDTO;
import com.hust.openerp.taskmanagement.dto.TaskStatisticByStatusDTO;
import com.hust.openerp.taskmanagement.dto.request.TaskStatisticByStatusRequest;
import com.hust.openerp.taskmanagement.dto.request.TaskStatisticByWorkloadRequest;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.Task_;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.TaskStatisticService;
import com.hust.openerp.taskmanagement.specification.TaskSpecification;
import com.hust.openerp.taskmanagement.specification.builder.GenericSpecificationsBuilder;
import com.hust.openerp.taskmanagement.util.CriteriaParser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskStatisticServiceImplement implements TaskStatisticService {
    private final TaskRepository taskRepository;
    private final ProjectMemberService projectMemberService;
    private final ModelMapper modelMapper;

    @Override
    public TaskStatisticByStatusDTO getTaskStatisticByStatus(String userId, TaskStatisticByStatusRequest request) {
        // check if the user has access to the project
        if (!projectMemberService.checkAddedMemberInProject(userId, request.getProjectId()))
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);

        // parse start date and end date from unix timestamp
        long startDateUnix = Long.parseLong(request.getStartDate());
        long endDateUnix = Long.parseLong(request.getEndDate());

        Date startDate = new Date(startDateUnix * 1000);
        Date endDate = new Date(endDateUnix * 1000);
        String status = request.getStatus();

        // check if the start date is before the end date
        if (startDate.after(endDate))
            throw new ApiException(ErrorCode.INVALID_REQUEST_PARAMETER_VALUE);

        var result = new TaskStatisticByStatusDTO(request.getStatus());

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(Task_.PROJECT_ID + ":" + request.getProjectId());
        queryBuilder.append(" AND " + Task_.CREATED_STAMP + "]" + startDateUnix);
        queryBuilder.append(" AND " + Task_.CREATED_STAMP + "[" + endDateUnix);

        TaskRepository.TaskStatistic taskStatistic;

        if (status.equalsIgnoreCase("all")) {
            taskStatistic = taskRepository.getCountAndTotalCountOfAllStatus(request.getProjectId(), startDate, endDate);
        } else {
            queryBuilder.append(" AND " + Task_.STATUS_ID + ":" + status);
            taskStatistic = taskRepository.getCountAndTotalCountByStatus(request.getProjectId(), startDate, endDate,
                    status);
        }

        result.setCount(taskStatistic.getCount());
        if (taskStatistic.getTotalCount() == 0) {
            result.setPercentage(0);
        } else {
            result.setPercentage((int) (taskStatistic.getCount() * 100 / taskStatistic.getTotalCount()));
        }

        if (request.isIncludeTasks() && taskStatistic.getCount() > 0) {
            GenericSpecificationsBuilder<Task> builder = new GenericSpecificationsBuilder<>();
            var specs = builder.build(new CriteriaParser().parse(queryBuilder.toString()), TaskSpecification::new);
            result.setTasks(taskRepository.findAll(specs).stream().map(this::convertToDto).toList());
        }

        return result;
    }

    @Override
    public List<TaskStatisticByStatusDTO> getTaskStatisticWorkloadByStatus(String userId,
            TaskStatisticByWorkloadRequest request) {
        // check if the user has access to the project
        if (!projectMemberService.checkAddedMemberInProject(userId, request.getProjectId()))
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);

        // parse start date and end date from unix timestamp
        long startDateUnix = Long.parseLong(request.getStartDate());
        long endDateUnix = Long.parseLong(request.getEndDate());

        Date startDate = new Date(startDateUnix * 1000);
        Date endDate = new Date(endDateUnix * 1000);

        // check if the start date is before the end date
        if (startDate.after(endDate))
            throw new ApiException(ErrorCode.INVALID_REQUEST_PARAMETER_VALUE);

        return taskRepository.getTaskStatisticWorkloadByStatus(request.getProjectId(), startDate, endDate);
    }

    private TaskDTO convertToDto(Task task) {
        var dto = modelMapper.map(task, TaskDTO.class);
        dto.setProject(null);
        dto.setCreator(null);
        return dto;
    }
}
