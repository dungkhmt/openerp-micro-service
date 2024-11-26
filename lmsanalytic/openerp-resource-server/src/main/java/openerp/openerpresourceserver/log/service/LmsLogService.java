package openerp.openerpresourceserver.log.service;

import openerp.openerpresourceserver.log.entity.LmsLog;
import openerp.openerpresourceserver.log.model.LmsLogModelCreate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LmsLogService {
    public LmsLog save(LmsLogModelCreate I);
    public List<LmsLog> getLmsLogs();

    public List<LmsLog> getAllLogs();
    public List<LmsLog> getMostRecentLogs(int size);


    public Page<LmsLog> search(LmsLog filter, Pageable pageable);
}
