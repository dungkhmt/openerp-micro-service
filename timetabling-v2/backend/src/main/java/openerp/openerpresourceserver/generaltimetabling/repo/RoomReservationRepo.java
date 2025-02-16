package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoomReservationRepo extends JpaRepository<RoomReservation, Long> {
    List<RoomReservation> findAllByIdIn(List<Long> rrIds);
    List<RoomReservation> findAllByGeneralClass(GeneralClass generalClass);

    @Transactional
    @Modifying
    @Query("DELETE FROM RoomReservation rr WHERE rr.generalClass.id IN :classIds")
    void deleteByGeneralClassIds(@Param("classIds") List<Long> classIds);
}
