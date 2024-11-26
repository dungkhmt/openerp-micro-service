package openerp.openerpresourceserver.programmingcontest.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.programmingcontest.entity.LmsContestSubmission;
import openerp.openerpresourceserver.programmingcontest.model.ModelCreateContestSubmission;
import openerp.openerpresourceserver.programmingcontest.repo.LmsContestSubmissionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
public class LmsContestSubmissionServiceImpl implements  LmsContestSubmissionService{
    private LmsContestSubmissionRepo lmsContestSubmissionRepo;

    @Override
    public LmsContestSubmission save(ModelCreateContestSubmission m) {
        LmsContestSubmission lcs = new LmsContestSubmission();

        lcs.setContestSubmissionId(m.getContestSubmissionId());
        lcs.setContestId(m.getContestId());
        lcs.setContestName(m.getContestName());
        lcs.setContestType(m.getContestType());
        lcs.setContestCreatedStamp(m.getContestCreatedStamp());
        lcs.setContestCreatedUserId(m.getContestCreatedUserId());

        lcs.setProblemId(m.getProblemId());
        lcs.setProblemName(m.getProblemName());
        lcs.setContestCreatedUserId(m.getProblemCreatedUserId());
        lcs.setProblemCreatedStamp(m.getProblemCreatedStamp());
        lcs.setProblemDescription(m.getProblemDescription());
        lcs.setProblemCategoryId(m.getProblemCategory());
        lcs.setProblemLevelId(m.getProblemLevelId());
        lcs.setProblemMemoryLimit(m.getProblemMemoryLimit());
        lcs.setProblemTimeLimit(m.getProblemTimeLimit());

        lcs.setUserSubmissionId(m.getParticipantUserId());
        lcs.setUserSubmissionFullName(m.getParticipantUserFullName());
        lcs.setStatus(m.getSubmissionStatus());
        lcs.setPoint(m.getPoint());
        lcs.setTestCasePass(m.getTestCasePasses());
        lcs.setSourceCode(m.getSourceCode());
        lcs.setSourceCodeLanguage(m.getSourceCodeLanguage());
        lcs.setRunTime(m.getRuntime());
        lcs.setMemoryUsage(m.getMemoryUsage());
        lcs.setSubmissionCreatedStamp(m.getSubmissionCreatedStamp());
        lcs.setLastUpdatedStamp(m.getSubmissionLastUpdatedStamp());
        lcs.setSubmittedByUserId(m.getSubmittedByUserId());
        lcs.setSubmittedByUserFullName(m.getSubmittedByUserFullname());
        lcs.setManagementStatus(m.getManagementStatus());
        lcs.setViolateForbiddenInstructions(m.getViolationForbiddenInstructionStatus());
        lcs.setViolateForbiddenInstructionMessage(m.getViolationForbiddenInstructionMessage());

        lcs.setCreatedStamp(new Date());
        lcs.setLastUpdatedStamp(new Date());

        lcs = lmsContestSubmissionRepo.save(lcs);

        return lcs;
    }

    @Override
    public List<LmsContestSubmission> findAll() {
        return lmsContestSubmissionRepo.findAll();
    }

    @Override
    public List<LmsContestSubmission> findSubmissionsByUserSubmissionId(String userSubmissionId) {
        return lmsContestSubmissionRepo.findAllByUserSubmissionId(userSubmissionId);
    }
}

