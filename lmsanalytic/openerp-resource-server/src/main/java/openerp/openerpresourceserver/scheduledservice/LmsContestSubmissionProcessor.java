package openerp.openerpresourceserver.scheduledservice;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.log.entity.LmsLog;
import openerp.openerpresourceserver.masterconfig.entity.LastTimeProcess;
import openerp.openerpresourceserver.masterconfig.repo.LastTimeProcessRepo;
import openerp.openerpresourceserver.programmingcontest.entity.CompositeProgrammingContestProblemRankingId;
import openerp.openerpresourceserver.programmingcontest.entity.LmsContestSubmission;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestProblemRanking;
import openerp.openerpresourceserver.programmingcontest.repo.LmsContestSubmissionRepo;
import openerp.openerpresourceserver.programmingcontest.repo.ProgrammingContestProblemRankingRepo;
import openerp.openerpresourceserver.programmingcontest.repo.ProgrammingContestRankingRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

    @Scheduled(fixedRate = 60000)
    public void process(){
        
        LastTimeProcess ltp = lastTimeProcessRepo.findById(TABLE_CONTEST_SUBMISSION).orElse(null);
        List<LmsContestSubmission> logs = null;
        Date currentDate = new Date();
        if(ltp == null){
            logs = lmsContestSubmissionRepo.findAll();
            ltp = new LastTimeProcess();
            ltp.setTableName(TABLE_CONTEST_SUBMISSION);
            ltp.setLastTimeProcess(currentDate);
            ltp = lastTimeProcessRepo.save(ltp);
        }else{
            logs = lmsContestSubmissionRepo.findAllByCreatedStampBetween(ltp.getLastTimeProcess(), currentDate);
            ltp.setLastTimeProcess(currentDate);
            ltp = lastTimeProcessRepo.save(ltp);
        }

        HashMap<CompositeProgrammingContestProblemRankingId, Long> mUserContestProblem2Point = new HashMap<>();
        for(LmsContestSubmission sub: logs){
            String userId = sub.getUserSubmissionId();
            String contestId = sub.getContestId();
            String problemId = sub.getProblemId();
            ProgrammingContestProblemRanking R = programmingContestProblemRankingRepo.findByUserIdAndContestIdAndProblemId(userId,contestId,problemId);
            if(R == null){
                R = new ProgrammingContestProblemRanking();
                R.setContestId(contestId);
                R.setUserId(userId);
                R.setProblemId(problemId);
                Long p = Long.valueOf(sub.getPoint());
                R.setPoint(p);
                R.setCreatedStamp(new Date());
                R.setLastUpdatedStamp(new Date());
                R = programmingContestProblemRankingRepo.save(R);
            }else{
                if(R.getPoint() < sub.getPoint()){
                    R.setPoint(Long.valueOf(sub.getPoint()));
                    R.setLastUpdatedStamp(new Date());
                    R = programmingContestProblemRankingRepo.save(R);
                }
            }
        }
    }
}
