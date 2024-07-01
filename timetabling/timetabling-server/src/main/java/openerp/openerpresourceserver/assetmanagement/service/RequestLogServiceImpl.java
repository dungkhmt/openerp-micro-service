package openerp.openerpresourceserver.assetmanagement.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.assetmanagement.entity.RequestLog;
import openerp.openerpresourceserver.assetmanagement.repo.RequestLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RequestLogServiceImpl implements RequestLogService {
    private RequestLogRepo requestLogRepo;

    @Override
    public RequestLog createRequestLog(Integer request_id, String user_id, String action) {
        RequestLog requestLog = new RequestLog();
        requestLog.setRequest_id(request_id);
        requestLog.setUser_id(user_id);
        requestLog.setAction(action);
        requestLog.setName(user_id + " " + action + " request " + request_id);
        requestLog.setDescription(user_id + " " + action + " request " + request_id);
        requestLog.setSince(new Date());
        requestLog.setLast_updated(new Date());
        return requestLogRepo.save(requestLog);
    }

    @Override
    public List<RequestLog> getByUserId(String user_id) {
        return requestLogRepo.getByUserId(user_id);
    }
}
