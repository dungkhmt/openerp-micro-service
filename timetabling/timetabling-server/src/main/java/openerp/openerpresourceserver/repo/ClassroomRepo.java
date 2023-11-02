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
}
