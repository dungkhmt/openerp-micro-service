package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.OpenBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OpenBatchRepo extends JpaRepository<OpenBatch, Long> {
    @Query(value = "SELECT DISTINCT open_batch FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getOpenBatch();
}
