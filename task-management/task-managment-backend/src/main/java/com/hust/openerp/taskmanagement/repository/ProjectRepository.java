package com.hust.openerp.taskmanagement.repository;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<Project> findById(UUID id);

    @Query(value = "DELETE FROM task_management_project e WHERE e.id = :projectId", nativeQuery = true)
    boolean deleteByProjectId(@Param("projectId") UUID projectId);

    @Query(value = "SELECT e.* FROM task_management_project e ORDER BY e.created_stamp DESC", nativeQuery = true)
    Page<Project> getAll(Pageable pageable);
}
