package com.hust.baseweb.applications.programmingcontest.service;

import com.hust.baseweb.applications.programmingcontest.entity.ContestEntity;
import com.hust.baseweb.applications.programmingcontest.entity.ContestProblem;
import com.hust.baseweb.applications.programmingcontest.model.ModelGetContestDetailResponse;
import com.hust.baseweb.applications.programmingcontest.model.ModelGetProblemDetailResponse;
import com.hust.baseweb.applications.programmingcontest.repo.ContestProblemRepo;
import com.hust.baseweb.applications.programmingcontest.repo.ContestRepo;
import com.hust.baseweb.applications.programmingcontest.repo.ContestSubmissionRepo;
import com.hust.baseweb.applications.programmingcontest.repo.UserRegistrationContestRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ContestService {

    private ContestRepo contestRepo;
    private UserRegistrationContestRepo userRegistrationContestRepo;
    private ContestProblemRepo contestProblemRepo;
    private ContestSubmissionRepo contestSubmissionRepo;

    private static final String HASH_CONTEST = "CONTEST";
    private static final String HASH_CONTEST_SOLVING_DETAIL = "CONTEST_SOLVING_DETAIL";

    @Cacheable(value = HASH_CONTEST, key = "#contestId")
    public ContestEntity findContestWithCache(String contestId) {
        return findContest(contestId);
    }

    @CachePut(value = HASH_CONTEST, key = "#contest.contestId")
    public ContestEntity updateContestWithCache(ContestEntity contest) {
        return updateContest(contest);
    }

    @CachePut(value = HASH_CONTEST, key = "#result.contestId")
    public ContestEntity saveContestWithCache(ContestEntity contest) {
        return saveContest(contest);
    }

    public ContestEntity findContest(String contestId) {
        return contestRepo.findContestByContestId(contestId);
    }

    public ContestEntity updateContest(ContestEntity contest) {
        return contestRepo.save(contest);
    }

    public ContestEntity saveContest(ContestEntity contest) {
        return contestRepo.save(contest);
    }

    @Cacheable(value = HASH_CONTEST_SOLVING_DETAIL, key = "#contest.contestId")
    public ModelGetContestDetailResponse getModelGetContestDetailResponseWithCache(ContestEntity contest) {
        return getModelGetContestDetailResponse(contest);
    }

    public ModelGetContestDetailResponse getModelGetContestDetailResponse(ContestEntity contestEntity) {
        String contestId = contestEntity.getContestId();
        List<ModelGetProblemDetailResponse> problems = new ArrayList<>();


        // Ensure problem list is not null
        if (contestEntity.getProblems() != null) {
            contestEntity.getProblems().forEach(contestProblem -> {
                ContestProblem cp = contestProblemRepo.findByContestIdAndProblemId(
                    contestEntity.getContestId(),
                    contestProblem.getProblemId());

                // Initialize default values
                String submissionMode = "";
                String problemRename = "";
                String problemRecode = "";
                String forbiddenInstructions = "";

                // If contest problem exists in the repository, update values
                if (cp != null) {
                    submissionMode = cp.getSubmissionMode();
                    problemRename = cp.getProblemRename();
                    problemRecode = cp.getProblemRecode();
                    forbiddenInstructions = cp.getForbiddenInstructions();
                }

                ModelGetProblemDetailResponse p = ModelGetProblemDetailResponse.builder()
                                                                               .levelId(contestProblem.getLevelId())
                                                                               .problemId(contestProblem.getProblemId())
                                                                               .problemName(contestProblem.getProblemName())
                                                                               .problemRename(problemRename)
                                                                               .problemRecode(problemRecode)
                                                                               .forbiddenInstructions(forbiddenInstructions)
                                                                               .levelOrder(contestProblem.getLevelOrder())
                                                                               .problemDescription(contestProblem.getProblemDescription())
                                                                               .createdByUserId(contestProblem.getUserId())
                                                                               .submissionMode(submissionMode)
                                                                               .build();

                problems.add(p);
            });
        }

        return ModelGetContestDetailResponse
            .builder()
            .contestId(contestId)
            .contestName(contestEntity.getContestName())
            .contestTime(contestEntity.getContestSolvingTime())
            .startAt(contestEntity.getStartedAt())
            .list(problems)
            .unauthorized(false)
            .statusId(contestEntity.getStatusId())
            .listStatusIds(ContestEntity.getStatusIds())
            .listSubmissionActionTypes(ContestEntity.getSubmissionActionTypes())
            .listParticipantViewModes(ContestEntity.getParticipantViewResultModes())
            .listProblemDescriptionViewTypes(ContestEntity.getProblemDescriptionViewTypes())
            .listJudgeModes(ContestEntity.getJudgeModes())
            .listEvaluateBothPublicPrivateTestcases(ContestEntity.getListEvaluateBothPublicPrivateTestcases())
            .submissionActionType(contestEntity.getSubmissionActionType())
            .maxNumberSubmission(contestEntity.getMaxNumberSubmissions())
            .participantViewResultMode(contestEntity.getParticipantViewResultMode())
            .problemDescriptionViewType(contestEntity.getProblemDescriptionViewType())
            .evaluateBothPublicPrivateTestcase(contestEntity.getEvaluateBothPublicPrivateTestcase())
            .maxSourceCodeLength(contestEntity.getMaxSourceCodeLength())
            .minTimeBetweenTwoSubmissions(contestEntity.getMinTimeBetweenTwoSubmissions())
            .judgeMode(contestEntity.getJudgeMode())
            .participantViewSubmissionMode(contestEntity.getParticipantViewSubmissionMode())
            .listParticipantViewSubmissionModes(ContestEntity.getListParticipantViewSubmissionModes())
            .languagesAllowed(contestEntity.getLanguagesAllowed())
            .listLanguagesAllowed(contestEntity.getListLanguagesAllowed())
            .contestType(contestEntity.getContestType())
            .listContestTypes(ContestEntity.getListContestTypes())
            .contestShowTag(contestEntity.getContestShowTag())
            .listContestShowTags(contestEntity.getListContestShowTag())
            .contestShowComment(contestEntity.getContestShowComment())
            .listContestShowComments(contestEntity.getListContestShowComment())
            .build();
    }

    @Transactional
    public void updateContestSubmissionStatus(UUID submissionId, String status) {
        contestSubmissionRepo.updateContestSubmissionStatus(submissionId, status);
    }

    public String getProblemNameInContest(String contestId, String problemId) {
        ContestProblem cp = contestProblemRepo.findByContestIdAndProblemId(contestId, problemId);
        return cp != null ? cp.getProblemRename() : null;
    }
}
