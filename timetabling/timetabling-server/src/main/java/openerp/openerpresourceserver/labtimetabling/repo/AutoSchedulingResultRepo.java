package openerp.openerpresourceserver.labtimetabling.repo;

import openerp.openerpresourceserver.labtimetabling.entity.AutoSchedulingResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AutoSchedulingResultRepo extends JpaRepository<AutoSchedulingResult, UUID> {
    List<AutoSchedulingResult> findAllBySemester_id(Long semester_id);
}
