package com.hust.openerp.taskmanagement.service.implement;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.SearchDTO;
import com.hust.openerp.taskmanagement.repository.ProjectRepository;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.service.SearchService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchServiceImplement implements SearchService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Override
    public SearchDTO getRecentActivity(String userId) {
        final int MAX_COUNT = 6;
        var tasks = taskRepository.getRecentTask(userId, 4);
        var projects = projectRepository.getRecentProjects(userId, MAX_COUNT - tasks.size());

        if (projects.isEmpty()) {
            projects = projectRepository.getRecentAddedProjects(userId, MAX_COUNT - tasks.size());
        }

        return new SearchDTO(projects, tasks);
    }

    @Override
    public SearchDTO search(String userId, String keyword) {
        // TODO: implement search with full-text search
        // tasks search
        var tasks = taskRepository.search(userId, keyword);

        // projects search
        var projects = projectRepository.search(userId, keyword);
        return new SearchDTO(projects, tasks);
    }
}
