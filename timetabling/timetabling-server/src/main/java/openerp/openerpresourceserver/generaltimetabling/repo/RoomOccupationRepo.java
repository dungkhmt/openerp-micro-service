package openerp.openerpresourceserver.generaltimetabling.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;

@Repository
public interface RoomOccupationRepo extends JpaRepository<RoomOccupation, String> {
    
    public List<RoomOccupation> findAllBySemester(String semester);

    List<RoomOccupation> findAllBySemesterAndClassCodeAndDayIndexAndStartPeriodAndEndPeriodAndClassRoom(
            String semester, String classCode, int dayIndex, int startPeriod, int endPeriod, String classRoom);
    void deleteBySemester(String semester);

    List<RoomOccupation> findAllBySemesterAndWeekIndex(String semester, int i);

    void deleteAllByClassCodeAndStartPeriodAndEndPeriodAndDayIndexAndClassRoom(String classCode, int startPeriod, int endPeriod, int weekDay, String classRoom);

    void deleteAllByClassCodeIn(List<String> stringStream);
}
