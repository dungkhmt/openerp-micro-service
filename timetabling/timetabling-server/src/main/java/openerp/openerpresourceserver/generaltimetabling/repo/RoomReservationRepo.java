package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomReservationRepo extends JpaRepository<RoomReservation, Long> {
}
