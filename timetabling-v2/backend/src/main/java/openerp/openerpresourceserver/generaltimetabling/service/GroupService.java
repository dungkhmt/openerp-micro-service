package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.GroupDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.PriorityGroupUpdateDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;

import java.util.List;

public interface GroupService {

    List<Group> getAllGroup();

    Group create(GroupDto groupDto);

    Group createGroup(String groupName);

    List<GroupDto> getGroupByGroupId(Long groupId);

    void updateGroup(PriorityGroupUpdateDto requestDto);

    void deletePriorityGroup(Long id, String roomId);

    void deleteById(Long id);

    void updateGroupName(Long id, String groupName);
}
