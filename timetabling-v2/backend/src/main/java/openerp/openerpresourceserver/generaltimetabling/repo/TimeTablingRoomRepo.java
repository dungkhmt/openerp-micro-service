package openerp.openerpresourceserver.generaltimetabling.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.TimeTablingRoom;

public interface TimeTablingRoomRepo extends JpaRepository<TimeTablingRoom, String> {

}
