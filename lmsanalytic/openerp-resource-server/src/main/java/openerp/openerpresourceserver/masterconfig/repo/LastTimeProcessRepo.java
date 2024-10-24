package openerp.openerpresourceserver.masterconfig.repo;

import openerp.openerpresourceserver.masterconfig.entity.LastTimeProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LastTimeProcessRepo extends JpaRepository<LastTimeProcess, String> {

}
