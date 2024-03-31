package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.dto.PaginationDTO;
import com.hust.openerp.taskmanagement.dto.ProjectDTO;
import com.hust.openerp.taskmanagement.dto.dao.HistoryDao;
import com.hust.openerp.taskmanagement.entity.Project;
import com.hust.openerp.taskmanagement.entity.ProjectMember;
import com.hust.openerp.taskmanagement.exception.ProjectNotFoundException;
import com.hust.openerp.taskmanagement.service.ProjectService;
import com.hust.openerp.taskmanagement.service.TaskExecutionService;
import com.hust.openerp.taskmanagement.service.TaskService;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/projects")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectController {
  private ModelMapper modelMapper;

  private ProjectService projectService;

  private final TaskService taskService;

  private final TaskExecutionService taskExecutionService;

  @GetMapping()
  public PaginationDTO<ProjectDTO> getProjectsPagination(Principal principal, Pageable pageable,
      @Nullable @RequestParam(value = "search", required = false) String search) {
    Page<ProjectDTO> result = projectService.findPaginated(principal.getName(), pageable, search)
        // TODO: move this logic to service
        .map(project -> {
          var dto = modelMapper.map(project, ProjectDTO.class);
          dto.setRole(project.getMembers().stream()
              .filter(member -> member.getUserId().equals(principal.getName())).findFirst()
              .orElse(ProjectMember.builder().roleId("").build()).getRoleId());
          return dto;
        });
    return new PaginationDTO<>(result);
  }

  @GetMapping("/all")
  public List<ProjectDTO> getAllProjects() {
    var projects = projectService.getAllProjects();
    return projects.stream().map(project -> modelMapper.map(project, ProjectDTO.class)).toList();
  }

  @PostMapping
  public ProjectDTO createProject(Principal principal, @RequestBody Project project) {
    String userId = principal.getName();
    project.setCreatedUserId(userId);
    var projectRes = projectService.createProject(project);
    var projectDto = modelMapper.map(projectRes, ProjectDTO.class);
    projectDto.setCreator(projectRes.getCreator());
    projectDto.setTaskCount(0);
    return projectDto;
  }

  @GetMapping("{projectId}")
  public ProjectDTO getProject(@PathVariable("projectId") UUID projectId) {
    Project project = projectService.getProjectById(projectId);
    if (project == null) {
      throw new ProjectNotFoundException("Project not found");
    }
    var projectDto = modelMapper.map(project, ProjectDTO.class);
    projectDto.setTaskCount(taskService.countTasksByProjectId(projectId));
    return projectDto;
  }

  @PutMapping("{projectId}")
  public ProjectDTO updateProject(@PathVariable("projectId") UUID projectId, @RequestBody Project entity) {
    var project = projectService.updateProject(projectId, entity);
    var projectDto = modelMapper.map(project, ProjectDTO.class);
    projectDto.setTaskCount(taskService.countTasksByProjectId(projectId));
    return projectDto;
  }

  @GetMapping("{projectId}/statics/{type}")
  public List<Map<String, String>> getTasksStaticsInProject(
      @PathVariable("projectId") UUID projectID,
      @PathVariable("type") String type) {
    int sumTasks = 0;
    List<Object[]> listTasks = null;

    if (type.equals("category")) {
      listTasks = taskService.getTaskStaticsCategoryInProject(projectID);
    } else if (type.equals("status")) {
      listTasks = taskService.getTaskStaticsStatusInProject(projectID);
    }

    List<Map<String, String>> result = new ArrayList<>();
    if (listTasks != null && !listTasks.isEmpty()) {
      for (Object[] objects : listTasks) {
        sumTasks += (int) objects[2];
      }
      for (Object[] objects : listTasks) {
        Map<String, String> temp = new HashMap<>();
        temp.put("id", (String) objects[0]);
        temp.put("name", (String) objects[1]);
        temp.put(
            "value",
            String.valueOf(Math.round(((int) objects[2] * 100 / (sumTasks * 1.0)) * 100.0) / 100.0));
        result.add(temp);
      }
    }

    return result;
  }

  @GetMapping("{projectId}/history")
  public ResponseEntity<Object> getHistory(@PathVariable("projectId") UUID projectId) {
    List<HistoryDao> historyDaos = new ArrayList<>();
    List<Object[]> objects = taskExecutionService.getAllDistinctDay(projectId);
    for (Object[] object : objects) {
      Date date = (Date) object[0];
      String dateStr = new SimpleDateFormat("E, dd MMM yyyy").format(date);
      HistoryDao historyDao = new HistoryDao();
      historyDao.setDate(dateStr);
      historyDao.setTaskExecutionList(taskExecutionService.getAllTaskExecutionByDate(date, projectId));
      historyDaos.add(historyDao);
    }
    return ResponseEntity.ok(historyDaos);
  }

  @ExceptionHandler(ProjectNotFoundException.class)
  public ResponseEntity<String> handleProjectNotFoundException(ProjectNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }
}