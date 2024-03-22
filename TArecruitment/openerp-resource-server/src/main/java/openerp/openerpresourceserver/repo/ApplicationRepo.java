package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepo extends JpaRepository<Application, Integer> {
    List<Application> findByUserId(String userId);
}
