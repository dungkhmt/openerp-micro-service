package com.hust.baseweb.applications.programmingcontest.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.hust.baseweb.applications.programmingcontest.model.externalapi.ContestProblemModelResponse;
import com.hust.baseweb.applications.programmingcontest.model.externalapi.SubmissionModelResponse;
import com.hust.baseweb.applications.programmingcontest.repo.*;
import com.hust.baseweb.applications.programmingcontest.service.helper.cache.ProblemTestCaseServiceCache;
import com.hust.baseweb.applications.programmingcontest.utils.ComputerLanguage;
import com.hust.baseweb.applications.programmingcontest.utils.DateTimeUtils;
import com.hust.baseweb.applications.programmingcontest.utils.TempDir;
import com.hust.baseweb.applications.programmingcontest.utils.codesimilaritycheckingalgorithms.CodeSimilarityCheck;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.model.ProblemFilter;
import com.hust.baseweb.model.ProblemProjection;
import com.hust.baseweb.model.TestCaseFilter;
import com.hust.baseweb.model.dto.ProblemDTO;
import com.hust.baseweb.repo.UserLoginRepo;
import com.hust.baseweb.service.UserService;
import com.hust.baseweb.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import vn.edu.hust.soict.judge0client.config.Judge0Config;
import vn.edu.hust.soict.judge0client.entity.Judge0Submission;
import vn.edu.hust.soict.judge0client.service.Judge0Service;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.hust.baseweb.config.rabbitmq.ProblemContestRoutingKey.JUDGE_PROBLEM;
import static com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestConfig.EXCHANGE;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ProblemTestCaseServiceImpl implements ProblemTestCaseService {

    public static final Integer MAX_SUBMISSIONS_CHECKSIMILARITY = 1000;

    private final ProblemRepo problemRepo;

    private TestCaseRepo testCaseRepo;

    private DockerClientBase dockerClientBase;

    private TempDir tempDir;

    private ProblemPagingAndSortingRepo problemPagingAndSortingRepo;

    private UserLoginRepo userLoginRepo;

    private ContestRepo contestRepo;

    private Constants constants;

    private ContestPagingAndSortingRepo contestPagingAndSortingRepo;

    private ContestSubmissionRepo contestSubmissionRepo;

    private UserRegistrationContestRepo userRegistrationContestRepo;

    private NotificationsService notificationsService;

    private UserRegistrationContestPagingAndSortingRepo userRegistrationContestPagingAndSortingRepo;

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

    private ProblemTestCaseServiceCache cacheService;

    private ContestProblemExportService exporter;

    private ContestUserParticipantGroupRepo contestUserParticipantGroupRepo;
    private UserRegistrationContestService userRegistrationContestService;

    private Judge0Service judge0Service;

    private ProblemTagRepo problemTagRepo;

    Judge0Config judge0Config;

    ObjectMapper objectMapper;

    @Override
    @Transactional
    public ProblemEntity createContestProblem(
        String createdBy,
        ModelCreateContestProblem dto,
        MultipartFile[] files
    ) {
        String problemId = dto.getProblemId().trim();

        if (problemRepo.findByProblemId(problemId) != null) {
            throw new DuplicateKeyException("Problem ID already exist");
        }

        List<String> attachmentId = new ArrayList<>();
        String[] fileId = dto.getFileId();
        List<MultipartFile> fileArray = Arrays.asList(files);

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

        ProblemEntity problem = ProblemEntity.builder()
                                             .problemId(problemId)
                                             .problemName(dto.getProblemName())
                                             .problemDescription(dto.getProblemDescription())
                                             .memoryLimit(dto.getMemoryLimit())
//                                                   .timeLimit(dto.getTimeLimitCPP()) //TODO: remove this after moving all to lms
                                             .timeLimitCPP(dto.getTimeLimitCPP())
                                             .timeLimitJAVA(dto.getTimeLimitJAVA())
                                             .timeLimitPYTHON(dto.getTimeLimitPYTHON())
                                             .levelId(dto.getLevelId())
                                             .correctSolutionLanguage(dto.getCorrectSolutionLanguage())
                                             .correctSolutionSourceCode(dto.getCorrectSolutionSourceCode())
                                             .solution(dto.getSolution())
                                             .isPreloadCode(dto.getIsPreloadCode())
                                             .preloadCode(dto.getPreloadCode())
                                             .solutionCheckerSourceCode(dto.getSolutionChecker())
                                             .solutionCheckerSourceLanguage(dto.getSolutionCheckerLanguage())
                                             .scoreEvaluationType(dto.getScoreEvaluationType() != null
                                                                      ? dto.getScoreEvaluationType()
                                                                      : Constants.ProblemResultEvaluationType.NORMAL.getValue())
                                             .isPublicProblem(dto.getIsPublic())
                                             .levelOrder(constants.getMapLevelOrder().get(dto.getLevelId()))
                                             .attachment(String.join(";", attachmentId))
                                             .statusId(dto.getStatus().toString())
                                             .sampleTestcase(dto.getSampleTestCase())
                                             .build();
        problem = problemService.saveProblemWithCache(problem);


        List<ProblemTag> problemTags = Arrays.stream(dto.getTagIds())
                                             .map(tagId -> ProblemTag.builder()
                                                                     .id(new ProblemTagId(tagId, problemId))
                                                                     .build())
                                             .collect(Collectors.toList());
        problemTagRepo.saveAll(problemTags);

        // grant role owner, manager, view to current user and admin
        List<String> roleIds = Arrays.asList(
            UserContestProblemRole.ROLE_OWNER,
            UserContestProblemRole.ROLE_EDITOR,
            UserContestProblemRole.ROLE_VIEWER
        );
        List<UserContestProblemRole> roles = new ArrayList<>();
        List<String> users = Arrays.asList(createdBy);
        if (!"admin".equals(createdBy)) {
            users.add("admin");
        }
        for (String user : users) {
            for (String roleId : roleIds) {
                UserContestProblemRole role = new UserContestProblemRole();

                role.setProblemId(problem.getProblemId());
                role.setUserId(user);
                role.setRoleId(roleId);

                roles.add(role);
            }
        }

        userContestProblemRoleRepo.saveAll(roles);

        // push notification to admin
        notificationsService.create(
            createdBy,
            "admin",
            createdBy + " has created a contest problem ID " + problem.getProblemId(),
            "");

        return problem;
    }

    @Override
    public ProblemEntity updateContestProblem(
        String problemId,
        String userId,
        ModelUpdateContestProblem dto,
        MultipartFile[] files
    ) throws Exception {
        List<UserContestProblemRole> roles = userContestProblemRoleRepo.findAllByProblemIdAndUserId(
            problemId,
            userId);

        boolean hasPermission = false;
        for (UserContestProblemRole role : roles) {
            if (role.getRoleId().equals(UserContestProblemRole.ROLE_EDITOR) ||
                role.getRoleId().equals(UserContestProblemRole.ROLE_OWNER)) {
                hasPermission = true;
                break;
            }
        }

        if (!hasPermission) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission");
        }

        ProblemEntity problem = problemRepo
            .findById(problemId)
            .orElseThrow(() -> new EntityNotFoundException("Problem ID not found"));
        if (!userId.equals(problem.getCreatedBy())
            &&
            !userContestProblemRoleRepo.existsByProblemIdAndUserIdAndRoleId(
                problemId,
                userId,
                UserContestProblemRole.ROLE_EDITOR)) {
            throw new MiniLeetCodeException("permission denied", 403);
        }

        // problem há been created, admin is shared edit role, but cannot perform the edit
        //if (!userId.equals(problem.getUserId())
        //    && !problem.getStatusId().equals(ProblemEntity.PROBLEM_STATUS_OPEN)) {
        //    throw new MiniLeetCodeException("Problem is not opened for edit", 400);
        //}

        List<TagEntity> tags = new ArrayList<>();
        Integer[] tagIds = dto.getTagIds();
        for (Integer tagId : tagIds) {
            TagEntity tag = tagRepo.findByTagId(tagId);
            tags.add(tag);
        }

        List<String> attachmentId = new ArrayList<>();
        attachmentId.add(problem.getAttachment());
        String[] fileId = dto.getFileId();
        List<MultipartFile> fileArray = Arrays.asList(files);

        List<String> removedFilesId = dto.getRemovedFilesId();
        if (problem.getAttachment() != null && !problem.getAttachment().isEmpty()) {
            String[] oldAttachmentIds = problem.getAttachment().split(";");
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

        problem.setProblemName(dto.getProblemName());
        problem.setProblemDescription(dto.getProblemDescription());
        problem.setLevelId(dto.getLevelId());
        problem.setSolution(dto.getSolution());
        problem.setIsPreloadCode(dto.getIsPreloadCode());
        problem.setPreloadCode(dto.getPreloadCode());
//        problem.setTimeLimit(dto.getTimeLimit());
        problem.setTimeLimitCPP(dto.getTimeLimitCPP());
        problem.setTimeLimitJAVA(dto.getTimeLimitJAVA());
        problem.setTimeLimitPYTHON(dto.getTimeLimitPYTHON());
        problem.setMemoryLimit(dto.getMemoryLimit());
        problem.setCorrectSolutionLanguage(dto.getCorrectSolutionLanguage());
        problem.setCorrectSolutionSourceCode(dto.getCorrectSolutionSourceCode());
        problem.setSolutionCheckerSourceCode(dto.getSolutionChecker());
        problem.setSolutionCheckerSourceLanguage(dto.getSolutionCheckerLanguage());
        problem.setScoreEvaluationType(dto.getScoreEvaluationType());
        problem.setPublicProblem(dto.getIsPublic());
        problem.setAttachment(String.join(";", attachmentId));
        problem.setTags(tags);
        problem.setSampleTestcase(dto.getSampleTestCase());

        if (userId.equals(problem.getCreatedBy())) {
            problem.setStatusId(dto.getStatus().toString());
        }

        return problemService.saveProblemWithCache(problem);
    }

    @Override
    public List<ModelProblemGeneralInfo> getAllProblemsGeneralInfo() {
        //return problemRepo.getAllProblemGeneralInformation();
        return problemRepo.getAllOpenProblemGeneralInformation();

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
            problemResponse.setUserId(problemEntity.getCreatedBy());
//            problemResponse.setTimeLimit(problemEntity.getTimeLimit());
            problemResponse.setTimeLimitCPP(problemEntity.getTimeLimitCPP());
            problemResponse.setTimeLimitJAVA(problemEntity.getTimeLimitJAVA());
            problemResponse.setTimeLimitPYTHON(problemEntity.getTimeLimitPYTHON());
            problemResponse.setMemoryLimit(problemEntity.getMemoryLimit());
            problemResponse.setLevelId(problemEntity.getLevelId());
            problemResponse.setCorrectSolutionSourceCode(problemEntity.getCorrectSolutionSourceCode());
            problemResponse.setCorrectSolutionLanguage(problemEntity.getCorrectSolutionLanguage());
            problemResponse.setSolutionCheckerSourceCode(problemEntity.getSolutionCheckerSourceCode());
            problemResponse.setSolutionCheckerSourceLanguage(problemEntity.getSolutionCheckerSourceLanguage());
            problemResponse.setScoreEvaluationType(problemEntity.getScoreEvaluationType());
            problemResponse.setSolution(problemEntity.getSolution());
            problemResponse.setIsPreloadCode(problemEntity.getIsPreloadCode());
            problemResponse.setPreloadCode(problemEntity.getPreloadCode());
            problemResponse.setLevelOrder(problemEntity.getLevelOrder());
            problemResponse.setCreatedAt(problemEntity.getCreatedAt());
            problemResponse.setPublicProblem(problemEntity.isPublicProblem());
            problemResponse.setTags(problemEntity.getTags());
            problemResponse.setSampleTestCase(problemEntity.getSampleTestcase());
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

    private float getTimeLimitByLanguage(ProblemEntity problem, String language) {
        float timeLimit;
        switch (language) {
            case ContestSubmissionEntity.LANGUAGE_CPP:
                timeLimit = problem.getTimeLimitCPP();
                break;
            case ContestSubmissionEntity.LANGUAGE_JAVA:
                timeLimit = problem.getTimeLimitJAVA();
                break;
            case ContestSubmissionEntity.LANGUAGE_PYTHON:
                timeLimit = problem.getTimeLimitPYTHON();
                break;
            default:
                timeLimit = problem.getTimeLimitCPP();
        }
        return timeLimit;
    }

//    @Override
//    public ModelGetTestCaseResultResponse getTestCaseResult(
//        String problemId,
//        String userName,
//        ModelGetTestCaseResult modelGetTestCaseResult
//    ) throws Exception {
//        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
//        String tempName = tempDir.createRandomScriptFileName(userName +
//                                                             "-" +
//                                                             problemEntity.getProblemId() +
//                                                             "-" +
//                                                             problemEntity.getCorrectSolutionLanguage());
//        String output = runCode(
//            problemEntity.getCorrectSolutionSourceCode(),
//            problemEntity.getCorrectSolutionLanguage(),
//            tempName,
//            modelGetTestCaseResult.getTestcase(),
//            getTimeLimitByLanguage(problemEntity, problemEntity.getCorrectSolutionLanguage()),
//            "Correct Solution Language Not Found");
//        if (output.contains("Time Limit Exceeded")) {
//            return ModelGetTestCaseResultResponse.builder()
//                                                 .result("")
//                                                 .status("Time Limit Exceeded")
//                                                 .build();
//        }
//        output = output.substring(0, output.length() - 1);
//        int lastLinetIndexExpected = output.lastIndexOf("\n");
//        output = output.substring(0, lastLinetIndexExpected);
////        output = output.replaceAll("\n", "");
//        //    log.info("output {}", output);
//        return ModelGetTestCaseResultResponse.builder()
//                                             .result(output)
//                                             .status("ok")
//                                             .build();
//    }

    /**
     * @param modelCheckCompile
     * @param userName
     * @return
     * @throws Exception
     */
    @Override
    public ModelCheckCompileResponse checkCompile(
        ModelCheckCompile modelCheckCompile,
        String userName
    ) throws Exception {
        int languageId;
        String compilerOptions = null;
        switch (ComputerLanguage.Languages.valueOf(modelCheckCompile.getComputerLanguage())) {
            case C:
                languageId = 50;
                compilerOptions = "-std=c17 -w -O2 -lm -fmax-errors=3";
                break;
            case CPP11:
                languageId = 54;
                compilerOptions = "-std=c++11 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
                break;
            case CPP14:
                languageId = 54;
                compilerOptions = "-std=c++14 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
                break;
            case CPP:
            case CPP17:
                languageId = 54;
                compilerOptions = "-std=c++17 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
                break;
            case JAVA:
                languageId = 62;
                break;
            case PYTHON3:
                languageId = 71;
                break;
            default:
                throw new Exception("Language not supported");
        }

        // Thực tế chỉ cần biên dịch, không cần chạy nên thiết lập giới hạn thời gian đủ nhỏ
        Judge0Submission submission = Judge0Submission.builder()
                                                      .sourceCode(modelCheckCompile.getSource())
                                                      .languageId(languageId)
                                                      .compilerOptions(compilerOptions)
                                                      .commandLineArguments(null)
                                                      .cpuTimeLimit(0.05F)
                                                      .cpuExtraTime(0.05F)
                                                      .wallTimeLimit(1.0F)
                                                      .memoryLimit(Float.valueOf(judge0Config
                                                                                     .getSubmission()
                                                                                     .getMaxMemoryLimit()))
                                                      .stackLimit(judge0Config.getSubmission().getMaxStackLimit())
                                                      .maxProcessesAndOrThreads(languageId != 62 ? 2 : 23)
                                                      .enablePerProcessAndThreadTimeLimit(false)
                                                      .enablePerProcessAndThreadMemoryLimit(false)
                                                      .maxFileSize(judge0Config.getSubmission().getMaxMaxFileSize())
                                                      .redirectStderrToStdout(false)
                                                      .enableNetwork(false)
                                                      .numberOfRuns(1)
                                                      .build();

        submission = judge0Service.createASubmission(submission, true, true);
        submission.decodeBase64();

        return ModelCheckCompileResponse.builder()
                                        .status(submission.getStatus().getDescription())
                                        .message(submission.getCompileOutput())
                                        .build();
    }

//    @Override
//    public TestCaseEntity saveTestCase(String problemId, ModelSaveTestcase modelSaveTestcase) {
//
//        TestCaseEntity testCaseEntity = TestCaseEntity.builder()
//                                                      .correctAnswer(modelSaveTestcase.getResult())
//                                                      .testCase(modelSaveTestcase.getInput())
//                                                      .testCasePoint(modelSaveTestcase.getPoint())
//                                                      .problemId(problemId)
//                                                      .isPublic(modelSaveTestcase.getIsPublic())
//                                                      .build();
//        return testCaseService.saveTestCaseWithCache(testCaseEntity);
//    }

    @Transactional
    @Override
    public ContestEntity createContest(ModelCreateContest modelCreateContest, String userName) throws Exception {
        try {
            String contestId = modelCreateContest.getContestId().trim();
            ContestEntity contestEntityExist = contestRepo.findContestByContestId(contestId);
            if (contestEntityExist != null) {
                throw new MiniLeetCodeException("Contest is already exist");
            }
            ContestEntity contestEntity = ContestEntity.builder()
                                                       .contestId(contestId)
                                                       .contestName(modelCreateContest.getContestName())
                                                       .contestSolvingTime(modelCreateContest.getContestTime())
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
                                                       .submissionActionType(ContestEntity.CONTEST_SUBMISSION_ACTION_TYPE_STORE_AND_EXECUTE)
                                                       .problemDescriptionViewType(ContestEntity.CONTEST_PROBLEM_DESCRIPTION_VIEW_TYPE_VISIBLE)
                                                       .participantViewResultMode(ContestEntity.CONTEST_PARTICIPANT_VIEW_TESTCASE_DETAIL_ENABLED)
                                                       .evaluateBothPublicPrivateTestcase(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES)
                                                       .sendConfirmEmailUponSubmission(ContestEntity.SEND_CONFIRM_EMAIL_UPON_SUBMISSION_NO)
                                                       .contestShowTag(ContestEntity.CONTEST_SHOW_TAG_PROBLEMS_NO)
                                                       .createdAt(new Date())
                                                       //.contestType(modelCreateContest.getContestType())
                                                       .contestType(ContestEntity.CONTEST_TYPE_TRAINING_NO_EVALUATION)
                                                       .build();

/*
            if (modelCreateContest.getStartedAt() != null) {
                contestEntity = ContestEntity.builder()
                                             .contestId(contestId)
                                             .contestName(modelCreateContest.getContestName())
                                             .contestSolvingTime(modelCreateContest.getContestTime())
//                                             .problems(problemEntities)
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
                                             .submissionActionType(ContestEntity.CONTEST_SUBMISSION_ACTION_TYPE_STORE_AND_EXECUTE)
                                             .problemDescriptionViewType(ContestEntity.CONTEST_PROBLEM_DESCRIPTION_VIEW_TYPE_VISIBLE)
                                             //.participantViewResultMode(ContestEntity.CONTEST_PARTICIPANT_VIEW_MODE_SEE_CORRECT_ANSWER)
                                             .participantViewResultMode(ContestEntity.CONTEST_PARTICIPANT_VIEW_TESTCASE_DETAIL_ENABLED)
                                             //.evaluateBothPublicPrivateTestcase(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_NO)
                                             .evaluateBothPublicPrivateTestcase(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES)

                                             .createdAt(new Date())
                                             .build();
            } else {
                contestEntity = ContestEntity.builder()
                                             .contestId(contestId)
                                             .contestName(modelCreateContest.getContestName())
                                             .contestSolvingTime(modelCreateContest.getContestTime())
//                                             .problems(problemEntities)
                                             .countDown(modelCreateContest.getCountDownTime())
                                             .userId(userName)
                                             .statusId(ContestEntity.CONTEST_STATUS_CREATED)
                                             .maxNumberSubmissions(modelCreateContest.getMaxNumberSubmissions())
                                             .maxSourceCodeLength(modelCreateContest.getMaxSourceCodeLength())
                                             .createdAt(new Date())
                                             .build();
            }
*/
            contestEntity = contestService.saveContestWithCache(contestEntity);

            // create corresponding entities in ContestRole
            ContestRole contestRole = new ContestRole();
            contestRole.setContestId(modelCreateContest.getContestId());
            contestRole.setUserLoginId(userName);
            Date fromDate = new Date();
            contestRole.setRoleId(ContestRole.CONTEST_ROLE_OWNER);
            contestRole.setFromDate(fromDate);
            contestRoleRepo.save(contestRole);

            contestRole = new ContestRole();
            contestRole.setContestId(modelCreateContest.getContestId());
            contestRole.setUserLoginId(userName);
            contestRole.setRoleId(ContestRole.CONTEST_ROLE_MANAGER);
            contestRole.setFromDate(fromDate);
            contestRoleRepo.save(contestRole);

            contestRole = new ContestRole();
            contestRole.setContestId(modelCreateContest.getContestId());
            contestRole.setUserLoginId(userName);
            contestRole.setRoleId(ContestRole.CONTEST_ROLE_PARTICIPANT);
            contestRole.setFromDate(fromDate);
            contestRoleRepo.save(contestRole);

            // create correspoding entities in UserRegistrationContestEntity
            UserRegistrationContestEntity urc = new UserRegistrationContestEntity();
            urc.setContestId(modelCreateContest.getContestId());
            urc.setRoleId(UserRegistrationContestEntity.ROLE_OWNER);
            urc.setUserId(userName);
            urc.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
            userRegistrationContestRepo.save(urc);

            urc = new UserRegistrationContestEntity();
            urc.setContestId(modelCreateContest.getContestId());
            urc.setRoleId(UserRegistrationContestEntity.ROLE_MANAGER);
            urc.setUserId(userName);
            urc.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
            userRegistrationContestRepo.save(urc);

            urc = new UserRegistrationContestEntity();
            urc.setContestId(modelCreateContest.getContestId());
            urc.setRoleId(UserRegistrationContestEntity.ROLE_PARTICIPANT);
            urc.setUserId(userName);
            urc.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
            userRegistrationContestRepo.save(urc);

            // add account admin to the contest
            String admin = "admin";
            UserLogin u = userLoginRepo.findByUserLoginId(admin);
            if (u != null) {
                urc = new UserRegistrationContestEntity();
                urc.setContestId(modelCreateContest.getContestId());
                urc.setRoleId(UserRegistrationContestEntity.ROLE_OWNER);
                urc.setUserId(admin);
                urc.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
                userRegistrationContestRepo.save(urc);

                urc = new UserRegistrationContestEntity();
                urc.setContestId(modelCreateContest.getContestId());
                urc.setRoleId(UserRegistrationContestEntity.ROLE_MANAGER);
                urc.setUserId(admin);
                urc.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
                userRegistrationContestRepo.save(urc);

                urc = new UserRegistrationContestEntity();
                urc.setContestId(modelCreateContest.getContestId());
                urc.setRoleId(UserRegistrationContestEntity.ROLE_PARTICIPANT);
                urc.setUserId(admin);
                urc.setStatus(UserRegistrationContestEntity.STATUS_SUCCESSFUL);
                userRegistrationContestRepo.save(urc);

                //push notification to admin
                notificationsService.create(
                    userName, u.getUserLoginId(),
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
        log.info("updateContest, languages = " + modelUpdateContest.getLanguagesAllowed());
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

        ContestEntity contestEntity = ContestEntity.builder()
                                                   .contestId(contestId)
                                                   .contestName(modelUpdateContest.getContestName())
                                                   .contestSolvingTime(modelUpdateContest.getContestSolvingTime())
                                                   .problems(contestEntityExist.getProblems())
                                                   .userId(contestEntityExist.getUserId())
                                                   .countDown(modelUpdateContest.getCountDownTime())
                                                   .startedAt(modelUpdateContest.getStartedAt())
                                                   .startedCountDownTime(DateTimeUtils.minusMinutesDate(
                                                       modelUpdateContest.getStartedAt(),
                                                       modelUpdateContest.getCountDownTime()))
                                                   .endTime(DateTimeUtils.addMinutesDate(
                                                       modelUpdateContest.getStartedAt(),
                                                       modelUpdateContest.getContestSolvingTime()))
                                                   .statusId(modelUpdateContest.getStatusId())
                                                   .submissionActionType(modelUpdateContest.getSubmissionActionType())
                                                   .maxNumberSubmissions(modelUpdateContest.getMaxNumberSubmission())
                                                   .participantViewResultMode(modelUpdateContest.getParticipantViewResultMode())
                                                   .problemDescriptionViewType(modelUpdateContest.getProblemDescriptionViewType())
                                                   .maxSourceCodeLength(modelUpdateContest.getMaxSourceCodeLength())
                                                   .evaluateBothPublicPrivateTestcase(modelUpdateContest.getEvaluateBothPublicPrivateTestcase())
                                                   .minTimeBetweenTwoSubmissions(modelUpdateContest.getMinTimeBetweenTwoSubmissions())
                                                   .judgeMode(modelUpdateContest.getJudgeMode())
                                                   .sendConfirmEmailUponSubmission(modelUpdateContest.getSendConfirmEmailUponSubmission())
                                                   .participantViewSubmissionMode(modelUpdateContest.getParticipantViewSubmissionMode())
                                                   .languagesAllowed(String.join(
                                                       ",",
                                                       modelUpdateContest.getLanguagesAllowed()))
                                                   .contestType(modelUpdateContest.getContestType())
                                                   .contestShowTag(modelUpdateContest.getContestShowTag())
                                                   .contestShowComment(modelUpdateContest.getContestShowComment())
                                                   .contestPublic(modelUpdateContest.getContestPublic())
                                                   .build();
        return contestService.updateContestWithCache(contestEntity);

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
            contestProblem.setForbiddenInstructions(modelProblemInfoInContest.getForbiddenInstructions());
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
        contestProblem.setForbiddenInstructions(modelProblemInfoInContest.getForbiddenInstructions());

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
    public ModelGetContestDetailResponse getContestDetailByContestIdAndTeacher(String contestId, String userName) {
        List<UserRegistrationContestEntity> lc = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
                contestId,
                userName,
                Constants.RegistrationType.SUCCESSFUL.getValue());

        boolean ok = (lc != null && !lc.isEmpty()) || (userName.equals("admin"));

        ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
        if (!ok || contestEntity == null) {
            return ModelGetContestDetailResponse.builder()
                                                .unauthorized(true)
                                                .build();
        }
        return contestService.getModelGetContestDetailResponse(contestEntity);
    }

    /**
     * @param submissionId
     * @param testcaseId
     * @return
     */
    @Override
    public List<SubmissionDetailByTestcaseOM> getSubmissionDetailByTestcase(UUID submissionId, UUID testcaseId) {
        ContestSubmissionEntity submission = contestSubmissionRepo.findById(submissionId).orElse(null);
        ContestEntity contest = null;
        String contestId = "";
        String problemId = "";

        if (submission != null) {
            contest = contestRepo.findContestByContestId(submission.getContestId());
            contestId = submission.getContestId();
            problemId = submission.getProblemId();
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

        List<UUID> activeTestcaseIds = testCaseRepo
            .findAllActiveTestcaseOfProblem(problemId)
            .stream()
            .map(TestCaseEntity::getTestCaseId)
            .filter(id -> testcaseId == null || testcaseId.compareTo(id) == 0)
            .collect(Collectors.toList());

        List<ContestSubmissionTestCaseEntity> submissionTestcases = contestSubmissionTestCaseEntityRepo.findAllByContestSubmissionId(
            (submissionId));

        Map<UUID, ContestSubmissionTestCaseEntity> mapTestcaseIdToLatestSubmission = getMapTestcaseToLatestSubmission(
            activeTestcaseIds,
            submissionTestcases);

        List<SubmissionDetailByTestcaseOM> result = new ArrayList<>();

        for (ContestSubmissionTestCaseEntity st : mapTestcaseIdToLatestSubmission.values()) {
            TestCaseEntity tc = testCaseRepo.findTestCaseByTestCaseId(st.getTestCaseId());
            SubmissionDetailByTestcaseOM testcaseOM = new SubmissionDetailByTestcaseOM(
//                st.getContestSubmissionTestcaseId(),
//                st.getContestId(),
//                st.getProblemId(),
//                st.getSubmittedByUserLoginId(),
                st.getTestCaseId(),
                null,
                st.getStatus(),
                st.getPoint(),
                st.getUsedToGrade(),
                st.getRuntime(),
                st.getMemoryUsage(),
                null,
                null,
                null,
                st.getCreatedStamp(),
                viewSubmitSolutionOutputMode
            );

            if (testcaseId != null) {
                String testcaseContent = "";
                String testcaseOutput = "";
                String participantSolutionOutput = "";
                String stderr = null;

                if (contest != null && tc != null) {
                    testcaseContent = tc.getTestCase();
                    testcaseOutput = tc.getCorrectAnswer();
                    participantSolutionOutput = st.getParticipantSolutionOutput();
                    stderr = st.getStderr();
                }

                testcaseOM.setTestCase(testcaseContent);
                testcaseOM.setTestCaseAnswer(testcaseOutput);
                testcaseOM.setParticipantAnswer(participantSolutionOutput);
                testcaseOM.setStderr(stderr);
            }

            result.add(testcaseOM);
        }

        return result;
    }

    @Override
    public ContestSubmissionEntity teacherDisableSubmission(String userId, UUID submissionId) {
        ContestSubmissionEntity sub = contestSubmissionRepo.findById(submissionId).orElse(null);
        if (sub != null) {
            sub.setManagementStatus(ContestSubmissionEntity.MANAGEMENT_STATUS_DISABLED);
            sub.setLastUpdatedByUserId(userId);
            sub.setUpdateAt(new Date());
            sub = contestSubmissionRepo.save(sub);
            return sub;
        }
        return null;
    }

    @Override
    public ContestSubmissionEntity teacherEnableSubmission(String userId, UUID submissionId) {
        ContestSubmissionEntity sub = contestSubmissionRepo.findById(submissionId).orElse(null);
        if (sub != null) {
            sub.setManagementStatus(ContestSubmissionEntity.MANAGEMENT_STATUS_ENABLED);
            sub.setLastUpdatedByUserId(userId);
            sub.setUpdateAt(new Date());
            sub = contestSubmissionRepo.save(sub);
            return sub;
        }
        return null;
    }

    private Map<UUID, ContestSubmissionTestCaseEntity> getMapTestcaseToLatestSubmission(
        List<UUID> activeTestcaseIds,
        List<ContestSubmissionTestCaseEntity> submissionTestcases
    ) {

        submissionTestcases = submissionTestcases.stream()
                                                 .filter(testcase -> activeTestcaseIds.contains(testcase.getTestCaseId()))
                                                 .collect(Collectors.toList());

        Map<UUID, ContestSubmissionTestCaseEntity> mapTestcaseIdToLatestSubmission = new HashMap<>();
        for (ContestSubmissionTestCaseEntity submissionTestCase : submissionTestcases) {
            UUID testCaseId = submissionTestCase.getTestCaseId();
            if (!mapTestcaseIdToLatestSubmission.containsKey(testCaseId)) {
                mapTestcaseIdToLatestSubmission.put(testCaseId, submissionTestCase);
            } else {
                ContestSubmissionTestCaseEntity oldSubmissionTestcase = mapTestcaseIdToLatestSubmission.get(testCaseId);
                if (submissionTestCase.getLastUpdatedStamp().after(oldSubmissionTestcase.getLastUpdatedStamp())) {
                    mapTestcaseIdToLatestSubmission.put(testCaseId, submissionTestCase);
                }
            }
        }

        return mapTestcaseIdToLatestSubmission;
    }

    /**
     * @param userId
     * @param submissionId
     * @return
     */
    @Override
    public List<SubmissionDetailByTestcaseOM> getParticipantSubmissionDetailByTestCase(
        String userId, UUID submissionId
    ) {
        ContestSubmissionEntity submission = contestSubmissionRepo.findContestSubmissionEntityByContestSubmissionId(
            submissionId);

        if (!userId.equals(submission.getUserId())) {
            throw new AccessDeniedException("No permission");
        }

        ContestEntity contest;
        String contestId = "";
        String problemId = "";

        contest = contestRepo.findContestByContestId(submission.getContestId());
        contestId = submission.getContestId();
        problemId = submission.getProblemId();

        String viewSubmitSolutionOutputMode = "N";
        ContestProblem contestProblem = contestProblemRepo.findByContestIdAndProblemId(contestId, problemId);
        if (contestProblem != null) {
            if (contestProblem.getSubmissionMode() != null) {
                if (contestProblem.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_SOLUTION_OUTPUT)) {
                    viewSubmitSolutionOutputMode = "Y";
                }
            }
        }

        List<UUID> activeTestcaseIds = testCaseRepo
            .findAllActiveTestcaseOfProblem(problemId)
            .stream()
            .map(TestCaseEntity::getTestCaseId)
            .collect(Collectors.toList());

        List<ContestSubmissionTestCaseEntity> submissionTestcases = contestSubmissionTestCaseEntityRepo.findAllByContestSubmissionId(
            (submissionId));

        Map<UUID, ContestSubmissionTestCaseEntity> mapTestcaseIdToLatestSubmission = getMapTestcaseToLatestSubmission(
            activeTestcaseIds,
            submissionTestcases);

        List<SubmissionDetailByTestcaseOM> result = new ArrayList<>();
        for (ContestSubmissionTestCaseEntity st : mapTestcaseIdToLatestSubmission.values()) {
            String testCaseContent = "";
            String testCaseOutput = "";
            String participantSolutionOutput = "";

            TestCaseEntity tc = testCaseRepo.findTestCaseByTestCaseId(st.getTestCaseId());
            if (contest != null && tc != null) {
                switch (contest.getParticipantViewResultMode()) {
                    case ContestEntity.CONTEST_PARTICIPANT_VIEW_TESTCASE_DETAIL_ENABLED:
                        testCaseContent = tc.getTestCase();
                        testCaseOutput = tc.getCorrectAnswer();
                        participantSolutionOutput = st.getParticipantSolutionOutput();
                        break;

                    case ContestEntity.CONTEST_PARTICIPANT_VIEW_TESTCASE_DETAIL_DISABLED:
                        if (tc.getIsPublic().equals("Y")) {
                            testCaseContent = tc.getTestCase();
                            testCaseOutput = tc.getCorrectAnswer();
                            participantSolutionOutput = st.getParticipantSolutionOutput();

                        } else {
                            testCaseContent = "---HIDDEN---";
                            testCaseOutput = "---HIDDEN---";
                            participantSolutionOutput = "---HIDDEN---";
                        }
                        break;
                    case ContestEntity.CONTEST_PARTICIPANT_VIEW_TESTCASE_DETAIL_INPUT_PARTICIPANT_OUTPUT:
                        testCaseContent = tc.getTestCase();
                        testCaseOutput = "---HIDDEN---";
                        if (tc.getIsPublic().equals("Y")) {
                            testCaseOutput = tc.getCorrectAnswer();
                        }
                        participantSolutionOutput = st.getParticipantSolutionOutput();
                        break;
                }
                //String graded = ContestSubmissionTestCaseEntity.USED_TO_GRADE_YES;

                result.add(new SubmissionDetailByTestcaseOM(
//                    st.getContestSubmissionTestcaseId(),
//                    st.getContestId(),
//                    st.getProblemId(),
//                    st.getSubmittedByUserLoginId(),
                    st.getTestCaseId(),
                    testCaseContent,
                    st.getStatus(),
                    st.getPoint(),
                    st.getUsedToGrade(),
                    st.getRuntime(),
                    st.getMemoryUsage(),
                    testCaseOutput,
                    participantSolutionOutput,
                    null,
                    st.getCreatedStamp(),
                    viewSubmitSolutionOutputMode
                ));
            }
        }

        return result;
    }

    @Transactional
    @Override
    public ModelContestSubmissionResponse submitContestProblemTestCaseByTestCaseWithFile(
        ModelContestSubmission modelContestSubmission,
        String userId, String submittedByUserId
    ) {
        Date submitTime = new Date();
        ContestSubmissionEntity submission = ContestSubmissionEntity.builder()
                                                                    .contestId(modelContestSubmission.getContestId())
                                                                    .problemId(modelContestSubmission.getProblemId())
                                                                    .sourceCode(modelContestSubmission.getSource())
                                                                    .sourceCodeLanguage(modelContestSubmission.getLanguage())
                                                                    .status(ContestSubmissionEntity.SUBMISSION_STATUS_EVALUATION_IN_PROGRESS)
                                                                    .point(0L)
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
    public ModelContestSubmissionResponse submitContestProblemNotExecuteDueToForbiddenInstructions(
        ModelContestSubmission modelContestSubmission,
        String userName,
        String submittedByUserId
    ) throws Exception {

        Date submitTime = new Date();
        ContestSubmissionEntity submission = ContestSubmissionEntity.builder()
                                                                    .contestId(modelContestSubmission.getContestId())
                                                                    .problemId(modelContestSubmission.getProblemId())
                                                                    .sourceCode(modelContestSubmission.getSource())
                                                                    .sourceCodeLanguage(modelContestSubmission.getLanguage())
                                                                    .status(ContestSubmissionEntity.SUBMISSION_STATUS_NOT_EVALUATED_FORBIDDEN_INSTRUCTIONS)
                                                                    .point(0L)
                                                                    .problemId(modelContestSubmission.getProblemId())
                                                                    .userId(userName)
                                                                    .submittedByUserId(submittedByUserId)
                                                                    .runtime(0L)
                                                                    .createdAt(submitTime)
                                                                    .build();
        log.info("submitContestProblemNotExecuteDueToForbiddenInstructions, save submission to DB");
        submission = contestSubmissionRepo.saveAndFlush(submission);

        return ModelContestSubmissionResponse.builder()
                                             .status("NO_EVALUATION_FORBIDDEN_INSTRUCTIONS")
                                             .message("Submission is not evaluated due to forbidden instructions")
                                             .build();
    }

    /**
     * @param modelContestSubmission
     * @param userName
     * @param submittedByUserId
     * @return
     */
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
                                                           .point(0L)
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

        for (int i = 0; i < testCaseEntityList.size(); i++) {

            ContestSubmissionTestCaseEntity cste = null;
            cste = ContestSubmissionTestCaseEntity.builder()
                                                  .contestId(contestId)
                                                  .problemId(problemId)
                                                  .contestSubmissionId(c.getContestSubmissionId())
                                                  .testCaseId(testCaseEntityList.get(i).getTestCaseId())
                                                  .submittedByUserLoginId(userName)
                                                  .point(0)
                                                  .status("N/A")
                                                  .participantSolutionOutput("")
                                                  .runtime(null)
                                                  .createdStamp(new Date())
                                                  .build();
            cste = contestSubmissionTestCaseEntityRepo.save(cste);
            //  log.info("submitContestProblemStoreOnlyNotExecute, save submission_testcase " + cste.getTestCaseId() + " submission " + cste.getContestSubmissionId());

        }


        //log.info("c {}", c.getRuntime());
        return ModelContestSubmissionResponse.builder()
                                             .status("STORED")
                                             .testCasePass(c.getTestCasePass())
                                             //.runtime(new Long(0))
                                             .runtime(0)
                                             .memoryUsage(c.getMemoryUsage())
                                             .problemName("")
                                             .contestSubmissionID(c.getContestSubmissionId())
                                             .submittedAt(c.getCreatedAt())
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

//    @Transactional
//    @Override
//    public ModelContestSubmissionResponse submitSolutionOutput(
//        String solutionOutput,
//        String contestId,
//        String problemId,
//        UUID testCaseId,
//        String userName
//    ) throws Exception {
//        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
//
//        UserRegistrationContestEntity userRegistrationContest = null;
//        List<UserRegistrationContestEntity> userRegistrationContests = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
//            contestId, userName, Constants.RegistrationType.SUCCESSFUL.getValue());
//        if (userRegistrationContests != null && userRegistrationContests.size() > 0) {
//            userRegistrationContest = userRegistrationContests.get(0);
//        }
//
//        //  log.info("submitSolutionOutput, userRegistrationContest {}", userRegistrationContest);
//        if (userRegistrationContest == null) {
//            throw new MiniLeetCodeException("User not register contest");
//        }
//        TestCaseEntity testCase = testCaseRepo.findTestCaseByTestCaseId(testCaseId);
//        Judge0Submission response = judgeSubmissionTestCaseOutput(
//            problemEntity.getSolutionCheckerSourceCode(),
//            problemEntity.getSolutionCheckerSourceLanguage(),
//            solutionOutput,
//            testCase,
//            1000000,
//            problemEntity.getMemoryLimit());
//
//
//        //  log.info("submitSolutionOutput, response = " + response);
//
//        ProblemSubmission problemSubmission = StringHandler.handleContestResponseSubmitSolutionOutputOneTestCase(
//            response,
//            testCase.getTestCasePoint());
//
//        String participantAns = "";
//        if (problemSubmission.getParticipantAns() != null && !problemSubmission.getParticipantAns().isEmpty()) {
//            participantAns = problemSubmission.getParticipantAns().get(0);
//        }
//        ContestSubmissionTestCaseEntity cste = ContestSubmissionTestCaseEntity.builder()
//                                                                              .contestId(contestId)
//                                                                              .problemId(problemId)
//                                                                              .testCaseId(testCase.getTestCaseId())
//                                                                              .submittedByUserLoginId(userName)
//                                                                              .point(problemSubmission.getScore())
//                                                                              .status(problemSubmission.getStatus())
//                                                                              .participantSolutionOutput(participantAns)
//                                                                              .runtime(problemSubmission.getRuntime())
//                                                                              .createdStamp(new Date())
//                                                                              .build();
//        cste = contestSubmissionTestCaseEntityRepo.save(cste);
//
//        return ModelContestSubmissionResponse.builder()
//                                             .status(problemSubmission.getStatus())
//                                             .testCasePass("1/1")
//                                             .runtime(problemSubmission.getRuntime())
//                                             .memoryUsage((float) 0.0)
//                                             .problemName(problemEntity.getProblemName())
//                                             .contestSubmissionID(null)
//                                             .submittedAt(new Date())
//                                             .score((long) problemSubmission.getScore())
//                                             .build();
//
//    }
//
//    @Override
//    public ModelContestSubmissionResponse submitSolutionOutputOfATestCase(
//        String userId,
//        String solutionOutput,
//        ModelSubmitSolutionOutputOfATestCase m
//    ) {
//        ModelContestSubmissionResponse res = new ModelContestSubmissionResponse();
//        ContestSubmissionEntity sub = contestSubmissionRepo.findContestSubmissionEntityByContestSubmissionId(m.getSubmissionId());
//        if (sub == null) {
//            return res;
//        }
//        String contestId = sub.getContestId();
//        String problemId = sub.getProblemId();
//        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
//
//        UserRegistrationContestEntity userRegistrationContest = null;
//        List<UserRegistrationContestEntity> userRegistrationContests = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
//            contestId, userId, Constants.RegistrationType.SUCCESSFUL.getValue());
//        if (userRegistrationContests != null && userRegistrationContests.size() > 0) {
//            userRegistrationContest = userRegistrationContests.get(0);
//        }
//
//        //log.info("submitSolutionOutput, userRegistrationContest {}", userRegistrationContest);
//        if (userRegistrationContest == null) {
//            return res;
//        }
//        TestCaseEntity testCase = testCaseRepo.findTestCaseByTestCaseId(m.getTestCaseId());
//        try {
//            Judge0Submission response = judgeSubmissionTestCaseOutput(
//                problemEntity.getSolutionCheckerSourceCode(),
//                problemEntity.getSolutionCheckerSourceLanguage(),
//                solutionOutput,
//                testCase,
//                1000000,
//                problemEntity.getMemoryLimit());
//
//            //   log.info("submitSolutionOutput, response = " + response);
//            ProblemSubmission problemSubmission = StringHandler.handleContestResponseSubmitSolutionOutputOneTestCase(
//                response,
//                testCase.getTestCasePoint());
//
//            String participantAns = "";
//            if (problemSubmission.getParticipantAns() != null && !problemSubmission.getParticipantAns().isEmpty()) {
//                participantAns = problemSubmission.getParticipantAns().get(0);
//            }
//            ContestSubmissionTestCaseEntity cste = null;
//            List<ContestSubmissionTestCaseEntity> l_cste = contestSubmissionTestCaseEntityRepo
//                .findAllByContestSubmissionIdAndTestCaseId(sub.getContestSubmissionId(), m.getTestCaseId());
//            long subPoint = sub.getPoint();
//            if (l_cste != null && !l_cste.isEmpty()) {
//                cste = l_cste.get(0);
//                subPoint = subPoint - cste.getPoint();// reduce point of submission by old point of test-case
//                cste.setPoint(problemSubmission.getScore());
//                cste.setStatus(problemSubmission.getStatus());
//                cste.setParticipantSolutionOutput(solutionOutput);
//                cste.setRuntime(problemSubmission.getRuntime());
//                cste.setCreatedStamp(new Date());
//            } else {
//                cste = ContestSubmissionTestCaseEntity.builder()
//                                                      .contestId(contestId)
//                                                      .problemId(problemId)
//                                                      .contestSubmissionId(sub.getContestSubmissionId())
//                                                      .testCaseId(testCase.getTestCaseId())
//                                                      .submittedByUserLoginId(userId)
//                                                      .point(problemSubmission.getScore())
//                                                      .status(problemSubmission.getStatus())
//                                                      .participantSolutionOutput(participantAns)
//                                                      .runtime(problemSubmission.getRuntime())
//                                                      .createdStamp(new Date())
//                                                      .build();
//            }
//
//            cste = contestSubmissionTestCaseEntityRepo.save(cste);
//
//            subPoint = subPoint + cste.getPoint(); // update Point;
//            sub.setPoint(subPoint);
//            sub.setStatus(ContestSubmissionEntity.SUBMISSION_STATUS_PARTIAL);
//            sub = contestSubmissionRepo.save(sub);
//
//            return ModelContestSubmissionResponse.builder()
//                                                 .contestId(contestId)
//                                                 .problemId(problemId)
//                                                 .contestSubmissionID(sub.getContestSubmissionId())
//                                                 .selectedTestCaseId(m.getTestCaseId())
//                                                 .status(problemSubmission.getStatus())
//                                                 .testCasePass("1/1")
//                                                 .runtime(problemSubmission.getRuntime())
//                                                 .memoryUsage((float) 0.0)
//                                                 .problemName(problemEntity.getProblemName())
//                                                 .submittedAt(new Date())
//                                                 .score((long) problemSubmission.getScore())
//                                                 .build();
//
//        } catch (Exception e) {
//            return res;
//        }
//
//    }

    @Override
    public ModelStudentRegisterContestResponse studentRegisterContest(
        String contestId,
        String userId
    ) throws MiniLeetCodeException {
        ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
        UserRegistrationContestEntity existed = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndRoleId(
            contestId,
            userId,
            UserRegistrationContestEntity.ROLE_PARTICIPANT);

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
        int cnt = 0;
        if (contestEntity.getUserId() == null || !contestEntity.getUserId().equals(teacherId)) {
            throw new MiniLeetCodeException(teacherId +
                                            " does not have privilege to manage contest " +
                                            modelTeacherManageStudentRegisterContest.getContestId());
        }
        List<UserRegistrationContestEntity> userRegistrationContestEntities = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserId(
            modelTeacherManageStudentRegisterContest.getContestId(),
            modelTeacherManageStudentRegisterContest.getUserId());

        UserRegistrationContestEntity userRegistrationContestEntity = null;
        if(userRegistrationContestEntities != null && userRegistrationContestEntities.size() > 0)
            userRegistrationContestEntity= userRegistrationContestEntities.get(0);

        if (Constants.RegisterCourseStatus.SUCCESSES
            .getValue()
            .equals(modelTeacherManageStudentRegisterContest.getStatus())) {
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
    public ModelGetContestPageResponse getAllContestsPagingByAdmin(String userName, Pageable pageable) {
        Page<ContestEntity> contestEntities = contestPagingAndSortingRepo.findAll(pageable);
        long count = contestPagingAndSortingRepo.count();
        return getModelGetContestPageResponse(contestEntities, count);
    }

    @Override
    public List<ModelGetContestResponse> getManagedContestOfTeacher(String userName) {
        List<String> roles = new ArrayList<>();
        roles.add(UserRegistrationContestEntity.ROLE_OWNER);
        roles.add(UserRegistrationContestEntity.ROLE_MANAGER);
        List<UserRegistrationContestEntity> userRegistrationContestList = userRegistrationContestRepo.findAllByUserIdAndRoleIdIn(
            userName,
            roles);

        Map<String, List<String>> mapContestIdToRoleList = new HashMap<>();
        for (UserRegistrationContestEntity userRegistrationContest : userRegistrationContestList) {
            String contestId = userRegistrationContest.getContestId();
            String role = userRegistrationContest.getRoleId();
            mapContestIdToRoleList.computeIfAbsent(contestId, k -> new ArrayList<>())
                                  .add(role);
        }

        Map<String, String> mapContestIdToRoleListString = new HashMap<>();
        mapContestIdToRoleList.forEach((contestId, roleList) -> {
            String rolesString = String.join(", ", roles);
            mapContestIdToRoleListString.put(contestId, rolesString);
        });

        Set<String> contestIds = mapContestIdToRoleListString.keySet();
        List<ContestEntity> contests = contestRepo.findByContestIdInAndStatusIdNot(
            contestIds,
            ContestEntity.CONTEST_STATUS_DISABLED);
        List<ModelGetContestResponse> res = contests.stream()
                                                    .map(contest -> ModelGetContestResponse.builder()
                                                                                           .contestId(contest.getContestId())
                                                                                           .contestName(contest.getContestName())
                                                                                           .startAt(contest.getStartedAt())
                                                                                           .statusId(contest.getStatusId())
                                                                                           .userId(contest.getUserId())
                                                                                           .roleId(
                                                                                               mapContestIdToRoleListString.get(
                                                                                                   contest.getContestId()))
                                                                                           .build())
                                                    .collect(Collectors.toList());

        res.sort((a, b) -> {
            if (a.getStartAt() == null && b.getStartAt() == null) {
                return 0;
            } else if (a.getStartAt() == null) {
                return 1;
            } else if (b.getStartAt() == null) {
                return -1;
            } else {
                return b.getStartAt().compareTo(a.getStartAt());
            }
        });

        return res;
    }

    @Override
    public List<ModelGetContestResponse> getAllContests(String userName) {
        /*
        List<String> roles = new ArrayList<>();
        roles.add(UserRegistrationContestEntity.ROLE_OWNER);
        roles.add(UserRegistrationContestEntity.ROLE_MANAGER);
        List<UserRegistrationContestEntity> userRegistrationContestList = userRegistrationContestRepo.findAllByUserIdAndRoleIdIn(userName, roles);

        Map<String, List<String>> mapContestIdToRoleList = new HashMap<>();
        for (UserRegistrationContestEntity userRegistrationContest : userRegistrationContestList) {
            String contestId = userRegistrationContest.getContestId();
            String role = userRegistrationContest.getRoleId();
            mapContestIdToRoleList.computeIfAbsent(contestId, k -> new ArrayList<>())
                                  .add(role);
        }

        Map<String, String> mapContestIdToRoleListString = new HashMap<>();
        mapContestIdToRoleList.forEach((contestId, roleList) -> {
            String rolesString = String.join(", ", roles);
            mapContestIdToRoleListString.put(contestId, rolesString);
        });
        */

        List<ModelGetContestResponse> res = new ArrayList<>();
        List<ContestEntity> allContests = contestRepo.findAll();
        for (ContestEntity contest : allContests) {
            ModelGetContestResponse modelGetContestResponse = ModelGetContestResponse.builder()
                                                                                     .contestId(contest.getContestId())
                                                                                     .contestName(contest.getContestName())
                                                                                     .startAt(contest.getStartedAt())
                                                                                     .statusId(contest.getStatusId())
                                                                                     .userId(contest.getUserId())
                                                                                     .roleId("")
                                                                                     .build();
            res.add(modelGetContestResponse);
        }
        /*
        for (Map.Entry<String, String> e : mapContestIdToRoleListString.entrySet()) {
            ContestEntity contest = contestRepo.findContestByContestId(e.getKey());
            if(contest.getStatusId().equals(ContestEntity.CONTEST_STATUS_DISABLED)){
                continue;
            }
            ModelGetContestResponse modelGetContestResponse = ModelGetContestResponse.builder()
                                                                                     .contestId(contest.getContestId())
                                                                                     .contestName(contest.getContestName())
                                                                                     .startAt(contest.getStartedAt())
                                                                                     .statusId(contest.getStatusId())
                                                                                     .userId(contest.getUserId())
                                                                                     .roleId(e.getValue())
                                                                                     .build();
            res.add(modelGetContestResponse);
        }
        */
        res.sort((a, b) -> {
            if (a.getStartAt() == null && b.getStartAt() == null) {
                return 0;
            } else if (a.getStartAt() == null) {
                return 1;
            } else if (b.getStartAt() == null) {
                return -1;
            } else {
                return b.getStartAt().compareTo(a.getStartAt());
            }
        });
        return res;
    }

    @Override
    public ListModelUserRegisteredContestInfo getListUserRegisterContestSuccessfulPaging(
        Pageable pageable,
        String contestId
    ) {
        Page<ModelUserRegisteredClassInfo> list = userRegistrationContestPagingAndSortingRepo.getAllUserRegisteredByContestIdAndStatusInfo(
            pageable,
            contestId,
            Constants.RegistrationType.SUCCESSFUL.getValue());
        return ListModelUserRegisteredContestInfo.builder()
                                                 .contents(list)
                                                 .build();
    }

    @Override
    public List<ContestMembers> getListMemberOfContest(String contestId) {
////       BUG: This implementation meet N+1 problem
//        List<UserRegistrationContestEntity> lst = userRegistrationContestRepo.findAllByContestIdAndStatus(
//            contestId,
//            UserRegistrationContestEntity.STATUS_SUCCESSFUL);
//
//        List<ModelMemberOfContestResponse> res = new ArrayList<>();
//        for (UserRegistrationContestEntity u : lst) {
//            ModelMemberOfContestResponse m = new ModelMemberOfContestResponse();
//            m.setId(u.getId());
//            m.setContestId(contestId);
//            m.setUserId(u.getUserId());
//            m.setRoleId(u.getRoleId());
//            m.setFullName(userService.getUserFullName(u.getUserId()));
//            m.setLastUpdatedDate(u.getLastUpdated());
//            m.setUpdatedByUserId(u.getUpdatedByUserLogin_id());
//            m.setPermissionId(u.getPermissionId());
//            res.add(m);
//        }
//
//        return res;

        return userRegistrationContestRepo.findByContestIdAndStatus(
            contestId,
            UserRegistrationContestEntity.STATUS_SUCCESSFUL);
    }

    @Override
    public List<ModelMemberOfContestResponse> getListMemberOfContestGroup(String contestId, String userId) {
        List<UserRegistrationContestEntity> lst = userRegistrationContestRepo.findAllInGroupByContestIdAndStatus(
            contestId, userId,
            UserRegistrationContestEntity.STATUS_SUCCESSFUL);
        List<ModelMemberOfContestResponse> res = new ArrayList<>();
        for (UserRegistrationContestEntity u : lst) {
            ModelMemberOfContestResponse m = new ModelMemberOfContestResponse();
            m.setId(u.getId());
            m.setContestId(contestId);
            m.setUserId(u.getUserId());
            m.setRoleId(u.getRoleId());
            m.setFullName(userService.getUserFullName(u.getUserId()));
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
        List<ModelMemberOfContestResponse> res = new ArrayList<>();
        for (UserRegistrationContestEntity u : lst) {
            ModelMemberOfContestResponse m = new ModelMemberOfContestResponse();
            m.setId(u.getId());
            m.setContestId(contestId);
            m.setUserId(u.getUserId());
            m.setRoleId(u.getRoleId());
            m.setFullName(userService.getUserFullName(u.getUserId()));
            res.add(m);
        }
        return res;
    }

    // TODO: try approach one join query
    @Override
    public ModelGetContestPageResponse getRegisteredContestsByUser(String userId) {
        List<UserRegistrationContestEntity> registrations = userRegistrationContestRepo
            .findAllByUserIdAndRoleIdAndStatus(
                userId,
                UserRegistrationContestEntity.ROLE_PARTICIPANT,
                UserRegistrationContestEntity.STATUS_SUCCESSFUL);

        List<ModelGetContestResponse> res = new ArrayList<>();
        if (registrations != null) {
            Set<String> contestIds = registrations
                .stream()
                .map(UserRegistrationContestEntity::getContestId)
                .collect(Collectors.toSet());

            List<ContestEntity> contests = contestRepo.findByContestIdInAndStatusIdNot(
                contestIds,
                ContestEntity.CONTEST_STATUS_DISABLED);

            res = contests.stream()
                          .map(contest -> ModelGetContestResponse.builder()
                                                                 .contestId(contest.getContestId())
                                                                 .contestName(contest.getContestName())
                                                                 .contestTime(contest.getContestSolvingTime())
                                                                 .countDown(contest.getCountDown())
                                                                 .startAt(contest.getStartedAt())
                                                                 .statusId(contest.getStatusId())
                                                                 .userId(contest.getUserId())
                                                                 .createdAt(contest.getCreatedAt())
                                                                 .build())
                          .collect(Collectors.toList());
        }

        Collections.reverse(res);
        return ModelGetContestPageResponse.builder()
                                          .contests(res)
                                          .build();
    }

    @Override
    public ModelGetContestPageResponse getNotRegisteredContestByUser(Pageable pageable, String userName) {
        Page<ContestEntity> list = userRegistrationContestPagingAndSortingRepo.getNotRegisteredContestByUserLogin(
            pageable,
            userName);
        long count = userRegistrationContestPagingAndSortingRepo.getNumberOfNotRegisteredContestByUserLogin(userName);
        return getModelGetContestPageResponse(list, count);
    }

    @Override
    public List<ContestSubmissionsByUser> getRankingByContestIdNew(
        String contestId,
        Constants.GetPointForRankingType getPointForRankingType
    ) {
        List<String> userIds = userRegistrationContestRepo
            .getAllUserIdsInContest(contestId, Constants.RegistrationType.SUCCESSFUL.getValue())
            .stream()
            .distinct()
            .collect(Collectors.toList());

        ContestEntity contest = contestRepo.findContestByContestId(contestId);
        List<String> problemIds = new ArrayList<>();

        List<ContestProblem> contestProblems = contestProblemRepo.findAllByContestId(contestId);
        if (contestProblems != null) {
            for (ContestProblem cp : contestProblems) {
                if (cp.getSubmissionMode() != null) {
                    if (!cp.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_HIDDEN)) {
                        problemIds.add(cp.getProblemId());
                    }
                }
            }
        }


        /*
        List<String> problemIds = contestRepo
            .findContestByContestId(contestId)
            .getProblems()
            .stream()
            .map(ProblemEntity::getProblemId)
            .collect(Collectors.toList());
        */
        LinkedHashMap<String, String> mapProblemIdToProblemName = new LinkedHashMap<>();
        for (ContestProblem contestProblem : contestProblemRepo.findAllByContestId(contestId)) {
            mapProblemIdToProblemName.put(contestProblem.getProblemId(), contestProblem.getProblemRename());
        }

        int nbProblems = problemIds.size();

        HashMap<String, Long> mProblem2MaxPoint = new HashMap<>();
        for (String problemId : problemIds) {
            long totalPoint = 0;
            List<TestCaseEntity> TC = testCaseRepo.findAllByProblemId(problemId);
            for (TestCaseEntity tc : TC) {
                if (contest.getEvaluateBothPublicPrivateTestcase().equals("Y")) {
                    totalPoint += tc.getTestCasePoint();
                } else {
                    if (tc.getIsPublic().equals("N")) {
                        totalPoint += tc.getTestCasePoint();
                    }
                }
            }
            mProblem2MaxPoint.put(problemId, totalPoint);
        }

        List<ContestSubmissionsByUser> listContestSubmissionsByUser = new ArrayList<>();
        for (String userId : userIds) {
            ContestSubmissionsByUser contestSubmission = new ContestSubmissionsByUser();
            contestSubmission.setUserId(userId);

            LinkedHashMap<String, Long> mapProblemToPoint = new LinkedHashMap<>();
            HashMap<String, Double> mapProblem2PointPercentage = new HashMap<>();

            for (String problemId : problemIds) {
                mapProblemToPoint.put(problemId, 0L);
            }

            List<ModelSubmissionInfoRanking> submissionsByUser = new ArrayList<>();

            switch (getPointForRankingType) {
                case HIGHEST:
                    submissionsByUser = contestSubmissionRepo.getHighestSubmissions(userId, contestId);
                    break;

                case LATEST:
                    submissionsByUser = contestSubmissionRepo.getLatestSubmissions(userId, contestId);
                    break;
            }
            //log.info("getRankingByContestIdNew, submisionByUser.sz = " + submissionsByUser.size());

            for (ModelSubmissionInfoRanking submission : submissionsByUser) {
                //log.info("getRankingByContestIdNew, submisionByUser, point = " + submission.getPoint());
                String problemId = submission.getProblemId();
                if (mapProblemToPoint.containsKey(problemId)) {
                    mapProblemToPoint.put(problemId, submission.getPoint());
                }
                long problemPoint = 0;
                if (mProblem2MaxPoint.get(problemId) != null) {
                    problemPoint = mProblem2MaxPoint.get(problemId);
                }
                double percentage = 0;
                if (problemPoint > 0) {
                    percentage = submission.getPoint() * 1.0 / problemPoint;
                    System.out.println("RANKING, problem " +
                                       problemId +
                                       " problemPoint = " +
                                       problemPoint +
                                       " submission points = " +
                                       submission.getPoint());
                }
                mapProblem2PointPercentage.put(problemId, percentage);
            }

            long totalPoint = 0;
            double totalPercentage = 0;

            List<ModelSubmissionInfoRanking> mapProblemsToPoints = new ArrayList<>();
            for (Map.Entry entry : mapProblemToPoint.entrySet()) {
                ModelSubmissionInfoRanking tmp = new ModelSubmissionInfoRanking();
                String problemId = entry.getKey().toString();
                tmp.setProblemId(mapProblemIdToProblemName.get(problemId));
                tmp.setPoint((Long) entry.getValue());
                mapProblemsToPoints.add(tmp);
                totalPoint += tmp.getPoint();

                double percent = 0;
                if (mapProblem2PointPercentage.get(problemId) != null) {
                    percent = mapProblem2PointPercentage.get(problemId);
                }
                totalPercentage = totalPercentage + percent;
                System.out.println("RANKING, problem " +
                                   problemId +
                                   " percent = " +
                                   percent +
                                   " total percent = " +
                                   totalPercentage);
                tmp.setPointPercentage(percent);
            }
            if (nbProblems > 0) {
                totalPercentage = totalPercentage * 100 / nbProblems;
                System.out.println("RANKING, nbProblem = " + nbProblems + " total percent = " + totalPercentage);
            }

            //contestSubmission.setFullname(userService.getUserFullName(userId));
            contestSubmission.setFullname(getUserFullNameOfContest(contestId,userId));
            contestSubmission.setMapProblemsToPoints(mapProblemsToPoints);
            contestSubmission.setTotalPoint(totalPoint);
            contestSubmission.setTotalPercentagePoint(totalPercentage);
            contestSubmission.setStringTotalPercentagePoint(String.format("%,.2f", totalPercentage));
            listContestSubmissionsByUser.add(contestSubmission);
        }

        return listContestSubmissionsByUser;
    }

    @Override
    public List<ContestSubmissionsByUser> getRankingGroupByContestIdNew(
        String userId,
        String contestId,
        Constants.GetPointForRankingType getPointForRankingType
    ) {
        List<ContestSubmissionsByUser> listContestSubmissionsByUser = getRankingByContestIdNew(
            contestId,
            getPointForRankingType);
        List<ContestSubmissionsByUser> selectedlistContestSubmissionsByUser = new ArrayList<>();
        List<ContestUserParticipantGroup> contestUserParticipantGroups = contestUserParticipantGroupRepo
            .findAllByContestIdAndUserId(contestId, userId);
        HashSet<String> participantIds = new HashSet<>();
        for (ContestUserParticipantGroup e : contestUserParticipantGroups) {
            participantIds.add(e.getParticipantId());
        }
        for (ContestSubmissionsByUser e : listContestSubmissionsByUser) {
            if (participantIds.contains(e.getUserId())) {
                selectedlistContestSubmissionsByUser.add(e);
            }
        }
        return selectedlistContestSubmissionsByUser;
    }

//    @Override
//    public Page<ProblemEntity> getPublicProblemPaging(Pageable pageable) {
//        return problemPagingAndSortingRepo.findAllByPublicIs(pageable);
//    }

    @Override
    public Page<ModelGetTestCaseDetail> getTestCaseByProblem(String problemId, TestCaseFilter filter) {
        Pageable pageable = CommonUtils.getPageable(
            filter.getPage(),
            filter.getSize(),
            Sort.by("lastUpdatedStamp").descending());

        if (filter.getFullView() != null && filter.getFullView()) {
            return testCaseRepo.getFullByProblemId(problemId, filter.getPublicOnly(), pageable);
        } else {
            return testCaseRepo.getPreviewByProblemId(problemId, pageable);
        }
    }

    @Override
    public TestCaseDetailProjection getTestCaseDetail(UUID testCaseId) {
        TestCaseDetailProjection testCase = testCaseRepo.getTestCaseDetailByTestCaseId(
            testCaseId,
            1_048_576); // 1MB, align with reality
        if (testCase == null) {
            throw new EntityNotFoundException("Test case not found");
        } else {
            return testCase;
        }
    }

//    @Override
//    public void editTestCase(UUID testCaseId, ModelSaveTestcase modelSaveTestcase) throws MiniLeetCodeException {
//        TestCaseEntity testCase = testCaseRepo.findTestCaseByTestCaseId(testCaseId);
//        if (testCase == null) {
//            throw new MiniLeetCodeException("test case not found");
//        }
//
//        testCase.setTestCase(modelSaveTestcase.getInput());
//        testCase.setCorrectAnswer(modelSaveTestcase.getResult());
//        testCase.setTestCasePoint(modelSaveTestcase.getPoint());
//        testCase.setIsPublic(modelSaveTestcase.getIsPublic());
//        testCaseService.saveTestCaseWithCache(testCase);
//    }

    @Override
    public ModelAddUserToContestResponse addUserToContest(ModelAddUserToContest modelAddUserToContest) {
        String contestId = modelAddUserToContest.getContestId();
        String userId = modelAddUserToContest.getUserId();
        String role = modelAddUserToContest.getRole();

        ModelAddUserToContestResponse response = new ModelAddUserToContestResponse();
        response.setUserId(userId);
        response.setRoleId(role);

        if (userLoginRepo.findByUserLoginId(userId) == null) {
            response.setStatus("User not found");
            return response;
        }

        UserRegistrationContestEntity userRegistrationContest = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndRoleId(contestId, userId, role);


        if (userRegistrationContest != null &&
            userRegistrationContest.getStatus().equals(Constants.RegistrationType.SUCCESSFUL.getValue())) {
            response.setStatus("Added");
            return response;
        }


        if (userRegistrationContest == null) {
            userRegistrationContestRepo.save(UserRegistrationContestEntity.builder()
                                                                          .contestId(modelAddUserToContest.getContestId())
                                                                          .userId(modelAddUserToContest.getUserId())
                                                                          .status(Constants.RegistrationType.SUCCESSFUL.getValue())
                                                                          .roleId(modelAddUserToContest.getRole())
                                                                            .fullname(modelAddUserToContest.getFullname())
                                                                          .permissionId(UserRegistrationContestEntity.PERMISSION_SUBMIT)
                                                                          .build());
        } else {
            userRegistrationContest.setStatus(Constants.RegistrationType.SUCCESSFUL.getValue());
            userRegistrationContestRepo.save(userRegistrationContest);
        }
        response.setStatus("Successful");
        return response;
    }

    @Override
    public ModelAddUserToContestResponse updateUserFullnameOfContest(ModelAddUserToContest modelAddUserToContest) {
        String contestId = modelAddUserToContest.getContestId();
        String userId = modelAddUserToContest.getUserId();
        String role = modelAddUserToContest.getRole();
        String fullname = modelAddUserToContest.getFullname();

        ModelAddUserToContestResponse response = new ModelAddUserToContestResponse();
        response.setUserId(userId);
        response.setRoleId(role);
        response.setFullname(fullname);

        if (userLoginRepo.findByUserLoginId(userId) == null) {
            response.setStatus("User not found");
            return response;
        }

        List<UserRegistrationContestEntity> userRegistrationContests = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserId(contestId, userId);
        if(userRegistrationContests != null){
            for(UserRegistrationContestEntity u: userRegistrationContests){
                u.setFullname(modelAddUserToContest.getFullname());
                u = userRegistrationContestRepo.save(u);
            }
        }

        response.setStatus("Successful");
        return response;

    }

    @Transactional
    @Override
    public void addUsers2ToContest(String contestId, AddUsers2Contest addUsers2Contest) {
        addUsers2Contest
            .getUserIds()
            .stream()
            .forEach(userId -> addUserToContest(new ModelAddUserToContest(
                contestId,
                userId,
                addUsers2Contest.getRole(),"")));
    }

    @Override
    public ModelAddUserToContestGroupResponse addUserToContestGroup(ModelAddUserToContestGroup modelAddUserToContestGroup) {
        String contestId = modelAddUserToContestGroup.getContestId();
        String userId = modelAddUserToContestGroup.getUserId();
        String participantId = modelAddUserToContestGroup.getParticipantId();

        ModelAddUserToContestGroupResponse response = new ModelAddUserToContestGroupResponse();
        response.setUserId(userId);
        response.setParticipantId(participantId);

        if (userLoginRepo.findByUserLoginId(participantId) == null) {
            response.setStatus("User not found");
            return response;
        }

        ContestUserParticipantGroup cupg = contestUserParticipantGroupRepo
            .findByContestIdAndUserIdAndParticipantId(contestId, userId, participantId);

        if (cupg != null) {
            response.setStatus("Added");
            return response;
        }


        contestUserParticipantGroupRepo.save(ContestUserParticipantGroup.builder()
                                                                        .contestId(contestId)
                                                                        .userId(userId)
                                                                        .participantId(participantId)
                                                                        .build());
        response.setStatus("Successful");

        return response;
    }

    @Override
    public void deleteUserContest(ModelAddUserToContest modelAddUserToContest) throws MiniLeetCodeException {
        //UserRegistrationContestEntity userRegistrationContest = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserId(
        //    modelAddUserToContest.getContestId(),
        //    modelAddUserToContest.getUserId());
        UserRegistrationContestEntity userRegistrationContest = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserIdAndRoleId(
            modelAddUserToContest.getContestId(),
            modelAddUserToContest.getUserId(),modelAddUserToContest.getRole());

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
        List<ContestSubmission> retList = new ArrayList<>();
        Set<String> keys = new HashSet<>();
        for (ContestSubmissionEntity s : lst) {
            String k = s.getContestId() + "@" + s.getProblemId() + "@" + s.getUserId();
            keys.add(k);
            //  log.info("getNewestSubmissionResults, read record " + s.getContestSubmissionId() + " created stamp " + s.getCreatedAt());
        }
        Set<String> ignores = new HashSet<>();
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

    private String getUserFullNameOfContest(String contestId, String userId){
        String fullname = userRegistrationContestService.findUserFullnameOfContest(contestId,userId);
        if(fullname == null) fullname = userService.getUserFullName(userId);
        return fullname;
    }
    @Override
    public Page<ContestSubmission> findContestSubmissionByContestIdPaging(
        Pageable pageable,
        String contestId,
        String searchTerm
    ) {
        searchTerm = searchTerm.toLowerCase();
        return contestSubmissionPagingAndSortingRepo
            .searchSubmissionInContestPaging(contestId, searchTerm, searchTerm, pageable)
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
                //.problemName(problemService.getProblemName(contestSubmissionEntity.getProblemId()))
                .problemName(contestService.getProblemNameInContest(
                    contestSubmissionEntity.getContestId(),
                    contestSubmissionEntity.getProblemId()))
                .testCasePass(contestSubmissionEntity.getTestCasePass())
                .status(contestSubmissionEntity.getStatus())
                .managementStatus(contestSubmissionEntity.getManagementStatus())
                .violationForbiddenInstruction(contestSubmissionEntity.getViolateForbiddenInstruction())
                .violationForbiddenInstructionMessage(contestSubmissionEntity.getViolateForbiddenInstructionMessage())
                .message(contestSubmissionEntity.getMessage())
                .userId(contestSubmissionEntity.getUserId())
                //.fullname(userService.getUserFullName(contestSubmissionEntity.getUserId()))
                //.fullname(userRegistrationContestService.findUserFullnameOfContest(contestId,contestSubmissionEntity.getUserId()))
                .fullname(getUserFullNameOfContest(contestId,contestSubmissionEntity.getUserId()))
                .build());
    }

    @Override
    public Page<ContestSubmission> findContestGroupSubmissionByContestIdPaging(
        Pageable pageable, String contestId, String userId,
        String searchTerm
    ) {
        searchTerm = searchTerm.toLowerCase();
        log.info("findContestGroupSubmissionByContestIdPaging, contestId = " + contestId + " userId = " + userId);
        return contestSubmissionPagingAndSortingRepo
//            .findAllByContestId(pageable, contestId)
.searchSubmissionInContestGroupPaging(contestId, userId, searchTerm, searchTerm, pageable)
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
    .fullname(userService.getUserFullName(contestSubmissionEntity.getUserId()))
    .build());
    }

    @Override
    public ContestSubmissionEntity getContestSubmissionDetailForTeacher(UUID submissionId) {
        return contestSubmissionRepo.findContestSubmissionEntityByContestSubmissionId(submissionId);
    }

    @Override
    public ContestSubmissionEntity getContestSubmissionDetailForStudent(String userId, UUID submissionId) {
        ContestSubmissionEntity submission = contestSubmissionRepo.findContestSubmissionEntityByContestSubmissionId(
            submissionId);
        if (userId.equals(submission.getUserId())) {
            return submission;
        }
        throw new AccessDeniedException("No permission");
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

        List<UserContestProblemRole> problemRoles = userContestProblemRoleRepo.findAllByProblemIdAndUserId(
            testCase.getProblemId(),
            userId);

        boolean isAuthorized = problemRoles
            .stream()
            .anyMatch(problemRole -> problemRole.getRoleId().equals(UserContestProblemRole.ROLE_OWNER) ||
                                     problemRole.getRoleId().equals(UserContestProblemRole.ROLE_EDITOR));
        if (!isAuthorized) {
            throw new MiniLeetCodeException("permission denied");
        }

        testCase.setStatusId(TestCaseEntity.STATUS_DISABLED);
        testCaseService.saveTestCaseWithCache(testCase);

    }

    class CodeSimilatiryComparator implements Comparator<CodeSimilarityElement> {

        @Override
        public int compare(CodeSimilarityElement e1, CodeSimilarityElement e2) {
            return Double.compare(e2.getScore(), e1.getScore());
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

                    List<CodePlagiarism> codePlagiarisms = codePlagiarismRepo
                        .findAllByContestIdAndProblemIdAndUserId1AndUserId2(
                            contestId,
                            problemId,
                            s1.getUserId(),
                            s2.getUserId());
                    if (codePlagiarisms != null) {
                        for (CodePlagiarism cp : codePlagiarisms) {
                            cp.setScore(score);
                            cp.setCreatedStamp(new Date());
                            cp = codePlagiarismRepo.save(cp);
                        }
                    } else {

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
                        //log.info("checkSimilarity, add new item score = " + score);
                    }
                }
            }
        }

        Collections.sort(list, new CodeSimilatiryComparator());

        ModelCodeSimilarityOutput model = new ModelCodeSimilarityOutput();
        model.setCodeSimilarityElementList(list);
        return model;
    }

    @Override
    public int checkForbiddenInstructions(String contestId) {
        List<ContestProblem> contestProblems = contestProblemRepo.findAllByContestId(contestId);
        Map<String, List<String>> mPro2ForbiddenIns = new HashMap();
        for (ContestProblem cp : contestProblems) {
            // log.info("checkForbiddenInstructions, forbidden instructions = " + cp.getForbiddenInstructions());
            String[] forbiddens = cp.getForbiddenInstructions().split(",");
            if (forbiddens != null) {
                List<String> L = new ArrayList();
                for (String s : forbiddens) {
                    s = s.trim();
                    if (s != null && s != "" && !s.equals("") && s.length() > 0) {
                        L.add(s.trim());
                    }
                }
                mPro2ForbiddenIns.put(cp.getProblemId(), L);
                //log.info("checkForbiddenInstructions, forbidden list L = " + L.toString());
            }
        }
        List<ContestSubmissionEntity> submissions = contestSubmissionRepo.findAllByContestId(contestId);
        int cnt = 0;
        for (ContestSubmissionEntity sub : submissions) {
            String problemId = sub.getProblemId();
            String msg = "";
            if (mPro2ForbiddenIns.get(problemId) != null) {
                List<String> forbiddenFound = new ArrayList<String>();
                for (String f : mPro2ForbiddenIns.get(problemId)) {
                    //log.info("checkForbiddenInstructions, , sourcecode " + sub.getSourceCode() + " forbidden f = " + f);
                    if (sub.getSourceCode() != null) {
                        if (sub.getSourceCode().contains(f)) {
                            sub.setViolateForbiddenInstruction(ContestSubmissionEntity.VIOLATION_FORBIDDEN_YES);
                            //msg = msg + f + ",";
                            forbiddenFound.add(f);
                            cnt++;
                            //log.info("checkForbiddenInstructions, , sourcecode " + sub.getSourceCode() + " forbidden f = " + f + " DISCOVER violations!!!");

                        }
                    }
                }
                for (int i = 0; i < forbiddenFound.size(); i++) {
                    msg = msg + forbiddenFound.get(i);
                    if (i < forbiddenFound.size() - 1) {
                        msg = msg + " :: ";
                    }
                }
            }
            sub.setViolateForbiddenInstructionMessage(msg);
            contestSubmissionRepo.save(sub);
        }
        return cnt;
    }

    @Override
    public ModelCodeSimilarityOutput computeSimilarity(
        String userLoginId,
        String contestId,
        ModelCheckSimilarityInput I
    ) {
        List<CodeSimilarityElement> list = new ArrayList();
        ModelCodeSimilarityOutput model = new ModelCodeSimilarityOutput();
        List<UserRegistrationContestEntity> participants = userRegistrationContestRepo
            .findAllByContestIdAndStatus(contestId, UserRegistrationContestEntity.STATUS_SUCCESSFUL);

        ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
        List<ProblemEntity> problems = contestEntity.getProblems();

        for (ProblemEntity p : problems) {
            String problemId = p.getProblemId();
            //log.info("computeSimilarity, consider problem " + problemId + " threshold  = " + I.getThreshold());
            List<ContestSubmissionEntity> listSubmissions = new ArrayList();
            for (UserRegistrationContestEntity participant : participants) {
                String userId = participant.getUserId();
                //log.info("computeSimilarity, consider problem " + problemId + " participant " + userId);
                List<ContestSubmissionEntity> submissions = contestSubmissionRepo.findAllByContestIdAndUserIdAndProblemId(
                    contestId,
                    userId,
                    problemId);
                //log.info("computeSimilarity, consider problem " + problemId + " participant " + userId
                //+ " submissions.sz = " +
                // submissions.size() +
                //  "");

                if (submissions != null && submissions.size() > 0) {// take the last submission in the sorted list
                    for (ContestSubmissionEntity sub : submissions) {
                        //ContestSubmissionEntity sub = submissions.get(0);
                        //if(sub.getPoint() > 0)// consider only submissions having points
                        listSubmissions.add(sub);
                    }
                }
            }
            if (listSubmissions.size() > MAX_SUBMISSIONS_CHECKSIMILARITY) {
                if (!userLoginId.equals("admin")) {
                    model.setMessage("Too Many submissions, only admin can do this task");
                    return model;
                }
            }
            // SORT listSubmissions in an increasing order of userId
            Collections.sort(
                listSubmissions, new Comparator<ContestSubmissionEntity>() {
                    @Override
                    public int compare(ContestSubmissionEntity o1, ContestSubmissionEntity o2) {
                        return o1.getUserId().compareTo(o2.getUserId());
                    }
                });

            //log.info("computeSimilarity, consider problem " + problemId + " listSubmissions = " + listSubmissions.size());
            //for(ContestSubmissionEntity e: listSubmissions){
            //log.info("computeSimilarity, user " + e.getUserId() + " submissionId " + e.getContestSubmissionId() + " point " + e.getPoint());
            //}

            // check similarity of submissions to the current problemId
            for (int i = 0; i < listSubmissions.size(); i++) {
                ContestSubmissionEntity s1 = listSubmissions.get(i);
                for (int j = i + 1; j < listSubmissions.size(); j++) {
                    ContestSubmissionEntity s2 = listSubmissions.get(j);
                    if (s1.getUserId().equals(s2.getUserId())) {
                        continue;
                    }
                    //log.info("checkSimilarity, consider problem " + problemId + " listSubmissions = " + listSubmissions.size()
                    //         + " score between codes " + i + " length = " + s1.getSourceCode().length() + " " + j + " length = " + s2.getSourceCode().length());

                    double score = CodeSimilarityCheck.check(s1.getSourceCode(), s2.getSourceCode());
                    //log.info("checkSimilarity, consider problem " + problemId + " listSubmissions = " + listSubmissions.size()
                    //     + " score between codes " + i + " and " + j + " = " + score + " threshold = " + I.getThreshold());

                    //if(score <= 0.0001) continue;
                    if (score <= I.getThreshold() * 0.01) {
                        log.info("checkSimilarity, consider problem " +
                                 problemId +
                                 " listSubmissions = " +
                                 listSubmissions.size()
                                 +
                                 " score SMALL between codes " +
                                 i +
                                 " and " +
                                 j +
                                 " = " +
                                 score +
                                 " threshold = " +
                                 I.getThreshold());

                        continue;
                    }
                    log.info("checkSimilarity, consider problem " +
                             problemId +
                             " listSubmissions = " +
                             listSubmissions.size()
                             +
                             " score between codes " +
                             i +
                             " and " +
                             j +
                             " = " +
                             score +
                             " threshold = " +
                             I.getThreshold());


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

                    List<CodePlagiarism> codePlagiarisms = codePlagiarismRepo
                        .findAllByContestIdAndProblemIdAndSubmissionId1AndSubmissionId2(
                            contestId, problemId,
                            s1.getContestSubmissionId(),
                            s2.getContestSubmissionId());

                    if (codePlagiarisms != null && codePlagiarisms.size() > 0) {
                        //log.info("checkSimilarity, codePlagiarism sz = " + codePlagiarisms.size());
                        for (CodePlagiarism cp : codePlagiarisms) {
                            cp.setScore(score);
                            cp.setCreatedStamp(new Date());
                            cp = codePlagiarismRepo.save(cp);
                            //log.info("checkSimilarity, codePlagiarism sz = " + codePlagiarisms.size() + " exist -> update score " + score);
                        }
                    } else {

                        CodePlagiarism codePlagiarism = new CodePlagiarism();
                        codePlagiarism.setContestId(contestId);
                        codePlagiarism.setProblemId(problemId);
                        codePlagiarism.setUserId1(s1.getUserId());
                        codePlagiarism.setUserId2(s2.getUserId());
                        codePlagiarism.setSourceCode1(s1.getSourceCode());
                        codePlagiarism.setSourceCode2(s2.getSourceCode());
                        codePlagiarism.setSubmitDate1(s1.getCreatedAt());
                        codePlagiarism.setSubmitDate2(s2.getCreatedAt());
                        codePlagiarism.setSubmissionId1(s1.getContestSubmissionId());
                        codePlagiarism.setSubmissionId2(s2.getContestSubmissionId());
                        codePlagiarism.setScore(score);
                        codePlagiarism.setCreatedStamp(new Date());

                        codePlagiarism = codePlagiarismRepo.save(codePlagiarism);
                        //log.info("computeSimilarity, add new item score = " + score);
                    }
                }
            }
        }

        Collections.sort(list, new CodeSimilatiryComparator());


        model.setCodeSimilarityElementList(list);
        return model;
    }

    @Override
    public void evaluateSubmission(UUID submissionId) {
        ContestSubmissionEntity submission = contestSubmissionRepo.findById(submissionId).orElse(null);
        ContestEntity contest = contestService.findContestWithCache(submission.getContestId());
        evaluateSubmission(submission, contest);
    }

    @Override
    public void evaluateSubmissions(String contestId, String problemId) {
        List<ContestSubmissionEntity> submissions = contestSubmissionRepo.findAllByContestIdAndProblemId(contestId, problemId);
        if(submissions == null){
            log.info("evaluateSubmissions, contest " + contestId + " problem " + problemId + " -> NO Submissions");
            return;
        }
        log.info("evaluateSubmissions, contest " + contestId + " problem " + problemId + " nbSubmissions = " + submissions.size());
        ContestEntity contest = contestService.findContestWithCache(contestId);

        for(ContestSubmissionEntity sub: submissions){
            log.info("evaluateSubmissions, contest " + contestId + " problem " + problemId + " submission " + sub.getContestSubmissionId());
            evaluateSubmission(sub, contest);
        }

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
            }
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
            for (ProblemEntity p : problems) {
                String problemId = p.getProblemId();
                List<ContestSubmissionEntity> submissions = contestSubmissionRepo.findAllByContestIdAndUserIdAndProblemId(
                    contestId,
                    userLoginId,
                    problemId);

                for (ContestSubmissionEntity sub : submissions) {// take the last submission in the sorted list
                    evaluateSubmission(sub, contestEntity);
                }
            }
        }
        return null;

    }

    private ModelEvaluateBatchSubmissionResponse judgeAllInProgressSubmissionsOfContest(String contestId) {
        List<ContestSubmissionEntity> submissions = contestSubmissionRepo.findAllByContestIdAndStatus(
            contestId,
            ContestSubmissionEntity.SUBMISSION_STATUS_EVALUATION_IN_PROGRESS);
        ContestEntity contest = contestService.findContestWithCache(contestId);
        for (ContestSubmissionEntity sub : submissions) {// take the last submission in the sorted list
            evaluateSubmission(sub, contest);
        }
        return null;


    }

    @Override
    public ModelEvaluateBatchSubmissionResponse judgeAllSubmissionsOfContest(String contestId) {
        return judgeAllInProgressSubmissionsOfContest(contestId);
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

//    /**
//     * FIXME
//     * @param sourceChecker
//     * @param computerLanguage
//     * @param submissionTestCaseOutput
//     * @param testCase
//     * @param timeLimit
//     * @param memoryLimit
//     * @return
//     * @throws Exception
//     */
//    private Judge0Submission judgeSubmissionTestCaseOutput(
//        String sourceChecker,
//        String computerLanguage,
//        String submissionTestCaseOutput,
//        TestCaseEntity testCase,
//        int timeLimit,
//        int memoryLimit
//    ) throws Exception {
//        int languageId;
//        String compilerOptions = null;
//        switch (ComputerLanguage.Languages.valueOf(computerLanguage)) {
//            case C:
//                languageId = 50;
//                compilerOptions = "-std=c17 -w -O2 -lm -fmax-errors=3";
//                break;
//            case CPP11:
//                languageId = 54;
//                compilerOptions = "-std=c++11 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
//                break;
//            case CPP14:
//                languageId = 54;
//                compilerOptions = "-std=c++14 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
//                break;
//            case CPP:
//            case CPP17:
//                languageId = 54;
//                compilerOptions = "-std=c++17 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
//                break;
//            case JAVA:
//                languageId = 62;
//                break;
//            case PYTHON3:
//                languageId = 71;
//                break;
//            default:
//                throw new Exception("Language not supported");
//        }
//
//        Judge0Submission submission = Judge0Submission.builder()
//                                                      .sourceCode(sourceChecker)
//                                                      .languageId(languageId)
//                                                      .compilerOptions(compilerOptions)
//                                                      .commandLineArguments(null)
//                                                      .stdin(String.join(
//                                                          "\n", new String[]{
//                                                              testCase.getTestCase(),
//                                                              testCase.getCorrectAnswer(),
//                                                              submissionTestCaseOutput}))
//                                                      .cpuTimeLimit((float) timeLimit)
//                                                      .cpuExtraTime((float) (timeLimit * 1.0 + 2.0))
//                                                      .wallTimeLimit((float) (timeLimit * 1.0 + 10.0))
//                                                      .memoryLimit((float) memoryLimit * 1024)
//                                                      .stackLimit(judge0Config.getSubmission().getMaxStackLimit())
//                                                      .maxProcessesAndOrThreads(languageId != 62 ? 2 : 23) // OK, chấm output thì không cần đa luồng, trừ Java
//                                                      .enablePerProcessAndThreadTimeLimit(false)
//                                                      .enablePerProcessAndThreadMemoryLimit(false)
//                                                      .maxFileSize(judge0Config.getSubmission().getMaxMaxFileSize())
//                                                      .redirectStderrToStdout(false)
//                                                      .enableNetwork(false)
//                                                      .numberOfRuns(1)
//                                                      .build();
//
//        submission = judge0Service.createASubmission(submission, true, true);
//        submission.decodeBase64();
//
//        return submission;
//    }

    /**
     * @param sourceCode
     * @param computerLanguage
     * @param input
     * @param memoryLimit
     * @param timeLimit
     * @return
     * @throws Exception
     */
    private Judge0Submission runCode(
        String sourceCode,
        String computerLanguage,
        String input,
        float memoryLimit,
        float timeLimit
    ) throws Exception {
        int languageId;
        String compilerOptions = null;
        switch (ComputerLanguage.Languages.valueOf(computerLanguage)) {
            case C:
                languageId = 50;
                compilerOptions = "-std=c17 -w -O2 -lm -fmax-errors=3";
                break;
            case CPP11:
                languageId = 54;
                compilerOptions = "-std=c++11 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
                break;
            case CPP14:
                languageId = 54;
                compilerOptions = "-std=c++14 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
                break;
            case CPP:
            case CPP17:
                languageId = 54;
                compilerOptions = "-std=c++17 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
                break;
            case JAVA:
                languageId = 62; // Xem xét JAVA_OPTS giới hạn bộ nhớ nhưng có vẻ không cần thiết lắm với Judge0
                break;
            case PYTHON3:
                languageId = 71;
                break;
            default:
                throw new Exception("Language not supported");
        }

        Judge0Submission submission = Judge0Submission.builder()
                                                      .sourceCode(sourceCode)
                                                      .languageId(languageId)
                                                      .compilerOptions(compilerOptions)
                                                      .commandLineArguments(null)
                                                      .stdin(input)
//                                                      .expectedOutput(Constants.ProblemResultEvaluationType.CUSTOM
//                                                                          .getValue()
//                                                                          .equals(evaluationType)
//                                                                          ? null
//                                                                          : testCase.getCorrectAnswer())
                                                      .cpuTimeLimit(timeLimit)
                                                      .cpuExtraTime((float) (timeLimit + 2.0))
                                                      .wallTimeLimit((float) (timeLimit + 10.0))
                                                      .memoryLimit(memoryLimit * 1024)
                                                      .stackLimit(judge0Config.getSubmission().getMaxStackLimit())
                                                      .maxProcessesAndOrThreads(languageId != 62 ? 2 : 23)
                                                      .enablePerProcessAndThreadTimeLimit(false)
                                                      .enablePerProcessAndThreadMemoryLimit(false)
                                                      .maxFileSize(judge0Config.getSubmission().getMaxMaxFileSize())
                                                      .redirectStderrToStdout(false)
                                                      .enableNetwork(false)
                                                      .numberOfRuns(1)
                                                      .build();

        submission = judge0Service.createASubmission(submission, true, true);
        submission.decodeBase64();

        return submission;
    }

//    @Override
//    public List<CodePlagiarism> findAllByContestId(String contestId) {
//        return codePlagiarismRepo.findAllByContestId(contestId);
//    }

    private boolean emptyString(String s) {
        return s == null || s.isEmpty();
    }

    @Override
    public List<CodePlagiarism> findAllBy(ModelGetCodeSimilarityParams input) {
        List<CodePlagiarism> codePlagiarisms = codePlagiarismRepo.findAllByContestId(input.getContestId());
        List<CodePlagiarism> res = new ArrayList<>();
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
        List<UserRegistrationContestEntity> UR = userRegistrationContestRepo.findAllByContestIdAndStatus(
            contestId,
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
        Collections.sort(
            res, new Comparator<ModelReponseCodeSimilaritySummaryParticipant>() {
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

    /**
     * Try to execute the solution code on this test case, if pass then store in DB
     * otherwise, ignore
     *
     * @param testCase
     * @param dto
     * @return
     */
    @Override
    public Object addTestcase(
        String testCase,
        ModelProgrammingContestUploadTestCase dto
    ) throws Exception {
        ProblemEntity problem = problemRepo.findByProblemId(dto.getProblemId());
        String testcaseCorrectAnswer;
        Judge0Submission output = null;

        if (TestcaseUploadMode.EXECUTE.equals(dto.getUploadMode())) {
            output = runCode(
                problem.getCorrectSolutionSourceCode(),
                problem.getCorrectSolutionLanguage(),
                testCase,
                problem.getMemoryLimit(),
                getTimeLimitByLanguage(problem, problem.getCorrectSolutionLanguage()));

            if (output.getStatus().getId() != 3) { // Chay khong thanh cong thi khong luu, tra ket qua luon
                return Judge0Submission.getSubmissionDetailsAfterExecution(output);
            }

            testcaseCorrectAnswer = output.getStdout();
        } else { // TestcaseUploadMode.NOT_EXECUTE
            testcaseCorrectAnswer = dto.getCorrectAnswer();
        }

        TestCaseEntity tc = TestCaseEntity.builder()
                                          .testCase(testCase)
                                          .problemId(dto.getProblemId())
                                          .isPublic(dto.getIsPublic() ? "Y" : "N")
                                          .testCasePoint(dto.getPoint())
                                          .correctAnswer(testcaseCorrectAnswer)
                                          .description(dto.getDescription())
                                          .build();

        testCaseService.saveTestCaseWithCache(tc);
        return Judge0Submission.getSubmissionDetailsAfterExecution(output);
    }

    /**
     * Can be use when editing the solution's source code
     *
     * @param problemId
     * @param testCaseId
     * @return
     */
    @Override
    public Object reCreateTestcaseCorrectAnswer(String problemId, UUID testCaseId) throws Exception {
        ProblemEntity problem = problemRepo
            .findById(problemId)
            .orElseThrow(() -> new EntityNotFoundException("Problem with ID " + problemId + " not found"));
        TestCaseEntity testCase = testCaseRepo
            .findById(testCaseId)
            .orElseThrow(() -> new EntityNotFoundException("Testcase with ID " + testCaseId + " not found"));
        ;

        String testcaseContent = testCase.getTestCase();
        Judge0Submission output = runCode(
            problem.getCorrectSolutionSourceCode(),
            problem.getCorrectSolutionLanguage(),
            testcaseContent,
            problem.getMemoryLimit(),
            getTimeLimitByLanguage(problem, problem.getCorrectSolutionLanguage()));

        if (output.getStatus().getId() != 3) { // Chay khong thanh cong thi khong luu, tra ket qua luon
            return Judge0Submission.getSubmissionDetailsAfterExecution(output);
        }

        testCase.setCorrectAnswer(output.getStdout());
        testCaseService.saveTestCaseWithCache(testCase);

        return Judge0Submission.getSubmissionDetailsAfterExecution(output);
    }

    /**
     * @param testCaseId
     * @param testcaseContent
     * @param dto
     * @return
     */
    @Override
    public Object editTestcase(
        UUID testCaseId,
        String testcaseContent,
        ModelProgrammingContestUploadTestCase dto
    ) throws Exception {
        TestCaseEntity testCase = testCaseRepo
            .findById(testCaseId)
            .orElseThrow(() -> new EntityNotFoundException("Testcase with ID " + testCaseId + " not found"));
        Judge0Submission output = null;

        if (TestcaseUploadMode.EXECUTE.equals(dto.getUploadMode())) {
            if (StringUtils.isNotBlank(testcaseContent)) {
                ProblemEntity problem = problemRepo.findByProblemId(dto.getProblemId());

                output = runCode(
                    problem.getCorrectSolutionSourceCode(),
                    problem.getCorrectSolutionLanguage(),
                    testcaseContent,
                    problem.getMemoryLimit(),
                    getTimeLimitByLanguage(problem, problem.getCorrectSolutionLanguage()));

                if (output.getStatus().getId() != 3) { // Chay khong thanh cong thi khong luu, tra ket qua luon
                    return Judge0Submission.getSubmissionDetailsAfterExecution(output);
                }

                testCase.setTestCase(testcaseContent);
                testCase.setCorrectAnswer(output.getStdout());
            } else {
                throw new IllegalArgumentException("The file is required when using EXECUTE mode");
            }
        } else { // TestcaseUploadMode.NOT_EXECUTE
            if (StringUtils.isNotBlank(testcaseContent)) {
                testCase.setTestCase(testcaseContent);
            }

            if (StringUtils.isNotBlank(dto.getCorrectAnswer())) {
                testCase.setCorrectAnswer(dto.getCorrectAnswer());
            }
        }

        testCase.setTestCasePoint(dto.getPoint());
        testCase.setIsPublic(dto.getIsPublic() ? "Y" : "N");
        testCase.setDescription(dto.getDescription());

        testCaseService.saveTestCaseWithCache(testCase);
        return Judge0Submission.getSubmissionDetailsAfterExecution(output);
    }

    private void updateMaxPoint(
        ContestSubmissionEntity s,
        HashMap<String, List<ModelUserJudgedProblemSubmissionResponse>> mUserId2Submission,
        HashMap<String, ProblemEntity> mID2Problem
    ) {
        if (mUserId2Submission.get(s.getUserId()) == null) {
            mUserId2Submission.put(s.getUserId(), new ArrayList<>());
            ModelUserJudgedProblemSubmissionResponse e = new ModelUserJudgedProblemSubmissionResponse();
            e.setUserId(s.getUserId());
            e.setFullName(userService.getUserFullName(s.getUserId()));
            e.setProblemId(s.getProblemId());
            e.setSubmissionSourceCode(s.getSourceCode());
            e.setPoint(s.getPoint());
            if (mID2Problem.get(s.getProblemId()) != null) {
                e.setProblemName(mID2Problem.get(s.getProblemId()).getProblemName());
            } else {
                e.setProblemName(s.getProblemId());
            }

            e.setTestCasePassed(s.getTestCasePass());
            e.setStatus(s.getStatus());
            mUserId2Submission.get(s.getUserId()).add(e);
        } else {
            // scan list problem & submission and update max point
            ModelUserJudgedProblemSubmissionResponse maxP = null;
            long maxPoint = -1000;
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
                e.setUserId(s.getUserId());
                e.setFullName(userService.getUserFullName(s.getUserId()));
                e.setProblemId(s.getProblemId());
                e.setSubmissionSourceCode(s.getSourceCode());
                e.setPoint(s.getPoint());
                if (mID2Problem.get(s.getProblemId()) != null) {
                    e.setProblemName(mID2Problem.get(s.getProblemId()).getProblemName());
                } else {
                    e.setProblemName(s.getProblemId());
                }
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
        List<ModelUserJudgedProblemSubmissionResponse> res = new ArrayList<>();
        HashMap<String, List<ModelUserJudgedProblemSubmissionResponse>> mUserId2Submission = new HashMap<>();
        HashMap<String, ProblemEntity> mID2Problem = new HashMap<>();
        ContestEntity contest = contestRepo.findContestByContestId(contestId);
        List<ProblemEntity> problems = contest.getProblems();
        for (ProblemEntity p : problems) {
            mID2Problem.put(p.getProblemId(), p);
        }
        for (ContestSubmissionEntity s : submissions) {
            updateMaxPoint(s, mUserId2Submission, mID2Problem);
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
            ContestEntity contest = contestRepo.findContestByContestId(u.getContestId());
            String createdBy = "";
            if (contest != null) {
                createdBy = contest.getUserId();
            }
            if (u.getUserId().equals("admin")) {
                return false;
            }
            if (u.getUserId().equals(createdBy)) {
                return false;
            }
            userRegistrationContestRepo.delete(u);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeMemberFromContestGroup(String contestId, String userId, String participantId) {
        ContestUserParticipantGroup item = contestUserParticipantGroupRepo
            .findByContestIdAndUserIdAndParticipantId(contestId, userId, participantId);
        if (item == null) {

            log.info("removeMemberFromContestGroup, cannot find record for contest " +
                     contestId +
                     " user " +
                     userId +
                     " participant " +
                     participantId);
            return false;
        } else {
            contestUserParticipantGroupRepo.delete(item);
            log.info("removeMemberFromContestGroup, DELETED record for contest " +
                     contestId +
                     " user " +
                     userId +
                     " participant " +
                     participantId);

            return true;
        }
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
    public boolean addUserProblemRole(String userName, ModelUserProblemRole input) throws Exception {
        boolean isOwner = this.userContestProblemRoleRepo.existsByProblemIdAndUserIdAndRoleId(
            input.getProblemId(),
            userName,
            UserContestProblemRole.ROLE_OWNER);
        if (!isOwner) {
            throw new MiniLeetCodeException("You are not owner of this problem.", 403);
        }
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
    public boolean removeUserProblemRole(String userName, ModelUserProblemRole input) throws Exception {
        boolean isOwner = this.userContestProblemRoleRepo.existsByProblemIdAndUserIdAndRoleId(
            input.getProblemId(),
            userName,
            UserContestProblemRole.ROLE_OWNER);
        if (!isOwner) {
            throw new MiniLeetCodeException("You are not owner of this problem.", 403);
        }
        List<UserContestProblemRole> L = userContestProblemRoleRepo.findAllByProblemIdAndUserIdAndRoleId(
            input.getProblemId(),
            input.getUserId(),
            input.getRoleId());
        //if (L != null && L.size() > 0) {
        if (L == null || L.size() == 0) {
            return false;
        }
        log.info("removeUserProblemRole(" + input.getUserId() + "," +
                 input.getProblemId() + "," + input.getRoleId() + ", got L.sz = " + L.size());
        for (UserContestProblemRole e : L) {
            userContestProblemRoleRepo.delete(e);
            //userContestProblemRoleRepo.remove(e);
        }
        return true;
    }

    @Override
    public List<TagEntity> getAllTags() {
        return tagRepo.findAll(Sort.by(Sort.Direction.ASC, "name"));
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

//    @Override
//    @Transactional
//    public TagEntity updateTag(Integer tagId, ModelTag newTag) {
//        TagEntity tagEntity = tagRepo.findByTagId(tagId);
//
//        tagEntity.setName(newTag.getName());
//
//        if (newTag.getDescription() != null) {
//            tagEntity.setDescription(newTag.getDescription());
//        } else {
//            tagEntity.setDescription("");
//        }
//
//        return tagRepo.save(tagEntity);
//    }
//
//    @Override
//    @Transactional
//    public void deleteTag(Integer tagId) {
//        TagEntity tagEntity = tagRepo.findByTagId(tagId);
//        tagRepo.delete(tagEntity);
//    }

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

    public ModelCreateContestProblemResponse getContestProblemDetailByIdAndTeacher(String problemId, String teacherId)
        throws Exception {

//        TODO: re-open this later
//        boolean hasPermission =
//                this.userContestProblemRoleRepo.existsByProblemIdAndUserIdAndRoleId(problemId,
//                        teacherId, UserContestProblemRole.ROLE_OWNER)
//                        || this.userContestProblemRoleRepo.existsByProblemIdAndUserIdAndRoleId(problemId,
//                                teacherId, UserContestProblemRole.ROLE_EDITOR)
//                        || this.userContestProblemRoleRepo.existsByProblemIdAndUserIdAndRoleId(problemId,
//                                teacherId, UserContestProblemRole.ROLE_VIEWER);
//        if (!hasPermission) {
//            throw new MiniLeetCodeException("You don't have permission to view this problem", 403);
//        }

        ProblemEntity problemEntity = problemRepo.findByProblemId(problemId);
        if (problemEntity == null) {
            throw new MiniLeetCodeException("Problem not found", 404);
        }

        if (problemEntity.isPublicProblem() != true) {

            List<UserContestProblemRole> ucpr = userContestProblemRoleRepo
                .findAllByProblemIdAndUserId(problemEntity.getProblemId(), teacherId);

            boolean ok = true;
            if (!problemEntity.getCreatedBy().equals(teacherId)) {
                if (ucpr == null || ucpr.size() == 0) {
                    ok = false;
                } else {
                    boolean owner = false;
                    for (UserContestProblemRole e : ucpr) {
                        if (e.getRoleId().equals(UserContestProblemRole.ROLE_OWNER)) {
                            owner = true;
                            break;
                        }
                    }
                    if (!owner && problemEntity.getStatusId() != null &&
                        !problemEntity.getStatusId().equals(ProblemEntity.PROBLEM_STATUS_OPEN)) {
                        ok = false;
                    }
                }
            }

            if (!ok) {
                throw new MiniLeetCodeException("Problem is not open or you do not have permission", 400);
            }
        }
        /*
        if (!problemEntity.getUserId().equals(teacherId) &&
            !problemEntity.getStatusId().equals(ProblemEntity.PROBLEM_STATUS_OPEN)) {
            throw new MiniLeetCodeException("Problem is not open", 400);
        }
        */

        ModelCreateContestProblemResponse problemResponse = new ModelCreateContestProblemResponse();
        problemResponse.setProblemId(problemEntity.getProblemId());
        problemResponse.setProblemName(problemEntity.getProblemName());
        problemResponse.setProblemDescription(problemEntity.getProblemDescription());
        problemResponse.setUserId(problemEntity.getCreatedBy());
//        problemResponse.setTimeLimit(problemEntity.getTimeLimit());
        problemResponse.setTimeLimitCPP(problemEntity.getTimeLimitCPP());
        problemResponse.setTimeLimitJAVA(problemEntity.getTimeLimitJAVA());
        problemResponse.setTimeLimitPYTHON(problemEntity.getTimeLimitPYTHON());
        problemResponse.setMemoryLimit(problemEntity.getMemoryLimit());
        problemResponse.setLevelId(problemEntity.getLevelId());
        problemResponse.setCorrectSolutionSourceCode(problemEntity.getCorrectSolutionSourceCode());
        problemResponse.setCorrectSolutionLanguage(problemEntity.getCorrectSolutionLanguage());
        problemResponse.setSolutionCheckerSourceCode(problemEntity.getSolutionCheckerSourceCode());
        problemResponse.setSolutionCheckerSourceLanguage(problemEntity.getSolutionCheckerSourceLanguage());
        problemResponse.setScoreEvaluationType(problemEntity.getScoreEvaluationType());
        problemResponse.setSolution(problemEntity.getSolution());
        problemResponse.setIsPreloadCode(problemEntity.getIsPreloadCode());
        problemResponse.setPreloadCode(problemEntity.getPreloadCode());
        problemResponse.setLevelOrder(problemEntity.getLevelOrder());
        problemResponse.setCreatedAt(problemEntity.getCreatedAt());
        problemResponse.setPublicProblem(problemEntity.isPublicProblem());
        problemResponse.setTags(problemEntity.getTags());
        problemResponse.setStatus(problemEntity.getStatusId());
        problemResponse.setSampleTestCase(problemEntity.getSampleTestcase());

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

        problemResponse.setRoles(userContestProblemRoleRepo.getRolesByProblemIdAndUserId(problemId, teacherId));

        return problemResponse;
    }

    @Override
    public List<ModelImportProblemFromContestResponse> importProblemsFromAContest(ModelImportProblemsFromAContestInput I) {
        ContestEntity contest = contestRepo.findContestByContestId(I.getFromContestId());
        if (contest == null) {
            throw new IllegalArgumentException("Contest ID " + I.getFromContestId() + " not found");
        }

        List<ModelImportProblemFromContestResponse> responseList = new ArrayList<>();

        for (ProblemEntity p : contest.getProblems()) {
            ModelImportProblemFromContestResponse response = new ModelImportProblemFromContestResponse();
            response.setProblemId(p.getProblemId());
            ContestProblem ocp = contestProblemRepo.findByContestIdAndProblemId(I.getFromContestId(), p.getProblemId());
            ContestProblem cp = contestProblemRepo.findByContestIdAndProblemId(I.getContestId(), p.getProblemId());
            if (cp != null) {
                response.setStatus("Problem already existed");
                responseList.add(response);
                continue;
            }
            cp = new ContestProblem();
            cp.setContestId(I.getContestId());
            cp.setProblemId(p.getProblemId());
            cp.setSubmissionMode(ocp.getSubmissionMode());
            cp.setProblemRename(ocp.getProblemRename());
            cp.setProblemRecode(ocp.getProblemRecode());
            contestProblemRepo.save(cp);
            response.setStatus("SUCCESSFUL");
            responseList.add(response);
        }

        return responseList;
    }

    /**
     * @param userId
     * @param filter
     * @param isPublic
     * @return
     */
    public Page<ProblemDTO> getProblems(String userId, ProblemFilter filter, Boolean isPublic) {
        return fetchProblems(userId, filter, isPublic, false);
    }

    /**
     * @param userId
     * @param filter
     * @return
     */
    public Page<ProblemDTO> getSharedProblems(String userId, ProblemFilter filter) {
        return fetchProblems(userId, filter, null, true);
    }

    /**
     * @param userId
     * @param filter
     * @return
     */
    public Page<ProblemDTO> getPublicProblems(String userId, ProblemFilter filter) {
        return this.getProblems(null, filter, true);
    }

    private Page<ProblemDTO> fetchProblems(
        String userId,
        ProblemFilter filter,
        Boolean isPublic,
        boolean isSharedProblems
    ) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());

        String name = StringUtils.isNotBlank(filter.getName()) ? filter.getName().trim() : null;

        normalizeFilter(filter);

        Page<ProblemProjection> problems = isSharedProblems
            ? problemRepo.findAllSharedProblemsBy(
            userId,
            name,
            filter.getLevelIds(),
            filter.getTagIds(),
            filter.getStatusIds(),
            pageable)
            : problemRepo.findAllBy(
                userId,
                name,
                filter.getLevelIds(),
                filter.getTagIds(),
                filter.getStatusIds(),
                isPublic,
                pageable);

        return problems.map(this::convertToProblemDTO);
    }

    private void normalizeFilter(ProblemFilter filter) {
        if (StringUtils.isBlank(filter.getLevelIds())) {
            filter.setLevelIds(null);
        }
        if (StringUtils.isBlank(filter.getStatusIds())) {
            filter.setStatusIds(null);
        }
        if (StringUtils.isBlank(filter.getTagIds())) {
            filter.setTagIds(null);
        }
    }

    private ProblemDTO convertToProblemDTO(ProblemProjection item) {
        ProblemDTO dto = objectMapper.convertValue(item, ProblemDTO.class);
        try {
            dto.setTags(objectMapper.readValue(
                item.getJsonTags(), new TypeReference<List<TagEntity>>() {
                }));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return dto;
    }

    @Override
    public List<ProblemEntity> getAllProblems(String userId) {
        List<ProblemEntity> problems = problemRepo.findAll();
        return problems;
    }

    @Override
    public List<ContestProblemModelResponse> extApiGetAllProblems(String userID) {
        List<ProblemEntity> problems = problemRepo.findAll();
        List<ContestProblemModelResponse> res = new ArrayList<>();
        for (ProblemEntity pe : problems) {
            ContestProblemModelResponse p = new ContestProblemModelResponse(
                pe.getProblemId(), pe.getProblemName(), pe.getLevelId());
        }
        return res;
    }

    @Override
    public List<SubmissionModelResponse> extApiGetSubmissions(String participantId) {
        List<ContestSubmissionEntity> sub = contestSubmissionPagingAndSortingRepo.findAllByUserId(participantId);
        List<SubmissionModelResponse> res = new ArrayList<SubmissionModelResponse>();
        for (ContestSubmissionEntity s : sub) {
            SubmissionModelResponse r = new SubmissionModelResponse(
                s.getUserId(),
                s.getProblemId(),
                s.getContestId(),
                s.getPoint(),
                s.getCreatedAt());
            res.add(r);
        }
        return res;
    }

    @Override
    public ModelGetContestPageResponse getAllPublicContests() {
        List<ContestEntity> publicContestEntities = contestRepo.findByContestPublicTrue();

        List<ModelGetContestResponse> publicContests = publicContestEntities.stream()
                                                                            .map(contest -> ModelGetContestResponse
                                                                                .builder()
                                                                                .contestId(contest.getContestId())
                                                                                .contestName(contest.getContestName())
                                                                                .contestTime(contest.getContestSolvingTime())
                                                                                .countDown(contest.getCountDown())
                                                                                .startAt(contest.getStartedAt())
                                                                                .statusId(contest.getStatusId())
                                                                                .userId(contest.getUserId())
                                                                                .createdAt(contest.getCreatedAt())
                                                                                .build())
                                                                            .collect(Collectors.toList());

        long count = publicContests.size();

        return ModelGetContestPageResponse.builder()
                                          .contests(publicContests)
                                          .count(count)
                                          .build();
    }

    @Transactional
    @Override
    public ProblemEntity cloneProblem(String userId, ModelCloneProblem cloneRequest) throws MiniLeetCodeException {

        ProblemEntity originalProblem = problemRepo.findById(cloneRequest.getOldProblemId())
                                                   .orElseThrow(() -> new MiniLeetCodeException(
                                                       "Original problem not found",
                                                       HttpStatus.NOT_FOUND.value()));

        if (problemRepo.existsByProblemId(cloneRequest.getNewProblemId())) {
            throw new MiniLeetCodeException("New problem ID already exists", HttpStatus.CONFLICT.value());
        }

        if (problemRepo.existsByProblemName(cloneRequest.getNewProblemName())) {
            throw new MiniLeetCodeException("New problem name already exists", HttpStatus.CONFLICT.value());
        }

        ProblemEntity newProblem = new ProblemEntity();

        newProblem.setProblemId(cloneRequest.getNewProblemId());
        newProblem.setProblemName(cloneRequest.getNewProblemName());
        newProblem.setProblemDescription(originalProblem.getProblemDescription());
        newProblem.setTimeLimitCPP(originalProblem.getTimeLimitCPP());
        newProblem.setTimeLimitJAVA(originalProblem.getTimeLimitJAVA());
        newProblem.setTimeLimitPYTHON(originalProblem.getTimeLimitPYTHON());
        newProblem.setMemoryLimit(originalProblem.getMemoryLimit());
        newProblem.setCorrectSolutionSourceCode(originalProblem.getCorrectSolutionSourceCode());
        newProblem.setCorrectSolutionLanguage(originalProblem.getCorrectSolutionLanguage());
        newProblem.setPublicProblem(originalProblem.isPublicProblem());
        newProblem.setTags(originalProblem.getTags());
        //newProblem.setCreatedBy(originalProblem.getUserId());
        newProblem.setCreatedBy(userId);
//        newProblem.setTimeLimit(originalProblem.getTimeLimit());
        newProblem.setLevelId(originalProblem.getLevelId());
        newProblem.setCategoryId(originalProblem.getCategoryId());
        newProblem.setSolutionCheckerSourceCode(originalProblem.getSolutionCheckerSourceCode());
        newProblem.setSolutionCheckerSourceLanguage(originalProblem.getSolutionCheckerSourceLanguage());
        newProblem.setSolution(originalProblem.getSolution());
        newProblem.setLevelOrder(originalProblem.getLevelOrder());
        newProblem.setAttachment(originalProblem.getAttachment());
        newProblem.setScoreEvaluationType(originalProblem.getScoreEvaluationType());
        newProblem.setPreloadCode(originalProblem.getPreloadCode());
        newProblem.setIsPreloadCode(originalProblem.getIsPreloadCode());
        newProblem.setStatusId(originalProblem.getStatusId());
        newProblem.setSampleTestcase(originalProblem.getSampleTestcase());

        newProblem = problemRepo.save(newProblem);

        List<TestCaseEntity> originalTestCases = testCaseRepo.findAllByProblemId(cloneRequest.getOldProblemId());
        for (TestCaseEntity originalTestCase : originalTestCases) {
            TestCaseEntity newTestCase = new TestCaseEntity();
            newTestCase.setTestCasePoint(originalTestCase.getTestCasePoint());
            newTestCase.setTestCase(originalTestCase.getTestCase());
            newTestCase.setCorrectAnswer(originalTestCase.getCorrectAnswer());
            newTestCase.setProblemId(newProblem.getProblemId());
            newTestCase.setIsPublic(originalTestCase.getIsPublic());
            newTestCase.setDescription(originalTestCase.getDescription());
            newTestCase.setStatusId(originalTestCase.getStatusId());

            testCaseRepo.save(newTestCase);
        }
        // grant role owner, manager, view to current user
        UserContestProblemRole upr = new UserContestProblemRole();
        upr.setProblemId(newProblem.getProblemId());
        upr.setUserId(userId);
        upr.setRoleId(UserContestProblemRole.ROLE_OWNER);
        upr.setUpdateByUserId(userId);
        upr.setCreatedStamp(new Date());
        upr.setLastUpdated(new Date());
        upr = userContestProblemRoleRepo.save(upr);

        upr = new UserContestProblemRole();
        upr.setProblemId(newProblem.getProblemId());
        upr.setUserId(userId);
        upr.setRoleId(UserContestProblemRole.ROLE_EDITOR);
        upr.setUpdateByUserId(userId);
        upr.setCreatedStamp(new Date());
        upr.setLastUpdated(new Date());
        upr = userContestProblemRoleRepo.save(upr);

        upr = new UserContestProblemRole();
        upr.setProblemId(newProblem.getProblemId());
        upr.setUserId(userId);
        upr.setRoleId(UserContestProblemRole.ROLE_VIEWER);
        upr.setUpdateByUserId(userId);
        upr.setCreatedStamp(new Date());
        upr.setLastUpdated(new Date());
        upr = userContestProblemRoleRepo.save(upr);


        // grant manager role to user admin
        UserLogin admin = userLoginRepo.findByUserLoginId("admin");
        if (admin != null) {
            upr = new UserContestProblemRole();
            upr.setProblemId(newProblem.getProblemId());
            upr.setUserId(admin.getUserLoginId());
            upr.setRoleId(UserContestProblemRole.ROLE_OWNER);
            upr.setUpdateByUserId(userId);
            upr.setCreatedStamp(new Date());
            upr.setLastUpdated(new Date());
            upr = userContestProblemRoleRepo.save(upr);

            upr = new UserContestProblemRole();
            upr.setProblemId(newProblem.getProblemId());
            upr.setUserId(admin.getUserLoginId());
            upr.setRoleId(UserContestProblemRole.ROLE_EDITOR);
            upr.setUpdateByUserId(userId);
            upr.setCreatedStamp(new Date());
            upr.setLastUpdated(new Date());
            upr = userContestProblemRoleRepo.save(upr);

            upr = new UserContestProblemRole();
            upr.setProblemId(newProblem.getProblemId());
            upr.setUserId(admin.getUserLoginId());
            upr.setRoleId(UserContestProblemRole.ROLE_VIEWER);
            upr.setUpdateByUserId(userId);
            upr.setCreatedStamp(new Date());
            upr.setLastUpdated(new Date());
            upr = userContestProblemRoleRepo.save(upr);

            // push notification to admin
            notificationsService.create(
                userId, admin.getUserLoginId(),
                userId + " has cloned a contest problem ID " +
                newProblem.getProblemId()
                , "");
        }
        return newProblem;
    }
}
