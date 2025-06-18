package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.dto.GroupUserDTO;
import com.hust.openerp.taskmanagement.dto.form.GroupUserForm;
import com.hust.openerp.taskmanagement.dto.projection.TaskCountProjection;
import com.hust.openerp.taskmanagement.entity.Group;
import com.hust.openerp.taskmanagement.entity.GroupUser;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.model.TaskStatusEnum;
import com.hust.openerp.taskmanagement.repository.GroupUserRepository;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.repository.UserRepository;
import com.hust.openerp.taskmanagement.service.GroupUserService;
import com.hust.openerp.taskmanagement.service.PermissionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GroupUserServiceImplement implements GroupUserService {

    private final GroupUserRepository groupUserRepository;
    private final PermissionService permissionService;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public List<GroupUserDTO> getUsersByGroupId(String currentUserId, UUID groupId) {
        permissionService.checkGroupMember(currentUserId, groupId);

        List<GroupUser> groupUsers = groupUserRepository.findActiveByGroupId(groupId);
        List<String> userIds = groupUsers.stream().map(GroupUser::getUserId).collect(Collectors.toList());

        List<TaskCountProjection> taskCounts = taskRepository.countTasksByUsersAndStatus(userIds);

        Map<String, Map<String, Long>> userTaskStats = new HashMap<>();
        for (TaskCountProjection p : taskCounts) {
            userTaskStats
                .computeIfAbsent(p.getUserId(), k -> new HashMap<>())
                .put(p.getStatusId(), p.getCount());
        }

        return groupUsers.stream().map(user -> {
            GroupUserDTO dto = modelMapper.map(user, GroupUserDTO.class);
            Map<String, Long> stats = userTaskStats.getOrDefault(user.getUserId(), Collections.emptyMap());

            long completed = stats.getOrDefault(TaskStatusEnum.TASK_RESOLVED.getStatusId(), 0L);
            long inProgress = stats.getOrDefault(TaskStatusEnum.TASK_INPROGRESS.getStatusId(), 0L);
            long total = stats.values().stream().mapToLong(Long::longValue).sum();
            long uncompleted = total - completed - inProgress;

            dto.setCompletedTasks((int) completed);
            dto.setInProgressTasks((int) inProgress);
            dto.setTotalTasks((int) total);
            dto.setUncompletedTasks((int) uncompleted);
            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void addUserToGroup(String currentUserId, UUID groupId, GroupUserForm request) {
        Group group = permissionService.checkGroupCreator(currentUserId, groupId);

        for (String userId : request.getUserIds()) {
            if (userRepository.existsById(userId)) {
                throw new ApiException(ErrorCode.USER_NOT_EXIST);
            }

            Optional<GroupUser> existingRecord = groupUserRepository.findByGroupIdAndUserId(groupId, userId);
            if (existingRecord.isPresent()) {
                GroupUser groupUser = existingRecord.get();
                if (groupUser.getThrsDate() == null) {
                    throw new ApiException(ErrorCode.GROUP_USER_ADDED);
                } else {
                    groupUser.setThrsDate(null);
                    groupUser.setFromDate(new Date());
                    groupUserRepository.save(groupUser);
                }
            } else {
                GroupUser groupUser = GroupUser.builder()
                    .groupId(groupId)
                    .userId(userId)
                    .fromDate(new Date())
                    .group(group)
                    .build();
                groupUserRepository.save(groupUser);
            }
        }
    }

    @Override
    @Transactional
    public void removeUserFromGroup(String currentUserId, UUID groupId, String userId) {
        permissionService.checkGroupCreator(currentUserId, groupId);

        GroupUser groupUser = groupUserRepository.findByGroupIdAndUserId(groupId, userId)
            .orElseThrow(() -> new ApiException(ErrorCode.GROUP_USER_NOT_FOUND));
        if (groupUser.getThrsDate() != null) {
            throw new ApiException(ErrorCode.GROUP_USER_DELETED);
        }

        groupUser.setThrsDate(new Date());
        groupUserRepository.save(groupUser);
    }
}