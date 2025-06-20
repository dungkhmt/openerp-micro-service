package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.entity.Skill;
import com.hust.openerp.taskmanagement.entity.TaskSkill;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.repository.TaskSkillRepository;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.TaskSkillService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskSkillServiceImplement implements TaskSkillService {
	private final TaskSkillRepository taskSkillRepository;

    private final TaskRepository taskRepository;
    
    private ProjectMemberService projectMemberService;
	

    @Override
    public List<Skill> getTaskSkills(UUID taskId, String userId) {
    	var task = taskRepository.findById(taskId).orElseThrow(
                () -> new ApiException(ErrorCode.TASK_NOT_FOUND));

        if (!projectMemberService.checkAddedMemberInProject(userId, task.getProjectId())) {
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
        }
        
        var entities = taskSkillRepository.findByTaskId(taskId);
        return entities.stream().map(entity -> entity.getSkill()).toList();
    }

	@Override
	@Transactional
	public void updateTaskSkills(UUID taskId, List<UUID> skillIdList, String userId) {
		var task = taskRepository.findById(taskId).orElseThrow(
                () -> new ApiException(ErrorCode.TASK_NOT_EXIST));

        if (!projectMemberService.checkAddedMemberInProject(userId, task.getProjectId())) {
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
        }
        
        taskSkillRepository.deleteByTaskId(taskId);
        
        for(UUID skillId : skillIdList) {
    		TaskSkill taskSkill = TaskSkill.builder().taskId(taskId).skillId(skillId).build();
    		taskSkillRepository.save(taskSkill);
    	}
	}
}
