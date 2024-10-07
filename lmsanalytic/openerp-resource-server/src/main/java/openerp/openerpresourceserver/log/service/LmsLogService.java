package openerp.openerpresourceserver.log.service;

import openerp.openerpresourceserver.log.entity.LmsLog;
import openerp.openerpresourceserver.log.model.LmsLogModelCreate;

public interface LmsLogService {
    public LmsLog save(LmsLogModelCreate I);
}
