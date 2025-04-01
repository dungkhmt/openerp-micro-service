package openerp.openerpresourceserver.generaltimetabling.service.impl;

import openerp.openerpresourceserver.generaltimetabling.exception.GroupNotFoundException;
import openerp.openerpresourceserver.generaltimetabling.exception.GroupUsedException;
import openerp.openerpresourceserver.generaltimetabling.mapper.GroupMapper;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.GroupDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.PriorityGroupUpdateDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassOpened;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;
import openerp.openerpresourceserver.generaltimetabling.model.entity.GroupRoomPriority;
import openerp.openerpresourceserver.generaltimetabling.repo.ClassOpenedRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.GroupRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.GroupRoomPriorityRepo;
import openerp.openerpresourceserver.generaltimetabling.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private ClassOpenedRepo classOpenedRepo;

    @Autowired
    private GroupRoomPriorityRepo groupRoomPriorityRepo;

    @Autowired
    private GroupMapper groupMapper;

    public List<GroupDto> getGroupByGroupId(Long groupId) {
        return groupRepo.getGroupWithRoomAndPriorityByGroupId(groupId);
    }

    @Override
    @Transactional
    public Group create(GroupDto groupDto) {
        // Kiểm tra xem groupName đã tồn tại chưa
        Group existingGroup = groupRepo.findByGroupName(groupDto.getGroupName()).orElse(null);

        // Kiểm tra xem cặp (groupId, roomId) đã tồn tại trong GroupRoomPriority chưa
        List<GroupRoomPriority> existingPriority = groupRoomPriorityRepo.findByGroupIdAndRoomId(existingGroup.getId(), groupDto.getRoomName());
        if (!existingPriority.isEmpty()) {
            throw new GroupUsedException("Nhóm " + groupDto.getGroupName() + " với phòng học " + groupDto.getRoomName() + " đã tồn tại!");
        }

        // Nếu không tồn tại, tạo mới GroupRoomPriority
        GroupRoomPriority groupRoomPriority = new GroupRoomPriority();
        groupRoomPriority.setGroupId(existingGroup.getId());
        groupRoomPriority.setRoomId(groupDto.getRoomName());
        groupRoomPriority.setPriority(groupDto.getPriority());

        groupRoomPriorityRepo.save(groupRoomPriority);
        return existingGroup; // Trả về nhóm đã tồn tại
    }

    @Override
    @Transactional
    public Group createGroup(String groupName) {
        Group existingGroup = groupRepo.findByGroupName(groupName).orElse(null);
        if (existingGroup != null) {
            return null;
        }

        Group group = new Group();
        group.setGroupName(groupName);
        groupRepo.save(group);
        return group;
    }

    @Override
    public List<Group> getAllGroup(){
        return groupRepo.findAll();
    }

    @Override
    @Transactional
    public void updateGroup(PriorityGroupUpdateDto requestDto) {
        Long groupId = requestDto.getId();
        String newRoomId = requestDto.getRoomName();
        String oldRoomId = requestDto.getOldRoomName();

        // Kiểm tra xem nhóm có tồn tại không
        Group group = groupRepo.findById(groupId).orElseThrow(() ->
                new GroupNotFoundException("Không tồn tại nhóm với ID: " + groupId));

        List<GroupRoomPriority> existingPriorities = groupRoomPriorityRepo.findByGroupId(groupId);

        // Nếu nhóm chưa có bản ghi nào trong `GroupRoomPriority`, tạo mới luôn
        if (existingPriorities.isEmpty()) {
            GroupRoomPriority newPriority = new GroupRoomPriority();
            newPriority.setGroupId(groupId);
            newPriority.setRoomId(newRoomId);
            newPriority.setPriority(requestDto.getPriority());

            groupRoomPriorityRepo.save(newPriority);
            return;  // Kết thúc vì đã tạo mới
        }

        // Kiểm tra nếu `oldRoomId` không tồn tại trong nhóm
        boolean oldRoomExists = existingPriorities.stream()
                .anyMatch(priority -> priority.getRoomId().equals(oldRoomId));

        if (!oldRoomExists) {
            // Nếu `oldRoomId` không có trong DB, tạo mới bản `GroupRoomPriority`
            GroupRoomPriority newPriority = new GroupRoomPriority();
            newPriority.setGroupId(groupId);
            newPriority.setRoomId(newRoomId);
            newPriority.setPriority(requestDto.getPriority());

            groupRoomPriorityRepo.save(newPriority);
            return;  // Kết thúc vì đã tạo mới
        }

        // Kiểm tra nếu `newRoomId` đã tồn tại (trừ bản `oldRoomId` đang sửa)
        boolean isDuplicate = existingPriorities.stream()
                .filter(priority -> !priority.getRoomId().equals(oldRoomId))
                .anyMatch(priority -> priority.getRoomId().equals(newRoomId));

        if (isDuplicate) {
            throw new GroupUsedException("Nhóm " + requestDto.getGroupName() +
                    " với phòng học " + newRoomId + " đã tồn tại!");
        }

        // Nếu `room_id` thay đổi, xóa bản ghi cũ và thêm bản mới
        groupRoomPriorityRepo.deleteByGroupIdAndRoomId(groupId, oldRoomId);

        GroupRoomPriority newPriority = new GroupRoomPriority();
        newPriority.setGroupId(groupId);
        newPriority.setRoomId(newRoomId);
        newPriority.setPriority(requestDto.getPriority());

        groupRoomPriorityRepo.save(newPriority);
    }


    @Override
    @Transactional
    public void deletePriorityGroup(Long id, String roomId) {
        try {
            Group group = groupRepo.findById(id).orElse(null);
            if (group == null) {
                throw new GroupNotFoundException("Không tồn tại nhóm học với ID: " + id);
            }

            List<GroupRoomPriority> existingPriority = groupRoomPriorityRepo.findByGroupIdAndRoomId(id, roomId);
            if (!existingPriority.isEmpty()) {
                groupRoomPriorityRepo.deleteByGroupIdAndRoomId(id, roomId);
            } else {
                throw new GroupUsedException("Cặp group_id và room_id không tồn tại trong bảng GroupRoomPriority!");
            }

        } catch (Exception e) {
            // Log the error
            System.out.println("Error during deleting group priority: " + e.getMessage());
            throw new RuntimeException("Error occurred while processing delete request.");
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id){
        groupRepo.deleteById(id);
    }


    @Override
    @Transactional
    public void updateGroupName(Long id, String newName) {
        Group group = groupRepo.findById(id).orElseThrow(() ->
                new GroupNotFoundException("Không tồn tại nhóm với ID: " + id)
        );
        List<Group> existingGroups = groupRepo.getAllByGroupName(newName);
        if (!existingGroups.isEmpty()) {
            throw new GroupUsedException("Tên nhóm " + newName + " đã tồn tại!");
        }

        group.setGroupName(newName);
        groupRepo.save(group);
    }

}
