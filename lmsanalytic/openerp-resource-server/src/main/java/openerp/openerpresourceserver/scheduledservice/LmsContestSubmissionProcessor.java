package openerp.openerpresourceserver.scheduledservice;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.log.entity.LmsLog;
import openerp.openerpresourceserver.masterconfig.entity.LastTimeProcess;
import openerp.openerpresourceserver.masterconfig.repo.LastTimeProcessRepo;
import openerp.openerpresourceserver.programmingcontest.entity.CompositeProgrammingContestProblemRankingId;
import openerp.openerpresourceserver.programmingcontest.entity.LmsContestSubmission;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestProblemRanking;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestRanking;
import openerp.openerpresourceserver.programmingcontest.repo.LmsContestSubmissionRepo;
import openerp.openerpresourceserver.programmingcontest.repo.ProgrammingContestProblemRankingRepo;
import openerp.openerpresourceserver.programmingcontest.repo.ProgrammingContestRankingRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class LmsContestSubmissionProcessor {
    public static final String TABLE_PROGRAMMING_CONTEST_PROBLEM_RANKING = "programming_contest_problem_ranking";
    public static final String TABLE_PROGRAMMING_CONTEST_RANKING = "programming_contest_ranking";
    public static final String TABLE_CONTEST_SUBMISSION = "lms_contest_submission";

    private static final Logger log = LoggerFactory.getLogger(LmsLogProcessor.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private LmsContestSubmissionRepo lmsContestSubmissionRepo;
    private LastTimeProcessRepo lastTimeProcessRepo;
    private ProgrammingContestProblemRankingRepo programmingContestProblemRankingRepo;
    private ProgrammingContestRankingRepo programmingContestRankingRepo;

    public static String composeKey(String userId, String contestId, String problemId){
        return userId + "$" + contestId + "$" + problemId;
    }
    public static String composeKey(String userId, String contestId){
        return userId + "$" + contestId;
    }
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void process(){
        //log.info("LmsContestSubmissionProcessor -> run process time = {}",new Date());
        LastTimeProcess ltp = lastTimeProcessRepo.findById(TABLE_CONTEST_SUBMISSION).orElse(null);
        List<LmsContestSubmission> logs = null;
        Date currentDate = new Date();
        if(ltp == null){
            logs = lmsContestSubmissionRepo.findAll();
            //log.info("process, last time process NULL, get number items lms_contest_submissions = " + logs.size());
            ltp = new LastTimeProcess();
            ltp.setTableName(TABLE_CONTEST_SUBMISSION);
            ltp.setLastTimeProcess(currentDate);
            ltp = lastTimeProcessRepo.save(ltp);
        }else{
            logs = lmsContestSubmissionRepo.findAllByCreatedStampBetween(ltp.getLastTimeProcess(), currentDate);
            //log.info("process, last time process = " + ltp.getLastTimeProcess() + ", get number items lms_contest_submissions = " + logs.size());

            ltp.setLastTimeProcess(currentDate);
            ltp = lastTimeProcessRepo.save(ltp);
        }

        //HashMap<CompositeProgrammingContestProblemRankingId, Long> mUserContestProblem2Point = new HashMap<>();
        HashSet<String> userContestChanged = new HashSet();
        for(LmsContestSubmission sub: logs){
            String userId = sub.getUserSubmissionId();
            String contestId = sub.getContestId();
            String problemId = sub.getProblemId();
            ProgrammingContestProblemRanking R = programmingContestProblemRankingRepo.findByUserIdAndContestIdAndProblemId(userId,contestId,problemId);
            //ProgrammingContestRanking pcr = programmingContestRankingRepo.findByUserIdAndContestId(userId,contestId);
            //List<ProgrammingContestProblemRanking> pcprs = programmingContestProblemRankingRepo.findByUserIdAndContestId(userId, contestId);
            String userContestKey = composeKey(userId, contestId);
            userContestChanged.add(userContestKey);

            if(R == null){
                R = new ProgrammingContestProblemRanking();
                R.setContestId(contestId);
                R.setUserId(userId);
                R.setProblemId(problemId);
                Long p = Long.valueOf(sub.getPoint());
                R.setPoint(p);
                R.setCreatedStamp(new Date());
                R.setLastUpdatedStamp(new Date());
                R = programmingContestProblemRankingRepo.saveAndFlush(R);
                //log.info("process, save new record point = " + R.getContestId() + " to DB");
            }else{
                if(R.getPoint() < sub.getPoint()){
                    R.setPoint(Long.valueOf(sub.getPoint()));
                    R.setLastUpdatedStamp(new Date());
                    R = programmingContestProblemRankingRepo.saveAndFlush(R);
                    //log.info("process update with new record point " + R.getPoint());
                }
            }
            /*
            if(pcr == null){
                pcr = new ProgrammingContestRanking();
                pcr.setUserId(userId); pcr.setContestId(contestId);
                pcr.setPoint(Long.valueOf(sub.getPoint()));
                pcr = programmingContestRankingRepo.save(pcr);
            }else{

            }

             */
        }
        //log.info("process, aggregate userContestChanged = " + userContestChanged.size());

        // update to programming_contest_ranking
        for(String k: userContestChanged){
            //log.info("process, aggregate key = " + k);
            String[] s = k.split("\\$");
            String userId = s[0];
            String contestId = s[1];
            List<ProgrammingContestProblemRanking> L = programmingContestProblemRankingRepo
                    .findByUserIdAndContestId(userId, contestId);
           // log.info("process, aggregate with userId " + userId + " contestId " + contestId + " L.sz = " + L.size());
            long point = 0;
            for(ProgrammingContestProblemRanking pcpr: L){
                point += pcpr.getPoint();
            }


            ProgrammingContestRanking pcr = programmingContestRankingRepo.findByUserIdAndContestId(userId,contestId);
           // log.info("process, aggregate with userId " + userId + " contestId " + contestId + " L.sz = " + L.size() + " point = " + point + " record pcr = {}",pcr);

            if(pcr == null){
                pcr = new ProgrammingContestRanking();
                pcr.setUserId(userId); pcr.setContestId(contestId);
                pcr.setPoint(Long.valueOf(point));
                pcr.setCreatedStamp(new Date()); pcr.setLastUpdatedStamp(new Date());
                pcr = programmingContestRankingRepo.saveAndFlush(pcr);
            }else{
                pcr.setPoint(Long.valueOf(point));
                pcr.setLastUpdatedStamp(new Date());
                pcr = programmingContestRankingRepo.save(pcr);
            }
        }
    }
    public static void main(String[] args){
        String key = "dungpq$123456";
        String[] s = key.split("\\$");
        System.out.println(s[0]);
        System.out.println(s[1]);
    }
}
