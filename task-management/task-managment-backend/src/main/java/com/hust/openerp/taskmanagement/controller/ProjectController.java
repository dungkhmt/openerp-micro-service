package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.hust.openerp.taskmanagement.dto.form.CreateProjectForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateProjectForm;
import com.hust.openerp.taskmanagement.entity.Project;
import com.hust.openerp.taskmanagement.service.ProjectService;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/projects")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectController {
  private ModelMapper modelMapper;

  private ProjectService projectService;

  @GetMapping()
  public PaginationDTO<ProjectDTO> getProjectsPagination(Principal principal, Pageable pageable,
      @Nullable @RequestParam(value = "search", required = false) String search) {
    Page<ProjectDTO> result = projectService.findPaginated(principal.getName(), pageable, search);
    return new PaginationDTO<>(result);
  }

  @PostMapping
  public ProjectDTO createProject(Principal principal, @RequestBody @Valid CreateProjectForm projectForm) {
    String userId = principal.getName();
    Project project = modelMapper.map(projectForm, Project.class);
    return projectService.createProject(project, userId);
  }

  @GetMapping("{projectId}")
  public ProjectDTO getProject(Principal principal, @PathVariable("projectId") UUID projectId) {
    return projectService.getProjectById(projectId, principal.getName());
  }

  @PutMapping("{projectId}")
  public ProjectDTO updateProject(Principal principal, @PathVariable("projectId") UUID projectId,
      @RequestBody @Valid UpdateProjectForm entity) {
    return projectService.updateProject(projectId, entity, principal.getName());
  }
}