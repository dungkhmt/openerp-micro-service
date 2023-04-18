package com.hust.baseweb.applications.taskmanagement.repository;

import com.hust.baseweb.applications.taskmanagement.entity.ProjectMember;
import com.hust.baseweb.applications.taskmanagement.entity.ProjectMemberId;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId>, CrudRepository<ProjectMember, ProjectMemberId> {

    @Query(value = "SELECT e.* FROM backlog_project_member e WHERE e.backlog_project_id = :projectId",
           nativeQuery = true)
    List<ProjectMember> findAllProjectMemberByProjectId(@Param("projectId") UUID projectId);

    @Query(value = "SELECT COUNT(e.*)\\:\\:int FROM backlog_project_member e WHERE e.backlog_project_id = :projectId AND e.member_party_id = :partyId", nativeQuery = true)
    int isAddedMemberInProject(@Param("partyId") UUID partyId, @Param("projectId") UUID projectId);
}
