package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.ManagementCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ManagementCodeRepo extends JpaRepository<ManagementCode, Long> {
    @Query(value = "SELECT DISTINCT management_code FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getManagementCode();
}
