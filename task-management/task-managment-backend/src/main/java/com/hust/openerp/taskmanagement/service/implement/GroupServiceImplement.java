package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.dto.GroupDTO;
import com.hust.openerp.taskmanagement.dto.form.GroupForm;
import com.hust.openerp.taskmanagement.entity.Group;
import com.hust.openerp.taskmanagement.entity.GroupUser;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.repository.GroupRepository;
import com.hust.openerp.taskmanagement.repository.GroupUserRepository;
import com.hust.openerp.taskmanagement.service.GroupService;
import com.hust.openerp.taskmanagement.service.PermissionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GroupServiceImplement implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;
    private final PermissionService permissionService;
    private final ModelMapper modelMapper;

    @Override
    public GroupDTO getGroupById(String currentUserId, UUID id) {
        Group group = permissionService.checkGroupMember(currentUserId, id);
        return modelMapper.map(group, GroupDTO.class);
    }

    @Override
    public List<GroupDTO> getGroupsByUserId(String userId) {
        List<Group> groups = groupRepository.findActiveByUserId(userId);
        List<UUID> groupIds = groups.stream().map(Group::getId).collect(Collectors.toList());
        List<GroupUser> groupUsers = groupIds.isEmpty() ? List.of() : groupUserRepository.findActiveByGroupIds(groupIds);

        Map<UUID, List<User>> usersByGroupId = groupUsers.stream()
            .collect(Collectors.groupingBy(
                GroupUser::getGroupId,
                Collectors.mapping(GroupUser::getUser, Collectors.toList())
            ));

        return groups.stream()
            .map(group -> {
                GroupDTO groupDTO = modelMapper.map(group, GroupDTO.class);
                groupDTO.setMembers(usersByGroupId.getOrDefault(group.getId(), List.of()));
                return groupDTO;
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GroupDTO createGroup(String currentUserId, GroupForm request) {
        Group group = Group.builder()
            .id(UUID.randomUUID())
            .name(request.getName())
            .description(request.getDescription())
            .createdBy(currentUserId)
            .createdStamp(new Date())
            .build();
        group = groupRepository.save(group);

        GroupUser groupUser = GroupUser.builder()
            .groupId(group.getId())
            .userId(currentUserId)
            .fromDate(new Date())
            .build();
        groupUserRepository.save(groupUser);

        return modelMapper.map(group, GroupDTO.class);
    }

    @Override
    @Transactional
    public GroupDTO updateGroup(String currentUserId, UUID id, GroupForm request) {
        Group group = permissionService.checkGroupCreator(currentUserId, id);

        group.setName(request.getName());
        group.setDescription(request.getDescription());

        group = groupRepository.save(group);
        return modelMapper.map(group, GroupDTO.class);
    }
}