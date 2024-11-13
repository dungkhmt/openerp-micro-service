package openerp.openerpresourceserver.log.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.log.entity.LmsLog;
import openerp.openerpresourceserver.log.model.LmsLogModelCreate;
import openerp.openerpresourceserver.log.repo.LmsLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public List<LmsLog> getMostRecentLogs(int size) {
        List<LmsLog> res = lmsLogRepo.getMostRecentLogs(size);
        return res;
    }

    @Override
    public Page<LmsLog> search(LmsLog filter, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths(
                        "id"
                        //"createdStamp"
                )
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();
        Example<LmsLog> example =Example.of(filter, matcher);
        Page<LmsLog> logs = lmsLogRepo.findAll(example,pageable);
        //Page<LmsLog> logs = lmsLogRepo.findAll(pageable);
        log.info("search, return size = " + logs.getNumberOfElements());
        //return logs;
        return logs.map(log -> LmsLog
                .builder()
                .id(log.getId())
                .userId(log.getUserId())
                .actionType(log.getActionType())
                .description(log.getDescription())
                .param1(log.getParam1())
                .param2(log.getParam2())
                .param3(log.getParam3())
                .param4(log.getParam4())
                .param5(log.getParam5())
                .createdStamp(log.getCreatedStamp())
                .build()
        );
    }
}
