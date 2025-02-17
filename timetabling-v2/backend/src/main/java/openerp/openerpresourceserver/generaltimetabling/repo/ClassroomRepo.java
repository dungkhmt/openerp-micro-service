package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomRepo extends JpaRepository<Classroom, String> {
    @Query(value = "SELECT DISTINCT class_room FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getClassroom();

    @Query(value = "SELECT DISTINCT name FROM public.timetabling_building", nativeQuery = true)
    List<String> getBuilding();

    @Query(value = "SELECT classroom_id, classroom, description, quantity_max, building_id FROM public.timetabling_classroom",
            nativeQuery = true)
    List<Classroom> findAllWithBuilding();

    List<Classroom> getClassroomByClassroom(String classroom);

    void deleteById(String id);

    @Query(value = "SELECT c FROM Classroom c WHERE " +
            " c.quantityMax >= :amountStudent ORDER BY SUBSTRING(c.classroom, 1, 2), CAST(SUBSTRING(c.classroom, 4) AS INTEGER), c.quantityMax ASC")
    List<Classroom> findClassroomByQuantityMaxAfter(Long amountStudent);

    @Query(value = "SELECT c FROM Classroom c WHERE LOWER(c.classroom) LIKE LOWER(CONCAT('%', :building, '%'))" +
            " AND c.quantityMax >= :amountStudent ORDER BY SUBSTRING(c.classroom, 1, 2), CAST(SUBSTRING(c.classroom, 4) AS INTEGER), c.quantityMax ASC")
    List<Classroom> findClassroomByBuildingAndQuantityMaxAfter(String building, Long amountStudent);

    List<Classroom> getClassRoomByBuilding(String priorityBuilding);

    List<Classroom> getClassRoomByBuildingIn(List<String> split);
}
