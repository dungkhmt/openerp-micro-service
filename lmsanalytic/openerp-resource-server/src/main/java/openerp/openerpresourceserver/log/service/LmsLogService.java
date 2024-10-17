package openerp.openerpresourceserver.log.service;

import openerp.openerpresourceserver.log.entity.LmsLog;
import openerp.openerpresourceserver.log.model.LmsLogModelCreate;

import java.util.List;

public interface LmsLogService {
    public LmsLog save(LmsLogModelCreate I);
    public List<LmsLog> getLmsLogs();

    public List<LmsLog> getAllLogs();
}
