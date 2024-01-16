package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.exception.GroupNotFoundException;
import openerp.openerpresourceserver.exception.GroupUsedException;
import openerp.openerpresourceserver.mapper.GroupMapper;
import openerp.openerpresourceserver.model.dto.request.GroupDto;
import openerp.openerpresourceserver.model.entity.ClassOpened;
import openerp.openerpresourceserver.model.entity.Group;
import openerp.openerpresourceserver.repo.ClassOpenedRepo;
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
    private ClassOpenedRepo classOpenedRepo;

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
            throw new GroupUsedException("Nhóm đã tồn tại: " + groupDto.getGroupName());
        }
        Group group = groupMapper.mapDtoToEntity(groupDto);
        groupRepo.save(group);
        return group;
    }

    @Override
    public void updateGroup(GroupDto requestDto) {
        Long id = requestDto.getId();
        Group group = groupRepo.findById(id).orElse(null);
        if (group == null) {
            throw new GroupNotFoundException("Không tồn tại nhóm với ID: " + id);
        }
        group.setGroupName(requestDto.getGroupName());
        group.setPriorityBuilding(requestDto.getPriorityBuilding());
        groupRepo.save(group);
    }

    @Override
    public void deleteById(Long id) {
        Group group = groupRepo.findById(id).orElse(null);
        if (group == null) {
            throw new GroupNotFoundException("Không tồn tại nhóm học với ID: " + id);
        }
        List<ClassOpened> classOpenedList = classOpenedRepo.getAllByGroupName(group.getGroupName(), null);
        if (!classOpenedList.isEmpty()) {
            throw new GroupUsedException("Nhóm đang được sử dụng. Không thể xóa!");
        }
        groupRepo.deleteById(id);
    }
}
