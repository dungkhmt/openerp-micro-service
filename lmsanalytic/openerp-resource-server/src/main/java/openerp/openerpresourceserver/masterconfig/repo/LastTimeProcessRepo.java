package openerp.openerpresourceserver.masterconfig.repo;

import openerp.openerpresourceserver.masterconfig.entity.CompositeLastTimeProcessId;
import openerp.openerpresourceserver.masterconfig.entity.LastTimeProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LastTimeProcessRepo extends JpaRepository<LastTimeProcess, CompositeLastTimeProcessId> {
    LastTimeProcess findByTableNameAndModule(String tableName, String module);
}
