package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.ProjectMember;

@Service
public interface ProjectMemberService {
    List<ProjectMember> getMembersOfProject(UUID projectId, String getterId);

    ProjectMember addMemberToProject(ProjectMember projectMemberForm, String adderId);

    boolean checkAddedMemberInProject(String memberId, UUID projectId);
}
