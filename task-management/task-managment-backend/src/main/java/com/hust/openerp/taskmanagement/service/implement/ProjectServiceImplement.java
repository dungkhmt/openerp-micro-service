package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.dto.dao.ProjectDao;
import com.hust.openerp.taskmanagement.dto.dao.ProjectPagination;
import com.hust.openerp.taskmanagement.dto.dao.StatusTaskDao;
import com.hust.openerp.taskmanagement.dto.dao.TaskDao;
import com.hust.openerp.taskmanagement.dto.form.BoardFilterInputForm;
import com.hust.openerp.taskmanagement.entity.Project;
import com.hust.openerp.taskmanagement.entity.StatusItem;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.repository.ProjectRepository;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.service.ProjectService;
import com.hust.openerp.taskmanagement.service.TaskAssignableService;
import com.hust.openerp.taskmanagement.service.TaskStatusService;
import com.hust.openerp.taskmanagement.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectServiceImplement implements ProjectService {

    private final ProjectRepository projectRepository;

    private final TaskStatusService taskStatusService;

    private final TaskRepository taskRepository;

    private final TaskAssignableService taskAssignableService;

    private final UserService userService;

    @Override
    public List<Project> getListProject() {
        return projectRepository.findAll();
    }

    @Override
    public Project getProjectById(UUID id) {
        return projectRepository.findById(id).orElse(null);
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
            if (boardFilterInputForm.getUserId() != null) {
                if (boardFilterInputForm.getStartDate() == null) {
                    taskList = taskRepository.getAllTaskByFiltersWithPartyId(
                        boardFilterInputForm.getProjectId(),
                        statusItem.getStatusId(),
                        boardFilterInputForm.getCategoryId(),
                        boardFilterInputForm.getUserId(),
                        boardFilterInputForm.getPriorityId(),
                        boardFilterInputForm.getKeyName());
                } else if (boardFilterInputForm.getEndDate() == null) {
                    taskList = taskRepository.getAllTaskByFiltersWithPartyIdAndRangeDate(
                        boardFilterInputForm.getProjectId(),
                        statusItem.getStatusId(),
                        boardFilterInputForm.getCategoryId(),
                        boardFilterInputForm.getUserId(),
                        boardFilterInputForm.getPriorityId(),
                        boardFilterInputForm.getKeyName(),
                        boardFilterInputForm.getStartDate(),
                        new Date());
                } else {
                    taskList = taskRepository.getAllTaskByFiltersWithPartyIdAndRangeDate(
                        boardFilterInputForm.getProjectId(),
                        statusItem.getStatusId(),
                        boardFilterInputForm.getCategoryId(),
                        boardFilterInputForm.getUserId(),
                        boardFilterInputForm.getPriorityId(),
                        boardFilterInputForm.getKeyName(),
                        boardFilterInputForm.getStartDate(),
                        boardFilterInputForm.getEndDate());
                }
            } else {
                if (boardFilterInputForm.getStartDate() == null) {
                    taskList = taskRepository.getAllTaskByFiltersWithoutPartyId(
                        boardFilterInputForm.getProjectId(),
                        statusItem.getStatusId(),
                        boardFilterInputForm.getCategoryId(),
                        boardFilterInputForm.getPriorityId(),
                        boardFilterInputForm.getKeyName());
                } else if (boardFilterInputForm.getEndDate() == null) {
                    taskList = taskRepository.getAllTaskByFiltersWithoutPartyIdAndRangeDate(
                        boardFilterInputForm.getProjectId(),
                        statusItem.getStatusId(),
                        boardFilterInputForm.getCategoryId(),
                        boardFilterInputForm.getPriorityId(),
                        boardFilterInputForm.getKeyName(),
                        boardFilterInputForm.getStartDate(),
                        new Date());
                } else {
                    taskList = taskRepository.getAllTaskByFiltersWithoutPartyIdAndRangeDate(
                        boardFilterInputForm.getProjectId(),
                        statusItem.getStatusId(),
                        boardFilterInputForm.getCategoryId(),
                        boardFilterInputForm.getPriorityId(),
                        boardFilterInputForm.getKeyName(),
                        boardFilterInputForm.getStartDate(),
                        boardFilterInputForm.getEndDate());
                }
            }
            List<TaskDao> taskDaoList = new ArrayList<>();
            for (Task task : taskList) {
                UUID taskId = task.getId();
                String assignee = "";
                String memberId = null;
                if (taskAssignableService.getByTaskId(taskId) != null) {
                    memberId = taskAssignableService.getByTaskId(taskId).getAssigneeId();
                    User assigneeUser = userService.findById(memberId);
                    assignee = userService.findById(memberId).getId() + " (" + assigneeUser.getFirstName() + " "
                        + assigneeUser.getLastName() + ")";
                }
                taskDaoList.add(new TaskDao(task, assignee));
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
