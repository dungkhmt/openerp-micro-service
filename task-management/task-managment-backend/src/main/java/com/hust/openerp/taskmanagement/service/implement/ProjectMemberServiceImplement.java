package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.dto.form.ProjectMemberForm;
import com.hust.openerp.taskmanagement.entity.Project;
import com.hust.openerp.taskmanagement.entity.ProjectMember;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.repository.ProjectMemberRepository;
import com.hust.openerp.taskmanagement.repository.ProjectRepository;
import com.hust.openerp.taskmanagement.service.MailService;
import com.hust.openerp.taskmanagement.service.NotificationService;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectMemberServiceImplement implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectRepository projectRepository;

    private final MailService mailService;

    private final UserService userService;

    private final NotificationService notificationsService;

    @Override
    public List<User> getMemberIdJoinedProject(UUID projectId) {
        List<ProjectMember> projectMembers = projectMemberRepository.findAllProjectMemberByProjectId(projectId);
        List<User> users = new ArrayList<>();
        for (ProjectMember projectMember : projectMembers) {
            User member = projectMember.getMember();
            if (member != null) {
                users.add(member);
            }
        }
        return users;
    }

    @Override
    public ProjectMember setProjectMember(ProjectMember projectMember) {
        return projectMemberRepository.save(projectMember);
    }

    @Override
    public ProjectMember addMemberToProject(ProjectMemberForm projectMemberForm) {
        UUID projectId = UUID.fromString(projectMemberForm.getProjectId());
        String memberId = projectMemberForm.getMemberId();
        Project project = projectRepository.findById(projectId).orElseThrow();
        // TODO: fix hard code role
        ProjectMember projectMember = ProjectMember.builder().projectId(projectId)
                .userId(memberId).roleId("member").build();
        ProjectMember projectMemberRes = projectMemberRepository.save(projectMember);

        notificationsService.sendNotification(
                "admin",
                memberId,
                "Bạn được thêm vào dự án " + project.getName(),
                "/taskmanagement/project/" + projectId + "/tasks");

        // send mail to anounce user to join project

        User member = userService.findById(memberId);

        if (member != null) {
            String emailUser = member.getEmail();
            mailService.sendSimpleMail(
                    new String[] { emailUser },
                    "OPEN ERP - Thông báo bạn đã được thêm vào dự án mới",
                    "Bạn đã được thêm dự án " +
                            project.getName() +
                            ". Đây là email tự động, bạn không trả lời lại email này!",
                    "OpenERP");
        }

        return projectMemberRes;
    }

    @Override
    public ProjectMember create(ProjectMember projectMember) {
        return projectMemberRepository.save(projectMember);
    }

    @Override
    public boolean checkAddedMemberInProject(String memberId, UUID projectId) {
        return projectMemberRepository.isAddedMemberInProject(memberId, projectId) > 0 ? true : false;
    }

}
