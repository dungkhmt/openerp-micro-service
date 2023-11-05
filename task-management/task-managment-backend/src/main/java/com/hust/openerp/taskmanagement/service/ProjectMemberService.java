package com.hust.openerp.taskmanagement.service;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.form.ProjectMemberForm;
import com.hust.openerp.taskmanagement.entity.ProjectMember;
import com.hust.openerp.taskmanagement.entity.User;

import java.util.List;
import java.util.UUID;

@Service
public interface ProjectMemberService {
    List<User> getMemberIdJoinedProject(UUID projectId);

    ProjectMember setProjectMember(ProjectMember projectMember);

    ProjectMember addMemberToProject(ProjectMemberForm projectMemberForm);

    ProjectMember create(ProjectMember projectMember);

    boolean checkAddedMemberInProject(String memberId, UUID projectId);
}
