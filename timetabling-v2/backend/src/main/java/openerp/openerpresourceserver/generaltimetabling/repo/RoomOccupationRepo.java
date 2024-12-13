package openerp.openerpresourceserver.generaltimetabling.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;

@Repository
public interface RoomOccupationRepo extends JpaRepository<RoomOccupation, String> {
    
    public List<RoomOccupation> findAllBySemester(String semester);

    List<RoomOccupation> findAllBySemesterAndClassCodeAndDayIndexAndStartPeriodAndEndPeriodAndClassRoom(
            String semester, String classCode, Integer dayIndex, Integer startPeriod, Integer endPeriod, String classRoom);

    List<RoomOccupation> findAllBySemesterAndCrewAndWeekIndexAndDayIndexAndStartPeriodAndEndPeriod(
            String semester, String crew, Integer weekIndex, Integer dayIndex, Integer startPeriod, Integer endPeriod);

    List<RoomOccupation> findAllBySemesterAndCrewAndWeekIndexAndDayIndex(
            String semester, String crew, Integer weekIndex, Integer dayIndex);

    void deleteBySemester(String semester);

    List<RoomOccupation> findAllBySemesterAndWeekIndex(String semester, int i);

    void deleteAllByClassCodeAndStartPeriodAndEndPeriodAndDayIndexAndClassRoom(String classCode, int startPeriod, int endPeriod, int weekDay, String classRoom);

    void deleteAllByClassCodeIn(List<String> stringStream);

    List<RoomOccupation> findAllByClassCodeIn(List<String> classCodes);

    List<RoomOccupation> findAllBySemesterAndClassCodeAndCrew(String semester, String classCode, String crew);
}
