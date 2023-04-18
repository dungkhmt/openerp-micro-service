package com.hust.baseweb.applications.taskmanagement.service.implement;

import com.hust.baseweb.applications.taskmanagement.dto.dao.ProjectDao;
import com.hust.baseweb.applications.taskmanagement.dto.dao.ProjectPagination;
import com.hust.baseweb.applications.taskmanagement.dto.dao.StatusTaskDao;
import com.hust.baseweb.applications.taskmanagement.dto.dao.TaskDao;
import com.hust.baseweb.applications.taskmanagement.dto.form.BoardFilterInputForm;
import com.hust.baseweb.applications.taskmanagement.entity.Project;
import com.hust.baseweb.applications.taskmanagement.entity.Task;
import com.hust.baseweb.applications.taskmanagement.entity.TaskAssignable;
import com.hust.baseweb.applications.taskmanagement.repository.ProjectRepository;
import com.hust.baseweb.applications.taskmanagement.repository.TaskRepository;
import com.hust.baseweb.applications.taskmanagement.service.ProjectMemberService;
import com.hust.baseweb.applications.taskmanagement.service.ProjectService;
import com.hust.baseweb.applications.taskmanagement.service.TaskAssignableService;
import com.hust.baseweb.applications.taskmanagement.service.TaskStatusService;
import com.hust.baseweb.entity.Status;
import com.hust.baseweb.entity.StatusItem;
import com.spotify.docker.client.messages.swarm.TaskStatus;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectServiceImplement implements ProjectService {

    private final ProjectRepository projectRepository;

    private final TaskStatusService taskStatusService;

    private final TaskRepository taskRepository;

    private final TaskAssignableService taskAssignableService;

    private final ProjectMemberService projectMemberService;

    @Override
    public List<Project> getListProject() {
        return projectRepository.findAll();
    }

    @Override
    public Project getProjectById(UUID id) {
        return projectRepository.findById(id);
    }

    @Override
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public void deleteProjectById(UUID id) {
        projectRepository.deleteByProjectId(id);
    }

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public ProjectPagination findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Project> pagedResult = projectRepository.getAll(pageable);

        ProjectPagination pagination = new ProjectPagination();
        List<Project> projects = pagedResult.toList();
        List<ProjectDao> projectDaos = new ArrayList<>();
        for (Project project : projects) {
            projectDaos.add(new ProjectDao(project));
        }
        pagination.setData(projectDaos);
        pagination.setTotalPage((long) Math.ceil(pagedResult.getTotalElements() / (pageSize * 1.0)));

        return pagination;
    }

    @Override
    public List<StatusTaskDao> getDataBoardWithFilters(BoardFilterInputForm boardFilterInputForm) {
        List<StatusTaskDao> statusTaskDaos = new ArrayList<>();
        List<StatusItem> taskStatuses = taskStatusService.getAllTaskStatus();
        for (StatusItem statusItem : taskStatuses) {
            if (statusItem.getStatusId().equals("ASSIGNMENT_ACTIVE") ||
                statusItem.getStatusId().equals("ASSIGNMENT_INACTIVE")) {
                continue;
            }
            List<Task> taskList = null;
            if (boardFilterInputForm.getPartyId() != null ) {
                if(boardFilterInputForm.getStartDate() == null){
                    taskList = taskRepository.getAllTaskByFiltersWithPartyId(
                        boardFilterInputForm.getProjectId(),
                        statusItem.getStatusId(),
                        boardFilterInputForm.getCategoryId(),
                        boardFilterInputForm.getPartyId(),
                        boardFilterInputForm.getPriorityId(),
                        boardFilterInputForm.getKeyName());
                }else if(boardFilterInputForm.getEndDate() == null){
                    taskList = taskRepository.getAllTaskByFiltersWithPartyIdAndRangeDate(
                        boardFilterInputForm.getProjectId(),
                        statusItem.getStatusId(),
                        boardFilterInputForm.getCategoryId(),
                        boardFilterInputForm.getPartyId(),
                        boardFilterInputForm.getPriorityId(),
                        boardFilterInputForm.getKeyName(),
                        boardFilterInputForm.getStartDate(),
                        new Date()
                    );
                }else {
                    taskList = taskRepository.getAllTaskByFiltersWithPartyIdAndRangeDate(
                        boardFilterInputForm.getProjectId(),
                        statusItem.getStatusId(),
                        boardFilterInputForm.getCategoryId(),
                        boardFilterInputForm.getPartyId(),
                        boardFilterInputForm.getPriorityId(),
                        boardFilterInputForm.getKeyName(),
                        boardFilterInputForm.getStartDate(),
                        boardFilterInputForm.getEndDate()
                    );
                }
            } else {
                if(boardFilterInputForm.getStartDate() == null){
                    taskList = taskRepository.getAllTaskByFiltersWithoutPartyId(
                        boardFilterInputForm.getProjectId(),
                        statusItem.getStatusId(),
                        boardFilterInputForm.getCategoryId(),
                        boardFilterInputForm.getPriorityId(),
                        boardFilterInputForm.getKeyName());
                }else if(boardFilterInputForm.getEndDate() == null){
                    taskList = taskRepository.getAllTaskByFiltersWithoutPartyIdAndRangeDate(
                        boardFilterInputForm.getProjectId(),
                        statusItem.getStatusId(),
                        boardFilterInputForm.getCategoryId(),
                        boardFilterInputForm.getPriorityId(),
                        boardFilterInputForm.getKeyName(),
                        boardFilterInputForm.getStartDate(),
                        new Date()
                    );
                }else {
                    taskList = taskRepository.getAllTaskByFiltersWithoutPartyIdAndRangeDate(
                        boardFilterInputForm.getProjectId(),
                        statusItem.getStatusId(),
                        boardFilterInputForm.getCategoryId(),
                        boardFilterInputForm.getPriorityId(),
                        boardFilterInputForm.getKeyName(),
                        boardFilterInputForm.getStartDate(),
                        boardFilterInputForm.getEndDate()
                    );
                }
            }
            List<TaskDao> taskDaoList = new ArrayList<>();
            for (Task task : taskList) {
                UUID taskId = task.getId();
                String assignee = "";
                UUID partyId = null;
                if (taskAssignableService.getByTaskId(taskId) != null) {
                    partyId = taskAssignableService.getByTaskId(taskId).getPartyId();
                    assignee = projectMemberService.getUserLoginByPartyId(partyId).getUserLoginId();
                }
                taskDaoList.add(new TaskDao(task, assignee, partyId));
            }
            StatusTaskDao statusTaskDao = new StatusTaskDao();
            statusTaskDao.setStatusItem(statusItem);
            statusTaskDao.setTaskList(taskDaoList);
            statusTaskDao.setTotal(taskDaoList.size());
            statusTaskDaos.add(statusTaskDao);
        }
        return statusTaskDaos;
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}
