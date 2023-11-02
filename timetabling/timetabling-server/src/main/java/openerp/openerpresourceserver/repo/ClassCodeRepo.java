package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.ClassCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassCodeRepo extends JpaRepository<ClassCode, Long> {
    @Query(value = "SELECT DISTINCT tb.class_code, tb.bundle_class_code " +
            "FROM public.timetabling_schedule tb", nativeQuery = true)
    List<String> getClassCode();
}
