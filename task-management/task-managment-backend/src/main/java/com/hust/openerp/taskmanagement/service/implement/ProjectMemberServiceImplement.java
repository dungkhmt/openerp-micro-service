package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.entity.Project;
import com.hust.openerp.taskmanagement.entity.ProjectMember;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.ProjectMemberRepository;
import com.hust.openerp.taskmanagement.repository.ProjectRepository;
import com.hust.openerp.taskmanagement.service.MailService;
import com.hust.openerp.taskmanagement.service.NotificationService;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<ProjectMember> getMembersOfProject(UUID projectId, String userId) {
        if (!this.checkAddedMemberInProject(userId, projectId)) {
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
        }
        return projectMemberRepository.findAllProjectMemberByProjectId(projectId);
    }

    @Override
    public ProjectMember addMemberToProject(ProjectMember projectMemberForm, String adderId) {

        UUID projectId = projectMemberForm.getProjectId();
        String memberId = projectMemberForm.getUserId();

        projectRepository.findById(projectId).orElseThrow(
                () -> new ApiException(ErrorCode.PROJECT_NOT_EXIST));

        if (!this.checkAddedMemberInProject(adderId, projectId)) {
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
        }

        User member = userService.findById(memberId);
        if (member == null) {
            throw new ApiException(ErrorCode.USER_NOT_EXIST);
        }

        // FIX: hard code
        if (projectMemberForm.getRoleId() == null) {
            projectMemberForm.setRoleId("member");
        }

        ProjectMember projectMember = ProjectMember.builder().projectId(projectId)
                .userId(memberId).roleId(projectMemberForm.getRoleId()).build();
        return projectMemberRepository.save(projectMember);

        // try {
        // notificationsService.sendNotification(
        // "admin",
        // memberId,
        // "Bạn được thêm vào dự án " + project.getName(),
        // "/project/" + projectId + "/tasks");

        // // send mail to anounce user to join project
        // String emailUser = member.getEmail();
        // mailService.sendSimpleMail(
        // new String[] { emailUser },
        // "OPEN ERP - Thông báo bạn đã được thêm vào dự án mới",
        // "Bạn đã được thêm dự án " +
        // project.getName() +
        // ". Đây là email tự động, bạn không trả lời lại email này!",
        // "OpenERP");

        // } catch (Exception e) {
        // e.printStackTrace();
        // // TODO: handle exception
        // }

        // return projectMemberRes;
    }

    @Override
    public boolean checkAddedMemberInProject(String memberId, UUID projectId) {
        return projectMemberRepository.isAddedMemberInProject(memberId, projectId) > 0;
    }

}
