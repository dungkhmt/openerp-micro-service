package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.RequestLog;

import java.util.List;

public interface RequestLogService {
    RequestLog createRequestLog(Integer request_id, String user_id, String action);

    List<RequestLog> getByUserId(String user_id);
}
