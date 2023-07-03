package com.hust.baseweb.applications.programmingcontest.service;

import com.hust.baseweb.applications.programmingcontest.constants.Constants;
import com.hust.baseweb.applications.programmingcontest.entity.*;
import com.hust.baseweb.applications.programmingcontest.exception.MiniLeetCodeException;
import com.hust.baseweb.applications.programmingcontest.model.*;
import com.hust.baseweb.model.ListPersonModel;
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

    Page<ProblemEntity> getContestProblemPaging(Pageable pageable);

    List<ProblemEntity> getAllProblems();

    List<ModelProblemGeneralInfo> getAllProblemsGeneralInfo();

    String executableIDECode(
        ModelRunCodeFromIDE modelRunCodeFromIDE,
        String userName,
        String computerLanguage
    ) throws Exception;

    ModelCreateContestProblemResponse getContestProblem(String problemId) throws Exception;

    ModelProblemDetailRunCodeResponse problemDetailRunCode(
        String problemId,
        ModelProblemDetailRunCode modelProblemDetailRunCode,
        String userName
    ) throws Exception;

    ModelGetTestCaseResultResponse getTestCaseResult(
        String problemId,
        String userName,
        ModelGetTestCaseResult modelGetTestCaseResult
    ) throws Exception;

    ModelCheckCompileResponse checkCompile(ModelCheckCompile modelCheckCompile, String userName) throws Exception;

    TestCaseEntity saveTestCase(String problemId, ModelSaveTestcase modelSaveTestcase);

    ModelContestSubmissionResponse problemDetailSubmission(
        ModelProblemDetailSubmission modelProblemDetailSubmission,
        String problemId,
        String userName
    ) throws Exception;

    ListProblemSubmissionResponse getListProblemSubmissionResponse(String problemId, String userId) throws Exception;

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

    ModelProblemSubmissionDetailResponse findProblemSubmissionById(
        UUID id,
        String userName
    ) throws MiniLeetCodeException;

    ModelGetContestPageResponse getContestPaging(Pageable pageable);

    ModelGetContestDetailResponse getContestDetailByContestIdAndTeacher(String contestId, String userName);

    ModelGetContestDetailResponse getContestDetailByContestId(String contestId);

//    ModelGetContestDetailResponse getContestSolvingDetailByContestId(String contestId, String userName) throws MiniLeetCodeException;

    Page<ModelProblemSubmissionDetailByTestCaseResponse> getContestProblemSubmissionDetailByTestCase(Pageable page);

    List<ModelProblemSubmissionDetailByTestCaseResponse> getContestProblemSubmissionDetailByTestCaseOfASubmission(UUID submissionId);

    List<ModelProblemSubmissionDetailByTestCaseResponse> getContestProblemSubmissionDetailByTestCaseOfASubmissionViewedByParticipant(
        UUID submissionId
    );

    ModelContestSubmissionResponse submitContestProblem(
        ModelContestSubmission modelContestSubmission,
        String userName
    ) throws Exception;

    ModelContestSubmissionResponse submitContestProblemTestCaseByTestCase(
        ModelContestSubmission modelContestSubmission,
        String userName
    ) throws Exception;

    ModelContestSubmissionResponse submitContestProblemTestCaseByTestCaseWithFile(
        ModelContestSubmission modelContestSubmission,
        String userName,
        String submittedByUserId
    ) throws Exception;

    void submitContestProblemTestCaseByTestCaseWithFileProcessor(UUID contestSubmissionId) throws Exception;

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

    void calculateContestResult(String contestId);

    ModelGetContestPageResponse getContestPagingByUserCreatedContest(String userName, Pageable pageable);

    ModelGetContestPageResponse getContestPagingByUserManagerContest(String userName, Pageable pageable);

    ModelGetContestPageResponse getAllContestsPagingByAdmin(String userName, Pageable pageable);

    List<ModelGetContestResponse> getContestByUserRole(String userName);

    ListModelUserRegisteredContestInfo getListUserRegisterContestSuccessfulPaging(Pageable pageable, String contestId);

    List<ModelMemberOfContestResponse> getListMemberOfContest(String contestId);

    ListModelUserRegisteredContestInfo getListUserRegisterContestPendingPaging(Pageable pageable, String contestId);

    List<ModelMemberOfContestResponse> getPendingRegisteredUsersOfContest(String contestId);

    ListModelUserRegisteredContestInfo searchUser(Pageable pageable, String contestId, String keyword);

    ListPersonModel searchUserBaseKeyword(Pageable pageable, String keyword);

    ModelGetContestPageResponse getRegisteredContestByUser(Pageable pageable, String userName);

    ModelGetContestPageResponse getRegisteredContestsByUser(String userName);

    ModelGetContestPageResponse getNotRegisteredContestByUser(Pageable pageable, String userName);

    List<ContestSubmissionsByUser> getRankingByContestIdNew(
        Pageable pageable,
        String contestId,
        Constants.GetPointForRankingType getPointForRankingType
    );

    Page<UserSubmissionContestResultNativeEntity> getRankingByContestId(Pageable pageable, String contestId);

    Page<ProblemEntity> getPublicProblemPaging(Pageable pageable);

    List<ModelGetTestCase> getTestCaseByProblem(String problemId);

    ModelGetTestCaseDetail getTestCaseDetail(UUID testCaseId) throws MiniLeetCodeException;

    ModelGetTestCaseDetail getTestCaseDetailShort(UUID testCaseId) throws MiniLeetCodeException;

    void editTestCase(UUID testCaseId, ModelSaveTestcase modelSaveTestcase) throws MiniLeetCodeException;

    int addUserToContest(ModelAddUserToContest modelAddUserToContest);

    int addAllUsersToContest(ModelAddUserToContest modelAddUserToContest);

    void deleteUserContest(ModelAddUserToContest modelAddUserToContest) throws MiniLeetCodeException;

    Page<ContestSubmission> findContestSubmissionByContestIdPaging(Pageable pageable, String contestId);

    Page<ContestSubmission> findContestNotEvaluatedSubmissionByContestIdPaging(Pageable pageable, String contestId);

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


    ContestSubmissionEntity getContestSubmissionDetail(UUID submissionId);

    ModelGetContestInfosOfSubmissionOutput getContestInfosOfASubmission(UUID submissionId);

    void deleteTestcase(UUID testcaseId, String userId) throws MiniLeetCodeException;

    ModelCodeSimilarityOutput checkSimilarity(String contestId, ModelCheckSimilarityInput I);

    ModelEvaluateBatchSubmissionResponse evaluateBatchSubmissionContest(String contestId);

    ModelEvaluateBatchSubmissionResponse reJudgeAllSubmissionsOfContest(String contestId);

    ModelEvaluateBatchSubmissionResponse judgeAllSubmissionsOfContest(String contestId);

    void evaluateSubmission(UUID submisionId);

    void evaluateSubmission(ContestSubmissionEntity sub, ContestEntity contest);

    void evaluateSubmissionUsingQueue(ContestSubmissionEntity submission, ContestEntity contest);

    List<ModelContestByRoleResponse> getContestsByRoleOfUser(String userLoginId);

    List<CodePlagiarism> findAllByContestId(String contestId);

    List<CodePlagiarism> findAllBy(ModelGetCodeSimilarityParams input);

    List<ModelSimilarityClusterOutput> computeSimilarityClusters(ModelGetCodeSimilarityParams input);

    List<ModelReponseCodeSimilaritySummaryParticipant> getListModelReponseCodeSimilaritySummaryParticipant(String contestId);

    ContestSubmissionEntity updateContestSubmissionSourceCode(ModelUpdateContestSubmission input);

    List<ModelGetContestResponse> getContestsUsingAProblem(String problemId);

    int addAdminToManagerAndParticipantAllContest();

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

    boolean forbidMemberFromSubmitToContest(UUID id);

    boolean updatePermissionMemberToContest(String userId, ModelUpdatePermissionMemberToContestInput input);

    boolean updateProblemContest(String userId, ModelUpdateProblemContestInput I);

    List<ModelResponseUserProblemRole> getUserProblemRoles(String problemId);

    boolean addUserProblemRole(String userName, ModelUserProblemRole input)  throws Exception;

    boolean removeUserProblemRole(String userName, ModelUserProblemRole input)  throws Exception;

    boolean grantRole2AllProblems(String userLoginId, String userId, String roleId);

    void evaluateCustomProblemSubmission(UUID contestSubmissionId) throws Exception;

    List<TagEntity> getAllTags();

    TagEntity addNewTag(ModelTag tag);

    TagEntity updateTag(Integer tagId, ModelTag tag);

    void deleteTag(Integer tagId);

    void switchAllContestJudgeMode(String judgeMode);

    void exportProblem(String id, OutputStream outputStream);

    Page<ProblemEntity> getOwnerProblemsPaging(Pageable pageable, String ownerId);

    Page<ProblemEntity> getSharedProblemsPaging(Pageable pageable, String userId);

    ModelCreateContestProblemResponse getContestProblemDetailByIdAndTeacher(String problemId, String teacherId) throws Exception;
}
