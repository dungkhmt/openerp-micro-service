package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.entity.Project;
import com.hust.openerp.taskmanagement.entity.ProjectMember;
import com.hust.openerp.taskmanagement.entity.Project_;
import com.hust.openerp.taskmanagement.repository.ProjectRepository;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.ProjectService;
import com.hust.openerp.taskmanagement.specification.ProjectSpecification;
import com.hust.openerp.taskmanagement.specification.builder.GenericSpecificationsBuilder;
import com.hust.openerp.taskmanagement.util.CriteriaParser;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectServiceImplement implements ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMemberService projectMemberService;

    @Override
    public Project getProjectById(UUID id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Project createProject(Project project) {
        var projectEntity = projectRepository.save(project);
        var projectMember = ProjectMember.builder()
                .projectId(projectEntity.getId())
                .userId(projectEntity.getCreatedUserId())
                .roleId("owner")
                .build();
        projectMemberService.addMemberToProject(projectMember);
        return projectEntity;
    }

    @Override
    public Project updateProject(UUID id, Project project) {
        Project projectToUpdate = projectRepository.findById(id).orElseThrow();
        projectToUpdate.setName(project.getName());
        projectToUpdate.setDescription(project.getDescription());
        projectToUpdate.setStatus(project.getStatus());
        return projectRepository.save(projectToUpdate);
    }

    @Override
    public void deleteProjectById(UUID id) {
        projectRepository.deleteByProjectId(id);
    }

    @Override
    public Page<Project> findPaginated(Pageable pageable, String searchString) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Project_.CREATED_STAMP).descending());
        }
        if (searchString != null && !searchString.equals("")) {
            var parser = new CriteriaParser();

            GenericSpecificationsBuilder<Project> builder = new GenericSpecificationsBuilder<>();
            var spec = builder.build(parser.parse(searchString), ProjectSpecification::new);
            return projectRepository.findAll(spec, pageable);
        } else {
            return projectRepository.findAll(pageable);
        }
    }

    // @Override
    // public List<StatusTaskDao> getDataBoardWithFilters(BoardFilterInputForm
    // boardFilterInputForm) {
    // List<StatusTaskDao> statusTaskDaos = new ArrayList<>();
    // List<StatusItem> taskStatuses = taskStatusService.getAllTaskStatus();
    // for (StatusItem statusItem : taskStatuses) {
    // if (statusItem.getStatusId().equals("ASSIGNMENT_ACTIVE") ||
    // statusItem.getStatusId().equals("ASSIGNMENT_INACTIVE")) {
    // continue;
    // }
    // List<Task> taskList = null;
    // if (boardFilterInputForm.getUserId() != null) {
    // if (boardFilterInputForm.getStartDate() == null) {
    // taskList = taskRepository.getAllTaskByFiltersWithPartyId(
    // boardFilterInputForm.getProjectId(),
    // statusItem.getStatusId(),
    // boardFilterInputForm.getCategoryId(),
    // boardFilterInputForm.getUserId(),
    // boardFilterInputForm.getPriorityId(),
    // boardFilterInputForm.getKeyName());
    // } else if (boardFilterInputForm.getEndDate() == null) {
    // taskList = taskRepository.getAllTaskByFiltersWithPartyIdAndRangeDate(
    // boardFilterInputForm.getProjectId(),
    // statusItem.getStatusId(),
    // boardFilterInputForm.getCategoryId(),
    // boardFilterInputForm.getUserId(),
    // boardFilterInputForm.getPriorityId(),
    // boardFilterInputForm.getKeyName(),
    // boardFilterInputForm.getStartDate(),
    // new Date());
    // } else {
    // taskList = taskRepository.getAllTaskByFiltersWithPartyIdAndRangeDate(
    // boardFilterInputForm.getProjectId(),
    // statusItem.getStatusId(),
    // boardFilterInputForm.getCategoryId(),
    // boardFilterInputForm.getUserId(),
    // boardFilterInputForm.getPriorityId(),
    // boardFilterInputForm.getKeyName(),
    // boardFilterInputForm.getStartDate(),
    // boardFilterInputForm.getEndDate());
    // }
    // } else {
    // if (boardFilterInputForm.getStartDate() == null) {
    // taskList = taskRepository.getAllTaskByFiltersWithoutPartyId(
    // boardFilterInputForm.getProjectId(),
    // statusItem.getStatusId(),
    // boardFilterInputForm.getCategoryId(),
    // boardFilterInputForm.getPriorityId(),
    // boardFilterInputForm.getKeyName());
    // } else if (boardFilterInputForm.getEndDate() == null) {
    // taskList = taskRepository.getAllTaskByFiltersWithoutPartyIdAndRangeDate(
    // boardFilterInputForm.getProjectId(),
    // statusItem.getStatusId(),
    // boardFilterInputForm.getCategoryId(),
    // boardFilterInputForm.getPriorityId(),
    // boardFilterInputForm.getKeyName(),
    // boardFilterInputForm.getStartDate(),
    // new Date());
    // } else {
    // taskList = taskRepository.getAllTaskByFiltersWithoutPartyIdAndRangeDate(
    // boardFilterInputForm.getProjectId(),
    // statusItem.getStatusId(),
    // boardFilterInputForm.getCategoryId(),
    // boardFilterInputForm.getPriorityId(),
    // boardFilterInputForm.getKeyName(),
    // boardFilterInputForm.getStartDate(),
    // boardFilterInputForm.getEndDate());
    // }
    // }
    // List<TaskDao> taskDaoList = new ArrayList<>();
    // for (Task task : taskList) {
    // UUID taskId = task.getId();
    // String assignee = "";
    // String memberId = null;
    // if (taskAssignableService.getByTaskId(taskId) != null) {
    // memberId = taskAssignableService.getByTaskId(taskId).getAssigneeId();
    // User assigneeUser = userService.findById(memberId);
    // assignee = userService.findById(memberId).getId() + " (" +
    // assigneeUser.getFirstName() + " "
    // + assigneeUser.getLastName() + ")";
    // }
    // taskDaoList.add(new TaskDao(task, assignee));
    // }
    // StatusTaskDao statusTaskDao = new StatusTaskDao();
    // statusTaskDao.setStatusItem(statusItem);
    // statusTaskDao.setTaskList(taskDaoList);
    // statusTaskDao.setTotal(taskDaoList.size());
    // statusTaskDaos.add(statusTaskDao);
    // }
    // return statusTaskDaos;
    // }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}
