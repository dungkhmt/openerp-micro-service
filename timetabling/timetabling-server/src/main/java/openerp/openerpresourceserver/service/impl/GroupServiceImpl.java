package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.exception.EntityAlreadyExistsException;
import openerp.openerpresourceserver.mapper.GroupMapper;
import openerp.openerpresourceserver.model.dto.request.GroupDto;
import openerp.openerpresourceserver.model.entity.Group;
import openerp.openerpresourceserver.repo.GroupRepo;
import openerp.openerpresourceserver.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private GroupMapper groupMapper;

    @Override
    public List<Group> getGroup() {
        return groupRepo.findAll();
    }

    @Override
    public Group create(GroupDto groupDto) {
        List<Group> groupExist = groupRepo.getAllByGroupName(groupDto.getGroupName());
        if (!groupExist.isEmpty()) {
            throw new EntityAlreadyExistsException("Group existed: " + groupDto.getGroupName());
        }
        Group group = groupMapper.mapDtoToEntity(groupDto);
        groupRepo.save(group);
        return group;
    }
}
