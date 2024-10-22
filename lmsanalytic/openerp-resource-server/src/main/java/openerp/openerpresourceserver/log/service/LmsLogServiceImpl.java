package openerp.openerpresourceserver.log.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.log.entity.LmsLog;
import openerp.openerpresourceserver.log.model.LmsLogModelCreate;
import openerp.openerpresourceserver.log.repo.LmsLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class LmsLogServiceImpl implements LmsLogService{

    private LmsLogRepo lmsLogRepo;
    @Override
    public LmsLog save(LmsLogModelCreate I) {
        LmsLog log = new LmsLog();
        log.setUserId(I.getUserId());
        log.setActionType(I.getActionType());
        log.setParam1(I.getParam1());
        log.setParam2(I.getParam2());
        log.setParam3(I.getParam3());
        log.setParam4(I.getParam4());
        log.setParam5(I.getParam5());
        log.setDescription(I.getDescription());
        log.setCreatedStamp(new Date());
        log = lmsLogRepo.save(log);
        return log;
    }

    @Override
    public List<LmsLog> getLmsLogs() {
        return lmsLogRepo.findAll();
    }

    @Override
    public List<LmsLog> getAllLogs() {
        List<LmsLog> lst = lmsLogRepo.findAll();
        return lst;
    }
}
