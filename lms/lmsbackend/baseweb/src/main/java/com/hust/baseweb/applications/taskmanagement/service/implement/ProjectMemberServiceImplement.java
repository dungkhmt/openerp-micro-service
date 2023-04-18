package com.hust.baseweb.applications.taskmanagement.service.implement;

import com.hust.baseweb.applications.taskmanagement.dto.form.ProjectMemberForm;
import com.hust.baseweb.applications.taskmanagement.entity.Project;
import com.hust.baseweb.applications.taskmanagement.entity.ProjectMember;
import com.hust.baseweb.applications.taskmanagement.entity.ProjectMemberId;
import com.hust.baseweb.applications.taskmanagement.repository.ProjectMemberRepository;
import com.hust.baseweb.applications.taskmanagement.repository.ProjectRepository;
import com.hust.baseweb.applications.taskmanagement.service.ProjectMemberService;
import com.hust.baseweb.applications.notifications.service.NotificationsService;
import com.hust.baseweb.applications.mail.service.MailService;
import com.hust.baseweb.entity.Person;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.entity.UserRegister;
import com.hust.baseweb.repo.UserLoginRepo;
import com.hust.baseweb.repo.UserRegisterRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hust.baseweb.repo.PersonRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectMemberServiceImplement implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    private final PersonRepo personRepo;

    private final UserLoginRepo userLoginRepo;

    private final NotificationsService notificationsService;

    private final MailService mailService;

    private final ProjectRepository projectRepository;

    private final UserRegisterRepo userRegisterRepo;

    @Override
    public List<Person> getMemberIdJoinedProject(UUID projectId) {
        List<ProjectMember> projectMembers = projectMemberRepository.findAllProjectMemberByProjectId(projectId);
        List<Person> persons = new ArrayList<>();
        for (ProjectMember projectMember : projectMembers) {
            Person person = personRepo.findByPartyId(projectMember.getId().getPartyID());
            persons.add(person);
        }
        return persons;
    }

    @Override
    public ProjectMember setProjectMember(ProjectMember projectMember) {
        return projectMemberRepository.save(projectMember);
    }

    @Override
    public UserLogin getUserLoginByPartyId(UUID partyId) {
        return userLoginRepo.finByPartyId(partyId);
    }

    @Override
    public ProjectMember addMemberToProject(ProjectMemberForm projectMemberForm) {
        UUID projectId = UUID.fromString(projectMemberForm.getProjectId());
        UUID partyId = UUID.fromString(projectMemberForm.getPartyId());
        Project project = projectRepository.findById(projectId);
        ProjectMember projectMember = new ProjectMember();
        ProjectMemberId projectMemberId = new ProjectMemberId();
        projectMemberId.setProjectId(projectId);
        projectMemberId.setPartyID(partyId);
        projectMember.setId(projectMemberId);
        ProjectMember projectMemberRes = projectMemberRepository.save(projectMember);

        // push notification
        String userLoginId = this.getUserLoginByPartyId(partyId).getUserLoginId();
        notificationsService.create(
            "admin",
            userLoginId,
            "Bạn được thêm vào dự án " + project.getName(),
            "/taskmanagement/project/" + projectId + "/tasks"
        );

        // send mail to anounce user to join project
        UserRegister userRegister = userRegisterRepo.findById(userLoginId).orElse(null);
        if (userRegister != null) {
            String emailUser = userRegister.getEmail();
            mailService.sendSimpleMail(
                new String[]{emailUser},
                "OPEN ERP - Thông báo bạn đã được thêm vào dự án mới",
                "Bạn đã được thêm dự án " +
                project.getName() +
                ". Đây là email tự động, bạn không trả lời lại email này!",
                "OpenERP"
            );
        }

        return projectMemberRes;
    }

    @Override
    public ProjectMember create(ProjectMember projectMember) {
        return projectMemberRepository.save(projectMember);
    }

    @Override
    public boolean checkAddedMemberInProject(UUID partyId, UUID projectId) {
        return projectMemberRepository.isAddedMemberInProject(partyId, projectId) > 0 ? true : false;
    }

}
