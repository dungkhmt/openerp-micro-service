package com.hust.baseweb.applications.programmingcontest.service;

import com.hust.baseweb.applications.programmingcontest.constants.Constants;
import com.hust.baseweb.applications.programmingcontest.entity.*;
import com.hust.baseweb.applications.programmingcontest.exception.MiniLeetCodeException;
import com.hust.baseweb.applications.programmingcontest.model.*;
import com.hust.baseweb.applications.programmingcontest.model.externalapi.ContestProblemModelResponse;
import com.hust.baseweb.applications.programmingcontest.model.externalapi.SubmissionModelResponse;
import com.hust.baseweb.model.ProblemFilter;
import com.hust.baseweb.model.TestCaseFilter;
import com.hust.baseweb.model.dto.ProblemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public interface ProblemTestCaseService {

    ProblemEntity createContestProblem(String userID, ModelCreateContestProblem dto, MultipartFile[] files);

    ProblemEntity updateContestProblem(
        String problemId,
        String userId,
        ModelUpdateContestProblem dto,
        MultipartFile[] files
    ) throws Exception;

    List<ModelProblemGeneralInfo> getAllProblemsGeneralInfo();

    ModelCreateContestProblemResponse getContestProblem(String problemId) throws Exception;

//    ModelGetTestCaseResultResponse getTestCaseResult(
//        String problemId,
//        String userName,
//        ModelGetTestCaseResult modelGetTestCaseResult
//    ) throws Exception;

    ModelCheckCompileResponse checkCompile(ModelCheckCompile modelCheckCompile, String userName) throws Exception;

//    TestCaseEntity saveTestCase(String problemId, ModelSaveTestcase modelSaveTestcase);

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


    List<SubmissionDetailByTestcaseOM> getSubmissionDetailByTestcase(UUID submissionId, UUID testcaseId);

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

    ModelContestSubmissionResponse submitContestProblemNotExecuteDueToForbiddenInstructions(
        ModelContestSubmission modelContestSubmission,
        String userName,
        String submittedByUserId
    ) throws Exception;

    ModelContestSubmissionResponse submitContestProblemStoreOnlyNotExecute(
        ModelContestSubmission modelContestSubmission,
        String userName,
        String submittedByUserId
    ) throws Exception;


//    ModelContestSubmissionResponse submitSolutionOutput(
//        String solutionOutput,
//        String contestId,
//        String problemId,
//        UUID testCaseId,
//        String userName
//    ) throws Exception;
//
//    ModelContestSubmissionResponse submitSolutionOutputOfATestCase(
//        String userId,
//        String solutionOutput,
//        ModelSubmitSolutionOutputOfATestCase m
//    );

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

    List<ContestMembers> getListMemberOfContest(String contestId);

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

//    Page<ProblemEntity> getPublicProblemPaging(Pageable pageable);

    Page<ModelGetTestCaseDetail> getTestCaseByProblem(String problemId, TestCaseFilter filter);

    TestCaseDetailProjection getTestCaseDetail(UUID testCaseId);

//    void editTestCase(UUID testCaseId, ModelSaveTestcase modelSaveTestcase) throws MiniLeetCodeException;

    ModelAddUserToContestResponse addUserToContest(ModelAddUserToContest modelAddUserToContest);

    ModelAddUserToContestResponse updateUserFullnameOfContest(ModelAddUserToContest modelAddUserToContest);

    void addUsers2ToContest(String contestId, AddUsers2Contest addUsers2Contest);

    ModelAddUserToContestGroupResponse addUserToContestGroup(ModelAddUserToContestGroup modelAddUserToContestGroup);

    void deleteUserContest(ModelAddUserToContest modelAddUserToContest) throws MiniLeetCodeException;

    Page<ContestSubmission> findContestSubmissionByContestIdPaging(
        Pageable pageable,
        String contestId,
        String searchTerm
    );

    Page<ContestSubmission> findContestGroupSubmissionByContestIdPaging(
        Pageable pageable,
        String contestId,
        String userId,
        String searchTerm
    );

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
    void evaluateSubmissions(String contestId, String problemId);


    void evaluateSubmission(ContestSubmissionEntity sub, ContestEntity contest);

    void evaluateSubmissionUsingQueue(ContestSubmissionEntity submission, ContestEntity contest);

//    List<CodePlagiarism> findAllByContestId(String contestId);

    List<CodePlagiarism> findAllBy(ModelGetCodeSimilarityParams input);

    List<ModelSimilarityClusterOutput> computeSimilarityClusters(ModelGetCodeSimilarityParams input);

    List<ModelReponseCodeSimilaritySummaryParticipant> getListModelReponseCodeSimilaritySummaryParticipant(String contestId);

    ContestSubmissionEntity updateContestSubmissionSourceCode(ModelUpdateContestSubmission input);

    List<ModelGetContestResponse> getContestsUsingAProblem(String problemId);

    Object addTestcase(
        String testCase,
        ModelProgrammingContestUploadTestCase modelUploadTestCase
    ) throws Exception;

    Object reCreateTestcaseCorrectAnswer(String problemId, UUID testCaseId) throws Exception;

    Object editTestcase(
        UUID testCaseId,
        String testcaseContent,
        ModelProgrammingContestUploadTestCase modelUploadTestCase
    ) throws Exception;

    List<ModelUserJudgedProblemSubmissionResponse> getUserJudgedProblemSubmissions(String contestId);

    ModelGetRolesOfUserInContestResponse getRolesOfUserInContest(String userId, String contestId);

    boolean removeMemberFromContest(UUID id);

    boolean removeMemberFromContestGroup(String contestId, String userId, String participantId);


    boolean updatePermissionMemberToContest(String userId, ModelUpdatePermissionMemberToContestInput input);

    List<ModelResponseUserProblemRole> getUserProblemRoles(String problemId);

    boolean addUserProblemRole(String userName, ModelUserProblemRole input) throws Exception;

    boolean removeUserProblemRole(String userName, ModelUserProblemRole input) throws Exception;

    List<TagEntity> getAllTags();

    TagEntity addNewTag(ModelTag tag);

//    TagEntity updateTag(Integer tagId, ModelTag tag);

//    void deleteTag(Integer tagId);

    void switchAllContestJudgeMode(String judgeMode);

    void exportProblem(String id, OutputStream outputStream);

    Page<ProblemDTO> getProblems(String ownerId, ProblemFilter filter, Boolean isPublic);

    Page<ProblemDTO> getSharedProblems(String userId, ProblemFilter filter);

    Page<ProblemDTO> getPublicProblems(String userId, ProblemFilter filter);

    List<ProblemEntity> getAllProblems(String userId);

    List<ContestProblemModelResponse> extApiGetAllProblems(String userID);

    List<SubmissionModelResponse> extApiGetSubmissions(String participantId);


    ModelCreateContestProblemResponse getContestProblemDetailByIdAndTeacher(
        String problemId,
        String teacherId
    ) throws Exception;

    List<ModelImportProblemFromContestResponse> importProblemsFromAContest(ModelImportProblemsFromAContestInput I);

    ModelGetContestPageResponse getAllPublicContests();

    ProblemEntity cloneProblem(String userId, ModelCloneProblem cloneRequest) throws MiniLeetCodeException;

}
