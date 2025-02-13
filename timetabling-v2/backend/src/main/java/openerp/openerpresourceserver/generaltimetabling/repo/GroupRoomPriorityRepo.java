package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.composite.CompositeGroupRoomId;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GroupRoomPriority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRoomPriorityRepo extends JpaRepository<GroupRoomPriority, CompositeGroupRoomId> {

}
