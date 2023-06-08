package com.hust.baseweb.applications.programmingcontest.service;

import com.google.gson.Gson;
import com.hust.baseweb.applications.contentmanager.model.ContentHeaderModel;
import com.hust.baseweb.applications.contentmanager.model.ContentModel;
import com.hust.baseweb.applications.contentmanager.repo.MongoContentService;
import com.hust.baseweb.applications.education.classmanagement.utils.ZipOutputStreamUtils;
import com.hust.baseweb.applications.notifications.service.NotificationsService;
import com.hust.baseweb.applications.programmingcontest.constants.Constants;
import com.hust.baseweb.applications.programmingcontest.docker.DockerClientBase;
import com.hust.baseweb.applications.programmingcontest.entity.*;
import com.hust.baseweb.applications.programmingcontest.exception.MiniLeetCodeException;
import com.hust.baseweb.applications.programmingcontest.model.*;
import com.hust.baseweb.applications.programmingcontest.repo.*;
import com.hust.baseweb.applications.programmingcontest.service.helper.SubmissionResponseHandler;
import com.hust.baseweb.applications.programmingcontest.service.helper.cache.ProblemTestCaseServiceCache;
import com.hust.baseweb.applications.programmingcontest.utils.ComputerLanguage;
import com.hust.baseweb.applications.programmingcontest.utils.DateTimeUtils;
import com.hust.baseweb.applications.programmingcontest.utils.TempDir;
import com.hust.baseweb.applications.programmingcontest.utils.codesimilaritycheckingalgorithms.CodeSimilarityCheck;
import com.hust.baseweb.applications.programmingcontest.utils.stringhandler.ProblemSubmission;
import com.hust.baseweb.applications.programmingcontest.utils.stringhandler.StringHandler;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.model.ListPersonModel;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.repo.UserLoginRepo;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.hust.baseweb.config.rabbitmq.ProblemContestRoutingKey.JUDGE_PROBLEM;
import static com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestConfig.EXCHANGE;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProblemTestCaseServiceImpl implements ProblemTestCaseService {

    private final ProblemRepo problemRepo;
    private TestCaseRepo testCaseRepo;
    private DockerClientBase dockerClientBase;
    private TempDir tempDir;
    private ProblemPagingAndSortingRepo problemPagingAndSortingRepo;
    private ProblemSubmissionRepo problemSubmissionRepo;
    private UserLoginRepo userLoginRepo;
    private ContestRepo contestRepo;
    private Constants constants;
    private ContestPagingAndSortingRepo contestPagingAndSortingRepo;
    private ContestSubmissionRepo contestSubmissionRepo;
    private UserRegistrationContestRepo userRegistrationContestRepo;
    private NotificationsService notificationsService;
    private UserSubmissionContestResultNativeRepo userSubmissionContestResultNativeRepo;
    private UserRegistrationContestPagingAndSortingRepo userRegistrationContestPagingAndSortingRepo;
    private UserSubmissionContestResultNativePagingRepo userSubmissionContestResultNativePagingRepo;
    private ContestSubmissionPagingAndSortingRepo contestSubmissionPagingAndSortingRepo;
    private ContestSubmissionTestCaseEntityRepo contestSubmissionTestCaseEntityRepo;
    private UserService userService;
    private ContestRoleRepo contestRoleRepo;
    private CodePlagiarismRepo codePlagiarismRepo;
    private ContestSubmissionHistoryRepo contestSubmissionHistoryRepo;
    private ContestProblemRepo contestProblemRepo;
    private UserContestProblemRoleRepo userContestProblemRoleRepo;
    private TagRepo tagRepo;
    private MongoContentService mongoContentService;
    private ProblemService problemService;
    private ContestService contestService;
    private TestCaseService testCaseService;
    private RabbitTemplate rabbitTemplate;
    private SubmissionResponseHandler submissionResponseHandler;
    private ProblemTestCaseServiceCache cacheService;
    private ContestProblemExportService exporter;

    @Override
    @Transactional
    public ProblemEntity createContestProblem(
        String userID,
        String json,
        MultipartFile[] files
    ) throws MiniLeetCodeException {
        Gson gson = new Gson();
        ModelCreateContestProblem modelCreateContestProblem = gson.fromJson(json, ModelCreateContestProblem.class);

        if (problemRepo.findByProblemId(modelCreateContestProblem.getProblemId()) != null) {
            throw new MiniLeetCodeException("problem id already exist");
        }

        List<TagEntity> tags = new ArrayList<>();
        Integer[] tagIds = modelCreateContestProblem.getTagIds();
        for (Integer tagId : tagIds) {
            TagEntity tag = tagRepo.findByTagId(tagId);
            tags.add(tag);
        }

        List<String> attachmentId = new ArrayList<>();
        String[] fileId = modelCreateContestProblem.getFileId();
        List<MultipartFile> fileArray = Arrays.asList(files);

        fileArray.forEach((file) -> {
            ContentModel model = new ContentModel(fileId[fileArray.indexOf(file)], file);

            ObjectId id = null;
            try {
                id = mongoContentService.storeFileToGridFs(model);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (id != null) {
                ContentHeaderModel rs = new ContentHeaderModel(id.toHexString());
                attachmentId.add(rs.getId());
            }
        });

        ProblemEntity problemEntity = ProblemEntity.builder()
                                                   .problemId(modelCreateContestProblem.getProblemId())
                                                   .problemName(modelCreateContestProblem.getProblemName())
                                                   .problemDescription(modelCreateContestProblem.getProblemDescription())
                                                   .categoryId(modelCreateContestProblem.getCategoryId())
                                                   .memoryLimit(modelCreateContestProblem.getMemoryLimit())
                                                   .timeLimit(modelCreateContestProblem.getTimeLimit())
                                                   .levelId(modelCreateContestProblem.getLevelId())
                                                   .correctSolutionLanguage(modelCreateContestProblem.getCorrectSolutionLanguage())
                                                   .correctSolutionSourceCode(modelCreateContestProblem.getCorrectSolutionSourceCode())
                                                   .solution(modelCreateContestProblem.getSolution())
                                                   .solutionCheckerSourceCode(modelCreateContestProblem.getSolutionChecker())
                                                   .solutionCheckerSourceLanguage(modelCreateContestProblem.getSolutionCheckerLanguage())
                                                   .scoreEvaluationType(modelCreateContestProblem.getScoreEvaluationType() !=
                                                                        null
                                                                            ?
                                                                            modelCreateContestProblem.getScoreEvaluationType()
                                                                            :
                                                                                Constants.ProblemResultEvaluationType.NORMAL.getValue())
                                                   .createdAt(new Date())
                                                   .isPublicProblem(modelCreateContestProblem.getIsPublic())
                                                   .levelOrder(constants
                                                                   .getMapLevelOrder()
                                                                   .get(modelCreateContestProblem.getLevelId()))
                                                   .attachment(String.join(";", attachmentId))
                                                   .tags(tags)
                                                   .userId(userID)
                                                   .build();
        problemEntity = problemService.saveProblemWithCache(problemEntity);

        // grant role owner, manager, view to current user
        UserContestProblemRole upr = new UserContestProblemRole();
        upr.setProblemId(problemEntity.getProblemId());
        upr.setUserId(userID);
        upr.setRoleId(UserContestProblemRole.ROLE_OWNER);
        upr.setUpdateByUserId(userID);
        upr.setCreatedStamp(new Date());
        upr.setLastUpdated(new Date());
        upr = userContestProblemRoleRepo.save(upr);

        upr = new UserContestProblemRole();
        upr.setProblemId(problemEntity.getProblemId());
        upr.setUserId(userID);
        upr.setRoleId(UserContestProblemRole.ROLE_MANAGER);
        upr.setUpdateByUserId(userID);
        upr.setCreatedStamp(new Date());
        upr.setLastUpdated(new Date());
        upr = userContestProblemRoleRepo.save(upr);

        upr = new UserContestProblemRole();
        upr.setProblemId(problemEntity.getProblemId());
        upr.setUserId(userID);
        upr.setRoleId(UserContestProblemRole.ROLE_VIEW);
        upr.setUpdateByUserId(userID);
        upr.setCreatedStamp(new Date());
        upr.setLastUpdated(new Date());
        upr = userContestProblemRoleRepo.save(upr);


        // grant manager role to user admin
        UserLogin admin = userLoginRepo.findByUserLoginId("admin");
        if (admin != null) {
            upr = new UserContestProblemRole();
            upr.setProblemId(problemEntity.getProblemId());
            upr.setUserId(admin.getUserLoginId());
            upr.setRoleId(UserContestProblemRole.ROLE_MANAGER);
            upr.setUpdateByUserId(userID);
            upr.setCreatedStamp(new Date());
            upr.setLastUpdated(new Date());
            upr = userContestProblemRoleRepo.save(upr);

            upr = new UserContestProblemRole();
            upr.setProblemId(problemEntity.getProblemId());
            upr.setUserId(admin.getUserLoginId());
            upr.setRoleId(UserContestProblemRole.ROLE_VIEW);
            upr.setUpdateByUserId(userID);
            upr.setCreatedStamp(new Date());
            upr.setLastUpdated(new Date());
            upr = userContestProblemRoleRepo.save(upr);

            // push notification to admin
            notificationsService.create(userID, admin.getUserLoginId(),
                                        userID + " has created a contest problem ID " +
                                        problemEntity.getProblemId()
                , "");

        }
        return problemEntity;
    }

    @Override
    public ProblemEntity updateContestProblem(
        String problemId,
        String userId,
        String json,
        MultipartFile[] files
    ) throws Exception {

        if (!problemRepo.existsById(problemId)) {
            throw new MiniLeetCodeException("problem id not found");
        }

        Gson gson = new Gson();
        ModelUpdateContestProblem modelUpdateContestProblem = gson.fromJson(json, ModelUpdateContestProblem.class);

        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
        if (!userId.equals(problemEntity.getUserId())) {
            throw new MiniLeetCodeException("permission denied");
        }

        List<TagEntity> tags = new ArrayList<>();
        Integer[] tagIds = modelUpdateContestProblem.getTagIds();
        for (Integer tagId : tagIds) {
            TagEntity tag = tagRepo.findByTagId(tagId);
            tags.add(tag);
        }

        List<String> attachmentId = new ArrayList<>();
        attachmentId.add(problemEntity.getAttachment());
        String[] fileId = modelUpdateContestProblem.getFileId();
        List<MultipartFile> fileArray = Arrays.asList(files);

        List<String> removedFilesId = modelUpdateContestProblem.getRemovedFilesId();
        if (problemEntity.getAttachment() != null && !problemEntity.getAttachment().equals("")) {
            List<String> oldAttachmentIds = Arrays.asList(problemEntity.getAttachment().split(";"));
            for (String s : oldAttachmentIds) {
                try {
                    GridFsResource content = mongoContentService.getById(s);
                    if (content != null) {
                        if (removedFilesId.contains(content.getFilename())) {
                            mongoContentService.deleteFilesById(s);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        fileArray.forEach((file) -> {
            ContentModel model = new ContentModel(fileId[fileArray.indexOf(file)], file);

            ObjectId id = null;
            try {
                id = mongoContentService.storeFileToGridFs(model);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (id != null) {
                ContentHeaderModel rs = new ContentHeaderModel(id.toHexString());
                attachmentId.add(rs.getId());
            }
        });

        problemEntity.setProblemName(modelUpdateContestProblem.getProblemName());
        problemEntity.setProblemDescription(modelUpdateContestProblem.getProblemDescription());
        problemEntity.setLevelId(modelUpdateContestProblem.getLevelId());
        problemEntity.setCategoryId(modelUpdateContestProblem.getCategoryId());
        problemEntity.setSolution(modelUpdateContestProblem.getSolution());
        problemEntity.setTimeLimit(modelUpdateContestProblem.getTimeLimit());
        problemEntity.setMemoryLimit(modelUpdateContestProblem.getMemoryLimit());
        problemEntity.setCorrectSolutionLanguage(modelUpdateContestProblem.getCorrectSolutionLanguage());
        problemEntity.setCorrectSolutionSourceCode(modelUpdateContestProblem.getCorrectSolutionSourceCode());
        problemEntity.setSolutionCheckerSourceCode(modelUpdateContestProblem.getSolutionChecker());
        problemEntity.setScoreEvaluationType(modelUpdateContestProblem.getScoreEvaluationType());
        problemEntity.setPublicProblem(modelUpdateContestProblem.getIsPublic());
        problemEntity.setAttachment(String.join(";", attachmentId));
        problemEntity.setTags(tags);

        problemEntity = problemService.saveProblemWithCache(problemEntity);
        return problemEntity;
    }


    @Override
    public Page<ProblemEntity> getContestProblemPaging(Pageable pageable) {
        return problemPagingAndSortingRepo.findAll(pageable);
    }

    @Override
    public List<ProblemEntity> getAllProblems() {
        List<ProblemEntity> problems = problemRepo.findAll();
        return problems;
    }

    @Override
    public List<ModelProblemGeneralInfo> getAllProblemsGeneralInfo() {
        List<ModelProblemGeneralInfo> problems = problemRepo.getAllProblemGeneralInformation();
        return problems;
    }

    @Override
    public String executableIDECode(
        ModelRunCodeFromIDE modelRunCodeFromIDE,
        String userName,
        String computerLanguage
    ) throws Exception {
        String tempName = tempDir.createRandomScriptFileName(userName + "-" + computerLanguage);
        String response = runCode(
            modelRunCodeFromIDE.getSource(),
            computerLanguage,
            tempName,
            modelRunCodeFromIDE.getInput(),
            10,
            "Language Not Found");
        tempDir.pushToConcurrentLinkedQueue(tempName);
        return response;
    }

    @Override
    public ModelCreateContestProblemResponse getContestProblem(String problemId) throws Exception {
        ProblemEntity problemEntity;
        ModelCreateContestProblemResponse problemResponse = new ModelCreateContestProblemResponse();
        try {
            problemEntity = problemRepo.findByProblemId(problemId);
            if (problemEntity == null) {
                throw new MiniLeetCodeException("Problem not found");
            }
            problemResponse.setProblemId(problemEntity.getProblemId());
            problemResponse.setProblemName(problemEntity.getProblemName());
            problemResponse.setProblemDescription(problemEntity.getProblemDescription());
            problemResponse.setUserId(problemEntity.getUserId());
            problemResponse.setTimeLimit(problemEntity.getTimeLimit());
            problemResponse.setMemoryLimit(problemEntity.getMemoryLimit());
            problemResponse.setLevelId(problemEntity.getLevelId());
            problemResponse.setCategoryId(problemEntity.getCategoryId());
            problemResponse.setCorrectSolutionSourceCode(problemEntity.getCorrectSolutionSourceCode());
            problemResponse.setCorrectSolutionLanguage(problemEntity.getCorrectSolutionLanguage());
            problemResponse.setSolutionCheckerSourceCode(problemEntity.getSolutionCheckerSourceCode());
            problemResponse.setSolutionCheckerSourceLanguage(problemEntity.getSolutionCheckerSourceLanguage());
            problemResponse.setScoreEvaluationType(problemEntity.getScoreEvaluationType());
            problemResponse.setSolution(problemEntity.getSolution());
            problemResponse.setLevelOrder(problemEntity.getLevelOrder());
            problemResponse.setCreatedAt(problemEntity.getCreatedAt());
            problemResponse.setPublicProblem(problemEntity.isPublicProblem());
            problemResponse.setTags(problemEntity.getTags());

            if (problemEntity.getAttachment() != null) {
                String[] fileId = problemEntity.getAttachment().split(";", -1);
                if (fileId.length != 0) {
                    List<byte[]> fileArray = new ArrayList<>();
                    List<String> fileNames = new ArrayList<>();
                    for (String s : fileId) {
                        try {
                            GridFsResource content = mongoContentService.getById(s);
                            if (content != null) {
                                InputStream inputStream = content.getInputStream();
                                fileArray.add(IOUtils.toByteArray(inputStream));
                                fileNames.add(content.getFilename());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    problemResponse.setAttachment(fileArray);
                    problemResponse.setAttachmentNames(fileNames);
                } else {
                    problemResponse.setAttachment(null);
                    problemResponse.setAttachmentNames(null);
                }
            } else {
                problemResponse.setAttachment(null);
                problemResponse.setAttachmentNames(null);
            }

            return problemResponse;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    @Override
    public ModelProblemDetailRunCodeResponse problemDetailRunCode(
        String problemId,
        ModelProblemDetailRunCode modelProblemDetailRunCode,
        String userName
    ) throws Exception {
        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
        String tempName = tempDir.createRandomScriptFileName(problemEntity.getProblemName() +
                                                             "-" +
                                                             problemEntity.getCorrectSolutionLanguage());
        String output = runCode(
            modelProblemDetailRunCode.getSourceCode(),
            modelProblemDetailRunCode.getComputerLanguage(),
            tempName + "-" + userName + "-code",
            modelProblemDetailRunCode.getInput(),
            problemEntity.getTimeLimit(),
            "User Source Code Language Not Found");

        output = output.substring(0, output.length() - 1);

        int lastLineIndexOutput = output.lastIndexOf("\n");
        if (output.equals("Time Limit Exceeded")) {
            return ModelProblemDetailRunCodeResponse.builder()
                                                    .status("Time Limit Exceeded")
                                                    .build();
        }
        String status = output.substring(lastLineIndexOutput);
        //  log.info("status {}", status);
        if (status.contains("Compile Error")) {
            return ModelProblemDetailRunCodeResponse.builder()
                                                    .output(output.substring(0, lastLineIndexOutput))
                                                    .status("Compile Error")
                                                    .build();
        }
        //   log.info("status {}", status);
        output = output.substring(0, lastLineIndexOutput);
        String expected = runCode(
            problemEntity.getCorrectSolutionSourceCode(),
            problemEntity.getCorrectSolutionLanguage(),
            tempName + "-solution",
            modelProblemDetailRunCode.getInput(),
            problemEntity.getTimeLimit(),
            "Correct Solution Language Not Found");
        expected = expected.substring(0, expected.length() - 1);
        int lastLinetIndexExpected = expected.lastIndexOf("\n");
        expected = expected.substring(0, lastLinetIndexExpected);
        expected = expected.replaceAll("\n", "");
        output = output.replaceAll("\n", "");
        if (output.equals(expected)) {
            status = "Accept";
        } else {
            status = "Wrong Answer";
        }
        // log.info("output {}", output);
        // log.info("expected {}", expected);
        return ModelProblemDetailRunCodeResponse.builder()
                                                .expected(expected)
                                                .output(output)
                                                .status(status)
                                                .build();
    }

    @Override
    public ModelGetTestCaseResultResponse getTestCaseResult(
        String problemId,
        String userName,
        ModelGetTestCaseResult modelGetTestCaseResult
    ) throws Exception {
        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
        String tempName = tempDir.createRandomScriptFileName(userName +
                                                             "-" +
                                                             problemEntity.getProblemName() +
                                                             "-" +
                                                             problemEntity.getCorrectSolutionLanguage());
        String output = runCode(
            problemEntity.getCorrectSolutionSourceCode(),
            problemEntity.getCorrectSolutionLanguage(),
            tempName,
            modelGetTestCaseResult.getTestcase(),
            problemEntity.getTimeLimit(),
            "Correct Solution Language Not Found");
        if (output.contains("Time Limit Exceeded")) {
            return ModelGetTestCaseResultResponse.builder()
                                                 .result("")
                                                 .status("Time Limit Exceeded")
                                                 .build();
        }
        output = output.substring(0, output.length() - 1);
        int lastLinetIndexExpected = output.lastIndexOf("\n");
        output = output.substring(0, lastLinetIndexExpected);
//        output = output.replaceAll("\n", "");
        //    log.info("output {}", output);
        return ModelGetTestCaseResultResponse.builder()
                                             .result(output)
                                             .status("ok")
                                             .build();
    }

    @Override
    public ModelCheckCompileResponse checkCompile(
        ModelCheckCompile modelCheckCompile,
        String userName
    ) throws Exception {
        String tempName = tempDir.createRandomScriptFileName(userName);
        String resp;
        switch (modelCheckCompile.getComputerLanguage()) {
            case "CPP":
                tempDir.createScriptCompileFile(
                    modelCheckCompile.getSource(),
                    ComputerLanguage.Languages.CPP,
                    tempName);
                resp = dockerClientBase.runExecutable(ComputerLanguage.Languages.CPP, tempName);
                break;
            case "JAVA":
                tempDir.createScriptCompileFile(
                    modelCheckCompile.getSource(),
                    ComputerLanguage.Languages.JAVA,
                    tempName);
                resp = dockerClientBase.runExecutable(ComputerLanguage.Languages.JAVA, tempName);
                break;
            case "PYTHON3":
                tempDir.createScriptCompileFile(
                    modelCheckCompile.getSource(),
                    ComputerLanguage.Languages.PYTHON3,
                    tempName);
                resp = dockerClientBase.runExecutable(ComputerLanguage.Languages.PYTHON3, tempName);
                break;
            case "GOLANG":
                tempDir.createScriptCompileFile(
                    modelCheckCompile.getSource(),
                    ComputerLanguage.Languages.GOLANG,
                    tempName);
                resp = dockerClientBase.runExecutable(ComputerLanguage.Languages.GOLANG, tempName);
                break;
            default:
                throw new Exception("Language not found");
        }
        if (resp.contains("Successful")) {
            return ModelCheckCompileResponse.builder()
                                            .status("Successful")
                                            .message("")
                                            .build();

        } else {
            return ModelCheckCompileResponse.builder()
                                            .status("Compile Error")
                                            .message(resp)
                                            .build();
        }
    }

    @Override
    public TestCaseEntity saveTestCase(String problemId, ModelSaveTestcase modelSaveTestcase) {

        TestCaseEntity testCaseEntity = TestCaseEntity.builder()
                                                      .correctAnswer(modelSaveTestcase.getResult())
                                                      .testCase(modelSaveTestcase.getInput())
                                                      .testCasePoint(modelSaveTestcase.getPoint())
                                                      .problemId(problemId)
                                                      .isPublic(modelSaveTestcase.getIsPublic())
                                                      .build();
        return testCaseService.saveTestCaseWithCache(testCaseEntity);
    }


    @Override
    public ListProblemSubmissionResponse getListProblemSubmissionResponse(
        String problemId,
        String userId
    ) throws Exception {
        UserLogin userLogin = userLoginRepo.findByUserLoginId(userId);
        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
        if (userLogin == null || problemEntity == null) {
            throw new Exception("not found");
        }
        List<Object[]> list = problemSubmissionRepo.getListProblemSubmissionByUserAndProblemId(
            userLogin,
            problemEntity);
        List<ProblemSubmissionResponse> problemSubmissionResponseList = new ArrayList<>();
        try {
            list.forEach(objects -> {
                //  log.info("objects {}", objects);
                ProblemSubmissionResponse problemSubmissionResponse = ProblemSubmissionResponse.builder()
                                                                                               .problemSubmissionId((UUID) objects[0])
                                                                                               .timeSubmitted((String) objects[1])
                                                                                               .status((String) objects[2])
                                                                                               .score((int) objects[3])
                                                                                               .runtime((String) objects[4])
                                                                                               .memoryUsage((float) objects[5])
                                                                                               .language((String) objects[6])
                                                                                               .build();
                problemSubmissionResponseList.add(problemSubmissionResponse);
            });
        } catch (Exception e) {
            // log.info("error");
            e.printStackTrace();
            throw e;
        }

        return ListProblemSubmissionResponse.builder()
                                            .contents(problemSubmissionResponseList)
                                            .isSubmitted(list.size() != 0)
                                            .build();
    }

    @Transactional
    @Override
    public ContestEntity createContest(ModelCreateContest modelCreateContest, String userName) throws Exception {
        try {
            ContestEntity contestEntityExist = contestRepo.findContestByContestId(modelCreateContest.getContestId());
            if (contestEntityExist != null) {
                throw new MiniLeetCodeException("Contest is already exist");
            }
            ContestEntity contestEntity = null;
//            List<ProblemEntity> problemEntities = getContestProblemsFromListContestId(modelCreateContest.getProblemIds());

            if (modelCreateContest.getStartedAt() != null) {
                contestEntity = ContestEntity.builder()
                                             .contestId(modelCreateContest.getContestId())
                                             .contestName(modelCreateContest.getContestName())
                                             .contestSolvingTime(modelCreateContest.getContestTime())
//                                             .problems(problemEntities)
                                             .isPublic(modelCreateContest.isPublic())
                                             .countDown(modelCreateContest.getCountDownTime())
                                             .startedAt(modelCreateContest.getStartedAt())
                                             .startedCountDownTime(DateTimeUtils.minusMinutesDate(
                                                 modelCreateContest.getStartedAt(),
                                                 modelCreateContest.getCountDownTime()))
                                             .endTime(DateTimeUtils.addMinutesDate(
                                                 modelCreateContest.getStartedAt(),
                                                 modelCreateContest.getContestTime()))
                                             .userId(userName)
                                             .statusId(ContestEntity.CONTEST_STATUS_CREATED)
                                             .maxNumberSubmissions(modelCreateContest.getMaxNumberSubmissions())
                                             .maxSourceCodeLength(modelCreateContest.getMaxSourceCodeLength())
                                             .minTimeBetweenTwoSubmissions(modelCreateContest.getMinTimeBetweenTwoSubmissions())
                                             .judgeMode(ContestEntity.ASYNCHRONOUS_JUDGE_MODE_QUEUE)
                                             .useCacheContestProblem(ContestEntity.USE_CACHE_CONTEST_PROBLEM_YES)
                                             .submissionActionType(ContestEntity.CONTEST_SUBMISSION_ACTION_TYPE_STORE_AND_EXECUTE)
                                             .problemDescriptionViewType(ContestEntity.CONTEST_PROBLEM_DESCRIPTION_VIEW_TYPE_VISIBLE)
                                             //.participantViewResultMode(ContestEntity.CONTEST_PARTICIPANT_VIEW_MODE_SEE_CORRECT_ANSWER)
                                             .participantViewResultMode(ContestEntity.CONTEST_PARTICIPANT_VIEW_MODE_SEE_CORRECT_ANSWER_AND_PRIVATE_TESTCASE_SHORT)
                                             .evaluateBothPublicPrivateTestcase(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_NO)
                                             .createdAt(new Date())
                                             .build();
            } else {
                contestEntity = ContestEntity.builder()
                                             .contestId(modelCreateContest.getContestId())
                                             .contestName(modelCreateContest.getContestName())
                                             .contestSolvingTime(modelCreateContest.getContestTime())
//                                             .problems(problemEntities)
                                             .isPublic(modelCreateContest.isPublic())
                                             .countDown(modelCreateContest.getCountDownTime())
                                             .userId(userName)
                                             .statusId(ContestEntity.CONTEST_STATUS_CREATED)
                                             .maxNumberSubmissions(modelCreateContest.getMaxNumberSubmissions())
                                             .maxSourceCodeLength(modelCreateContest.getMaxSourceCodeLength())
                                             .createdAt(new Date())
                                             .build();
            }
            contestEntity = contestService.saveContestWithCache(contestEntity);

            // create corresponding entities in ContestRole
            ContestRole contestRole = new ContestRole();
            contestRole.setContestId(modelCreateContest.getContestId());
            contestRole.setUserLoginId(userName);
            Date fromDate = new Date();
            contestRole.setRoleId(ContestRole.CONTEST_ROLE_OWNER);
            contestRole.setFromDate(fromDate);
            contestRole = contestRoleRepo.save(contestRole);

            contestRole = new ContestRole();
            contestRole.setContestId(modelCreateContest.getContestId());
            contestRole.setUserLoginId(userName);
            contestRole.setRoleId(ContestRole.CONTEST_ROLE_MANAGER);
            contestRole.setFromDate(fromDate);
            contestRole = contestRoleRepo.save(contestRole);

            contestRole = new ContestRole();
            contestRole.setContestId(modelCreateContest.getContestId());
            contestRole.setUserLoginId(userName);
            contestRole.setRoleId(ContestRole.CONTEST_ROLE_PARTICIPANT);
            contestRole.setFromDate(fromDate);
            contestRole = contestRoleRepo.save(contestRole);

            // create correspoding entities in UserRegistrationContestEntity
            UserRegistrationContestEntity urc = new UserRegistrationContestEntity();
            urc.setContestId(modelCreateContest.getContestId());
            urc.setRoleId(UserRegistrationContestEntity.ROLE_OWNER);
            urc.setUserId(userName);
            urc.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
            urc = userRegistrationContestRepo.save(urc);

            urc = new UserRegistrationContestEntity();
            urc.setContestId(modelCreateContest.getContestId());
            urc.setRoleId(UserRegistrationContestEntity.ROLE_MANAGER);
            urc.setUserId(userName);
            urc.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
            urc = userRegistrationContestRepo.save(urc);

            urc = new UserRegistrationContestEntity();
            urc.setContestId(modelCreateContest.getContestId());
            urc.setRoleId(UserRegistrationContestEntity.ROLE_PARTICIPANT);
            urc.setUserId(userName);
            urc.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
            urc = userRegistrationContestRepo.save(urc);

            // add account admin to the contest
            String admin = "admin";
            UserLogin u = userLoginRepo.findByUserLoginId(admin);
            if (u != null) {
                urc = new UserRegistrationContestEntity();
                urc.setContestId(modelCreateContest.getContestId());
                urc.setRoleId(UserRegistrationContestEntity.ROLE_OWNER);
                urc.setUserId(admin);
                urc.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
                urc = userRegistrationContestRepo.save(urc);

                urc = new UserRegistrationContestEntity();
                urc.setContestId(modelCreateContest.getContestId());
                urc.setRoleId(UserRegistrationContestEntity.ROLE_MANAGER);
                urc.setUserId(admin);
                urc.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
                urc = userRegistrationContestRepo.save(urc);

                urc = new UserRegistrationContestEntity();
                urc.setContestId(modelCreateContest.getContestId());
                urc.setRoleId(UserRegistrationContestEntity.ROLE_PARTICIPANT);
                urc.setUserId(admin);
                urc.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
                urc = userRegistrationContestRepo.save(urc);

                //push notification to admin
                notificationsService.create(userName, u.getUserLoginId(),
                                            userName + " has created a contest " +
                                            modelCreateContest.getContestId()
                    , "");

            }
            return contestEntity;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ContestEntity updateContest(
        ModelUpdateContest modelUpdateContest,
        String userName,
        String contestId
    ) throws Exception {
        ContestEntity contestEntityExist = contestRepo.findContestByContestId(contestId);
        if (contestEntityExist == null) {
            throw new MiniLeetCodeException("Contest does not exist");
        }
        //  log.info("updateContest, isPublic = " + modelUpdateContest.getIsPublic());
        boolean isPublic = !modelUpdateContest.getIsPublic().equals("false");

        //    log.info("updateContest, modelUpdateContest.isPublic = " + modelUpdateContest.getIsPublic() + " -> isPublic  = " + isPublic);
        UserLogin userLogin = userLoginRepo.findByUserLoginId(userName);

        List<UserRegistrationContestEntity> L = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
                contestId,
                userName,
                UserRegistrationContestEntity.STATUS_SUCCESSFUL);
        boolean canUpdate = false;
        for (UserRegistrationContestEntity u : L) {
            if (u.getRoleId().equals(UserRegistrationContestEntity.ROLE_MANAGER) ||
                u.getRoleId().equals(UserRegistrationContestEntity.ROLE_OWNER)) {
                canUpdate = true;
                break;
            }
        }
        if (!canUpdate) {
            throw new MiniLeetCodeException("You don't have privileged");
        }
        //if(!userName.equals(contestEntityExist.getUserId())){
        //    throw new MiniLeetCodeException("You don't have privileged");
        //}


//        List<ProblemEntity> problemEntities = getContestProblemsFromListContestId(modelUpdateContest.getProblemIds());
        if (modelUpdateContest.getStartedAt() != null) {
            ContestEntity contestEntity = ContestEntity.builder()
                                                       .contestId(contestId)
                                                       .contestName(modelUpdateContest.getContestName())
                                                       .contestSolvingTime(modelUpdateContest.getContestSolvingTime())
                                                       .problems(contestEntityExist.getProblems())
                                                       .userId(userName)
                                                       .countDown(modelUpdateContest.getCountDownTime())
                                                       .startedAt(modelUpdateContest.getStartedAt())
                                                       .startedCountDownTime(DateTimeUtils.minusMinutesDate(
                                                           modelUpdateContest.getStartedAt(),
                                                           modelUpdateContest.getCountDownTime()))
                                                       .endTime(DateTimeUtils.addMinutesDate(
                                                           modelUpdateContest.getStartedAt(),
                                                           modelUpdateContest.getContestSolvingTime()))
                                                       .isPublic(isPublic)
                                                       .statusId(modelUpdateContest.getStatusId())
                                                       .submissionActionType(modelUpdateContest.getSubmissionActionType())
                                                       .maxNumberSubmissions(modelUpdateContest.getMaxNumberSubmission())
                                                       .participantViewResultMode(modelUpdateContest.getParticipantViewResultMode())
                                                       .problemDescriptionViewType(modelUpdateContest.getProblemDescriptionViewType())
                                                       .useCacheContestProblem(modelUpdateContest.getUseCacheContestProblem())
                                                       .maxSourceCodeLength(modelUpdateContest.getMaxSourceCodeLength())
                                                       .evaluateBothPublicPrivateTestcase(modelUpdateContest.getEvaluateBothPublicPrivateTestcase())
                                                       .minTimeBetweenTwoSubmissions(modelUpdateContest.getMinTimeBetweenTwoSubmissions())
                                                       .judgeMode(modelUpdateContest.getJudgeMode())
                                                       .build();
            return contestService.updateContestWithCache(contestEntity);

        } else {
            ContestEntity contestEntity = ContestEntity.builder()
                                                       .contestId(contestId)
                                                       .contestName(modelUpdateContest.getContestName())
                                                       .contestSolvingTime(modelUpdateContest.getContestSolvingTime())
                                                       .problems(contestEntityExist.getProblems())
                                                       .userId(userName)
                                                       .countDown(modelUpdateContest.getCountDownTime())
                                                       .statusId(modelUpdateContest.getStatusId())
                                                       .build();
            return contestService.updateContestWithCache(contestEntity);
        }

    }

    @Override
    public ContestProblem saveProblemInfoInContest(
        ModelProblemInfoInContest modelProblemInfoInContest,
        String userName
    ) {
        String contestId = modelProblemInfoInContest.getContestId();
        String problemId = modelProblemInfoInContest.getProblemId();

//        ContestEntity contest = contestService.findContest(contestId);
//        List<ProblemEntity> problems = contest.getProblems();

//        if (problems.stream().anyMatch(p -> p.getProblemId().equals(problemId)))
//            return null;

        // ProblemEntity newProblem = problemService.findProblem(problemId);
        // problems.add(newProblem);
        // contest.setProblems(problems);

        // contestService.saveContest(contest);

        ContestProblem contestProblem = contestProblemRepo.findByContestIdAndProblemId(contestId, problemId);

        if (contestProblem == null) {
            contestProblem = new ContestProblem();
            contestProblem.setContestId(contestId);
            contestProblem.setProblemId(problemId);
        }

        if (modelProblemInfoInContest.getProblemRename().isEmpty()) {
            contestProblem.setProblemRename(modelProblemInfoInContest.getProblemName());
        } else {
            contestProblem.setProblemRename(modelProblemInfoInContest.getProblemRename());
        }

        if (modelProblemInfoInContest.getProblemRecode().isEmpty()) {
            contestProblem.setProblemRecode("P__" + modelProblemInfoInContest.getProblemName());
        } else {
            contestProblem.setProblemRecode(modelProblemInfoInContest.getProblemRecode());
        }

        contestProblem.setSubmissionMode(modelProblemInfoInContest.getSubmissionMode());

        return contestProblemRepo.save(contestProblem);
    }

    @Override
    public void removeProblemFromContest(String contestId, String problemId, String userName) {
        ContestProblem contestProblem = contestProblemRepo.findByContestIdAndProblemId(contestId, problemId);
        if (contestProblem != null) {
            contestProblemRepo.delete(contestProblem);
        }
    }

    @Override
    public ModelProblemSubmissionDetailResponse findProblemSubmissionById(
        UUID id,
        String userName
    ) throws MiniLeetCodeException {
        ProblemSubmissionEntity problemSubmissionEntity = problemSubmissionRepo.findByProblemSubmissionId(id);
        if (!problemSubmissionEntity.getUserLogin().getUserLoginId().equals(userName)) {
            throw new MiniLeetCodeException("unauthor");
        }
        return ModelProblemSubmissionDetailResponse.builder()
                                                   .problemSubmissionId(problemSubmissionEntity.getProblemSubmissionId())
                                                   .problemId(problemSubmissionEntity.getProblem().getProblemId())
                                                   .problemName(problemSubmissionEntity.getProblem().getProblemName())
                                                   .submittedAt(problemSubmissionEntity.getTimeSubmitted())
                                                   .submissionSource(problemSubmissionEntity.getSourceCode())
                                                   .submissionLanguage(problemSubmissionEntity.getSourceCodeLanguages())
                                                   .score(problemSubmissionEntity.getScore())
                                                   .testCasePass(problemSubmissionEntity.getTestCasePass())
                                                   .runTime(problemSubmissionEntity.getRuntime())
                                                   .memoryUsage(problemSubmissionEntity.getMemoryUsage())
                                                   .status(problemSubmissionEntity.getStatus())
                                                   .build();
    }

    @Override
    public ModelGetContestPageResponse getContestPaging(Pageable pageable) {
        Page<ContestEntity> contestPage = contestPagingAndSortingRepo.findAll(pageable);
        return getModelGetContestPageResponse(contestPage);
    }

    @Override
    public ModelGetContestDetailResponse getContestDetailByContestIdAndTeacher(String contestId, String userName) {
        //ContestEntity contestEntity = contestRepo.findContestEntityByContestIdAndUserId(contestId, userName);
        boolean ok = false;
        // check role of teacher
        //UserRegistrationContestEntity c = userRegistrationContestRepo
        //    .findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(contestId, userName, Constants.RegistrationType.SUCCESSFUL.getValue());
        List<UserRegistrationContestEntity> lc = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
                contestId,
                userName,
                Constants.RegistrationType.SUCCESSFUL.getValue());

        ok = (lc != null && lc.size() > 0) || (userName.equals("admin"));
        //   log.info("getContestDetailByContestIdAndTeacher, userName = " + userName + " ok = " + ok);

        /*
        List<ModelContestByRoleResponse> L = getContestsByRoleOfUser(userName);
        for(UserRegistrationContestEntity c: L){
            log.info("getContestDetailByContestIdAndTeacher, user " + userName + " contest "
                     + c.getContestId() + " role = " + c.getRoleId());
            if(c.getContestId().equals(contestId) && (c.getRoleId().equals(UserRegistrationContestEntity.ROLE_MANAGER)
                                                      || c.getRoleId().equals(UserRegistrationContestEntity.ROLE_OWNER))){
                ok = true; break;
            }
        }
        */
        ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
        //    log.info("contestEntity {}", contestEntity);
        if (!ok || contestEntity == null) {
            //  log.info("user does not create contest");
            return ModelGetContestDetailResponse.builder()
                                                .unauthorized(true)
                                                .build();
        }
        return contestService.getModelGetContestDetailResponse(contestEntity);
    }

    @Override
    public ModelGetContestDetailResponse getContestDetailByContestId(String contestId) {
        ContestEntity contestEntity = contestService.findContestWithCache(contestId);
        return contestService.getModelGetContestDetailResponse(contestEntity);
    }

    @Override
    public Page<ModelProblemSubmissionDetailByTestCaseResponse> getContestProblemSubmissionDetailByTestCase(Pageable page) {
        Page<ContestSubmissionTestCaseEntity> L = contestSubmissionTestCaseEntityRepo.findAll(page);
        Page<ModelProblemSubmissionDetailByTestCaseResponse> retLst = L.map(e -> {
            TestCaseEntity tc = testCaseRepo.findTestCaseByTestCaseId(e.getTestCaseId());
            String testCase = "";
            String correctAnswer = "";
            if (tc != null) {
                testCase = tc.getTestCase();
                correctAnswer = tc.getCorrectAnswer();
            }
            return new ModelProblemSubmissionDetailByTestCaseResponse(
                e.getContestSubmissionTestcaseId(),
                e.getContestId(),
                e.getProblemId(),
                e.getSubmittedByUserLoginId(),
                e.getTestCaseId(),
                testCase,
                e.getStatus(),
                e.getPoint(),
                correctAnswer,
                e.getParticipantSolutionOtput(),
                e.getCreatedStamp(),
                "N"
            );
        });
        return retLst;
    }

    @Override
    public List<ModelProblemSubmissionDetailByTestCaseResponse> getContestProblemSubmissionDetailByTestCaseOfASubmission(
        UUID submissionId
    ) {
        ContestSubmissionEntity sub = contestSubmissionRepo.findById(submissionId).orElse(null);
        ContestEntity contest = null;
        String contestId = "";
        String problemId = "";

        if (sub != null) {
            contest = contestRepo.findContestByContestId(sub.getContestId());
            contestId = sub.getContestId();
            problemId = sub.getProblemId();
        }
        String viewSubmitSolutionOutputMode = "N";
        ContestProblem contestProblem = contestProblemRepo.findByContestIdAndProblemId(contestId, problemId);
        if (contestProblem != null) {
            if (contestProblem.getSubmissionMode() != null) {
                if (contestProblem.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_SOLUTION_OUTPUT)) {
                    viewSubmitSolutionOutputMode = "Y";
                }
            }
        }

        List<ContestSubmissionTestCaseEntity> L = contestSubmissionTestCaseEntityRepo.findAllByContestSubmissionId((submissionId));
        //log.info("getContestProblemSubmissionDetailByTestCaseOfASubmission, submissionId  = " + submissionId + " retList = " + L.size());
        List<ModelProblemSubmissionDetailByTestCaseResponse> retLst = new ArrayList();

        for (ContestSubmissionTestCaseEntity e : L) {
            TestCaseEntity tc = testCaseRepo.findTestCaseByTestCaseId(e.getTestCaseId());
            String testCase = "";
            String testCaseOutput = "";
            String participantSolutionOutput = e.getParticipantSolutionOtput();
            if (tc != null) {
                testCase = tc.getTestCase();
                testCaseOutput = tc.getCorrectAnswer();
            }
            if (contest != null) {
                if (contest
                    .getParticipantViewResultMode()
                    .equals(ContestEntity.CONTEST_PARTICIPANT_VIEW_MODE_SEE_CORRECT_ANSWER_AND_PRIVATE_TESTCASE_SHORT)) {
                    testCaseOutput = StringHandler.shorthen(testCaseOutput, 100);
                    participantSolutionOutput = StringHandler.shorthen(participantSolutionOutput, 100);
                }
            }
            retLst.add(new ModelProblemSubmissionDetailByTestCaseResponse(
                e.getContestSubmissionTestcaseId(),
                e.getContestId(),
                e.getProblemId(),
                e.getSubmittedByUserLoginId(),
                e.getTestCaseId(),
                testCase,
                e.getStatus(),
                e.getPoint(),
                //e.getTestCaseOutput(),
                testCaseOutput,
                //e.getParticipantSolutionOtput(),
                participantSolutionOutput,
                e.getCreatedStamp(),
                viewSubmitSolutionOutputMode
            ));
        }
        return retLst;
    }

    @Override
    public List<ModelProblemSubmissionDetailByTestCaseResponse> getContestProblemSubmissionDetailByTestCaseOfASubmissionViewedByParticipant(
        UUID submissionId
    ) {
        ContestSubmissionEntity sub = contestSubmissionRepo.findContestSubmissionEntityByContestSubmissionId(
            submissionId);
        ContestEntity contest = null;
        String contestId = "";
        String problemId = "";
        if (sub != null) {
            contest = contestRepo.findContestByContestId(sub.getContestId());
            contestId = sub.getContestId();
            problemId = sub.getProblemId();
        }
        String viewSubmitSolutionOutputMode = "N";
        ContestProblem contestProblem = contestProblemRepo.findByContestIdAndProblemId(contestId, problemId);
        if (contestProblem != null) {
            if (contestProblem.getSubmissionMode() != null) {
                if (contestProblem.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_SOLUTION_OUTPUT)) {
                    viewSubmitSolutionOutputMode = "Y";
                }
            }
        }

        List<ContestSubmissionTestCaseEntity> L = contestSubmissionTestCaseEntityRepo.findAllByContestSubmissionId((submissionId));
        //log.info("getContestProblemSubmissionDetailByTestCaseOfASubmission, submissionId  = " + submissionId + " retList = " + L.size());
        List<ModelProblemSubmissionDetailByTestCaseResponse> retLst = new ArrayList();
        for (ContestSubmissionTestCaseEntity e : L) {

            String testCase = "";
            String testCaseOutput = "";
            String participantSolutionOutput = e.getParticipantSolutionOtput();
            if (contest != null) {
                TestCaseEntity tc = testCaseRepo.findTestCaseByTestCaseId(e.getTestCaseId());
                if (tc == null) {
                    break;
                }

                testCase = tc.getTestCase();
                testCaseOutput = tc.getCorrectAnswer();
                switch (contest.getParticipantViewResultMode()) {
                    case ContestEntity.CONTEST_PARTICIPANT_VIEW_MODE_SEE_CORRECT_ANSWER_AND_PRIVATE_TESTCASE: {
//                        testCaseOutput = tc.getCorrectAnswer();
                        break;
                    }
                    case ContestEntity.CONTEST_PARTICIPANT_VIEW_MODE_SEE_CORRECT_ANSWER_AND_PRIVATE_TESTCASE_SHORT: {
//                        testCaseOutput = tc.getCorrectAnswer();
                        //testCase = StringHandler.shorthen(testCase,100);
                        testCaseOutput = StringHandler.shorthen(testCaseOutput, 100);
                        participantSolutionOutput = StringHandler.shorthen(participantSolutionOutput, 100);
                        break;
                    }
                    case ContestEntity.CONTEST_PARTICIPANT_VIEW_MODE_NOT_SEE_CORRECT_ANSWER: {
                        testCaseOutput = "HIDDEN";//StringHandler.shorthen(testCaseOutput,100);
                        participantSolutionOutput = StringHandler.shorthen(participantSolutionOutput, 100);
                        break;
                    }
                }
            }

            retLst.add(new ModelProblemSubmissionDetailByTestCaseResponse(
                e.getContestSubmissionTestcaseId(),
                e.getContestId(),
                e.getProblemId(),
                e.getSubmittedByUserLoginId(),
                e.getTestCaseId(),
                testCase,
                e.getStatus(),
                e.getPoint(),
                //e.getTestCaseOutput(),
                testCaseOutput,

                //e.getParticipantSolutionOtput(),
                participantSolutionOutput,
                e.getCreatedStamp(),
                viewSubmitSolutionOutputMode
            ));
        }
        return retLst;
    }

    @Override
    public ModelContestSubmissionResponse problemDetailSubmission(
        ModelProblemDetailSubmission modelProblemDetailSubmission,
        String problemId,
        String userName
    ) throws Exception {
        // log.info("source {} ", modelProblemDetailSubmission.getSource());
        UserLogin userLogin = userLoginRepo.findByUserLoginId(userName);
        if (userLogin == null) {
            throw new Exception(("user not found"));
        }
        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
        if (problemEntity == null) {
            throw new Exception("Contest problem does not exist");
        }
        List<TestCaseEntity> testCaseEntityList = testCaseRepo.findAllByProblemId(problemId);
        if (testCaseEntityList == null) {
            throw new Exception("Problem Does not have testcase");
        }
        String tempName = tempDir.createRandomScriptFileName(userName + "-" + problemId);
        String response = submission(
            modelProblemDetailSubmission.getSource(),
            modelProblemDetailSubmission.getLanguage(),
            tempName,
            testCaseEntityList,
            "Language Not Found",
            problemEntity.getTimeLimit(),
            problemEntity.getMemoryLimit());

        List<String> correctAns = testCaseEntityList
            .stream()
            .map(TestCaseEntity::getCorrectAnswer)
            .collect(Collectors.toList());
        List<Integer> points = testCaseEntityList
            .stream()
            .map(TestCaseEntity::getTestCasePoint)
            .collect(Collectors.toList());
        ProblemSubmission problemSubmission = StringHandler.handleContestResponse(response, correctAns, points);
        //   log.info("problemSubmission {}", problemSubmission);
        ProblemSubmissionEntity p = ProblemSubmissionEntity.builder()
                                                           .problem(problemEntity)
                                                           .score(problemSubmission.getScore())
                                                           .userLogin(userLogin)
                                                           .testCasePass(problemSubmission.getTestCasePass())
                                                           .status(problemSubmission.getStatus())
                                                           .runtime("" + problemSubmission.getRuntime())
                                                           .sourceCode(modelProblemDetailSubmission.getSource())
                                                           .sourceCodeLanguages(modelProblemDetailSubmission.getLanguage())
                                                           .build();
        problemSubmissionRepo.save(p);
        return ModelContestSubmissionResponse.builder()
                                             .status(problemSubmission.getStatus())
                                             .testCasePass(p.getTestCasePass())
                                             .runtime(problemSubmission.getRuntime())
                                             .memoryUsage(p.getMemoryUsage())
                                             .problemName(problemEntity.getProblemName())
                                             .score(problemSubmission.getScore())
                                             .build();
    }

    @Override
    public ModelContestSubmissionResponse submitContestProblem(
        ModelContestSubmission modelContestSubmission,
        String userName
    ) throws Exception {
        //    log.info("submitContestProblem");
        //    log.info("modelContestSubmission {}", modelContestSubmission);
        ProblemEntity problemEntity = problemRepo.findByProblemId(modelContestSubmission.getProblemId());

        //UserRegistrationContestEntity userRegistrationContest = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(modelContestSubmission.getContestId(), userName, Constants.RegistrationType.SUCCESSFUL.getValue());
        List<UserRegistrationContestEntity> userRegistrationContests = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
            modelContestSubmission.getContestId(),
            userName,
            Constants.RegistrationType.SUCCESSFUL.getValue());
        UserRegistrationContestEntity userRegistrationContest = null;
        if (userRegistrationContests != null && userRegistrationContests.size() > 0) {
            userRegistrationContest = userRegistrationContests.get(0);
        }

        //   log.info("userRegistrationContest {}", userRegistrationContest);
        if (userRegistrationContest == null) {
            throw new MiniLeetCodeException("User not register contest");
        }
        List<TestCaseEntity> testCaseEntityList = testCaseRepo.findAllByProblemId(modelContestSubmission.getProblemId());
        String tempName = tempDir.createRandomScriptFileName(userName +
                                                             "-" +
                                                             modelContestSubmission.getContestId() +
                                                             "-" +
                                                             modelContestSubmission.getProblemId());

        String response = submission(
            modelContestSubmission.getSource(),
            modelContestSubmission.getLanguage(),
            tempName,
            testCaseEntityList,
            "language not found",
            problemEntity.getTimeLimit(),
            problemEntity.getMemoryLimit());

        List<String> testCaseAns = testCaseEntityList
            .stream()
            .map(TestCaseEntity::getCorrectAnswer)
            .collect(Collectors.toList());
        List<Integer> points = testCaseEntityList
            .stream()
            .map(TestCaseEntity::getTestCasePoint)
            .collect(Collectors.toList());
        ProblemSubmission problemSubmission = StringHandler.handleContestResponse(response, testCaseAns, points);
        ContestSubmissionEntity c = ContestSubmissionEntity.builder()
                                                           .contestId(modelContestSubmission.getContestId())
                                                           .status(problemSubmission.getStatus())
                                                           .point(problemSubmission.getScore())
                                                           .problemId(modelContestSubmission.getProblemId())
                                                           .userId(userName)
                                                           .testCasePass(problemSubmission.getTestCasePass())
                                                           .sourceCode(modelContestSubmission.getSource())
                                                           .sourceCodeLanguage(modelContestSubmission.getLanguage())
                                                           .runtime(problemSubmission.getRuntime())
                                                           .createdAt(new Date())
                                                           .build();
        c = contestSubmissionRepo.save(c);
        //  log.info("c {}", c.getRuntime());
        return ModelContestSubmissionResponse.builder()
                                             .status(problemSubmission.getStatus())
                                             .testCasePass(c.getTestCasePass())
                                             .runtime(problemSubmission.getRuntime())
                                             .memoryUsage(c.getMemoryUsage())
                                             .problemName(problemEntity.getProblemName())
                                             .contestSubmissionID(c.getContestSubmissionId())
                                             .submittedAt(c.getCreatedAt())
                                             .score(problemSubmission.getScore())
                                             .build();
    }

    @Transactional
    @Override
    public ModelContestSubmissionResponse submitContestProblemTestCaseByTestCase(
        ModelContestSubmission modelContestSubmission,
        String userName
    ) throws Exception {
        //log.info("submitContestProblem");
        //log.info("modelContestSubmission {}", modelContestSubmission);
        ProblemEntity problemEntity = problemRepo.findByProblemId(modelContestSubmission.getProblemId());
        ContestEntity contest = contestRepo.findContestByContestId(modelContestSubmission.getContestId());

        //UserRegistrationContestEntity userRegistrationContest = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(modelContestSubmission.getContestId(), userName, Constants.RegistrationType.SUCCESSFUL.getValue());
        UserRegistrationContestEntity userRegistrationContest = null;
        List<UserRegistrationContestEntity> userRegistrationContests = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
            modelContestSubmission.getContestId(),
            userName,
            Constants.RegistrationType.SUCCESSFUL.getValue());
        if (userRegistrationContests != null && userRegistrationContests.size() > 0) {
            userRegistrationContest = userRegistrationContests.get(0);
        }

        //log.info("userRegistrationContest {}", userRegistrationContest);
        if (userRegistrationContest == null) {
            throw new MiniLeetCodeException("User not register contest");
        }
        //List<TestCaseEntity> testCaseEntityList = testCaseRepo.findAllByProblemId(modelContestSubmission.getProblemId());
        List<TestCaseEntity> testCaseEntityList;
        boolean evalPrivatePublic = contest != null &&
                                    contest.getEvaluateBothPublicPrivateTestcase() != null
                                    && contest
                                        .getEvaluateBothPublicPrivateTestcase()
                                        .equals(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES);

        if (evalPrivatePublic) {
            testCaseEntityList = testCaseRepo.findAllByProblemId(modelContestSubmission.getProblemId());
        } else {
            testCaseEntityList = testCaseRepo
                .findAllByProblemIdAndIsPublic(modelContestSubmission.getProblemId(), "N");
        }

        List<TestCaseEntity> lstFiltered = new ArrayList();
        for (TestCaseEntity tc : testCaseEntityList) {
            if (tc.getStatusId() != null && tc.getStatusId().equals(TestCaseEntity.STATUS_DISABLED)) {
                continue;
            }
            lstFiltered.add(tc);
        }
        testCaseEntityList = lstFiltered;

        String tempName = tempDir.createRandomScriptFileName(userName +
                                                             "-" +
                                                             modelContestSubmission.getContestId() +
                                                             "-" +
                                                             modelContestSubmission.getProblemId() +
                                                             "-" +
                                                             Math.random());

        int runtime = 0;
        int score = 0;
        int nbTestCasePass = 0;
        String totalStatus = "";
        List<String> statusList = new ArrayList<String>();
        List<ContestSubmissionTestCaseEntity> LCSTE = new ArrayList();
        String message = "";
        boolean compileError = false;
        for (TestCaseEntity testCaseEntity : testCaseEntityList) {
            List<TestCaseEntity> L = new ArrayList();
            L.add(testCaseEntity);

            String response = submission(
                modelContestSubmission.getSource(),
                modelContestSubmission.getLanguage(),
                tempName,
                L,
                "language not found",
                problemEntity.getTimeLimit(),
                problemEntity.getMemoryLimit());

            List<String> testCaseAns = L.stream().map(TestCaseEntity::getCorrectAnswer).collect(Collectors.toList());
            List<Integer> points = L.stream().map(TestCaseEntity::getTestCasePoint).collect(Collectors.toList());
            ProblemSubmission problemSubmission = StringHandler.handleContestResponse(response, testCaseAns, points);

            //log.info("submitContestProblemTestCaseByTestCase, run tesecase " + (i+1) + " message = " + problemSubmission.getMessage());
            // check if there is error compile
            if (problemSubmission.getMessage() != null && !problemSubmission.getMessage().contains("successful")) {
                message = problemSubmission.getMessage();
                compileError = true;
                break;
            }

            runtime = runtime + problemSubmission.getRuntime().intValue();
            score = score + problemSubmission.getScore();
            nbTestCasePass += problemSubmission.getNbTestCasePass();
            //status = status + "#" + (i+1) + ": " + problemSubmission.getStatus() + "; ";
            statusList.add(problemSubmission.getStatus());
            List<String> output = problemSubmission.getParticipantAns();
            String participantAns = "";
            if (output != null && output.size() > 0) {
                participantAns = output.get(0);
            }

            ContestSubmissionTestCaseEntity cste = ContestSubmissionTestCaseEntity.builder()
                                                                                  .contestId(modelContestSubmission.getContestId())
                                                                                  .problemId(modelContestSubmission.getProblemId())
                                                                                  .testCaseId(testCaseEntity
                                                                                                  .getTestCaseId())
                                                                                  .submittedByUserLoginId(userName)
                                                                                  .point(problemSubmission.getScore())
                                                                                  .status(problemSubmission.getStatus())
                                                                                  .participantSolutionOtput(
                                                                                      participantAns)
                                                                                  .runtime(problemSubmission.getRuntime())
                                                                                  .createdStamp(new Date())
                                                                                  .build();
            cste = contestSubmissionTestCaseEntityRepo.save(cste);
            LCSTE.add(cste);
        }

        totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_ACCEPTED;
        for (String s : statusList) {
            if (s.equals(ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR)) {
                totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR;
                break;
            }
        }

        if (compileError) {
            totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR;
        } else if (nbTestCasePass == 0) {
            totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_FAILED;
        } else if (nbTestCasePass > 0 && nbTestCasePass < testCaseEntityList.size()) {
            totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_PARTIAL;
        }

        ContestSubmissionEntity c = ContestSubmissionEntity.builder()
                                                           .contestId(modelContestSubmission.getContestId())
                                                           .status(totalStatus)
                                                           .message(message)
                                                           .point(score)
                                                           .problemId(modelContestSubmission.getProblemId())
                                                           .userId(userName)
                                                           .testCasePass(nbTestCasePass +
                                                                         "/" +
                                                                         testCaseEntityList.size())
                                                           .sourceCode(modelContestSubmission.getSource())
                                                           .sourceCodeLanguage(modelContestSubmission.getLanguage())
                                                           .runtime((long) runtime)
                                                           .createdAt(new Date())
                                                           .build();
        c = contestSubmissionRepo.save(c);

        for (ContestSubmissionTestCaseEntity e : LCSTE) {
            e.setContestSubmissionId(c.getContestSubmissionId());
            e = contestSubmissionTestCaseEntityRepo.save(e);
        }

        //log.info("c {}", c.getRuntime());

        return ModelContestSubmissionResponse.builder()
                                             .status(totalStatus)
                                             .message(message)
                                             .testCasePass(c.getTestCasePass())
                                             .runtime(runtime)
                                             .memoryUsage(c.getMemoryUsage())
                                             .problemName(problemEntity.getProblemName())
                                             .contestSubmissionID(c.getContestSubmissionId())
                                             .submittedAt(c.getCreatedAt())
                                             .score(score)
                                             .numberTestCasePassed(nbTestCasePass)
                                             .totalNumberTestCase(testCaseEntityList.size())
                                             .build();
    }

    @Transactional
    @Override
    public ModelContestSubmissionResponse submitContestProblemTestCaseByTestCaseWithFile(
        ModelContestSubmission modelContestSubmission,
        String userId, String submittedByUserId
    ) throws Exception {
        Date submitTime = new Date();
        ContestSubmissionEntity submission = ContestSubmissionEntity.builder()
                                                                    .contestId(modelContestSubmission.getContestId())
                                                                    .problemId(modelContestSubmission.getProblemId())
                                                                    .sourceCode(modelContestSubmission.getSource())
                                                                    .sourceCodeLanguage(modelContestSubmission.getLanguage())
                                                                    .status(ContestSubmissionEntity.SUBMISSION_STATUS_EVALUATION_IN_PROGRESS)
                                                                    .point(0)
                                                                    .problemId(modelContestSubmission.getProblemId())
                                                                    .userId(userId)
                                                                    .submittedByUserId(submittedByUserId)
                                                                    .runtime(0L)
                                                                    .createdAt(submitTime)
                                                                    .build();
        //log.info("submitContestProblemTestCaseByTestCaseWithFile, save submission to DB");
        submission = contestSubmissionRepo.saveAndFlush(submission);

        rabbitTemplate.convertAndSend(
            EXCHANGE,
            JUDGE_PROBLEM,
            submission.getContestSubmissionId()
        );

        return ModelContestSubmissionResponse.builder()
                                             .status("IN_PROGRESS")
                                             .message("Submission is being evaluated")
                                             .build();
    }

    @Override
    public void submitContestProblemTestCaseByTestCaseWithFileProcessor(
        UUID submissionId
    ) throws Exception {

        ContestSubmissionEntity submission = contestSubmissionRepo.findContestSubmissionEntityByContestSubmissionId(
            submissionId);
        ProblemEntity problem = problemService.findProblemWithCache(submission.getProblemId());
        ContestEntity contest = contestService.findContestWithCache(submission.getContestId());

        String userId = submission.getUserId();

        List<TestCaseEntity> testCaseEntityList = null;
        boolean evaluatePrivatePublic = contest != null &&
                                        contest.getEvaluateBothPublicPrivateTestcase() != null &&
                                        contest.getEvaluateBothPublicPrivateTestcase()
                                               .equals(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES);

        testCaseEntityList = testCaseService.findListTestCaseWithCache(
            submission.getProblemId(),
            evaluatePrivatePublic);

        List<TestCaseEntity> listTestCaseAvailable = new ArrayList<>();
        for (TestCaseEntity tc : testCaseEntityList) {
            if (tc.getStatusId() != null && tc.getStatusId().equals(TestCaseEntity.STATUS_DISABLED)) {
                continue;
            }
            listTestCaseAvailable.add(tc);
        }
        testCaseEntityList = listTestCaseAvailable;

        String tempName = tempDir.createRandomScriptFileName(userId + "-" +
                                                             submission.getContestId() + "-" +
                                                             submission.getProblemId() + "-" +
                                                             Math.random());

        List<String> listSubmissionResponse = new ArrayList<>();
        for (TestCaseEntity testCaseEntity : testCaseEntityList) {
            List<TestCaseEntity> L = new ArrayList();
            L.add(testCaseEntity);

            String response = submission(
                submission.getSourceCode(),
                submission.getSourceCodeLanguage(),
                tempName,
                L,
                "language not found",
                problem.getTimeLimit(),
                problem.getMemoryLimit());

            listSubmissionResponse.add(response);
        }

        tempDir.removeDir(tempName);
        submissionResponseHandler.processSubmissionResponse(
            testCaseEntityList,
            listSubmissionResponse,
            submission,
            problem.getScoreEvaluationType());
    }

    @Override
    public void evaluateCustomProblemSubmission(UUID submissionId) throws Exception {

        ContestSubmissionEntity submission = contestSubmissionRepo.findContestSubmissionEntityByContestSubmissionId(
            submissionId);
        ProblemEntity problemEntity = problemService.findProblemWithCache(submission.getProblemId());
        ContestEntity contest = contestService.findContestWithCache(submission.getContestId());
        List<ContestSubmissionTestCaseEntity> submissionTestCases = contestSubmissionTestCaseEntityRepo.findAllByContestSubmissionId(
            submissionId);

        String userId = submission.getUserId();

        List<TestCaseEntity> testCaseEntityList;
        boolean evaluatePrivatePublic = contest != null &&
                                        contest.getEvaluateBothPublicPrivateTestcase() != null &&
                                        contest.getEvaluateBothPublicPrivateTestcase()
                                               .equals(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES);

        testCaseEntityList = testCaseService.findListTestCaseWithCache(
            submission.getProblemId(),
            evaluatePrivatePublic);

        List<TestCaseEntity> listTestCaseAvailable = new ArrayList<>();
        for (TestCaseEntity tc : testCaseEntityList) {
            if (tc.getStatusId() != null && tc.getStatusId().equals(TestCaseEntity.STATUS_DISABLED)) {
                continue;
            }
            listTestCaseAvailable.add(tc);
        }
        testCaseEntityList = listTestCaseAvailable;

        String tempName = tempDir.createRandomScriptFileName(userId + "-" +
                                                             submission.getContestId() + "-" +
                                                             submission.getProblemId() + "-" +
                                                             "custom-" +
                                                             Math.random());

        Map<UUID, String> submissionResponses = new HashMap<>();
        for (TestCaseEntity testCaseEntity : testCaseEntityList) {
            ContestSubmissionTestCaseEntity submissionTestCase = submissionTestCases
                .stream()
                .filter(tc -> tc.getTestCaseId().equals(testCaseEntity.getTestCaseId()))
                .findFirst().get();

            String response = submissionSolutionOutput(
                problemEntity.getSolutionCheckerSourceCode(),
                problemEntity.getSolutionCheckerSourceLanguage(),
                submissionTestCase.getParticipantSolutionOtput(),
                tempName,
                testCaseEntity,
                "language not found",
                problemEntity.getTimeLimit(),
                problemEntity.getMemoryLimit());

            submissionResponses.put(submissionTestCase.getContestSubmissionTestcaseId(), response);
        }

        tempDir.removeDir(tempName);
        submissionResponseHandler.processCustomSubmissionResponse(submission, submissionResponses);
    }


    @Override
    public ModelContestSubmissionResponse submitContestProblemStoreOnlyNotExecute(
        ModelContestSubmission modelContestSubmission,
        String userName,
        String submittedByUserId
    ) {
        String problemId = modelContestSubmission.getProblemId();
        String contestId = modelContestSubmission.getContestId();
        ContestEntity contest = contestRepo.findContestByContestId(modelContestSubmission.getContestId());

        ContestSubmissionEntity c = ContestSubmissionEntity.builder()
                                                           .contestId(modelContestSubmission.getContestId())
                                                           .status(ContestSubmissionEntity.SUBMISSION_STATUS_NOT_AVAILABLE)
                                                           .point(0)
                                                           .problemId(modelContestSubmission.getProblemId())
                                                           .userId(userName)
                                                           .submittedByUserId(submittedByUserId)
                                                           .testCasePass("")
                                                           .sourceCode(modelContestSubmission.getSource())
                                                           .sourceCodeLanguage(modelContestSubmission.getLanguage())
                                                           .runtime(0L)
                                                           .createdAt(new Date())
                                                           .build();
        c = contestSubmissionRepo.save(c);

        // generated test-case with empty result
        List<TestCaseEntity> testCaseEntityList = null;

        if (contest.getEvaluateBothPublicPrivateTestcase() != null &&
            contest
                .getEvaluateBothPublicPrivateTestcase()
                .equals(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES)) {
            testCaseEntityList = testCaseRepo.findAllByProblemId(problemId);
        } else {
            testCaseEntityList = testCaseRepo.findAllByProblemIdAndIsPublic(problemId, "N");
        }
        if (testCaseEntityList == null) {
            testCaseEntityList = new ArrayList<>();
        }

        //   log.info("submitContestProblemStoreOnlyNotExecute, testCaseList.sz = " + testCaseEntityList.size());

        int runtime = 0;
        int score = 0;
        int nbTestCasePass = 0;
        String totalStatus = "";
        List<String> statusList = new ArrayList<String>();
        List<ContestSubmissionTestCaseEntity> LCSTE = new ArrayList();
        String message = "";
        boolean compileError = false;
        for (int i = 0; i < testCaseEntityList.size(); i++) {
            TestCaseEntity testCase = testCaseEntityList.get(i);

            ContestSubmissionTestCaseEntity cste = null;
            cste = ContestSubmissionTestCaseEntity.builder()
                                                  .contestId(contestId)
                                                  .problemId(problemId)
                                                  .contestSubmissionId(c.getContestSubmissionId())
                                                  .testCaseId(testCaseEntityList.get(i).getTestCaseId())
                                                  .submittedByUserLoginId(userName)
                                                  .point(0)
                                                  .status("N/A")
                                                  .participantSolutionOtput("")
                                                  .runtime(null)
                                                  .createdStamp(new Date())
                                                  .build();
            cste = contestSubmissionTestCaseEntityRepo.save(cste);
            //  log.info("submitContestProblemStoreOnlyNotExecute, save submission_testcase " + cste.getTestCaseId() + " submission " + cste.getContestSubmissionId());
            LCSTE.add(cste);

        }


        //log.info("c {}", c.getRuntime());
        return ModelContestSubmissionResponse.builder()
                                             .status("STORED")
                                             .testCasePass(c.getTestCasePass())
                                             .runtime(new Long(0))
                                             .memoryUsage(c.getMemoryUsage())
                                             .problemName("")
                                             .contestSubmissionID(c.getContestSubmissionId())
                                             .submittedAt(c.getCreatedAt())
                                             .score(0)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

    @Transactional
    @Override
    public ModelContestSubmissionResponse submitSolutionOutput(
        String solutionOutput,
        String contestId,
        String problemId,
        UUID testCaseId,
        String userName
    ) throws Exception {
        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
        //UserRegistrationContestEntity userRegistrationContest = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
        //    contestId, userName, Constants.RegistrationType.SUCCESSFUL.getValue());

        UserRegistrationContestEntity userRegistrationContest = null;
        List<UserRegistrationContestEntity> userRegistrationContests = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
            contestId, userName, Constants.RegistrationType.SUCCESSFUL.getValue());
        if (userRegistrationContests != null && userRegistrationContests.size() > 0) {
            userRegistrationContest = userRegistrationContests.get(0);
        }

        //  log.info("submitSolutionOutput, userRegistrationContest {}", userRegistrationContest);
        if (userRegistrationContest == null) {
            throw new MiniLeetCodeException("User not register contest");
        }
        TestCaseEntity testCase = testCaseRepo.findTestCaseByTestCaseId(testCaseId);
        String tempName = tempDir.createRandomScriptFileName(userName + "-" + contestId + "-" + problemId);
        String response = submissionSolutionOutput(
            problemEntity.getSolutionCheckerSourceCode(),
            problemEntity.getSolutionCheckerSourceLanguage(),
            solutionOutput,
            tempName,
            testCase,
            "language not found",
            1000000,
            problemEntity.getMemoryLimit());


        //  log.info("submitSolutionOutput, response = " + response);

        ProblemSubmission problemSubmission = StringHandler.handleContestResponseSubmitSolutionOutputOneTestCase(
            response,
            testCase.getTestCasePoint());

        String participantAns = "";
        if (problemSubmission.getParticipantAns() != null && problemSubmission.getParticipantAns().size() > 0) {
            participantAns = problemSubmission.getParticipantAns().get(0);
        }
        ContestSubmissionTestCaseEntity cste = ContestSubmissionTestCaseEntity.builder()
                                                                              .contestId(contestId)
                                                                              .problemId(problemId)
                                                                              .testCaseId(testCase.getTestCaseId())
                                                                              .submittedByUserLoginId(userName)
                                                                              .point(problemSubmission.getScore())
                                                                              .status(problemSubmission.getStatus())
                                                                              .participantSolutionOtput(participantAns)
                                                                              .runtime(problemSubmission.getRuntime())
                                                                              .createdStamp(new Date())
                                                                              .build();
        cste = contestSubmissionTestCaseEntityRepo.save(cste);

        return ModelContestSubmissionResponse.builder()
                                             .status(problemSubmission.getStatus())
                                             .testCasePass("1/1")
                                             .runtime(problemSubmission.getRuntime())
                                             .memoryUsage((float) 0.0)
                                             .problemName(problemEntity.getProblemName())
                                             .contestSubmissionID(null)
                                             .submittedAt(new Date())
                                             .score(problemSubmission.getScore())
                                             .build();

    }

    @Override
    public ModelContestSubmissionResponse submitSolutionOutputOfATestCase(
        String userId,
        String solutionOutput,
        ModelSubmitSolutionOutputOfATestCase m
    ) {
        ModelContestSubmissionResponse res = new ModelContestSubmissionResponse();
        ContestSubmissionEntity sub = contestSubmissionRepo.findContestSubmissionEntityByContestSubmissionId(m.getSubmissionId());
        if (sub == null) {
            return res;
        }
        String contestId = sub.getContestId();
        String problemId = sub.getProblemId();
        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
        //UserRegistrationContestEntity userRegistrationContest = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
        //    contestId, userName, Constants.RegistrationType.SUCCESSFUL.getValue());

        UserRegistrationContestEntity userRegistrationContest = null;
        List<UserRegistrationContestEntity> userRegistrationContests = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
            contestId, userId, Constants.RegistrationType.SUCCESSFUL.getValue());
        if (userRegistrationContests != null && userRegistrationContests.size() > 0) {
            userRegistrationContest = userRegistrationContests.get(0);
        }

        //log.info("submitSolutionOutput, userRegistrationContest {}", userRegistrationContest);
        if (userRegistrationContest == null) {
            return res;
        }
        TestCaseEntity testCase = testCaseRepo.findTestCaseByTestCaseId(m.getTestCaseId());
        String tempName = tempDir.createRandomScriptFileName(userId + "-" + contestId + "-" + problemId);
        try {
            String response = submissionSolutionOutput(
                problemEntity.getSolutionCheckerSourceCode(),
                problemEntity.getSolutionCheckerSourceLanguage(),
                solutionOutput,
                tempName,
                testCase,
                "language not found",
                1000000,
                problemEntity.getMemoryLimit());

            //   log.info("submitSolutionOutput, response = " + response);
            ProblemSubmission problemSubmission = StringHandler.handleContestResponseSubmitSolutionOutputOneTestCase(
                response,
                testCase.getTestCasePoint());

            String participantAns = "";
            if (problemSubmission.getParticipantAns() != null && problemSubmission.getParticipantAns().size() > 0) {
                participantAns = problemSubmission.getParticipantAns().get(0);
            }
            ContestSubmissionTestCaseEntity cste = null;
            List<ContestSubmissionTestCaseEntity> l_cste = contestSubmissionTestCaseEntityRepo
                .findAllByContestSubmissionIdAndTestCaseId(sub.getContestSubmissionId(), m.getTestCaseId());
            int subPoint = sub.getPoint();
            if (l_cste != null && l_cste.size() > 0) {
                cste = l_cste.get(0);
                subPoint = subPoint - cste.getPoint();// reduce point of submission by old point of test-case
                cste.setPoint(problemSubmission.getScore());
                cste.setStatus(problemSubmission.getStatus());
                cste.setParticipantSolutionOtput(solutionOutput);
                cste.setRuntime(problemSubmission.getRuntime());
                cste.setCreatedStamp(new Date());
            } else {
                cste = ContestSubmissionTestCaseEntity.builder()
                                                      .contestId(contestId)
                                                      .problemId(problemId)
                                                      .contestSubmissionId(sub.getContestSubmissionId())
                                                      .testCaseId(testCase.getTestCaseId())
                                                      .submittedByUserLoginId(userId)
                                                      .point(problemSubmission.getScore())
                                                      .status(problemSubmission.getStatus())
                                                      .participantSolutionOtput(participantAns)
                                                      .runtime(problemSubmission.getRuntime())
                                                      .createdStamp(new Date())
                                                      .build();
            }

            cste = contestSubmissionTestCaseEntityRepo.save(cste);

            subPoint = subPoint + cste.getPoint(); // update Point;
            sub.setPoint(subPoint);
            sub.setStatus(ContestSubmissionEntity.SUBMISSION_STATUS_PARTIAL);
            sub = contestSubmissionRepo.save(sub);

            return ModelContestSubmissionResponse.builder()
                                                 .contestId(contestId)
                                                 .problemId(problemId)
                                                 .contestSubmissionID(sub.getContestSubmissionId())
                                                 .selectedTestCaseId(m.getTestCaseId())
                                                 .status(problemSubmission.getStatus())
                                                 .testCasePass("1/1")
                                                 .runtime(problemSubmission.getRuntime())
                                                 .memoryUsage((float) 0.0)
                                                 .problemName(problemEntity.getProblemName())
                                                 //.contestSubmissionID(null)
                                                 .submittedAt(new Date())
                                                 .score(problemSubmission.getScore())
                                                 .build();

        } catch (Exception e) {
            return res;
        }

    }

    @Override
    public ModelStudentRegisterContestResponse studentRegisterContest(
        String contestId,
        String userId
    ) throws MiniLeetCodeException {
        ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
//
//        UserLogin userLogin = userLoginRepo.findByUserLoginId(userId);
        //UserRegistrationContestEntity existed = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserId(contestId, userId);
        UserRegistrationContestEntity existed = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndRoleId(
            contestId,
            userId,
            UserRegistrationContestEntity.ROLE_PARTICIPANT);
        //   log.info("existed {}", existed);
//        if(existed != null && Constants.RegisterCourseStatus.SUCCESSES.getValue().equals(existed.getStatus())){
//            throw new MiniLeetCodeException("You are already register course successful");
//        }
        if (existed == null) {
            UserRegistrationContestEntity userRegistrationContestEntity = UserRegistrationContestEntity
                .builder()
                .contestId(contestId)
                .userId(userId)
                .status(Constants.RegistrationType.PENDING.getValue())
                .roleId(UserRegistrationContestEntity.ROLE_PARTICIPANT)
                .createdStamp(new Date())
                .permissionId(UserRegistrationContestEntity.PERMISSION_SUBMIT)
                .build();
            userRegistrationContestRepo.save(userRegistrationContestEntity);

        } else {
            if (Constants.RegistrationType.SUCCESSFUL.getValue().equals(existed.getStatus())) {
                throw new MiniLeetCodeException("You are already register course successful");
            } else {
                existed.setStatus(Constants.RegistrationType.PENDING.getValue());
                userRegistrationContestRepo.save(existed);
            }
        }
        notificationsService.create(
            userId,
            contestEntity.getUserId(),
            userId + " register contest " + contestId,
            "/programming-contest/contest-manager/" + contestId + "#pending");

        return ModelStudentRegisterContestResponse.builder()
                                                  .status(Constants.RegistrationType.PENDING.getValue())
                                                  .message("You have send request to register contest " +
                                                           contestId +
                                                           ", please wait to accept")
                                                  .build();
    }

    @Override
    public int teacherManageStudentRegisterContest(
        String teacherId,
        ModelTeacherManageStudentRegisterContest modelTeacherManageStudentRegisterContest
    ) throws MiniLeetCodeException {
        ContestEntity contestEntity = contestRepo.findContestByContestId(modelTeacherManageStudentRegisterContest.getContestId());
//        UserLogin student = userLoginRepo.findByUserLoginId(modelTeacherManageStudentRegisterContest.getUserId());
        //     log.info("teacherid {}", teacherId);
        //   log.info("created contest {}", contestEntity.getUserId());
        int cnt = 0;
        if (contestEntity.getUserId() == null || !contestEntity.getUserId().equals(teacherId)) {
            throw new MiniLeetCodeException(teacherId +
                                            " does not have privilege to manage contest " +
                                            modelTeacherManageStudentRegisterContest.getContestId());
        }
        UserRegistrationContestEntity userRegistrationContestEntity = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserId(
            modelTeacherManageStudentRegisterContest.getContestId(),
            modelTeacherManageStudentRegisterContest.getUserId());

        if (Constants.RegisterCourseStatus.SUCCESSES
            .getValue()
            .equals(modelTeacherManageStudentRegisterContest.getStatus())) {
            //  log.info("approve");
            if (!userRegistrationContestEntity.getStatus().equals(Constants.RegistrationType.SUCCESSFUL.getValue())) {
                userRegistrationContestEntity.setStatus(Constants.RegistrationType.SUCCESSFUL.getValue());
                userRegistrationContestRepo.save(userRegistrationContestEntity);
                notificationsService.create(
                    teacherId,
                    modelTeacherManageStudentRegisterContest.getUserId(),
                    "Your register contest " +
                    modelTeacherManageStudentRegisterContest.getContestId() +
                    " is approved ",
                    null);
                cnt += 1;
            }
        } else if (Constants.RegisterCourseStatus.FAILED
            .getValue()
            .equals(modelTeacherManageStudentRegisterContest.getStatus())) {

            //  log.info("reject");
            if (!userRegistrationContestEntity.getStatus().equals(Constants.RegistrationType.FAILED.getValue())) {
                userRegistrationContestEntity.setStatus(Constants.RegistrationType.FAILED.getValue());
                userRegistrationContestRepo.save(userRegistrationContestEntity);
                notificationsService.create(
                    teacherId,
                    modelTeacherManageStudentRegisterContest.getUserId(),
                    "Your register contest " +
                    modelTeacherManageStudentRegisterContest.getContestId() +
                    " is rejected ",
                    null);
                cnt += 1;
            }
        } else {
            throw new MiniLeetCodeException("Status not found");
        }
        return cnt;
    }

    @Override
    public boolean approveRegisteredUser2Contest(
        String teacherId,
        ModelApproveRegisterUser2ContestInput input
    ) {

        UserRegistrationContestEntity u = userRegistrationContestRepo.findById(input.getId()).orElse(null);
        if (u != null) {
            u.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
            u.setLastUpdated(new Date());
            u.setUpdatedByUserLogin_id(teacherId);
            u = userRegistrationContestRepo.save(u);
            return true;
        }

        return false;
    }

    @Override
    public void calculateContestResult(String contestId) {
        List<Object[]> list = contestSubmissionRepo.calculatorContest(contestId);
        //  log.info("size {}", list.size());
        //  log.info("list {}", list);
        List<UserSubmissionContestResultNativeEntity> list1 = list.stream()
                                                                  .map(objects -> convertObjectsToUserSubmissionContestResultNativeEntity(
                                                                      objects,
                                                                      contestId))
                                                                  .collect(Collectors.toList());
//        log.info("list1 {}", list1);


        userSubmissionContestResultNativeRepo.saveAll(list1);
    }

    @Override
    public ModelGetContestPageResponse getContestPagingByUserCreatedContest(String userName, Pageable pageable) {
//        UserLogin userCreateContest = userLoginRepo.findByUserLoginId(userName);
        Page<ContestEntity> contestPage = contestPagingAndSortingRepo.findAllByUserId(pageable, userName);
        return getModelGetContestPageResponse(contestPage);
    }

    @Override
    public ModelGetContestPageResponse getContestPagingByUserManagerContest(String userName, Pageable pageable) {
        List<UserRegistrationContestEntity> L = userRegistrationContestRepo.findAllByUserIdAndRoleId(
            userName,
            UserRegistrationContestEntity.ROLE_MANAGER);
        //Page<ContestEntity> contestPage =  contestPagingAndSortingRepo.findAllByUserIdAndRoleId(pageable, userName, UserRegistrationContestEntity.ROLE_MANAGER);
        HashSet<String> contestIds = new HashSet();
        for (UserRegistrationContestEntity e : L) {
            contestIds.add(e.getContestId());
        }
        List<ContestEntity> contestEntities = contestPagingAndSortingRepo.findAllByContestIdIn(contestIds);
        return getModelGetContestPageResponse(contestEntities);
    }

    @Override
    public ModelGetContestPageResponse getAllContestsPagingByAdmin(String userName, Pageable pageable) {
        //List<ContestEntity> contestEntities = contestPagingAndSortingRepo.findAll();
        Page<ContestEntity> contestEntities = contestPagingAndSortingRepo.findAll(pageable);
        long count = contestPagingAndSortingRepo.count();
        return getModelGetContestPageResponse(contestEntities, count);
    }

    @Override
    public List<ModelGetContestResponse> getContestByUserRole(String userName) {
        List<UserRegistrationContestEntity> L = userRegistrationContestRepo.findAllByUserId(userName);

        List<ModelGetContestResponse> res = new ArrayList();
        for (UserRegistrationContestEntity e : L) {
            ContestEntity contest = contestRepo.findContestByContestId(e.getContestId());
            ModelGetContestResponse modelGetContestResponse = ModelGetContestResponse.builder()
                                                                                     .contestId(contest.getContestId())
                                                                                     .contestName(contest.getContestName())
                                                                                     .contestTime(contest.getContestSolvingTime())
                                                                                     .countDown(contest.getCountDown())
                                                                                     .startAt(contest.getStartedAt())
                                                                                     .isPublic(contest.getIsPublic())
                                                                                     .statusId(contest.getStatusId())
                                                                                     .userId(contest.getUserId())
                                                                                     .createdAt(contest.getCreatedAt())
                                                                                     .roleId(e.getRoleId())
                                                                                     .registrationStatusId(e.getStatus())
                                                                                     .build();
            res.add(modelGetContestResponse);
        }
        return res;
    }

    @Override
    public ListModelUserRegisteredContestInfo getListUserRegisterContestSuccessfulPaging(
        Pageable pageable,
        String contestId
    ) {
//        ContestEntity contest = contestRepo.findContestByContestId(contestId);
        Page<ModelUserRegisteredClassInfo> list = userRegistrationContestPagingAndSortingRepo.getAllUserRegisteredByContestIdAndStatusInfo(
            pageable,
            contestId,
            Constants.RegistrationType.SUCCESSFUL.getValue());
        return ListModelUserRegisteredContestInfo.builder()
                                                 .contents(list)
                                                 .build();
    }

    @Override
    public List<ModelMemberOfContestResponse> getListMemberOfContest(String contestId) {
        List<UserRegistrationContestEntity> lst = userRegistrationContestRepo.findAllByContestIdAndStatus(
            contestId,
            UserRegistrationContestEntity.STATUS_SUCCESSFUL);
        List<ModelMemberOfContestResponse> res = new ArrayList();
        for (UserRegistrationContestEntity u : lst) {
            PersonModel person = userService.findPersonByUserLoginId(u.getUserId());
            ModelMemberOfContestResponse m = new ModelMemberOfContestResponse();
            m.setId(u.getId());
            m.setContestId(contestId);
            m.setUserId(u.getUserId());
            m.setRoleId(u.getRoleId());
            m.setFullName(person.getFullName());
            m.setLastUpdatedDate(u.getLastUpdated());
            m.setUpdatedByUserId(u.getUpdatedByUserLogin_id());
            m.setPermissionId(u.getPermissionId());
            res.add(m);
        }
        return res;
    }

    @Override
    public ListModelUserRegisteredContestInfo getListUserRegisterContestPendingPaging(
        Pageable pageable,
        String contestId
    ) {
//        ContestEntity contest = contestRepo.findContestByContestId(contestId);
        Page<ModelUserRegisteredClassInfo> list = userRegistrationContestPagingAndSortingRepo.getAllUserRegisteredByContestIdAndStatusInfo(
            pageable,
            contestId,
            Constants.RegistrationType.PENDING.getValue());
        return ListModelUserRegisteredContestInfo.builder()
                                                 .contents(list)
                                                 .build();
    }

    @Override
    public List<ModelMemberOfContestResponse> getPendingRegisteredUsersOfContest(String contestId) {
        List<UserRegistrationContestEntity> lst = userRegistrationContestRepo.findAllByContestIdAndStatus(
            contestId,
            UserRegistrationContestEntity.STATUS_PENDING);
        List<ModelMemberOfContestResponse> res = new ArrayList();
        for (UserRegistrationContestEntity u : lst) {
            PersonModel person = userService.findPersonByUserLoginId(u.getUserId());
            ModelMemberOfContestResponse m = new ModelMemberOfContestResponse();
            m.setId(u.getId());
            m.setContestId(contestId);
            m.setUserId(u.getUserId());
            m.setRoleId(u.getRoleId());
            m.setFullName(person.getFullName());
            res.add(m);
        }
        return res;
    }

    @Override
    public ListModelUserRegisteredContestInfo searchUser(Pageable pageable, String contestId, String keyword) {
//        ContestEntity contest = contestRepo.findContestByContestId(contestId);
        Page<ModelUserRegisteredClassInfo> list = userRegistrationContestPagingAndSortingRepo.searchUser(
            pageable,
            contestId,
            keyword);
        return ListModelUserRegisteredContestInfo.builder()
                                                 .contents(list)
                                                 .build();
    }

    @Override
    public ListPersonModel searchUserBaseKeyword(Pageable pageable, String keyword) {
//        ContestEntity contest = contestRepo.findContestByContestId(contestId);
        Page<PersonModel> list = userLoginRepo.searchUser(pageable, keyword);
        // log.info("searchUserBaseKeyword, list.sz = ");
        return ListPersonModel.builder()
                              .contents(list)
                              .build();
    }

    @Override
    public ModelGetContestPageResponse getRegisteredContestByUser(Pageable pageable, String userName) {
//        Page<ContestEntity> list = userRegistrationContestPagingAndSortingRepo.getContestByUserAndStatusSuccessful(pageable, userName);
        Date currentDate = new Date();
        //log.info("getRegisteredContestByUser, currentDateTime = " + currentDate);
        //Page<ContestEntity> list = userRegistrationContestPagingAndSortingRepo.getContestByUserAndStatusSuccessfulInSolvingTime(pageable, userName, currentDate);
        Page<ContestEntity> list = userRegistrationContestPagingAndSortingRepo.getContestByUser(pageable, userName);
        return getModelGetContestPageResponse(list);
    }

    @Override
    public ModelGetContestPageResponse getRegisteredContestsByUser(String userName) {
        List<UserRegistrationContestEntity> lst = userRegistrationContestRepo
            .findAllByUserIdAndRoleIdAndStatus(
                userName,
                UserRegistrationContestEntity.ROLE_PARTICIPANT,
                UserRegistrationContestEntity.STATUS_SUCCESSFUL);

        List<ModelGetContestResponse> lists = new ArrayList<>();
        if (lst != null) {
            lst.forEach(ur -> {
                ContestEntity contest = contestRepo.findContestByContestId(ur.getContestId());
                ModelGetContestResponse modelGetContestResponse = ModelGetContestResponse.builder()
                                                                                         .contestId(contest.getContestId())
                                                                                         .contestName(contest.getContestName())
                                                                                         .contestTime(contest.getContestSolvingTime())
                                                                                         .countDown(contest.getCountDown())
                                                                                         .startAt(contest.getStartedAt())
                                                                                         .isPublic(contest.getIsPublic())
                                                                                         .statusId(contest.getStatusId())
                                                                                         .userId(contest.getUserId())
                                                                                         .createdAt(contest.getCreatedAt())
                                                                                         .build();
                lists.add(modelGetContestResponse);
            });
        }

        return ModelGetContestPageResponse.builder()
                                          .contests(lists)
                                          .build();

    }

    @Override
    public ModelGetContestPageResponse getNotRegisteredContestByUser(Pageable pageable, String userName) {
//        UserLogin u = userLoginRepo.findByUserLoginId(userName);
        Page<ContestEntity> list = userRegistrationContestPagingAndSortingRepo.getNotRegisteredContestByUserLogin(
            pageable,
            userName);
        long count = userRegistrationContestPagingAndSortingRepo.getNumberOfNotRegisteredContestByUserLogin(userName);
        return getModelGetContestPageResponse(list, count);
    }

    @Override
    public Page<UserSubmissionContestResultNativeEntity> getRankingByContestId(Pageable pageable, String contestId) {
        return userSubmissionContestResultNativePagingRepo.findAllByContestId(pageable, contestId);
    }

    @Override
    public List<ContestSubmissionsByUser> getRankingByContestIdNew(
        Pageable pageable,
        String contestId,
        Constants.GetPointForRankingType getPointForRankingType
    ) {
        ListModelUserRegisteredContestInfo users = this.getListUserRegisterContestSuccessfulPaging(pageable, contestId);
        ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);

        List<ContestSubmissionsByUser> listContestSubmissionsByUser = new ArrayList<>();
        for (ModelUserRegisteredClassInfo user : users.getContents()) {

            List<ContestSubmission> submissionsByUser = contestSubmissionPagingAndSortingRepo
                .findAllByUserIdAndContestId(user.getUserName(), contestId)
                .stream()
                .map(contestSubmissionEntity -> ContestSubmission
                    .builder()
                    .contestSubmissionId(contestSubmissionEntity.getContestSubmissionId())
                    .contestId(contestSubmissionEntity.getContestId())
                    .createAt(contestSubmissionEntity.getCreatedAt() != null ? DateTimeUtils.dateToString(
                        contestSubmissionEntity.getCreatedAt(),
                        DateTimeUtils.DateTimeFormat.DATE_TIME_ISO_FORMAT) : null)
                    .sourceCodeLanguage(contestSubmissionEntity.getSourceCodeLanguage())
                    .point(contestSubmissionEntity.getPoint())
                    .problemId(contestSubmissionEntity.getProblemId())
                    .testCasePass(contestSubmissionEntity.getTestCasePass())
                    .status(contestSubmissionEntity.getStatus())
                    .userId(contestSubmissionEntity.getUserId())
                    .build()
                )
                .collect(Collectors.toList());

            ContestSubmissionsByUser contestSubmission = new ContestSubmissionsByUser();
            contestSubmission.setUserId(user.getUserName());

            HashMap<String, Integer> mapProblemToPoint = new HashMap<>();

            for (ProblemEntity problem : contestEntity.getProblems()) {
                mapProblemToPoint.put(problem.getProblemId(), 0);
            }

            switch (getPointForRankingType) {
                // get highest submission score on each problem
                case HIGHEST:
                    for (ContestSubmission submission : submissionsByUser) {
                        String problemId = submission.getProblemId();
                        if (mapProblemToPoint.containsKey(problemId)) {
                            if (submission.getPoint() > mapProblemToPoint.get(problemId)) {
                                mapProblemToPoint.put(problemId, submission.getPoint());
                            }
                        }
                    }
                    break;

                // get latest submission score on each problem
                case LATEST:
                    HashMap<String, ContestSubmission> mapProblemToLatestSubmission = new HashMap<>();

                    for (ContestSubmission submission : submissionsByUser) {
                        String problemId = submission.getProblemId();
                        if (mapProblemToLatestSubmission.containsKey(problemId)) {
                            Date tmpSubmissionTime = DateTimeUtils.convertDateTimeStr2Date(submission.getCreateAt());
                            Date currentSubmissionTime = DateTimeUtils.convertDateTimeStr2Date(
                                mapProblemToLatestSubmission.get(problemId).getCreateAt());
                            if (tmpSubmissionTime.compareTo(currentSubmissionTime) >= 0) {
                                mapProblemToLatestSubmission.put(problemId, submission);
                            }
                        } else {
                            mapProblemToLatestSubmission.put(problemId, submission);
                        }
                    }

//                    mapProblemToLatestSubmission.entrySet().stream().map(entry -> mapProblemToPoint.put(entry.getKey(), entry.getValue().getPoint()));

                    for (Map.Entry<String, ContestSubmission> entry : mapProblemToLatestSubmission.entrySet()) {
                        mapProblemToPoint.put(entry.getKey(), entry.getValue().getPoint());
                    }
                    break;
            }

            int totalPoint = 0;

            List<ContestSubmissionsByUserCustom> mapProblemsToPoints = new ArrayList<>();
            for (Map.Entry entry : mapProblemToPoint.entrySet()) {
                ContestSubmissionsByUserCustom tmp = new ContestSubmissionsByUserCustom();
                String problemId = entry.getKey().toString();
                String problemName = contestEntity.getProblems()
                                                  .stream().filter(p -> Objects.equals(p.getProblemId(), problemId))
                                                  .collect(Collectors.toList()).get(0)
                                                  .getProblemName();
                tmp.setProblemId(problemName);
                tmp.setPoint(Integer.valueOf(entry.getValue().toString()));
                mapProblemsToPoints.add(tmp);
                totalPoint += tmp.getPoint();
            }
            //PersonModel person = userService.findPersonByUserLoginId(user.getUserLoginId());
            String fullname = user.getLastName() + " " + user.getMiddleName() + " " + user.getFirstName();
            contestSubmission.setFullname(fullname);
            contestSubmission.setMapProblemsToPoints(mapProblemsToPoints);
            contestSubmission.setTotalPoint(totalPoint);
            listContestSubmissionsByUser.add(contestSubmission);
        }

        return listContestSubmissionsByUser;
    }

    @Override
    public Page<ProblemEntity> getPublicProblemPaging(Pageable pageable) {
        return problemPagingAndSortingRepo.findAllByPublicIs(pageable);
    }

    @Override
    public List<ModelGetTestCase> getTestCaseByProblem(String problemId) {
//        ProblemEntity problem = problemRepo.findByProblemId(problemId);
        List<TestCaseEntity> testCases = testCaseRepo.findAllByProblemId(problemId);
        return testCases.stream().map(this::convertToModelGetTestCase).collect(Collectors.toList());
    }

    @Override
    public ModelGetTestCaseDetail getTestCaseDetail(UUID testCaseId) throws MiniLeetCodeException {
        TestCaseEntity testCase = testCaseRepo.findTestCaseByTestCaseId(testCaseId);
        if (testCase == null) {
            throw new MiniLeetCodeException("testcase not found");
        }

        ProblemEntity problem = problemRepo.findByProblemId(testCase.getProblemId());

        return ModelGetTestCaseDetail.builder()
                                     .testCaseId(testCaseId)
                                     .correctAns(testCase.getCorrectAnswer())
                                     .testCase(testCase.getTestCase())
                                     .point(testCase.getTestCasePoint()).isPublic(testCase.getIsPublic())
                                     .problemSolution(problem.getSolution())
                                     .problemDescription(problem.getProblemDescription())
                                     .description(testCase.getDescription())
                                     .build();
    }

    @Override
    public ModelGetTestCaseDetail getTestCaseDetailShort(UUID testCaseId) throws MiniLeetCodeException {
        TestCaseEntity testCase = testCaseRepo.findTestCaseByTestCaseId(testCaseId);
        if (testCase == null) {
            throw new MiniLeetCodeException("testcase not found");
        }

        ProblemEntity problem = problemRepo.findByProblemId(testCase.getProblemId());

        return ModelGetTestCaseDetail.builder()
                                     .testCaseId(testCaseId)
                                     .correctAns(testCase.getCorrectAnswerShort(20))
                                     .testCase(testCase.getTestCaseShort(20))
                                     .point(testCase.getTestCasePoint()).isPublic(testCase.getIsPublic())
                                     .problemSolution(problem.getSolution())
                                     .problemDescription(problem.getProblemDescription())
                                     .description(testCase.getDescription())
                                     .build();
    }

    @Override
    public void editTestCase(UUID testCaseId, ModelSaveTestcase modelSaveTestcase) throws MiniLeetCodeException {
        TestCaseEntity testCase = testCaseRepo.findTestCaseByTestCaseId(testCaseId);
        if (testCase == null) {
            throw new MiniLeetCodeException("test case not found");
        }

        testCase.setTestCase(modelSaveTestcase.getInput());
        testCase.setCorrectAnswer(modelSaveTestcase.getResult());
        testCase.setTestCasePoint(modelSaveTestcase.getPoint());
        testCase.setIsPublic(modelSaveTestcase.getIsPublic());
        testCaseService.saveTestCaseWithCache(testCase);
    }

    @Override
    public int addUserToContest(ModelAddUserToContest modelAddUserToContest) {
        UserRegistrationContestEntity userRegistrationContest = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndRoleId(
                modelAddUserToContest.getContestId(),
                modelAddUserToContest.getUserId(),
                modelAddUserToContest.getRole());
        if (userRegistrationContest == null) {
            userRegistrationContestRepo.save(UserRegistrationContestEntity.builder()
                                                                          .contestId(modelAddUserToContest.getContestId())
                                                                          .userId(modelAddUserToContest.getUserId())
                                                                          .status(Constants.RegistrationType.SUCCESSFUL.getValue())
                                                                          .roleId(modelAddUserToContest.getRole())
                                                                          .build());
            return 1;
        } else {
            userRegistrationContest.setStatus(Constants.RegistrationType.SUCCESSFUL.getValue());
            userRegistrationContestRepo.save(userRegistrationContest);
            return 0;
        }

    }

    @Override
    public int addAllUsersToContest(ModelAddUserToContest model) {
        List<UserLogin> users = userService.getAllUserLogins();
        int cnt = 0;
        for (UserLogin u : users) {
            model.setUserId(u.getUserLoginId());
            cnt += addUserToContest(model);
        }
        return cnt;
    }

    @Override
    public void deleteUserContest(ModelAddUserToContest modelAddUserToContest) throws MiniLeetCodeException {
        UserRegistrationContestEntity userRegistrationContest = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserId(
            modelAddUserToContest.getContestId(),
            modelAddUserToContest.getUserId());
        if (userRegistrationContest == null) {
            throw new MiniLeetCodeException("user not register contest");
        }

        userRegistrationContest.setStatus(Constants.RegistrationType.FAILED.getValue());
        userRegistrationContestRepo.delete(userRegistrationContest);
    }

    @Override
    public Page<ContestSubmission> findContestSubmissionByUserLoginIdPaging(Pageable pageable, String userLoginId) {
        return contestSubmissionPagingAndSortingRepo.findAllByUserId(pageable, userLoginId)
                                                    .map(contestSubmissionEntity -> ContestSubmission
                                                        .builder()
                                                        .contestSubmissionId(contestSubmissionEntity.getContestSubmissionId())
                                                        .contestId(contestSubmissionEntity.getContestId())
                                                        .createAt(contestSubmissionEntity.getCreatedAt() != null
                                                                      ? DateTimeUtils.dateToString(
                                                            contestSubmissionEntity.getCreatedAt(),
                                                            DateTimeUtils.DateTimeFormat.DATE_TIME_ISO_FORMAT)
                                                                      : null)
                                                        .sourceCodeLanguage(contestSubmissionEntity.getSourceCodeLanguage())
                                                        .point(contestSubmissionEntity.getPoint())
                                                        .problemId(contestSubmissionEntity.getProblemId())
                                                        .testCasePass(contestSubmissionEntity.getTestCasePass())
                                                        .status(contestSubmissionEntity.getStatus())
                                                        .message(contestSubmissionEntity.getMessage())
                                                        .userId(contestSubmissionEntity.getUserId())
                                                        .build()
                                                    );
    }

    @Override
    public Page<ContestSubmission> findContestSubmissionByUserLoginIdAndContestIdPaging(
        Pageable pageable,
        String userLoginId,
        String contestId
    ) {
        //log.info("findContestSubmissionByUserLoginIdAndContestIdPaging, user = " + userLoginId + " contestId = " + contestId);
        return contestSubmissionPagingAndSortingRepo.findAllByUserIdAndContestId(pageable, userLoginId, contestId)
                                                    .map(contestSubmissionEntity -> ContestSubmission
                                                        .builder()
                                                        .contestSubmissionId(contestSubmissionEntity.getContestSubmissionId())
                                                        .contestId(contestSubmissionEntity.getContestId())
                                                        .createAt(contestSubmissionEntity.getCreatedAt() != null
                                                                      ? DateTimeUtils.dateToString(
                                                            contestSubmissionEntity.getCreatedAt(),
                                                            DateTimeUtils.DateTimeFormat.DATE_TIME_ISO_FORMAT)
                                                                      : null)
                                                        .sourceCodeLanguage(contestSubmissionEntity.getSourceCodeLanguage())
                                                        .point(contestSubmissionEntity.getPoint())
                                                        .problemId(contestSubmissionEntity.getProblemId())
                                                        .testCasePass(contestSubmissionEntity.getTestCasePass())
                                                        .status(contestSubmissionEntity.getStatus())
                                                        .message(contestSubmissionEntity.getMessage())
                                                        .userId(contestSubmissionEntity.getUserId())
                                                        .build()
                                                    );
    }

    @Override
    public Page<ContestSubmission> findContestSubmissionByUserLoginIdAndContestIdAndProblemIdPaging(
        Pageable pageable,
        String userLoginId,
        String contestId,
        String problemId
    ) {
        //log.info("findContestSubmissionByUserLoginIdAndContestIdPaging, user = " + userLoginId + " contestId = " + contestId);
        return contestSubmissionPagingAndSortingRepo
            .findAllByUserIdAndContestIdAndProblemId(pageable, userLoginId, contestId, problemId)
            .map(contestSubmissionEntity -> ContestSubmission
                .builder()
                .contestSubmissionId(contestSubmissionEntity.getContestSubmissionId())
                .contestId(contestSubmissionEntity.getContestId())
                .createAt(contestSubmissionEntity.getCreatedAt() != null
                              ? DateTimeUtils.dateToString(
                    contestSubmissionEntity.getCreatedAt(),
                    DateTimeUtils.DateTimeFormat.DATE_TIME_ISO_FORMAT)
                              : null)
                .sourceCodeLanguage(contestSubmissionEntity.getSourceCodeLanguage())
                .point(contestSubmissionEntity.getPoint())
                .problemId(contestSubmissionEntity.getProblemId())
                .testCasePass(contestSubmissionEntity.getTestCasePass())
                .status(contestSubmissionEntity.getStatus())
                .message(contestSubmissionEntity.getMessage())
                .userId(contestSubmissionEntity.getUserId())
                .build()
            );
    }

    @Override
    public List<ContestSubmission> getNewestSubmissionResults(String userLoginId) {
        List<ContestSubmissionEntity> lst = contestSubmissionPagingAndSortingRepo
            .findAllByUserId(userLoginId);
        List<ContestSubmission> retList = new ArrayList();
        Set<String> keys = new HashSet();
        for (ContestSubmissionEntity s : lst) {
            String k = s.getContestId() + "@" + s.getProblemId() + "@" + s.getUserId();
            keys.add(k);
            //  log.info("getNewestSubmissionResults, read record " + s.getContestSubmissionId() + " created stamp " + s.getCreatedAt());
        }
        Set<String> ignores = new HashSet();
        for (ContestSubmissionEntity s : lst) {
            String k = s.getContestId() + "@" + s.getProblemId() + "@" + s.getUserId();
            if (ignores.contains(k)) {
                continue;
            }
            if (keys.contains(k)) {
                ContestSubmission cs = new ContestSubmission();
                cs.setContestSubmissionId(s.getContestSubmissionId());
                cs.setStatus(s.getStatus());
                cs.setContestId(s.getContestId());
                cs.setProblemId(s.getProblemId());
                cs.setUserId(s.getUserId());
                cs.setPoint(s.getPoint());
                cs.setCreateAt(s.getCreatedAt() != null
                                   ? DateTimeUtils.dateToString(
                    s.getCreatedAt(),
                    DateTimeUtils.DateTimeFormat.DATE_TIME_ISO_FORMAT)
                                   : null);
                cs.setTestCasePass(s.getTestCasePass());
                cs.setSourceCodeLanguage(s.getSourceCodeLanguage());
                retList.add(cs);
                ignores.add(k);// process only the first meet
                //break;// break when reach a first entry
            }
        }
        return retList;
    }

    @Override
    public Page<ContestSubmission> findContestSubmissionByContestIdPaging(Pageable pageable, String contestId) {
        return contestSubmissionPagingAndSortingRepo
            .findAllByContestId(pageable, contestId)
            .map(contestSubmissionEntity -> ContestSubmission
                .builder()
                .contestSubmissionId(contestSubmissionEntity.getContestSubmissionId())
                .contestId(contestSubmissionEntity.getContestId())
                .createAt(contestSubmissionEntity.getCreatedAt() != null
                              ? DateTimeUtils.dateToString(
                    contestSubmissionEntity.getCreatedAt(),
                    DateTimeUtils.DateTimeFormat.DATE_TIME_ISO_FORMAT)
                              : null)
                .sourceCodeLanguage(contestSubmissionEntity.getSourceCodeLanguage())
                .point(contestSubmissionEntity.getPoint())
                .problemId(contestSubmissionEntity.getProblemId())
                .testCasePass(contestSubmissionEntity.getTestCasePass())
                .status(contestSubmissionEntity.getStatus())
                .message(contestSubmissionEntity.getMessage())
                .userId(contestSubmissionEntity.getUserId())
                .fullname(userService.findPersonByUserLoginId(contestSubmissionEntity.getUserId()).getFullName())
                .build());
    }

    @Override
    public Page<ContestSubmission> findContestNotEvaluatedSubmissionByContestIdPaging(
        Pageable pageable,
        String contestId
    ) {
        //return contestSubmissionPagingAndSortingRepo.findAllByContestId(pageable, contestId)
        return contestSubmissionPagingAndSortingRepo
            .findAllByContestIdAndStatus(pageable, contestId, ContestSubmissionEntity.SUBMISSION_STATUS_NOT_AVAILABLE)
            .map(contestSubmissionEntity -> ContestSubmission
                .builder()
                .contestSubmissionId(contestSubmissionEntity.getContestSubmissionId())
                .contestId(contestSubmissionEntity.getContestId())
                .createAt(contestSubmissionEntity.getCreatedAt() != null ? DateTimeUtils.dateToString(
                    contestSubmissionEntity.getCreatedAt(),
                    DateTimeUtils.DateTimeFormat.DATE_TIME_ISO_FORMAT) : null)
                .sourceCodeLanguage(contestSubmissionEntity.getSourceCodeLanguage())
                .point(contestSubmissionEntity.getPoint())
                .problemId(contestSubmissionEntity.getProblemId())
                .testCasePass(contestSubmissionEntity.getTestCasePass())
                .status(contestSubmissionEntity.getStatus())
                .message(contestSubmissionEntity.getMessage())
                .userId(contestSubmissionEntity.getUserId())
                .fullname(userService.findPersonByUserLoginId(contestSubmissionEntity.getUserId()).getFullName())
                .build());
    }

    @Override
    public ContestSubmissionEntity getContestSubmissionDetail(UUID submissionId) {
        return contestSubmissionRepo.findContestSubmissionEntityByContestSubmissionId(submissionId);
    }

    @Override
    public ModelGetContestInfosOfSubmissionOutput getContestInfosOfASubmission(UUID submissionId) {
        ContestSubmissionEntity sub = contestSubmissionRepo.findContestSubmissionEntityByContestSubmissionId(
            submissionId);
        String contestId = sub.getContestId();
        ContestEntity contest = contestRepo.findContestByContestId(contestId);
        ModelGetContestInfosOfSubmissionOutput res = new ModelGetContestInfosOfSubmissionOutput();
        res.setSubmissionId(submissionId);
        res.setContestId(contestId);
        List<String> problemIds = new ArrayList();
        for (ProblemEntity p : contest.getProblems()) {
            problemIds.add(p.getProblemId());
        }
        res.setProblemIds(problemIds);
        res.setProblems(contest.getProblems());
        return res;
    }

    @Override
    @Transactional
    public void deleteTestcase(UUID testcaseId, String userId) throws MiniLeetCodeException {
        TestCaseEntity testCase = testCaseRepo.findTestCaseByTestCaseId(testcaseId);
        if (testCase == null) {
            return;
        }

        ProblemEntity problem = problemRepo.findByProblemId(testCase.getProblemId());
        if (!problem.getUserId().equals(userId)) {
            throw new MiniLeetCodeException("permission denied");
        }

        testCase.setStatusId(TestCaseEntity.STATUS_DISABLED);
        testCaseService.saveTestCaseWithCache(testCase);

    }

    class CodeSimilatiryComparator implements Comparator<CodeSimilarityElement> {

        @Override
        public int compare(CodeSimilarityElement e1, CodeSimilarityElement e2) {
            if (e1.getScore() > e2.getScore()) {
                return -1;
            } else if (e1.getScore() < e2.getScore()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public ModelCodeSimilarityOutput checkSimilarity(String contestId, ModelCheckSimilarityInput I) {
        List<CodeSimilarityElement> list = new ArrayList();

        List<UserRegistrationContestEntity> participants = userRegistrationContestRepo
            .findAllByContestIdAndStatus(contestId, UserRegistrationContestEntity.STATUS_SUCCESSFUL);

        ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
        List<ProblemEntity> problems = contestEntity.getProblems();

        for (ProblemEntity p : problems) {
            String problemId = p.getProblemId();
            //  log.info("checkSimilarity, consider problem " + problemId + " threshold  = " + I.getThreshold());
            List<ContestSubmissionEntity> listSubmissions = new ArrayList();
            for (UserRegistrationContestEntity participant : participants) {
                String userLoginId = participant.getUserId();
                //log.info("checkSimilarity, consider problem " + problemId + " participant " + userLoginId);
                List<ContestSubmissionEntity> submissions = contestSubmissionRepo.findAllByContestIdAndUserIdAndProblemId(
                    contestId,
                    userLoginId,
                    problemId);
                //log.info("checkSimilarity, consider problem " + problemId + " participant " + userLoginId
                //         + " submissions.sz = " +
                //         submissions.size() +
                //         "");

                if (submissions != null && submissions.size() > 0) {// take the last submission in the sorted list
                    ContestSubmissionEntity sub = submissions.get(0);
                    listSubmissions.add(sub);
                }
            }

            //  log.info("checkSimilarity, consider problem " + problemId + " listSubmissions = " + listSubmissions.size());

            // check similarity of submissions to the current problemId
            for (int i = 0; i < listSubmissions.size(); i++) {
                ContestSubmissionEntity s1 = listSubmissions.get(i);
                for (int j = i + 1; j < listSubmissions.size(); j++) {
                    ContestSubmissionEntity s2 = listSubmissions.get(j);
                    if (s1.getUserId().equals(s2.getUserId())) {
                        continue;
                    }

                    double score = CodeSimilarityCheck.check(s1.getSourceCode(), s2.getSourceCode());
                    //  log.info("checkSimilarity, consider problem " + problemId + " listSubmissions = " + listSubmissions.size()
                    //     + " score between codes " + i + " and " + j + " = " + score + " threshold = " + I.getThreshold());

                    //if(score <= 0.0001) continue;
                    if (score <= I.getThreshold() * 0.01) {
                        continue;
                    }

                    CodeSimilarityElement e = new CodeSimilarityElement();
                    e.setScore(score);
                    e.setSource1(s1.getSourceCode());
                    e.setUserLoginId1(s1.getUserId());
                    e.setSubmitDate1(s1.getCreatedAt());
                    e.setProblemId1(s1.getProblemId());

                    e.setSource2(s2.getSourceCode());
                    e.setUserLoginId2(s2.getUserId());
                    e.setSubmitDate2(s2.getCreatedAt());
                    e.setProblemId2(s2.getProblemId());

                    list.add(e);


                    CodePlagiarism codePlagiarism = new CodePlagiarism();
                    codePlagiarism.setContestId(contestId);
                    codePlagiarism.setProblemId(problemId);
                    codePlagiarism.setUserId1(s1.getUserId());
                    codePlagiarism.setUserId2(s2.getUserId());
                    codePlagiarism.setSourceCode1(s1.getSourceCode());
                    codePlagiarism.setSourceCode2(s2.getSourceCode());
                    codePlagiarism.setSubmitDate1(s1.getCreatedAt());
                    codePlagiarism.setSubmitDate2(s2.getCreatedAt());
                    codePlagiarism.setScore(score);
                    codePlagiarism.setCreatedStamp(new Date());

                    codePlagiarism = codePlagiarismRepo.save(codePlagiarism);
                    //  log.info("checkSimilarity, save score = " + score);
                }
            }
        }


        /*
        List<ContestSubmissionEntity> submissions = contestSubmissionPagingAndSortingRepo.findAllByContestId(contestId);
        for(int i = 0; i < submissions.size(); i++){
            ContestSubmissionEntity s1 = submissions.get(i);
            for(int j = i+1; j < submissions.size(); j++){
                ContestSubmissionEntity s2 = submissions.get(j);
                if(s1.getUserId().equals(s2.getUserId())) continue;

                double score = CodeSimilarityCheck.check(s1.getSourceCode(), s2.getSourceCode());
                CodeSimilarityElement e = new CodeSimilarityElement();
                e.setScore(score);
                e.setSource1(s1.getSourceCode());
                e.setUserLoginId1(s1.getUserId());
                e.setSubmitDate1(s1.getCreatedAt());
                e.setProblemId1(s1.getProblemId());

                e.setSource2(s2.getSourceCode());
                e.setUserLoginId2(s2.getUserId());
                e.setSubmitDate2(s2.getCreatedAt());
                e.setProblemId2(s2.getProblemId());

                list.add(e);
            }
        }
        */

        Collections.sort(list, new CodeSimilatiryComparator());

        ModelCodeSimilarityOutput model = new ModelCodeSimilarityOutput();
        model.setCodeSimilarityElementList(list);
        return model;
    }

    @Override
    public void evaluateSubmission(UUID submissionId) {
        // log.info("evaluateSubmission(" + submissionId);
        ContestSubmissionEntity submission = contestSubmissionRepo.findById(submissionId).orElse(null);
        ContestEntity contest = contestService.findContestWithCache(submission.getContestId());
        evaluateSubmission(submission, contest);
    }

    @Override
    public void evaluateSubmissionUsingQueue(ContestSubmissionEntity submission, ContestEntity contest) {
        contestService.updateContestSubmissionStatus(
            submission.getContestSubmissionId(),
            ContestSubmissionEntity.SUBMISSION_STATUS_EVALUATION_IN_PROGRESS);
        rabbitTemplate.convertAndSend(
            EXCHANGE,
            JUDGE_PROBLEM,
            submission.getContestSubmissionId()
        );
    }


    @Override
    public void evaluateSubmission(ContestSubmissionEntity sub, ContestEntity contest) {
        if (sub != null) {
            // QUEUE MODE
            if (contest.getJudgeMode().equals(ContestEntity.ASYNCHRONOUS_JUDGE_MODE_QUEUE)) {
                evaluateSubmissionUsingQueue(sub, contest);
                return;
            }

            //NORMAL MODE
            // set status of submission and store in DB
            sub.setStatus(ContestSubmissionEntity.SUBMISSION_STATUS_EVALUATION_IN_PROGRESS);
            sub = contestSubmissionRepo.save(sub);
            //   log.info("evaluateSubmission(" + sub.getContestSubmissionId() + " Saved in DB");

            ProblemEntity p = problemRepo.findById(sub.getProblemId()).orElse(null);
            if (p == null) {
                //     log.info("evaluateSubmission, problem is NULL???");
                return;
            }
            //   log.info("evaluateBatchSubmissionContest, consider participant " + sub.getUserId() + " problem " +
            //     sub.getProblemId() + " submissions " + sub.getContestSubmissionId());

            List<TestCaseEntity> testCaseEntityList;

            if (contest.getEvaluateBothPublicPrivateTestcase() != null &&
                contest
                    .getEvaluateBothPublicPrivateTestcase()
                    .equals(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES)) {
                testCaseEntityList = testCaseRepo.findAllByProblemId(sub.getProblemId());
            } else {
                testCaseEntityList = testCaseRepo.findAllByProblemIdAndIsPublic(sub.getProblemId(), "N");
            }
            if (testCaseEntityList == null) {
                testCaseEntityList = new ArrayList<>();
            }

            String tempName = tempDir.createRandomScriptFileName(sub.getUserId() +
                                                                 "-" +
                                                                 sub.getContestId() +
                                                                 "-" +
                                                                 sub.getProblemId());

            int runtime = 0;
            int score = 0;
            int nbTestCasePass = 0;
            String totalStatus = "";
            List<String> statusList = new ArrayList<String>();
            List<ContestSubmissionTestCaseEntity> LCSTE = new ArrayList();
            String message = "";
            boolean compileError = false;
            for (int i = 0; i < testCaseEntityList.size(); i++) {
                List<TestCaseEntity> L = new ArrayList();
                TestCaseEntity testCase = testCaseEntityList.get(i);

                L.add(testCaseEntityList.get(i));

                try {
                    String response = submission(
                        sub.getSourceCode(),
                        sub.getSourceCodeLanguage(),
                        tempName,
                        L,
                        "language not found",
                        p.getTimeLimit(),
                        p.getMemoryLimit());
                    List<String> testCaseAns = L
                        .stream()
                        .map(TestCaseEntity::getCorrectAnswer)
                        .collect(Collectors.toList());
                    List<Integer> points = L
                        .stream()
                        .map(TestCaseEntity::getTestCasePoint)
                        .collect(Collectors.toList());
                    ProblemSubmission problemSubmission = StringHandler.handleContestResponse(
                        response,
                        testCaseAns,
                        points);

                    //  log.info("submitContestProblemTestCaseByTestCase, run tesecase " + (i+1) + " message = " + problemSubmission.getMessage());
                    // check if there is error compile
                    if (problemSubmission.getMessage() != null &&
                        !problemSubmission.getMessage().contains("successful")) {
                        message = problemSubmission.getMessage();
                        compileError = true;
                        break;
                    }

                    runtime = runtime + problemSubmission.getRuntime().intValue();
                    score = score + problemSubmission.getScore();
                    nbTestCasePass += problemSubmission.getNbTestCasePass();
                    //status = status + "#" + (i+1) + ": " + problemSubmission.getStatus() + "; ";
                    statusList.add(problemSubmission.getStatus());
                    List<String> output = problemSubmission.getParticipantAns();
                    String participantAns = "";
                    if (output != null && output.size() > 0) {
                        participantAns = output.get(0);
                    }

                    List<ContestSubmissionTestCaseEntity> LT = contestSubmissionTestCaseEntityRepo
                        .findAllByContestSubmissionIdAndContestIdAndProblemIdAndSubmittedByUserLoginIdAndTestCaseId(
                            sub.getContestSubmissionId(),
                            sub.getContestId(),
                            sub.getProblemId(),
                            sub.getUserId(),
                            testCase.getTestCaseId());
                    ContestSubmissionTestCaseEntity cste = null;
                    if (LT != null && LT.size() > 0) {
                        cste = LT.get(0);
                        cste.setStatus(problemSubmission.getStatus());
                        cste.setPoint(problemSubmission.getScore());
                        cste.setRuntime(problemSubmission.getRuntime());
                    } else {
                        cste = ContestSubmissionTestCaseEntity.builder()
                                                              .contestId(sub.getContestId())
                                                              .problemId(sub.getProblemId())
                                                              .testCaseId(testCaseEntityList.get(i).getTestCaseId())
                                                              .submittedByUserLoginId(sub.getUserId())
                                                              .point(problemSubmission.getScore())
                                                              .status(problemSubmission.getStatus())
                                                              .participantSolutionOtput(participantAns)
                                                              .runtime(problemSubmission.getRuntime())
                                                              .createdStamp(new Date())
                                                              .build();
                    }
                    cste = contestSubmissionTestCaseEntityRepo.save(cste);
                    LCSTE.add(cste);
                    //  log.info("evaluateBatchSubmissionContest, consider participant " + sub.getUserId()
                    //          + " problem " + sub.getProblemId() + " submissions "
                    //         + sub.getContestSubmissionId() + " DONE with testcase " + testCase.getTestCaseId() + " get point " + problemSubmission.getScore());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_ACCEPTED;
            for (String s : statusList) {
                if (s.equals(ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR)) {
                    totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR;
                    break;
                }
            }

            if (compileError) {
                totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR;
            } else if (nbTestCasePass == 0) {
                totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_FAILED;
            } else if (nbTestCasePass > 0 && nbTestCasePass < testCaseEntityList.size()) {
                totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_PARTIAL;
            }

            sub.setPoint(score);
            sub.setTestCasePass(nbTestCasePass + "/" + testCaseEntityList.size());
            sub.setRuntime(new Long(runtime));
            sub.setStatus(totalStatus);
            sub.setMessage(message);
            ContestSubmissionEntity c = contestSubmissionRepo.save(sub);

            for (ContestSubmissionTestCaseEntity e : LCSTE) {
                e.setContestSubmissionId(c.getContestSubmissionId());
                e = contestSubmissionTestCaseEntityRepo.save(e);
            }

            //log.info("c {}", c.getRuntime());

        }
    }

    @Override
    public ModelEvaluateBatchSubmissionResponse reJudgeAllSubmissionsOfContest(String contestId) {
        List<UserRegistrationContestEntity> participants = userRegistrationContestRepo
            .findAllByContestIdAndStatus(contestId, UserRegistrationContestEntity.STATUS_SUCCESSFUL);

        ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
        List<ProblemEntity> problems = contestEntity.getProblems();
        for (UserRegistrationContestEntity participant : participants) {
            String userLoginId = participant.getUserId();
            //  log.info("evaluateBatchSubmissionContest, consider participant " + userLoginId);
            for (ProblemEntity p : problems) {
                String problemId = p.getProblemId();
                List<ContestSubmissionEntity> submissions = contestSubmissionRepo.findAllByContestIdAndUserIdAndProblemId(
                    contestId,
                    userLoginId,
                    problemId);
                //    log.info("evaluateBatchSubmissionContest, consider participant " + userLoginId + " problem " + problemId + " submissions.sz = " + submissions.size() + "");

                for (int i = 0; i < submissions.size(); i++) {// take the last submission in the sorted list
                    ContestSubmissionEntity sub = submissions.get(i);
                    //   log.info("evaluateBatchSubmissionContest, consider participant " + userLoginId + " problem " + problemId + " submissions " + sub.getContestSubmissionId());
                    evaluateSubmission(sub, contestEntity);

                }
            }
        }
        return null;

    }

    private ModelEvaluateBatchSubmissionResponse judgeAllSubmissionsOfContestBasedOnUserAndProblems(String contestId) {
        List<UserRegistrationContestEntity> participants = userRegistrationContestRepo
            .findAllByContestIdAndStatus(contestId, UserRegistrationContestEntity.STATUS_SUCCESSFUL);

        ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
        List<ProblemEntity> problems = contestEntity.getProblems();
        for (UserRegistrationContestEntity participant : participants) {
            String userLoginId = participant.getUserId();
            //    log.info("evaluateBatchSubmissionContest, consider participant " + userLoginId);
            for (ProblemEntity p : problems) {
                String problemId = p.getProblemId();
                List<ContestSubmissionEntity> submissions = contestSubmissionRepo.findAllByContestIdAndUserIdAndProblemId(
                    contestId,
                    userLoginId,
                    problemId);
                //     log.info("evaluateBatchSubmissionContest, consider participant " + userLoginId + " problem " + problemId + " submissions.sz = " + submissions.size() + "");

                for (int i = 0; i < submissions.size(); i++) {// take the last submission in the sorted list
                    ContestSubmissionEntity sub = submissions.get(i);

                    ///      log.info("evaluateBatchSubmissionContest, consider participant " + userLoginId + " problem " + problemId + " submissions " + sub.getContestSubmissionId());
                    if (sub.getStatus() == null || sub.getStatus().equals("")) {
                        evaluateSubmission(sub, contestEntity);
                    } else {
                        //     log.info("evaluateBatchSubmissionContest, consider participant " + userLoginId + " problem " + problemId +
                        //            " submissions " + sub.getContestSubmissionId() + " has evaluated --> IGNORE");
                    }
                }
            }
        }
        return null;

    }

    private ModelEvaluateBatchSubmissionResponse judgeAllSubmissionsOfContestBasedSubmissionDate(String contestId) {
        List<ContestSubmissionEntity> submissions = contestSubmissionRepo.findAllByContestIdAndStatus(
            contestId,
            ContestSubmissionEntity.SUBMISSION_STATUS_EVALUATION_IN_PROGRESS);
        ContestEntity contest = contestService.findContestWithCache(contestId);
        for (ContestSubmissionEntity sub : submissions) {// take the last submission in the sorted list
            //  log.info("evaluateBatchSubmissionContest, start " + i + "/" + submissions.size()
            //         + " consider submission " + sub.getContestSubmissionId() + " participant " + sub.getUserId() + " problem "
//
            //                   + sub.getProblemId() + " submissions " + sub.getContestSubmissionId());

            evaluateSubmission(sub, contest);
            /*
            if(sub.getStatus() == null || sub.getStatus().equals("")) {
                ModelContestSubmissionResponse res = evaluateSubmission(sub);
            }else{
                log.info("evaluateBatchSubmissionContest, consider participant " + sub.getUserId()
                         + " problem " + sub.getProblemId() +
                         " submissions " + sub.getContestSubmissionId() + " has evaluated --> IGNORE");
            }
            */
        }
        return null;


    }

    @Override
    public ModelEvaluateBatchSubmissionResponse judgeAllSubmissionsOfContest(String contestId) {
        //return judgeAllSubmissionsOfContestBasedOnUserAndProblems(contestId);
        return judgeAllSubmissionsOfContestBasedSubmissionDate(contestId);
    }

    @Override
    public ModelEvaluateBatchSubmissionResponse evaluateBatchSubmissionContest(String contestId) {
        List<UserRegistrationContestEntity> participants = userRegistrationContestRepo
            .findAllByContestIdAndStatus(contestId, UserRegistrationContestEntity.STATUS_SUCCESSFUL);

        ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
        List<ProblemEntity> problems = contestEntity.getProblems();
        for (UserRegistrationContestEntity participant : participants) {
            String userLoginId = participant.getUserId();
            // log.info("evaluateBatchSubmissionContest, consider participant " + userLoginId);
            for (ProblemEntity p : problems) {
                String problemId = p.getProblemId();
                List<ContestSubmissionEntity> submissions = contestSubmissionRepo.findAllByContestIdAndUserIdAndProblemId(
                    contestId,
                    userLoginId,
                    problemId);
                //   log.info("evaluateBatchSubmissionContest, consider participant " + userLoginId + " problem " + problemId + " submissions.sz = " + submissions.size() + "");

                if (submissions != null && submissions.size() > 0) {// take the last submission in the sorted list
                    ContestSubmissionEntity sub = submissions.get(0);
                    //    log.info("evaluateBatchSubmissionContest, consider participant " + userLoginId + " problem " + problemId + " submissions " + sub.getContestSubmissionId());
                    List<TestCaseEntity> testCaseEntityList = testCaseRepo.findAllByProblemId(problemId);
                    String tempName = tempDir.createRandomScriptFileName(userLoginId +
                                                                         "-" +
                                                                         contestId +
                                                                         "-" +
                                                                         problemId);

                    int runtime = 0;
                    int score = 0;
                    int nbTestCasePass = 0;
                    String totalStatus = "";
                    List<String> statusList = new ArrayList<String>();
                    List<ContestSubmissionTestCaseEntity> LCSTE = new ArrayList();
                    String message = "";
                    boolean compileError = false;
                    for (int i = 0; i < testCaseEntityList.size(); i++) {
                        List<TestCaseEntity> L = new ArrayList();
                        TestCaseEntity testCase = testCaseEntityList.get(i);
                        L.add(testCaseEntityList.get(i));

                        try {
                            String response = submission(
                                sub.getSourceCode(),
                                sub.getSourceCodeLanguage(),
                                tempName,
                                L,
                                "language not found",
                                p.getTimeLimit(),
                                p.getMemoryLimit());
                            List<String> testCaseAns = L
                                .stream()
                                .map(TestCaseEntity::getCorrectAnswer)
                                .collect(Collectors.toList());
                            List<Integer> points = L
                                .stream()
                                .map(TestCaseEntity::getTestCasePoint)
                                .collect(Collectors.toList());
                            ProblemSubmission problemSubmission = StringHandler.handleContestResponse(
                                response,
                                testCaseAns,
                                points);

                            log.info("submitContestProblemTestCaseByTestCase, run testcase " +
                                     (i + 1) +
                                     " message = " +
                                     problemSubmission.getMessage());
                            // check if there is error compile
                            if (problemSubmission.getMessage() != null &&
                                !problemSubmission.getMessage().contains("successful")) {
                                message = problemSubmission.getMessage();
                                compileError = true;
                                break;
                            }

                            runtime = runtime + problemSubmission.getRuntime().intValue();
                            score = score + problemSubmission.getScore();
                            nbTestCasePass += problemSubmission.getNbTestCasePass();
                            //status = status + "#" + (i+1) + ": " + problemSubmission.getStatus() + "; ";
                            statusList.add(problemSubmission.getStatus());
                            List<String> output = problemSubmission.getParticipantAns();
                            String participantAns = "";
                            if (output != null && output.size() > 0) {
                                participantAns = output.get(0);
                            }

                            List<ContestSubmissionTestCaseEntity> LT = contestSubmissionTestCaseEntityRepo
                                .findAllByContestSubmissionIdAndContestIdAndProblemIdAndSubmittedByUserLoginIdAndTestCaseId(
                                    sub.getContestSubmissionId(),
                                    sub.getContestId(),
                                    sub.getProblemId(),
                                    sub.getUserId(),
                                    testCase.getTestCaseId());
                            ContestSubmissionTestCaseEntity cste = null;
                            if (LT != null && LT.size() > 0) {
                                cste = LT.get(0);
                                cste.setStatus(problemSubmission.getStatus());
                                cste.setPoint(problemSubmission.getScore());
                                cste.setRuntime(problemSubmission.getRuntime());
                            } else {
                                cste = ContestSubmissionTestCaseEntity.builder()
                                                                      .contestId(contestId)
                                                                      .problemId(problemId)
                                                                      .testCaseId(testCaseEntityList
                                                                                      .get(i)
                                                                                      .getTestCaseId())
                                                                      .submittedByUserLoginId(userLoginId)
                                                                      .point(problemSubmission.getScore())
                                                                      .status(problemSubmission.getStatus())
                                                                      .participantSolutionOtput(participantAns)
                                                                      .runtime(problemSubmission.getRuntime())
                                                                      .createdStamp(new Date())
                                                                      .build();
                            }
                            cste = contestSubmissionTestCaseEntityRepo.save(cste);
                            LCSTE.add(cste);
                            //  log.info("evaluateBatchSubmissionContest, consider participant " + userLoginId + " problem " + problemId + " submissions "
                            //         + sub.getContestSubmissionId() + " DONE with testcase " + testCase.getTestCaseId() + " get point " + problemSubmission.getScore());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_ACCEPTED;
                    for (String s : statusList) {
                        if (s.equals(ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR)) {
                            totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR;
                            break;
                        }
                    }

                    if (compileError) {
                        totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR;
                    } else if (nbTestCasePass == 0) {
                        totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_FAILED;
                    } else if (nbTestCasePass > 0 && nbTestCasePass < testCaseEntityList.size()) {
                        totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_PARTIAL;
                    }

                    sub.setPoint(score);
                    sub.setTestCasePass(nbTestCasePass + "/" + testCaseEntityList.size());
                    sub.setRuntime(new Long(runtime));
                    sub.setStatus(totalStatus);
                    ContestSubmissionEntity c = contestSubmissionRepo.save(sub);

                    for (ContestSubmissionTestCaseEntity e : LCSTE) {
                        e.setContestSubmissionId(c.getContestSubmissionId());
                        e = contestSubmissionTestCaseEntityRepo.save(e);
                    }

                    //    log.info("c {}", c.getRuntime());

                }
            }
        }
        return null;
    }

    @Override
    public List<ModelContestByRoleResponse> getContestsByRoleOfUser(String userLoginId) {
        List<ContestRole> contestRoles = contestRoleRepo.findAllByUserLoginIdAndThruDate(userLoginId, null);
        List<ModelContestByRoleResponse> modelContestByRoleResponses = new ArrayList();
        for (ContestRole cr : contestRoles) {
            ModelContestByRoleResponse m = new ModelContestByRoleResponse();
            m.setContestId(cr.getContestId());
            m.setRoleId(cr.getRoleId());
            modelContestByRoleResponses.add(m);
        }
        return modelContestByRoleResponses;
    }

    private ModelGetTestCase convertToModelGetTestCase(TestCaseEntity testCaseEntity) {
        String correctAns = testCaseEntity.getCorrectAnswer();
        String testCase = testCaseEntity.getTestCase();
        int point = testCaseEntity.getTestCasePoint();

        return ModelGetTestCase.builder()
                               .correctAns(correctAns)
                               .testCase(testCase)
                               .point(point)
                               .isPublic(testCaseEntity.getIsPublic())
                               .status(testCaseEntity.getStatusId())
                               .viewMore(false)
                               .testCaseId(testCaseEntity.getTestCaseId())
                               .description(testCaseEntity.getDescription())
                               .build();
    }

    private ModelGetContestPageResponse getModelGetContestPageResponse(List<ContestEntity> contestPage) {
        List<ModelGetContestResponse> lists = new ArrayList<>();
        if (contestPage != null) {
            contestPage.forEach(contest -> {
                ModelGetContestResponse modelGetContestResponse = ModelGetContestResponse.builder()
                                                                                         .contestId(contest.getContestId())
                                                                                         .contestName(contest.getContestName())
                                                                                         .contestTime(contest.getContestSolvingTime())
                                                                                         .countDown(contest.getCountDown())
                                                                                         .startAt(contest.getStartedAt())
                                                                                         .isPublic(contest.getIsPublic())
                                                                                         .statusId(contest.getStatusId())
                                                                                         .userId(contest.getUserId())
                                                                                         .createdAt(contest.getCreatedAt())
                                                                                         .build();
                lists.add(modelGetContestResponse);
            });
        }

        return ModelGetContestPageResponse.builder()
                                          .contests(lists)
                                          .build();
    }

    private ModelGetContestPageResponse getModelGetContestPageResponse(Page<ContestEntity> contestPage) {
        List<ModelGetContestResponse> lists = new ArrayList<>();
        if (contestPage != null) {
            contestPage.forEach(contest -> {
                ModelGetContestResponse modelGetContestResponse = ModelGetContestResponse.builder()
                                                                                         .contestId(contest.getContestId())
                                                                                         .contestName(contest.getContestName())
                                                                                         .contestTime(contest.getContestSolvingTime())
                                                                                         .countDown(contest.getCountDown())
                                                                                         .startAt(contest.getStartedAt())
                                                                                         .isPublic(contest.getIsPublic())
                                                                                         .statusId(contest.getStatusId())
                                                                                         .userId(contest.getUserId())
                                                                                         .createdAt(contest.getCreatedAt())
                                                                                         .build();
                lists.add(modelGetContestResponse);
            });
        }

        return ModelGetContestPageResponse.builder()
                                          .contests(lists)
                                          .build();
    }

    private ModelGetContestPageResponse getModelGetContestPageResponse(Page<ContestEntity> contestPage, long count) {
        List<ModelGetContestResponse> lists = new ArrayList<>();
        if (contestPage != null) {
            contestPage.forEach(contest -> {
                ModelGetContestResponse modelGetContestResponse = ModelGetContestResponse.builder()
                                                                                         .contestId(contest.getContestId())
                                                                                         .contestName(contest.getContestName())
                                                                                         .contestTime(contest.getContestSolvingTime())
                                                                                         .countDown(contest.getCountDown())
                                                                                         .startAt(contest.getStartedAt())
                                                                                         .isPublic(contest.getIsPublic())
                                                                                         .statusId(contest.getStatusId())
                                                                                         .userId(contest.getUserId())
                                                                                         .createdAt(contest.getCreatedAt())
                                                                                         .build();
                lists.add(modelGetContestResponse);
            });
        }

        return ModelGetContestPageResponse.builder()
                                          .contests(lists)
                                          .count(count)
                                          .build();
    }

    private UserSubmissionContestResultNativeEntity convertObjectsToUserSubmissionContestResultNativeEntity(
        Object[] objects,
        String contestId
    ) {
        if (objects.length < 6) {
            return null;
        }
//        String fullName = objects[3] != null ? objects[3].toString() : "" + objects[4] != null ? objects[4].toString() : "" + objects[5] != null ? objects[5].toString() : "" ;
        StringBuilder fullName = new StringBuilder();
        for (int i = 3; i < 6; i++) {
            if (objects[i] != null) {
                fullName.append(objects[i]).append(" ");
            }
        }
        //  log.info("full name {}", fullName);
        return UserSubmissionContestResultNativeEntity.builder()
                                                      .contestId(contestId)
                                                      .userId(objects[0] != null ? objects[0].toString() : null)
                                                      .point(objects[1] != null
                                                                 ? Integer.parseInt(objects[1].toString())
                                                                 : 0)
                                                      .email(objects[2] != null ? objects[2].toString() : null)
                                                      .fullName(fullName.toString())
                                                      .build();
    }

    private List<ProblemEntity> getContestProblemsFromListContestId(List<String> problemIds) {
        return problemRepo.getAllProblemWithArray(problemIds);
    }


    private String submission(
        String source,
        String computerLanguage,
        String tempName,
        List<TestCaseEntity> testCaseList,
        String exception,
        int timeLimit,
        int memoryLimit
    ) throws Exception {
        String ans;
        tempName = tempName.replace(" ", "");
        switch (computerLanguage) {
            case "CPP":
                tempDir.createScriptSubmissionFile(
                    ComputerLanguage.Languages.CPP,
                    tempName,
                    testCaseList,
                    source,
                    timeLimit,
                    memoryLimit);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.CPP, tempName);
                break;
            case "JAVA":
                tempDir.createScriptSubmissionFile(
                    ComputerLanguage.Languages.JAVA,
                    tempName,
                    testCaseList,
                    source,
                    timeLimit,
                    memoryLimit);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.JAVA, tempName);
                break;
            case "PYTHON3":
                tempDir.createScriptSubmissionFile(
                    ComputerLanguage.Languages.PYTHON3,
                    tempName,
                    testCaseList,
                    source,
                    timeLimit,
                    memoryLimit);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.PYTHON3, tempName);
                break;
            case "GOLANG":
                tempDir.createScriptSubmissionFile(
                    ComputerLanguage.Languages.GOLANG,
                    tempName,
                    testCaseList,
                    source,
                    timeLimit,
                    memoryLimit);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.GOLANG, tempName);
                break;
            default:
                throw new Exception(exception);
        }
//        tempDir.pushToConcurrentLinkedQueue(tempName);
        return ans;
    }

    private String submissionSolutionOutput(
        String sourceChecker,
        String computerLanguage,
        String solutionOutput,
        String tempName,
        TestCaseEntity testCase,
        String exception,
        int timeLimit,
        int memoryLimit
    ) throws Exception {
        //log.info("submissionSolutionOutput, sourceChecker = " + sourceChecker + " solutionOutput = " + solutionOutput + " testCase = " + testCase.getTestCase());

        String ans = "";
        tempName = tempName.replaceAll(" ", "");
        switch (computerLanguage) {
            case "CPP":
                tempDir.createScriptSubmissionSolutionOutputFile(
                    ComputerLanguage.Languages.CPP,
                    tempName,
                    solutionOutput,
                    testCase,
                    sourceChecker,
                    timeLimit);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.CPP, tempName);
                //  log.info("submissionSolutionOutput, sourceChecker = " + sourceChecker + " solutionOutput = " + solutionOutput + " testCase = " + testCase.getTestCase()
                //  + " ans = " + ans);
                break;
            case "JAVA":
                break;
            case "PYTHON3":
                break;
            case "GOLANG":
                break;
            default:
                throw new Exception(exception);
        }
//        tempDir.pushToConcurrentLinkedQueue(tempName);
        return ans;
    }

    private String runCode(
        String sourceCode,
        String computerLanguage,
        String tempName,
        String input,
        int timeLimit,
        String exception
    ) throws Exception {
        String ans;
        tempName = tempName.replaceAll(" ", "");
        switch (computerLanguage) {
            case "CPP":
                tempDir.createScriptFile(sourceCode, input, timeLimit, ComputerLanguage.Languages.CPP, tempName);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.CPP, tempName);
                break;
            case "JAVA":
                tempDir.createScriptFile(sourceCode, input, timeLimit, ComputerLanguage.Languages.JAVA, tempName);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.JAVA, tempName);
                break;
            case "PYTHON3":
                tempDir.createScriptFile(sourceCode, input, timeLimit, ComputerLanguage.Languages.PYTHON3, tempName);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.PYTHON3, tempName);
                break;
            case "GOLANG":
                tempDir.createScriptFile(sourceCode, input, timeLimit, ComputerLanguage.Languages.GOLANG, tempName);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.GOLANG, tempName);
                break;
            default:
                throw new Exception(exception);
        }
//        tempDir.pushToConcurrentLinkedQueue(tempName);
        return ans;
    }

    @Override
    public List<CodePlagiarism> findAllByContestId(String contestId) {
        List<CodePlagiarism> codePlagiarisms = codePlagiarismRepo.findAllByContestId(contestId);
        return codePlagiarisms;
    }

    private boolean emptyString(String s) {
        return s == null || s.equals("");
    }

    @Override
    public List<CodePlagiarism> findAllBy(ModelGetCodeSimilarityParams input) {
        List<CodePlagiarism> codePlagiarisms = codePlagiarismRepo.findAllByContestId(input.getContestId());
        List<CodePlagiarism> res = new ArrayList();
        if (!emptyString(input.getProblemId()) && !emptyString(input.getUserId())) {
            for (CodePlagiarism e : codePlagiarisms) {
                if (e.getProblemId().equals(input.getProblemId()) &&
                    (e.getUserId1().equals(input.getUserId()) || e.getUserId2().equals(input.getUserId()))) {
                    res.add(e);
                }
            }
        } else if (!emptyString(input.getProblemId()) && emptyString(input.getUserId())) {
            for (CodePlagiarism e : codePlagiarisms) {
                if (e.getProblemId().equals(input.getProblemId())) {
                    res.add(e);
                }
            }
        } else if (emptyString(input.getProblemId()) && !emptyString(input.getUserId())) {
            for (CodePlagiarism e : codePlagiarisms) {
                if (e.getUserId1().equals(input.getUserId()) || e.getUserId2().equals(input.getUserId())) {
                    res.add(e);
                }
            }
        } else {
            return codePlagiarisms;
        }

        return res;
    }

    class DFS {

        private Set<String> V;
        private Map<String, Set<String>> A;
        private Map<String, Integer> idxCC;
        private int nbCC;
        private List<List<String>> connectedComponents;

        public DFS(Set<String> V, Map<String, Set<String>> A) {
            this.V = V;
            this.A = A;
            /*
            for(String e: V){
                String a = "";
                for(String u: A.get(e)) a = a + u + ", ";
                log.info("DFS, node e = " + e + ": " + a);
            }
            */
        }

        private void Try(String u) {
            idxCC.put(u, nbCC);
            //log.info("DFS.Try(" + u + "), nbCC = " + nbCC + " idxCC.put(" + u + "," + nbCC + ")");
            for (String v : A.get(u)) {
                if (idxCC.get(v) == null) {
                    Try(v);
                }
            }
        }

        public void solve() {
            nbCC = 0;
            idxCC = new HashMap();
            connectedComponents = new ArrayList();
            for (String v : V) {
                if (idxCC.get(v) == null) {
                    nbCC++;
                    Try(v);
                }
            }
            for (int i = 1; i <= nbCC; i++) {
                List<String> cc = new ArrayList<String>();
                for (String e : V) {
                    if (idxCC.get(e) == i) {
                        cc.add(e);
                        //log.info("DFS.solve, cc i   = " + i + " add e = " + e);
                    }
                }
                connectedComponents.add(cc);
            }
        }

        public List<List<String>> getConnectedComponents() {
            return connectedComponents;
        }
    }

    @Override
    public List<ModelSimilarityClusterOutput> computeSimilarityClusters(ModelGetCodeSimilarityParams input) {
        ContestEntity contest = contestRepo.findContestByContestId(input.getContestId());
        List<CodePlagiarism> codePlagiarisms = codePlagiarismRepo.findAllByContestId(input.getContestId());
        double threshold = input.getThreshold() * 0.01;
        List<ModelSimilarityClusterOutput> res = new ArrayList();
        for (ProblemEntity p : contest.getProblems()) {
            // build graph and compute connected component related to the selected problem p
            Map<String, Set<String>> A = new HashMap();
            Set<String> V = new HashSet();
            List<CodePlagiarism> EP = new ArrayList();
            for (CodePlagiarism cp : codePlagiarisms) {
                //log.info("computeSimilarityClusters, problem " + p.getProblemId() + " user1 " + cp.getUserId1() + " user2 = "
                //         + cp.getUserId2() + " score = " + cp.getScore() + " threshold = " + threshold);
                if (cp.getProblemId().equals(p.getProblemId()) && cp.getScore() >= threshold) {
                    EP.add(cp);
                    //log.info("computeSimilarityClusters, problem " + p.getProblemId() + "ADD edge (" + cp.getUserId1() + ","
                    //         + cp.getUserId2() + ")");

                    String u1 = cp.getUserId1();
                    String u2 = cp.getUserId2();
                    V.add(u1);
                    V.add(u2);
                    if (A.get(u1) == null) {
                        A.put(u1, new HashSet());
                    }
                    if (A.get(u2) == null) {
                        A.put(u2, new HashSet());
                    }
                    A.get(u1).add(u2);
                    A.get(u2).add(u1);
                }

            }
            DFS dfs = new DFS(V, A);
            dfs.solve();
            List<List<String>> connectedComponents = dfs.getConnectedComponents();
            //ModelSimilarityClusterOutput c = new ModelSimilarityClusterOutput();
            //c.setProblemId(p.getProblemId());
            //c.setClusters(connectedComponents);
            for (List<String> cc : connectedComponents) {
                ModelSimilarityClusterOutput c = new ModelSimilarityClusterOutput();
                c.setProblemId(p.getProblemId());
                StringBuilder userIds = new StringBuilder();
                for (String s : cc) {
                    userIds.append(s).append(", ");
                }
                c.setUserIds(userIds.toString());
                res.add(c);
            }
        }

        return res;
    }

    @Override
    public List<ModelReponseCodeSimilaritySummaryParticipant> getListModelReponseCodeSimilaritySummaryParticipant(String contestId) {
        List<CodePlagiarism> L = codePlagiarismRepo.findAllByContestId(contestId);
        List<ModelReponseCodeSimilaritySummaryParticipant> res = new ArrayList();
        List<UserRegistrationContestEntity> UR = userRegistrationContestRepo.findAllByContestIdAndStatus(contestId,
                                                                                                         UserRegistrationContestEntity.STATUS_SUCCESSFUL);
        HashMap<String, Double> mUser2HighestSimilarity = new HashMap();
        for (UserRegistrationContestEntity ur : UR) {
            mUser2HighestSimilarity.put(ur.getUserId(), 0.0);
        }
        for (CodePlagiarism cp : L) {
            String u1 = cp.getUserId1();
            String u2 = cp.getUserId2();
            double s = cp.getScore();
            if (mUser2HighestSimilarity.get(u1) != null && mUser2HighestSimilarity.get(u1) < s) {
                mUser2HighestSimilarity.put(u1, s);
            }
            if (mUser2HighestSimilarity.get(u2) != null && mUser2HighestSimilarity.get(u2) < s) {
                mUser2HighestSimilarity.put(u2, s);
            }
        }
        for (String u : mUser2HighestSimilarity.keySet()) {
            ModelReponseCodeSimilaritySummaryParticipant e = new ModelReponseCodeSimilaritySummaryParticipant();
            e.setUserId(u);
            e.setHighestSimilarity(mUser2HighestSimilarity.get(u));
            res.add(e);
        }
        Collections.sort(res, new Comparator<ModelReponseCodeSimilaritySummaryParticipant>() {
            @Override
            public int compare(
                ModelReponseCodeSimilaritySummaryParticipant u1,
                ModelReponseCodeSimilaritySummaryParticipant u2
            ) {
                if (u2.getHighestSimilarity() > u1.getHighestSimilarity()) {
                    return 1;
                } else if (u2.getHighestSimilarity() < u1.getHighestSimilarity()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return res;
    }

    @Transactional
    @Override
    public ContestSubmissionEntity updateContestSubmissionSourceCode(ModelUpdateContestSubmission input) {
        ContestSubmissionEntity sub = contestSubmissionRepo.findById(input.getContestSubmissionId()).orElse(null);
        if (sub != null) {
            sub.setSourceCode(input.getModifiedSourceCodeSubmitted());
            if (input.getProblemId() != null && !input.getProblemId().equals("")) {
                sub.setProblemId(input.getProblemId());
            }
            if (input.getContestId() != null && !input.getContestId().equals("")) {
                sub.setContestId(input.getContestId());
            }

            sub.setUpdateAt(new Date());
            sub = contestSubmissionRepo.save(sub);

            ContestSubmissionHistoryEntity e = new ContestSubmissionHistoryEntity();
            e.setContestSubmissionId(sub.getContestSubmissionId());
            e.setModifiedSourceCodeSubmitted(input.getModifiedSourceCodeSubmitted());
            e.setLanguage(sub.getSourceCodeLanguage());
            e.setProblemId(sub.getProblemId());

            if (input.getContestId() != null && !input.getContestId().equals("")) {
                e.setContestId(sub.getContestId());
            }
            e.setCreatedStamp(new Date());
            e = contestSubmissionHistoryRepo.save(e);
            return sub;
        }
        return null;
    }

    @Override
    public List<ModelGetContestResponse> getContestsUsingAProblem(String problemId) {
        List<ModelGetContestResponse> res = new ArrayList();
        List<ContestProblem> contestProblems = contestProblemRepo.findAllByProblemId(problemId);
        for (ContestProblem cp : contestProblems) {
            ContestEntity contest = contestRepo.findContestByContestId(cp.getContestId());
            ModelGetContestResponse m = ModelGetContestResponse.builder()
                                                               .contestId(contest.getContestId())
                                                               .userId(contest.getUserId())
                                                               .createdAt(contest.getCreatedAt())
                                                               .statusId(contest.getStatusId())
                                                               .build();
            res.add(m);
        }
        return res;
    }

    @Override
    public int addAdminToManagerAndParticipantAllContest() {
        int cnt = 0;
        String admin = "admin";
        List<ContestEntity> contests = contestRepo.findAll();
        for (ContestEntity c : contests) {
            String contestId = c.getContestId();
            List<UserRegistrationContestEntity> L = userRegistrationContestRepo
                .findUserRegistrationContestEntityByContestIdAndUserIdAndStatusAndRoleId(
                    contestId,
                    admin,
                    UserRegistrationContestEntity.STATUS_SUCCESSFUL,
                    UserRegistrationContestEntity.ROLE_MANAGER);
            if (L == null || L.size() == 0) {
                UserRegistrationContestEntity ur = new UserRegistrationContestEntity();
                ur.setContestId(contestId);
                ur.setUserId(admin);
                ur.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
                ur.setRoleId(UserRegistrationContestEntity.ROLE_MANAGER);
                ur = userRegistrationContestRepo.save(ur);
                cnt++;
            }

            L = userRegistrationContestRepo
                .findUserRegistrationContestEntityByContestIdAndUserIdAndStatusAndRoleId(
                    contestId,
                    admin,
                    UserRegistrationContestEntity.STATUS_SUCCESSFUL,
                    UserRegistrationContestEntity.ROLE_PARTICIPANT);
            if (L == null || L.size() == 0) {
                UserRegistrationContestEntity ur = new UserRegistrationContestEntity();
                ur.setContestId(contestId);
                ur.setUserId(admin);
                ur.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
                ur.setRoleId(UserRegistrationContestEntity.ROLE_PARTICIPANT);
                ur = userRegistrationContestRepo.save(ur);
                cnt++;
            }

        }
        return cnt;
    }

    @Override
    public ModelUploadTestCaseOutput addTestCase(
        String testCase,
        ModelProgrammingContestUploadTestCase modelUploadTestCase,
        String userName
    ) {
        // try to execute the solution code on this test-case, if pass then store in DB
        // otherwise, ignore
        String problemId = modelUploadTestCase.getProblemId();
        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
        String output = "";
        ModelUploadTestCaseOutput res = new ModelUploadTestCaseOutput();
        if (modelUploadTestCase.getUploadMode().equals("EXECUTE")) {
            String tempName = tempDir.createRandomScriptFileName(userName +
                                                                 "-" +
                                                                 problemEntity.getProblemName() +
                                                                 "-" +
                                                                 problemEntity.getCorrectSolutionLanguage());

            try {
                output = runCode(
                    problemEntity.getCorrectSolutionSourceCode(),
                    problemEntity.getCorrectSolutionLanguage(),
                    tempName,
                    testCase,
                    problemEntity.getTimeLimit(),
                    "Correct Solution Language Not Found");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (output.contains("Time Limit Exceeded")) {
                res.setMessage("Time Limit Exceeded");
                res.setStatus("TLE");
                return res;
            }
            output = output.substring(0, output.length() - 1);
            int lastLinetIndexExpected = output.lastIndexOf("\n");
            output = output.substring(0, lastLinetIndexExpected);
//        output = output.replaceAll("\n", "");
            //  log.info("addTestCase, output = {}", output);

        } else {
            output = modelUploadTestCase.getCorrectAnswer();
        }


        TestCaseEntity tc = new TestCaseEntity();
        tc.setTestCase(testCase);
        tc.setProblemId(modelUploadTestCase.getProblemId());
        tc.setIsPublic(modelUploadTestCase.getIsPublic());
        tc.setTestCasePoint(modelUploadTestCase.getPoint());
        tc.setCorrectAnswer(output);
        tc.setDescription(modelUploadTestCase.getDescription());
        testCaseService.saveTestCaseWithCache(tc);
        res.setMessage("Upload Successfully!");
        res.setStatus("OK");
        return res;
    }

    @Override
    public ModelUploadTestCaseOutput rerunCreateTestCaseSolution(String problemId, UUID testCaseId, String userId) {
        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
        ModelUploadTestCaseOutput res = new ModelUploadTestCaseOutput();

        TestCaseEntity tc = testCaseRepo.findTestCaseByTestCaseId(testCaseId);
        String testCase = tc.getTestCase();
        String tempName = tempDir.createRandomScriptFileName(userId +
                                                             "-" +
                                                             problemEntity.getProblemName() +
                                                             "-" +
                                                             problemEntity.getCorrectSolutionLanguage());
        String output = "";
        try {
            output = runCode(
                problemEntity.getCorrectSolutionSourceCode(),
                problemEntity.getCorrectSolutionLanguage(),
                tempName,
                testCase,
                problemEntity.getTimeLimit(),
                "Correct Solution Language Not Found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (output.contains("Time Limit Exceeded")) {
            res.setMessage("Time Limit Exceeded");
            res.setStatus("TLE");
            return res;
        }
        output = output.substring(0, output.length() - 1);
        int lastLinetIndexExpected = output.lastIndexOf("\n");
        output = output.substring(0, lastLinetIndexExpected);
        //  log.info("rerunCreateTestCaseSolution, output " + output);
        tc.setCorrectAnswer(output);
        tc = testCaseService.saveTestCaseWithCache(tc);
        res.setMessage("Upload Successfully!");
        res.setStatus("OK");

        return res;
    }

    @Override
    public ModelUploadTestCaseOutput uploadUpdateTestCase(
        UUID testCaseId,
        String testCase,
        ModelProgrammingContestUploadTestCase modelUploadTestCase,
        String userName
    ) {
        TestCaseEntity tc = testCaseRepo.findTestCaseByTestCaseId(testCaseId);
        if (testCase != null && !testCase.equals("")) {
            String problemId = modelUploadTestCase.getProblemId();
            ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
            String tempName = tempDir.createRandomScriptFileName(userName +
                                                                 "-" +
                                                                 problemEntity.getProblemName() +
                                                                 "-" +
                                                                 problemEntity.getCorrectSolutionLanguage());
            String output = "";
            try {
                output = runCode(
                    problemEntity.getCorrectSolutionSourceCode(),
                    problemEntity.getCorrectSolutionLanguage(),
                    tempName,
                    testCase,
                    problemEntity.getTimeLimit(),
                    "Correct Solution Language Not Found");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ModelUploadTestCaseOutput res = new ModelUploadTestCaseOutput();
            if (output.contains("Time Limit Exceeded")) {
                res.setMessage("Time Limit Exceeded");
                res.setStatus("TLE");
                return res;
            }
            output = output.substring(0, output.length() - 1);
            int lastLinetIndexExpected = output.lastIndexOf("\n");
            output = output.substring(0, lastLinetIndexExpected);
//        output = output.replaceAll("\n", "");
            //log.info("addTestCase, output = {}", output);

            tc.setTestCase(testCase);
            tc.setCorrectAnswer(output);
        }
        tc.setIsPublic(modelUploadTestCase.getIsPublic());
        tc.setTestCasePoint(modelUploadTestCase.getPoint());
        tc.setDescription(modelUploadTestCase.getDescription());
        tc.setCorrectAnswer(modelUploadTestCase.getCorrectAnswer());
        tc = testCaseService.saveTestCaseWithCache(tc);
        ModelUploadTestCaseOutput res = new ModelUploadTestCaseOutput();
        res.setMessage("Successfully");
        res.setStatus("OK");
        return res;
    }

    private void updateMaxPoint(
        ContestSubmissionEntity s,
        HashMap<String, List<ModelUserJudgedProblemSubmissionResponse>> mUserId2Submission,
        HashMap<String, ProblemEntity> mID2Problem
    ) {
        if (mUserId2Submission.get(s.getUserId()) == null) {
            mUserId2Submission.put(s.getUserId(), new ArrayList<ModelUserJudgedProblemSubmissionResponse>());
            ModelUserJudgedProblemSubmissionResponse e = new ModelUserJudgedProblemSubmissionResponse();
            PersonModel person = userService.findPersonByUserLoginId(s.getUserId());
            e.setUserId(s.getUserId());
            e.setFullName(person.getFullName());
            e.setProblemId(s.getProblemId());
            e.setSubmissionSourceCode(s.getSourceCode());
            e.setPoint(s.getPoint());
            e.setProblemName(mID2Problem.get(s.getProblemId()).getProblemName());
            e.setTestCasePassed(s.getTestCasePass());
            e.setStatus(s.getStatus());
            mUserId2Submission.get(s.getUserId()).add(e);
        } else {
            // scan list problem & submission and update max point
            ModelUserJudgedProblemSubmissionResponse maxP = null;
            int maxPoint = -1000;
            for (ModelUserJudgedProblemSubmissionResponse e : mUserId2Submission.get(s.getUserId())) {
                if (e.getProblemId().equals(s.getProblemId())) {
                    if (e.getPoint() > maxPoint) {
                        maxP = e;
                        maxPoint = e.getPoint();
                    }
                }
            }
            if (maxP == null) {
                ModelUserJudgedProblemSubmissionResponse e = new ModelUserJudgedProblemSubmissionResponse();
                PersonModel person = userService.findPersonByUserLoginId(s.getUserId());
                e.setUserId(s.getUserId());
                e.setFullName(person.getFullName());
                e.setProblemId(s.getProblemId());
                e.setSubmissionSourceCode(s.getSourceCode());
                e.setPoint(s.getPoint());
                e.setProblemName(mID2Problem.get(s.getProblemId()).getProblemName());
                e.setTestCasePassed(s.getTestCasePass());
                e.setStatus(s.getStatus());
                mUserId2Submission.get(s.getUserId()).add(e);
            } else {
                if (maxP.getPoint() < s.getPoint()) {// update max point submission
                    maxP.setPoint(s.getPoint());
                    maxP.setSubmissionSourceCode(s.getSourceCode());
                    maxP.setStatus(s.getStatus());
                    maxP.setTestCasePassed(s.getTestCasePass());
                }
            }
        }
    }

    @Override
    public List<ModelUserJudgedProblemSubmissionResponse> getUserJudgedProblemSubmissions(String contestId) {
        List<ContestSubmissionEntity> submissions = contestSubmissionRepo.findAllByContestId(contestId);
        List<ModelUserJudgedProblemSubmissionResponse> res = new ArrayList();
        HashMap<String, List<ModelUserJudgedProblemSubmissionResponse>> mUserId2Submission = new HashMap();
        HashMap<String, ProblemEntity> mID2Problem = new HashMap();
        ContestEntity contest = contestRepo.findContestByContestId(contestId);
        List<ProblemEntity> problems = contest.getProblems();
        for (ProblemEntity p : problems) {
            mID2Problem.put(p.getProblemId(), p);
        }
        for (ContestSubmissionEntity s : submissions) {
            updateMaxPoint(s, mUserId2Submission, mID2Problem);
            //ModelUserJudgedProblemSubmissionResponse e = new ModelUserJudgedProblemSubmissionResponse();
            //PersonModel person = userService.findPersonByUserLoginId(s.getUserId());
            //e.setUserId(s.getUserId());
            //e.setFullName(person.getFullName());
            //e.setProblemId(s.getProblemId());
            //e.setSubmissionSourceCode(s.getSourceCode());
            //e.setPoint(s.getPoint());
            //res.add(e);
        }
        for (String userId : mUserId2Submission.keySet()) {
            if (mUserId2Submission.get(userId) != null) {
                res.addAll(mUserId2Submission.get(userId));
            }
        }
        return res;
    }

    @Override
    public ModelGetRolesOfUserInContestResponse getRolesOfUserInContest(String userId, String contestId) {
        List<UserRegistrationContestEntity> lst = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
                contestId,
                userId,
                UserRegistrationContestEntity.STATUS_SUCCESSFUL);
        List<String> roles = UserRegistrationContestEntity.getListRoles();
        List<String> rolesApproved = new ArrayList();
        List<String> rolesNotApproved = new ArrayList();
        //  log.info("getRolesOfUserInContest, userId = " + userId + " contestId = " + contestId + " lst.sz = " + lst.size());
        for (String role : roles) {
            boolean approved = false;
            for (UserRegistrationContestEntity e : lst) {
                String r = e.getRoleId();
                //  log.info("getRolesOfUserInContest, userId = " + userId + " contestId = " + contestId + " lst.sz = " + lst.size()
                // +" role approved r = " + r + " consider role = " + role);
                if (role.equals(r)) {
                    approved = true;
                    break;
                }
            }
            if (approved) {
                rolesApproved.add(role);
            } else {
                rolesNotApproved.add(role);
            }
        }
        return new ModelGetRolesOfUserInContestResponse(userId, contestId, rolesApproved, rolesNotApproved);
    }

    @Override
    public boolean removeMemberFromContest(UUID id) {
        UserRegistrationContestEntity u = userRegistrationContestRepo.findById(id).orElse(null);
        if (u != null) {
            userRegistrationContestRepo.delete(u);
            return true;
        }
        return false;
    }

    @Override
    public boolean forbidMemberFromSubmitToContest(UUID id) {
        UserRegistrationContestEntity u = userRegistrationContestRepo.findById(id).orElse(null);
        if (u != null) {
            u.setPermissionId(UserRegistrationContestEntity.PERMISSION_FORBIDDEN_SUBMIT);
            userRegistrationContestRepo.save(u);
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePermissionMemberToContest(String userId, ModelUpdatePermissionMemberToContestInput input) {
        UserRegistrationContestEntity u = userRegistrationContestRepo.findById(input.getUserRegisId()).orElse(null);
        if (u != null) {
            u.setPermissionId(input.getPermissionId());
            u.setLastUpdated(new Date());
            u.setUpdatedByUserLogin_id(userId);
            userRegistrationContestRepo.save(u);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateProblemContest(String userId, ModelUpdateProblemContestInput I) {
        ContestProblem cp = contestProblemRepo.findByContestIdAndProblemId(I.getContestId(), I.getProblemId());
        if (cp == null) {
            return false;
        }
        cp.setSubmissionMode(I.getSubmissionMode());
        cp = contestProblemRepo.save(cp);
        return true;
    }

    @Override
    public List<ModelResponseUserProblemRole> getUserProblemRoles(String problemId) {
        List<UserContestProblemRole> lst = userContestProblemRoleRepo.findAllByProblemId(problemId);
        List<ModelResponseUserProblemRole> res = new ArrayList();
        for (UserContestProblemRole upr : lst) {
            ModelResponseUserProblemRole e = new ModelResponseUserProblemRole();
            e.setUserLoginId(upr.getUserId());
            e.setProblemId(upr.getProblemId());
            e.setRoleId(upr.getRoleId());
            res.add(e);
        }
        return res;
    }

    @Override
    public boolean addUserProblemRole(String userName, ModelUserProblemRole input) {
        List<UserContestProblemRole> L = userContestProblemRoleRepo.findAllByProblemIdAndUserIdAndRoleId(
            input.getProblemId(),
            input.getUserId(),
            input.getRoleId());
        if (L != null && L.size() > 0) {
            return false;
        }
        UserContestProblemRole e = new UserContestProblemRole();
        e.setUserId(input.getUserId());
        e.setProblemId(input.getProblemId());
        e.setRoleId(input.getRoleId());
        e.setUpdateByUserId(userName);
        e = userContestProblemRoleRepo.save(e);

        return true;
    }

    @Override
    public boolean removeUserProblemRole(String userName, ModelUserProblemRole input) {
        List<UserContestProblemRole> L = userContestProblemRoleRepo.findAllByProblemIdAndUserIdAndRoleId(
            input.getProblemId(),
            input.getUserId(),
            input.getRoleId());
        if (L != null && L.size() > 0) {
            return false;
        }
        for (UserContestProblemRole e : L) {
            userContestProblemRoleRepo.delete(e);
            //userContestProblemRoleRepo.remove(e);
        }
        return true;
    }

    @Override
    public boolean grantRole2AllProblems(String userLoginId, String userId, String roleId) {
        List<ProblemEntity> problems = problemRepo.findAll();
        for (ProblemEntity p : problems) {
            UserContestProblemRole upr = new UserContestProblemRole();
            upr.setUpdateByUserId(userLoginId);
            upr.setUserId(userId);
            upr.setRoleId(roleId);
            upr.setProblemId(p.getProblemId());
            upr.setCreatedStamp(new Date());
            upr = userContestProblemRoleRepo.save(upr);
        }
        return true;
    }

    @Override
    public List<TagEntity> getAllTags() {
        return tagRepo.findAll();
    }

    @Override
    @Transactional
    public TagEntity addNewTag(ModelTag tag) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(tag.getName());

        if (tag.getDescription() != null) {
            tagEntity.setDescription(tag.getDescription());
        } else {
            tagEntity.setDescription("");
        }

        return tagRepo.save(tagEntity);
    }

    @Override
    @Transactional
    public TagEntity updateTag(Integer tagId, ModelTag newTag) {
        TagEntity tagEntity = tagRepo.findByTagId(tagId);

        tagEntity.setName(newTag.getName());

        if (newTag.getDescription() != null) {
            tagEntity.setDescription(newTag.getDescription());
        } else {
            tagEntity.setDescription("");
        }

        return tagRepo.save(tagEntity);
    }

    @Override
    @Transactional
    public void deleteTag(Integer tagId) {
        TagEntity tagEntity = tagRepo.findByTagId(tagId);
        tagRepo.delete(tagEntity);
    }

    @Override
    @Transactional
    public void switchAllContestJudgeMode(String judgeMode) {
        contestRepo.switchAllContestToJudgeMode(judgeMode);
        cacheService.flushAllCache();
    }

    public void exportProblem(String problemId, OutputStream outputStream) {
        try {
            ModelCreateContestProblemResponse problem = getContestProblem(problemId);

            if (problem != null) {
                handleExportProblem(problem, outputStream);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleExportProblem(
        ModelCreateContestProblemResponse problem,
        OutputStream outputStream
    ) throws IOException {
        List<File> files = new ArrayList<>();

        try {
            File problemGeneralInfoFile = exporter.exportProblemInfoToFile(problem);
            File problemDescriptionFile = exporter.exportProblemDescriptionToFile(problem);
            File problemCorrectSolutionFile = exporter.exportProblemCorrectSolutionToFile(problem);

            files.add(problemGeneralInfoFile);
            files.add(problemCorrectSolutionFile);

            if (problem.getScoreEvaluationType().equals(Constants.ProblemResultEvaluationType.CUSTOM.getValue())) {
                File problemCustomCheckerFile = exporter.exportProblemCustomCheckerToFile(problem);
                files.add(problemCustomCheckerFile);
            }

            if (!problem.getAttachmentNames().isEmpty()) {
                files.addAll(exporter.exportProblemAttachmentToFile(problem));
            }

            files.addAll(exporter.exportProblemTestCasesToFile(problem));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Zip files.
        ZipOutputStreamUtils.zip(
            outputStream,
            files,
            CompressionMethod.DEFLATE,
            null,
            EncryptionMethod.AES,
            AesKeyStrength.KEY_STRENGTH_256);

        //delete files
        for (File file : files) {
            file.delete();
        }
    }

}
