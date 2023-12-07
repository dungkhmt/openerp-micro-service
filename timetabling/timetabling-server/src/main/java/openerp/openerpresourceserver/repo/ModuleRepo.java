package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModuleRepo extends JpaRepository<Module, Long> {
    @Query(value = "SELECT DISTINCT module_code, module_name, module_name_by_english, mass " +
            "FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getModule();
}
