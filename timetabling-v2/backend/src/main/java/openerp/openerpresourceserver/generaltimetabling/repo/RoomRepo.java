package openerp.openerpresourceserver.generaltimetabling.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.Room;

public interface RoomRepo extends JpaRepository<Room, String> {

}
