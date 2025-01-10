package openerp.openerpresourceserver.scheduledservice;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.masterconfig.repo.LastTimeProcessRepo;
import openerp.openerpresourceserver.programmingcontest.repo.LmsContestSubmissionRepo;
import openerp.openerpresourceserver.userfeaturestore.repo.UserFeaturesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class LmsContestFeatureNumberAcceptOnFirstSubmission {


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private LmsContestSubmissionRepo lmsContestSubmissionRepo;
    private LastTimeProcessRepo lastTimeProcessRepo;
    private UserFeaturesRepo userFeaturesRepo;

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void processCountAcceptSubmissionsFirstTime() {
        // compute the number of submission with accept status for the first time

    }

}
