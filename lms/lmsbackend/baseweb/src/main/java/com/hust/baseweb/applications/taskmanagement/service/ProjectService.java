package com.hust.baseweb.applications.taskmanagement.service;

import com.hust.baseweb.applications.taskmanagement.dto.dao.ProjectPagination;
import com.hust.baseweb.applications.taskmanagement.dto.dao.StatusTaskDao;
import com.hust.baseweb.applications.taskmanagement.dto.form.BoardFilterInputForm;
import com.hust.baseweb.applications.taskmanagement.entity.Project;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
