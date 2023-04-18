package com.hust.baseweb.applications.taskmanagement.repository;

import com.hust.baseweb.applications.taskmanagement.entity.Project;
import com.hust.baseweb.applications.taskmanagement.entity.TaskAssignment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;


public interface ProjectRepository extends JpaRepository<Project, Integer>, CrudRepository<Project, Integer>, PagingAndSortingRepository<Project, Integer> {

    Project findById(UUID id);

    @Query(value = "DELETE FROM backlog_project e WHERE e.backlog_project_id = :projectId", nativeQuery = true)
    boolean deleteByProjectId(@Param("projectId") UUID projectId);

    @Query(value = "SELECT e.* FROM backlog_project e ORDER BY e.created_stamp DESC", nativeQuery = true)
    Page<Project> getAll(Pageable pageable);
}
