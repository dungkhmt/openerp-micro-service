package com.hust.openerp.taskmanagement.service.implement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.Project;
import com.hust.openerp.taskmanagement.entity.ProjectMember;
import com.hust.openerp.taskmanagement.entity.ProjectMember.ProjectMemberId;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.ProjectMemberRepository;
import com.hust.openerp.taskmanagement.repository.ProjectRepository;
import com.hust.openerp.taskmanagement.service.NotificationService;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectMemberServiceImplement implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectRepository projectRepository;

    private final UserService userService;

    private final NotificationService notificationService;

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
        var projectMemberRes = projectMemberRepository.save(projectMember);

        Project project = projectRepository.findById(projectId).orElseThrow();

        try {
            notificationService.createInAppNotification(
                    adderId,
                    memberId,
                    "Bạn được thêm vào dự án " + project.getName(),
                    "/project/" + projectId);

            String emailUser = member.getEmail();
            User adder = userService.findById(adderId);
            if (emailUser != null) {
                Map<String, Object> model = new HashMap<>();
                User creator = project.getCreator();
                model.put("projectName", project.getName());
                model.put("memberFirstName", member.getFirstName());
                model.put("memberLastName", member.getLastName());
                model.put("projectManager",
                        creator == null ? ""
                                : project.getCreator().getFirstName() + " " + project.getCreator().getLastName());
                // mailService.sendUsingTemplate(new String[] { emailUser }, null, null,
                // "Bạn được thêm vào dự án " + project.getName(), "new-member", model);
                notificationService.createMailNotification(adder.getEmail(), emailUser,
                        "Bạn được thêm vào dự án " + project.getName(), "new-member", model);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

        return projectMemberRes;
    }

    @Override
    public boolean checkAddedMemberInProject(String memberId, UUID projectId) {
        return projectMemberRepository.isAddedMemberInProject(memberId, projectId) > 0;
    }
    
    @Override
	public String getMemberRole(String memberId, UUID projectId) {
    	ProjectMember pm = projectMemberRepository.findByProjectIdAndUserId(projectId, memberId);
    	return pm.getRoleId();
	}

	@Override
	public void deleteMemberFromProject(String userId, UUID projectId, String memberId, String roleId) {
		projectRepository.findById(projectId).orElseThrow(() -> new ApiException(ErrorCode.PROJECT_NOT_EXIST));

		if (!this.checkAddedMemberInProject(userId, projectId)) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}
		
		if(!this.getMemberRole(userId, projectId).equals("owner")) {
			throw new ApiException(ErrorCode.NOT_OWNER_OF_PROJECT);
		}

		ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId, roleId);

		if (projectMemberRepository.existsById(projectMemberId)) {
			projectMemberRepository.deleteById(projectMemberId);
		} else {
			throw new ApiException(ErrorCode.PROJECT_MEMBER_NOT_EXIST);
		}
	}

}
