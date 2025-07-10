package openerp.openerpresourceserver.generaltimetabling.service.impl;

import openerp.openerpresourceserver.generaltimetabling.exception.GroupNotFoundException;
import openerp.openerpresourceserver.generaltimetabling.exception.GroupUsedException;
import openerp.openerpresourceserver.generaltimetabling.mapper.GroupMapper;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.GroupDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassOpened;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;
import openerp.openerpresourceserver.generaltimetabling.repo.ClassOpenedRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.GroupRepo;
import openerp.openerpresourceserver.generaltimetabling.service.GroupService;
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
            throw new GroupUsedException("Nhóm " + groupDto.getGroupName() + " đã tồn tại!");
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
        if (!group.getGroupName().equals(requestDto.getGroupName())) {
            List<Group> groupList = groupRepo.getAllByGroupName(requestDto.getGroupName());
            if (!groupList.isEmpty()) {
                throw new GroupUsedException("Nhóm " + requestDto.getGroupName() + "đã tồn tại! ");
            }
            List<ClassOpened> classOpenedList = classOpenedRepo.getAllByGroupName(group.getGroupName(), null);
            classOpenedList.forEach(el -> {
                el.setGroupName(requestDto.getGroupName());
                classOpenedRepo.save(el);
            });
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
