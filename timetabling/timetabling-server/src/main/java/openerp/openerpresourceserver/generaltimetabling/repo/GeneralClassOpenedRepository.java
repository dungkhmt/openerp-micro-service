package openerp.openerpresourceserver.generaltimetabling.repo;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClassOpened;

@Repository
public interface GeneralClassOpenedRepository extends JpaRepository<GeneralClassOpened, Long> {


    @Query("SELECT DISTINCT g FROM GeneralClassOpened g LEFT JOIN FETCH g.timeSlots WHERE g.semester = :semester")
    List<GeneralClassOpened> findBySemester(@Param("semester") String semester);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE timetabling_general_classes SET time_slots = ?2\\:\\:json WHERE id = ?1")
    void saveTimeSlot(long id, String newTimeSlotString);

    void deleteBySemester(String semester);

    List<GeneralClassOpened> findAllBySemester(String semester);

    List<GeneralClassOpened> findAllBySemesterAndGroupName(String semester, String groupName);

    List<GeneralClassOpened> findAllByIdIn(List<Long> l);
}
