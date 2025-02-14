package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomReservationRepo extends JpaRepository<RoomReservation, Long> {
    List<RoomReservation> findAllByIdIn(List<Long> rrIds);
    List<RoomReservation> findAllByGeneralClass(GeneralClass generalClass);
}
