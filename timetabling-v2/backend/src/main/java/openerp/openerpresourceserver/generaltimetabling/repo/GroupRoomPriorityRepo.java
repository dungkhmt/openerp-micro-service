package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.GroupRoomPriority;
import openerp.openerpresourceserver.generaltimetabling.model.entity.GroupRoomPriorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRoomPriorityRepo extends JpaRepository<GroupRoomPriority, GroupRoomPriorityId> {

    @Query("SELECT g FROM GroupRoomPriority g WHERE g.groupId = :groupId AND g.roomId = :roomId")
    List<GroupRoomPriority> findByGroupIdAndRoomId(Long groupId, String roomId);

    void deleteByGroupIdAndRoomId(Long groupId, String roomId);

    void deleteByGroupId(Long groupId);

    List<GroupRoomPriority> findByGroupId(Long groupId);
}
