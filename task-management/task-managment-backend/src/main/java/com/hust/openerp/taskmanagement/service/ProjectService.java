package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.form.ProjectForm;
import com.hust.openerp.taskmanagement.entity.Project;

@Service
public interface ProjectService {
    Project getProjectById(UUID id);

    Project createProject(Project project);

    Project updateProject(UUID id, ProjectForm project);

    void deleteProjectById(UUID id);

    Page<Project> findPaginated(String memberId, Pageable pageable, String searchString);

    // List<StatusTaskDao> getDataBoardWithFilters(BoardFilterInputForm
    // boardFilterInputForm);

    List<Project> getAllProjects();
}
