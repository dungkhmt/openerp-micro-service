package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.ProjectDTO;
import com.hust.openerp.taskmanagement.dto.form.UpdateProjectForm;
import com.hust.openerp.taskmanagement.entity.Project;

@Service
public interface ProjectService {
    ProjectDTO getProjectById(UUID id, String userId);

    ProjectDTO createProject(Project project, String creatorId);

    ProjectDTO updateProject(UUID id, UpdateProjectForm project, String updaterId);

    void deleteProjectById(UUID id, String deleterId);

    Page<ProjectDTO> findPaginated(String memberId, Pageable pageable, String searchString);
}
