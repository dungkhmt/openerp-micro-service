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
import com.hust.openerp.taskmanagement.entity.ProjectRole;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.EventUserRepository;
import com.hust.openerp.taskmanagement.repository.ProjectMemberRepository;
import com.hust.openerp.taskmanagement.repository.ProjectRepository;
import com.hust.openerp.taskmanagement.repository.ProjectRoleRepository;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.service.NotificationService;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.UserService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectMemberServiceImplement implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectRepository projectRepository;

    private final UserService userService;
    
    private final ProjectRoleRepository projectRoleRepository;
    
    private final TaskRepository taskRepository;
    
    private final EventUserRepository eventUserRepository;

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
    	projectRepository.findById(projectId).orElseThrow(() -> new ApiException(ErrorCode.PROJECT_NOT_EXIST));

		if (!this.checkAddedMemberInProject(memberId, projectId)) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}
		
    	ProjectMember pm = projectMemberRepository.findByProjectIdAndUserId(projectId, memberId);
    	if(pm == null) throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
    	
    	return pm.getRoleId();
	}

	@Override
	@Transactional
	public void deleteMemberFromProject(String userId, UUID projectId, String memberId, String roleId) {
		projectRepository.findById(projectId).orElseThrow(() -> new ApiException(ErrorCode.PROJECT_NOT_EXIST));

		if (!this.checkAddedMemberInProject(userId, projectId)) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}
		
		String userRole = this.getMemberRole(userId, projectId);
	    String targetRole = this.getMemberRole(memberId, projectId);

	    // Rule enforcement
	    if (!userId.equals(memberId)) {
	        if (userRole.equals("member")) {
	            throw new ApiException(ErrorCode.INSUFFICIENT_PERMISSIONS);
	        }
	        if (userRole.equals("maintainer") && !targetRole.equals("member")) {
	            throw new ApiException(ErrorCode.INSUFFICIENT_PERMISSIONS);
	        }
	    }
		
		// Check if the leaving member is an owner
	    List<ProjectMember> owners = projectMemberRepository.findByProjectIdAndRoleId(projectId, "owner");
	    boolean isLeavingOwner = owners.stream().anyMatch(owner -> owner.getMember().getId().equals(memberId));

	    // If the leaving member is the only owner, prevent deletion
	    if (isLeavingOwner && owners.size() == 1) {
	        throw new ApiException(ErrorCode.LAST_OWNER_CANNOT_LEAVE);
	    }

		ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId, roleId);

		if (projectMemberRepository.existsById(projectMemberId)) {
			projectMemberRepository.deleteById(projectMemberId);
		} else {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}
		
		// Unassign member from all tasks in this project
		taskRepository.unassignUserFromTasks(memberId, projectId);

	    // Remove the member from all events in this project.
	    eventUserRepository.removeUserFromEvents(projectId, memberId);
	    
		if (memberId == null || (userId != null && userId.equals(memberId))) {
			return;
		}
		
		Project project = projectRepository.findById(projectId).orElseThrow();
        User member = userService.findById(memberId);
        if (member == null) {
            throw new ApiException(ErrorCode.USER_NOT_EXIST);
        }
		try {
		    // Create in-app notification
		    notificationService.createInAppNotification(
		    	userId,
		        memberId,
		        "Bạn đã bị xóa khỏi dự án " + project.getName(),
		        "/projects"
		    );

		    // Send email notification if the user's email is available
		    String emailUser = member.getEmail();
		    User remover = userService.findById(userId);
		    if (emailUser != null) {
		        Map<String, Object> model = new HashMap<>();
		        model.put("projectName", project.getName());
		        model.put("memberFirstName", member.getFirstName());
		        model.put("memberLastName", member.getLastName());
		        model.put("projectManager",
		                remover.getFirstName() + " " + remover.getLastName());

		        notificationService.createMailNotification(remover.getEmail(), emailUser,
		                "Bạn đã bị xóa khỏi dự án " + project.getName(), "remove-member", model);
		    }

		} catch (Exception e) {
		    e.printStackTrace();
		    // TODO: handle exception
		}
	}

	@Override
	public ProjectMember updateMemberRole(String updaterId, ProjectMember projectMemberForm) {
		UUID projectId = projectMemberForm.getProjectId();
        String memberId = projectMemberForm.getUserId();
        String roleId = projectMemberForm.getRoleId();

        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ApiException(ErrorCode.PROJECT_NOT_EXIST));

        if(!this.getMemberRole(updaterId, projectId).equals("owner")) {
			throw new ApiException(ErrorCode.INSUFFICIENT_PERMISSIONS);
		}
        
        if (!this.checkAddedMemberInProject(memberId, projectId)) {
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
        }
        
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, memberId);
        if(projectMember == null) throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
        
        projectMemberRepository.delete(projectMember);
        
        ProjectMember newProjectMember = new ProjectMember();
        
        if (projectId != null) {
        	newProjectMember.setProjectId(projectId);
		}
		if (memberId != null) {
			newProjectMember.setUserId(memberId);
		}
		if (roleId != null ) {
			newProjectMember.setRoleId(roleId);
		}
		
        var projectMemberRes = projectMemberRepository.save(newProjectMember);

        
        ProjectRole newRole = projectRoleRepository.findById(roleId).orElseThrow(
        		() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));
        User member = userService.findById(memberId);
        if (member == null) {
            throw new ApiException(ErrorCode.USER_NOT_EXIST);
        }
        try {
            // Create in-app notification
            notificationService.createInAppNotification(
                updaterId,
                memberId,
                "Vai trò của bạn trong dự án " + project.getName() + " đã được cập nhật",
                "/project/" + projectId + "/members"
            );

            // Send email notification if the user's email is available
            String emailUser = member.getEmail();
            User adder = userService.findById(updaterId);
            if (emailUser != null) {
                Map<String, Object> model = new HashMap<>();
                User creator = project.getCreator();
                model.put("projectName", project.getName());
                model.put("memberFirstName", member.getFirstName());
                model.put("memberLastName", member.getLastName());
                model.put("projectManager",
                        creator == null ? ""
                                : project.getCreator().getFirstName() + " " + project.getCreator().getLastName());
                model.put("newRole", newRole.getDescription());  // Assume newRole is the updated role of the member

                notificationService.createMailNotification(adder.getEmail(), emailUser,
                        "Vai trò của bạn trong dự án " + project.getName() + " đã được cập nhật", "update-role", model);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }


        return projectMemberRes;
	}

}
