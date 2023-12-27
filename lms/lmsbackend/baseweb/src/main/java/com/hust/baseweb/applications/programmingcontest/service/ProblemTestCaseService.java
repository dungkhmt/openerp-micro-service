package com.hust.baseweb.applications.programmingcontest.service;

import com.hust.baseweb.applications.programmingcontest.constants.Constants;
import com.hust.baseweb.applications.programmingcontest.entity.*;
import com.hust.baseweb.applications.programmingcontest.exception.MiniLeetCodeException;
import com.hust.baseweb.applications.programmingcontest.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public interface ProblemTestCaseService {

    ProblemEntity createContestProblem(String userID, String json, MultipartFile[] files) throws MiniLeetCodeException;

    ProblemEntity updateContestProblem(
        String problemId,
        String userId,
        String json,
        MultipartFile[] files
    ) throws Exception;

    List<ModelProblemGeneralInfo> getAllProblemsGeneralInfo();

    ModelCreateContestProblemResponse getContestProblem(String problemId) throws Exception;

    ModelGetTestCaseResultResponse getTestCaseResult(
        String problemId,
        String userName,
        ModelGetTestCaseResult modelGetTestCaseResult
    ) throws Exception;

    ModelCheckCompileResponse checkCompile(ModelCheckCompile modelCheckCompile, String userName) throws Exception;

    TestCaseEntity saveTestCase(String problemId, ModelSaveTestcase modelSaveTestcase);

    ContestEntity createContest(ModelCreateContest modelCreateContest, String userName) throws Exception;

    ContestEntity updateContest(
        ModelUpdateContest modelUpdateContest,
        String userName,
        String contestId
    ) throws Exception;

    ContestProblem saveProblemInfoInContest(
        ModelProblemInfoInContest modelProblemInfoInContest,
        String userName
    ) throws Exception;

    void removeProblemFromContest(String contestId, String problemId, String userName);

    ModelGetContestDetailResponse getContestDetailByContestIdAndTeacher(String contestId, String userName);


    List<SubmissionDetailByTestcaseOM> getSubmissionDetailByTestcase(UUID submissionId);

    ContestSubmissionEntity teacherDisableSubmission(String userId, UUID submissionId);
    ContestSubmissionEntity teacherEnableSubmission(String userId, UUID submissionId);


    List<SubmissionDetailByTestcaseOM> getParticipantSubmissionDetailByTestCase(
        String userId, UUID submissionId
    );

    ModelContestSubmissionResponse submitContestProblemTestCaseByTestCaseWithFile(
        ModelContestSubmission modelContestSubmission,
        String userName,
        String submittedByUserId
    ) throws Exception;

    ModelContestSubmissionResponse submitContestProblemStoreOnlyNotExecute(
        ModelContestSubmission modelContestSubmission,
        String userName,
        String submittedByUserId
    ) throws Exception;


    ModelContestSubmissionResponse submitSolutionOutput(
        String solutionOutput,
        String contestId,
        String problemId,
        UUID testCaseId,
        String userName
    ) throws Exception;

    ModelContestSubmissionResponse submitSolutionOutputOfATestCase(
        String userId,
        String solutionOutput,
        ModelSubmitSolutionOutputOfATestCase m
    );

    ModelStudentRegisterContestResponse studentRegisterContest(
        String contestId,
        String userId
    ) throws MiniLeetCodeException;

    int teacherManageStudentRegisterContest(
        String teacherId,
        ModelTeacherManageStudentRegisterContest modelTeacherManageStudentRegisterContest
    ) throws MiniLeetCodeException;

    boolean approveRegisteredUser2Contest(
        String teacherId,
        ModelApproveRegisterUser2ContestInput input
    ) throws MiniLeetCodeException;

    ModelGetContestPageResponse getAllContestsPagingByAdmin(String userName, Pageable pageable);

    List<ModelGetContestResponse> getManagedContestOfTeacher(String userName);
    List<ModelGetContestResponse> getAllContests(String userName);

    ListModelUserRegisteredContestInfo getListUserRegisterContestSuccessfulPaging(Pageable pageable, String contestId);

    List<ModelMemberOfContestResponse> getListMemberOfContest(String contestId);
    List<ModelMemberOfContestResponse> getListMemberOfContestGroup(String contestId, String userId);


    ListModelUserRegisteredContestInfo getListUserRegisterContestPendingPaging(Pageable pageable, String contestId);

    List<ModelMemberOfContestResponse> getPendingRegisteredUsersOfContest(String contestId);

    ModelGetContestPageResponse getRegisteredContestsByUser(String userName);

    ModelGetContestPageResponse getNotRegisteredContestByUser(Pageable pageable, String userName);

    List<ContestSubmissionsByUser> getRankingByContestIdNew(
        String contestId,
        Constants.GetPointForRankingType getPointForRankingType
    );
    List<ContestSubmissionsByUser> getRankingGroupByContestIdNew(
        String userId,
        String contestId,
        Constants.GetPointForRankingType getPointForRankingType
    );
    Page<ProblemEntity> getPublicProblemPaging(Pageable pageable);

    List<ModelGetTestCase> getTestCaseByProblem(String problemId);

    ModelGetTestCaseDetail getTestCaseDetail(UUID testCaseId) throws MiniLeetCodeException;

    void editTestCase(UUID testCaseId, ModelSaveTestcase modelSaveTestcase) throws MiniLeetCodeException;

    ModelAddUserToContestResponse addUserToContest(ModelAddUserToContest modelAddUserToContest);

    ModelAddUserToContestGroupResponse addUserToContestGroup(ModelAddUserToContestGroup modelAddUserToContestGroup);

    void deleteUserContest(ModelAddUserToContest modelAddUserToContest) throws MiniLeetCodeException;

    Page<ContestSubmission> findContestSubmissionByContestIdPaging(Pageable pageable, String contestId, String searchTerm);

    Page<ContestSubmission> findContestGroupSubmissionByContestIdPaging(Pageable pageable, String contestId, String userId, String searchTerm);

    Page<ContestSubmission> findContestSubmissionByUserLoginIdPaging(Pageable pageable, String userLoginId);

    Page<ContestSubmission> findContestSubmissionByUserLoginIdAndContestIdPaging(
        Pageable pageable,
        String userLoginId,
        String contestId
    );

    Page<ContestSubmission> findContestSubmissionByUserLoginIdAndContestIdAndProblemIdPaging(
        Pageable pageable,
        String userLoginId,
        String contestId,
        String problemId
    );

    List<ContestSubmission> getNewestSubmissionResults(String userLoginId);


    ContestSubmissionEntity getContestSubmissionDetailForTeacher(UUID submissionId);

    ContestSubmissionEntity getContestSubmissionDetailForStudent(String userId, UUID submissionId);

    ModelGetContestInfosOfSubmissionOutput getContestInfosOfASubmission(UUID submissionId);

    void deleteTestcase(UUID testcaseId, String userId) throws MiniLeetCodeException;

    ModelCodeSimilarityOutput checkSimilarity(String contestId, ModelCheckSimilarityInput I);
    ModelCodeSimilarityOutput computeSimilarity(String userLoginId, String contestId, ModelCheckSimilarityInput I);

    int checkForbiddenInstructions(String contestId);

    ModelEvaluateBatchSubmissionResponse reJudgeAllSubmissionsOfContest(String contestId);

    ModelEvaluateBatchSubmissionResponse judgeAllSubmissionsOfContest(String contestId);

    void evaluateSubmission(UUID submisionId);

    void evaluateSubmission(ContestSubmissionEntity sub, ContestEntity contest);

    void evaluateSubmissionUsingQueue(ContestSubmissionEntity submission, ContestEntity contest);

    List<CodePlagiarism> findAllByContestId(String contestId);

    List<CodePlagiarism> findAllBy(ModelGetCodeSimilarityParams input);

    List<ModelSimilarityClusterOutput> computeSimilarityClusters(ModelGetCodeSimilarityParams input);

    List<ModelReponseCodeSimilaritySummaryParticipant> getListModelReponseCodeSimilaritySummaryParticipant(String contestId);

    ContestSubmissionEntity updateContestSubmissionSourceCode(ModelUpdateContestSubmission input);

    List<ModelGetContestResponse> getContestsUsingAProblem(String problemId);

    ModelUploadTestCaseOutput addTestCase(
        String testCase,
        ModelProgrammingContestUploadTestCase modelUploadTestCase,
        String userName
    );

    ModelUploadTestCaseOutput rerunCreateTestCaseSolution(String problemId, UUID testCaseId, String userId);

    ModelUploadTestCaseOutput uploadUpdateTestCase(
        UUID testCaseId,
        String testCase,
        ModelProgrammingContestUploadTestCase modelUploadTestCase,
        String userName
    );

    List<ModelUserJudgedProblemSubmissionResponse> getUserJudgedProblemSubmissions(String contestId);

    ModelGetRolesOfUserInContestResponse getRolesOfUserInContest(String userId, String contestId);

    boolean removeMemberFromContest(UUID id);

    boolean removeMemberFromContestGroup(String contestId, String userId, String participantId);



    boolean updatePermissionMemberToContest(String userId, ModelUpdatePermissionMemberToContestInput input);

    List<ModelResponseUserProblemRole> getUserProblemRoles(String problemId);

    boolean addUserProblemRole(String userName, ModelUserProblemRole input)  throws Exception;

    boolean removeUserProblemRole(String userName, ModelUserProblemRole input)  throws Exception;

    List<TagEntity> getAllTags();

    TagEntity addNewTag(ModelTag tag);

    TagEntity updateTag(Integer tagId, ModelTag tag);

    void deleteTag(Integer tagId);

    void switchAllContestJudgeMode(String judgeMode);

    void exportProblem(String id, OutputStream outputStream);

    List<ProblemEntity> getOwnerProblems(String ownerId);

    List<ProblemEntity> getSharedProblems(String userId);

    List<ProblemEntity> getAllProblems(String userId);

    ModelCreateContestProblemResponse getContestProblemDetailByIdAndTeacher(String problemId, String teacherId) throws Exception;

    List<ModelImportProblemFromContestResponse> importProblemsFromAContest(ModelImportProblemsFromAContestInput I);
}
