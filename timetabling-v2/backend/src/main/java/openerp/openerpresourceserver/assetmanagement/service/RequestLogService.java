package openerp.openerpresourceserver.assetmanagement.service;

import openerp.openerpresourceserver.assetmanagement.entity.RequestLog;

import java.util.List;

public interface RequestLogService {
    RequestLog createRequestLog(Integer request_id, String user_id, String action);

    List<RequestLog> getByUserId(String user_id);
}
