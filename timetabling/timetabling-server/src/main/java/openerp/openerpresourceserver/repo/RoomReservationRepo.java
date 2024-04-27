package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.general.RoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomReservationRepo extends JpaRepository<RoomReservation, Long> {
}
