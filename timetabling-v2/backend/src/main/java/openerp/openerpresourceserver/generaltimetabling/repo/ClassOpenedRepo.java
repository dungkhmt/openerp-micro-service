package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassOpened;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ClassOpenedRepo extends JpaRepository<ClassOpened, Long> {

    void deleteById(Long id);

    @Transactional
    @Query("DELETE FROM ClassOpened c WHERE c.id IN :ids")
    void deleteByIds(List<Long> ids);

    List<ClassOpened> findAll(Sort sort);

    List<ClassOpened> getAllByIdIn(List<Long> ids, Sort sort);

    List<ClassOpened> getAllBySemester(String semester, Sort sort);

    List<ClassOpened> getAllByClassroom(String classroom, Sort sort);

    List<ClassOpened> getAllByGroupName(String groupName, Sort sort);

    List<ClassOpened> getAllBySemesterAndGroupName(String semester, String groupName, Sort sort);

    List<ClassOpened> getAllBySemesterAndClassroomAndWeekdayAndCrewAndStartPeriodIsNotNullAndIdNot(String semester,
            String classroom, String weekday, String crew, Long id);

    List<ClassOpened> getAllBySemesterAndClassroomAndWeekdayAndCrewAndStartPeriodIsNotNull(String semester,
            String classroom, String weekday, String crew);

    List<ClassOpened> getAllBySemesterAndSecondClassroomAndSecondWeekdayAndCrewAndSecondStartPeriodIsNotNullAndIdNot(
            String semester, String secondClassroom, String secondWeekday, String crew, Long id);

    List<ClassOpened> getAllBySemesterAndSecondClassroomAndSecondWeekdayAndCrewAndSecondStartPeriodIsNotNull(
            String semester, String secondClassroom, String secondWeekday, String crew);

    

}
