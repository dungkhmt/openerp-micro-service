package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.dao.ProjectPagination;
import com.hust.openerp.taskmanagement.dto.dao.StatusTaskDao;
import com.hust.openerp.taskmanagement.dto.form.BoardFilterInputForm;
import com.hust.openerp.taskmanagement.entity.Project;

@Service
public interface ProjectService {

    List<Project> getListProject();

    Project getProjectById(UUID id);

    Project createProject(Project project);

    void deleteProjectById(UUID id);

    Project save(Project project);

    ProjectPagination findPaginated(int pageNo, int pageSize);

    List<StatusTaskDao> getDataBoardWithFilters(BoardFilterInputForm boardFilterInputForm);

    List<Project> getAllProjects();
}
