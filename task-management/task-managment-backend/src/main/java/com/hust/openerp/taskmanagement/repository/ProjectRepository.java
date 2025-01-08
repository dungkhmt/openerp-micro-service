package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.dto.SearchProjectDTO;
import com.hust.openerp.taskmanagement.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID>, JpaSpecificationExecutor<Project> {
    boolean existsByCode(String code);

    @Query("""
            Select new com.hust.openerp.taskmanagement.dto.SearchProjectDTO(p.id, p.name) from Project p
            join (
                select t.projectId as projectId, MAX(tl.createdAt) as lastActivity from Task t
                join TaskLog tl on t.id = tl.taskId
                where tl.creatorId = ?1
                group by t.projectId
                order by lastActivity desc
                limit ?2
            ) as recentProjects on p.id = recentProjects.projectId
            order by recentProjects.lastActivity desc
            """)
    List<SearchProjectDTO> getRecentProjects(String userId, Integer limit);

    @Query("""
            Select new com.hust.openerp.taskmanagement.dto.SearchProjectDTO(p.id, p.name) from Project p
            join ProjectMember pm on p.id = pm.projectId
            where pm.userId = ?1
            order by pm.createdStamp desc
            limit ?2
            """)
    List<SearchProjectDTO> getRecentAddedProjects(String userId, Integer limit);

    @Query("""
            Select new com.hust.openerp.taskmanagement.dto.SearchProjectDTO(p.id, p.name) from Project p
            where p.name ilike %:keyword%
            and p.id in (
                select pm.projectId from ProjectMember pm where pm.userId = :userId
            )
            """)
    List<SearchProjectDTO> search(String userId, String keyword);
    
    @Query("SELECT p.code FROM Project p")
    List<String> findAllProjectCode();
}
