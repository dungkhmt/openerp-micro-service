package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassTypeRepo extends JpaRepository<ClassType, Long> {
    @Query(value = "SELECT DISTINCT class_type FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getClassType();
}
