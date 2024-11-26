package openerp.openerpresourceserver.scheduledservice;


import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.log.entity.LmsLog;
import openerp.openerpresourceserver.log.repo.LmsLogRepo;
import openerp.openerpresourceserver.masterconfig.entity.LastTimeProcess;
import openerp.openerpresourceserver.masterconfig.repo.LastTimeProcessRepo;
import openerp.openerpresourceserver.userfeaturestore.entity.UserFeatures;
import openerp.openerpresourceserver.userfeaturestore.repo.UserFeaturesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.*;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class LmsLogProcessor {
    private static final Logger log = LoggerFactory.getLogger(LmsLogProcessor.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private LmsLogRepo lmsLogRepo;
    private LastTimeProcessRepo lastTimeProcessRepo;
    private UserFeaturesRepo userFeaturesRepo;

    @Scheduled(fixedRate = 60000)
    public void process(){
        //log.info("The time is now {}, number rows = {}", dateFormat.format(new Date()), lmsLogRepo.count());
        //LastTimeProcess ltp = lastTimeProcessRepo.findById("lms_log").orElse(null);
        LastTimeProcess ltp = lastTimeProcessRepo.findByTableNameAndModule("lms_log",UserFeatures.FEATURE_NUMBER_ACTIONS);

        List<LmsLog> logs = null;
        Date currentDate = new Date();
        if(ltp == null){
            logs = lmsLogRepo.findAll();
            ltp = new LastTimeProcess();
            ltp.setTableName("lms_log");
            log.info("LmsLogProcessor::process, last_time_process = NULL, findAll gots " + logs.size());
            ltp.setModule(UserFeatures.FEATURE_NUMBER_ACTIONS);
            ltp.setLastTimeProcess(currentDate);
            ltp = lastTimeProcessRepo.save(ltp);
            log.info("LmsLogProcessor::process, last_time_process = NULL, save new record");
        }else{
            logs = lmsLogRepo.findAllByCreatedStampBetween(ltp.getLastTimeProcess(), currentDate);
            ltp.setLastTimeProcess(currentDate);
            ltp = lastTimeProcessRepo.save(ltp);
        }
        HashMap<String, Integer> mUser2Actions = new HashMap();
        for(LmsLog log: logs){
            String userId = log.getUserId();
            if(mUser2Actions.get(userId)==null){
                mUser2Actions.put(userId,1);
            }else{
                mUser2Actions.put(userId,mUser2Actions.get(userId) + 1);
            }
        }

        // update to DB
        for(String userId: mUser2Actions.keySet()){
            UserFeatures uf = userFeaturesRepo.findByUserIdAndFeatureId(userId,UserFeatures.FEATURE_NUMBER_ACTIONS);
            if(uf == null){
                uf = new UserFeatures();
                uf.setUserId(userId);
                uf.setFeatureId(UserFeatures.FEATURE_NUMBER_ACTIONS);
                uf.setValue(mUser2Actions.get(userId));
                uf.setCreatedStamp(new Date());
                uf.setLastUpdatedStamp(new Date());
                uf = userFeaturesRepo.save(uf);
            }else{
                uf.setValue(uf.getValue() + mUser2Actions.get(userId));
                uf = userFeaturesRepo.save(uf);
                uf.setLastUpdatedStamp(new Date());
            }

        }
    }
}
