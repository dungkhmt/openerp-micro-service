package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectMemberRepository
    extends JpaRepository<ProjectMember, ProjectMember.ProjectMemberId> {

    @Query("SELECT pm FROM ProjectMember pm JOIN FETCH pm.member WHERE pm.projectId = :projectId ORDER BY pm.userId")
    List<ProjectMember> findAllProjectMemberByProjectId(@Param("projectId") UUID projectId);

    @Query(value = "SELECT COUNT(e.*)\\:\\:int FROM task_management_project_member e WHERE e.project_id = :projectId AND e.user_id = :memberId", nativeQuery = true)
    int isAddedMemberInProject(@Param("memberId") String memberId, @Param("projectId") UUID projectId);

    ProjectMember findByProjectIdAndUserId(UUID projectId, String userId);

    List<ProjectMember> findByProjectIdAndRoleId(UUID projectId, String roleId);
}
