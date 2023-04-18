package com.hust.baseweb.applications.taskmanagement.service;

import com.hust.baseweb.applications.taskmanagement.dto.form.ProjectMemberForm;
import com.hust.baseweb.applications.taskmanagement.entity.ProjectMember;
import com.hust.baseweb.entity.Person;
import com.hust.baseweb.entity.UserLogin;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ProjectMemberService {
    List<Person> getMemberIdJoinedProject(UUID projectId);

    ProjectMember setProjectMember(ProjectMember projectMember);

    UserLogin getUserLoginByPartyId(UUID partyId);

    ProjectMember addMemberToProject(ProjectMemberForm projectMemberForm);

    ProjectMember create(ProjectMember projectMember);

    boolean checkAddedMemberInProject(UUID partyId, UUID projectId);
}
