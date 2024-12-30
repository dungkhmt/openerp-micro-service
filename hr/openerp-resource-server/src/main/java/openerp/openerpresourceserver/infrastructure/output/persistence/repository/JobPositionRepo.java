package openerp.openerpresourceserver.infrastructure.output.persistence.repository;

import openerp.openerpresourceserver.infrastructure.output.persistence.entity.JobPositionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobPositionRepo extends IBaseRepository<JobPositionEntity, String>  {
    boolean existsByPositionName(String name);
    List<JobPositionEntity> findByPositionCodeIn(List<String> codes);
    @Query("SELECT MAX(d.positionCode) FROM JobPositionEntity d WHERE d.positionCode LIKE CONCAT(:prefix, '%')")
    String findMaxJobCode(@Param("prefix") String prefix);

}

