package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.ProjectMember;

@Service
public interface ProjectMemberService {
    List<ProjectMember> getMembersOfProject(UUID projectId);

    /**
     * @deprecated use {@link #addMemberToProject(ProjectMember)} instead
     * @param projectMember
     * @return
     */
    @Deprecated
    ProjectMember setProjectMember(ProjectMember projectMember);

    ProjectMember addMemberToProject(ProjectMember projectMemberForm);

    /**
     * @deprecated use {@link #addMemberToProject(ProjectMember)} instead
     * @param projectMember
     * @return
     */
    @Deprecated
    ProjectMember create(ProjectMember projectMember);

    boolean checkAddedMemberInProject(String memberId, UUID projectId);
}
