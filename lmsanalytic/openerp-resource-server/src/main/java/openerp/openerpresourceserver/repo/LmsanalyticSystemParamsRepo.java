package openerp.openerpresourceserver.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import openerp.openerpresourceserver.entity.LmsanalyticSystemParams;

import java.util.List;
import java.util.UUID;

public interface LmsanalyticSystemParamsRepo extends JpaRepository<LmsanalyticSystemParams, UUID> {
    List<LmsanalyticSystemParams> findAllByParam(String param);
}
