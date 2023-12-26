package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomRepo extends JpaRepository<Classroom, Long> {
    @Query(value = "SELECT DISTINCT class_room FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getClassroom();

    @Query(value = "SELECT DISTINCT building FROM public.timetabling_classroom", nativeQuery = true)
    List<String> getBuilding();

    void deleteById(Long id);

    @Query(value = "SELECT c FROM Classroom c WHERE " +
            " c.quantityMax >= :amountStudent ORDER BY SUBSTRING(c.classroom, 1, 2), CAST(SUBSTRING(c.classroom, 4) AS INTEGER), c.quantityMax ASC")
    List<Classroom> findClassroomByQuantityMaxAfter(Long amountStudent);

    @Query(value = "SELECT c FROM Classroom c WHERE LOWER(c.classroom) LIKE LOWER(CONCAT('%', :building, '%'))" +
            " AND c.quantityMax >= :amountStudent ORDER BY SUBSTRING(c.classroom, 1, 2), CAST(SUBSTRING(c.classroom, 4) AS INTEGER), c.quantityMax ASC")
    List<Classroom> findClassroomByBuildingAndQuantityMaxAfter(String building, Long amountStudent);
}
