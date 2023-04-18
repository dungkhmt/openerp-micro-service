package com.hust.baseweb.applications.education.service;

import com.google.gson.Gson;
import com.hust.baseweb.applications.contentmanager.model.ContentHeaderModel;
import com.hust.baseweb.applications.contentmanager.model.ContentModel;
import com.hust.baseweb.applications.contentmanager.repo.MongoContentService;
import com.hust.baseweb.applications.education.classmanagement.service.storage.exception.StorageException;
import com.hust.baseweb.applications.education.entity.*;
import com.hust.baseweb.applications.education.model.quiz.ModelCreateQuizQuestionUserRole;
import com.hust.baseweb.applications.education.model.quiz.QuizChooseAnswerInputModel;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionCreateInputModel;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionUpdateInputModel;
import com.hust.baseweb.applications.education.quiztest.model.QuizChoiceAnswerHideCorrectAnswer;
import com.hust.baseweb.applications.education.quiztest.utils.Utils;
import com.hust.baseweb.applications.education.repo.*;
import com.hust.baseweb.applications.notifications.service.NotificationsService;
import com.hust.baseweb.config.FileSystemStorageProperties;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class QuizQuestionServiceImpl implements QuizQuestionService {
    public static final String PREFIX_CHOICE_CODE = "C";
    private QuizQuestionRepo quizQuestionRepo;

    private QuizCourseTopicRepo quizCourseTopicRepo;

    private QuizCourseTopicService quizCourseTopicService;

    private QuizQuestionUserRoleRepo quizQuestionUserRoleRepo;

    private QuizChoiceAnswerRepo quizChoiceAnswerRepo;

    private LogUserLoginQuizQuestionRepo logUserLoginQuizQuestionRepo;

    private ClassRepo classRepo;

    private FileSystemStorageProperties properties;

    private NotificationsService notificationsService;
    private UserService userService;

    private MongoContentService mongoContentService;

    @Override
    public QuizQuestion save(QuizQuestionCreateInputModel input) {
        QuizQuestion quizQuestion = new QuizQuestion();
        quizQuestion.setLevelId(input.getLevelId());
        quizQuestion.setQuestionContent(input.getQuestionContent());
        quizQuestion.setStatusId(QuizQuestion.STATUS_PRIVATE);

        QuizCourseTopic quizCourseTopic = quizCourseTopicRepo.findById(input.getQuizCourseTopicId()).orElse(null);

        quizQuestion.setQuizCourseTopic(quizCourseTopic);

        quizQuestion = quizQuestionRepo.save(quizQuestion);

        return quizQuestion;
    }

    @Override
    @Transactional
    public QuizQuestion save(UserLogin u, String json, MultipartFile[] files, MultipartFile[] solutionAttachments) {

        //Do save file
//        Date now = new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        String prefixFileName = formatter.format(now);
//        ArrayList<String> attachmentPaths = new ArrayList<>();
//        Arrays.asList(files).forEach(file -> {
//            try {
//                Path path = Paths.get(properties.getFilesystemRoot() + "\\" + properties.getClassManagementDataPath());
//                String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
//
//                if (file.isEmpty()) {
//                    throw new StorageException("Failed to store empty file " + originalFileName);
//                }
//
//                if (originalFileName.contains("..")) {
//                    // This is a security check
//                    throw new StorageException(
//                        "Cannot store file with relative path outside current directory "
//                        + originalFileName);
//                }
//
//                // Can throw IOExeption, e.g NoSuchFileException.
//                Files.copy(
//                    file.getInputStream(),
//                    path.resolve(prefixFileName + "-" + originalFileName),
//                    StandardCopyOption.REPLACE_EXISTING);
//                attachmentPaths.add(prefixFileName + "-" + file.getOriginalFilename());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
        //taskInput.setAttachmentPaths(attachmentPaths.toArray(new String[0]));

        Gson gson = new Gson();
        QuizQuestionCreateInputModel input = gson.fromJson(json, QuizQuestionCreateInputModel.class);
        List<String> attachmentId = new ArrayList<>();
        String[] fileId = input.getFileId();
        List<MultipartFile> fileArray = Arrays.asList(files);

        fileArray.forEach((file) -> {
            ContentModel model = new ContentModel(fileId[fileArray.indexOf(file)], file);
            log.info("createQuizQuestion, fileId: " + fileId[fileArray.indexOf(file)]);

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

        List<String> solutionAttachmentStorageIds = mongoContentService.storeFiles(solutionAttachments);

        QuizQuestion quizQuestion = new QuizQuestion();
        quizQuestion.setLevelId(input.getLevelId());
        quizQuestion.setQuestionContent(input.getQuestionContent());
        //quizQuestion.setCreatedByUserLogin(u);
        quizQuestion.setCreatedByUserLoginId(u.getUserLoginId());
        QuizCourseTopic quizCourseTopic = quizCourseTopicRepo.findById(input.getQuizCourseTopicId()).orElse(null);

        quizQuestion.setQuizCourseTopic(quizCourseTopic);
        //quizQuestion.setStatusId(QuizQuestion.STATUS_PUBLIC);
        quizQuestion.setStatusId(QuizQuestion.STATUS_PRIVATE);
        quizQuestion.setAttachment(String.join(";", attachmentId));
        quizQuestion.setSolutionContent(input.getSolutionContent());
        quizQuestion.setSolutionAttachment(String.join(";", solutionAttachmentStorageIds));
        quizQuestion.setCreatedStamp(new Date());
        quizQuestion = quizQuestionRepo.save(quizQuestion);

        // grant role to userId
        QuizQuestionUserRole r = new QuizQuestionUserRole();
        r.setUserId(u.getUserLoginId());
        r.setQuestionId(quizQuestion.getQuestionId());
        r.setRoleId(QuizQuestionUserRole.ROLE_OWNER);
        r.setCreatedStamp(new Date());
        r = quizQuestionUserRoleRepo.save(r);

        r = new QuizQuestionUserRole();
        r.setUserId(u.getUserLoginId());
        r.setQuestionId(quizQuestion.getQuestionId());
        r.setRoleId(QuizQuestionUserRole.ROLE_MANAGER);
        r.setCreatedStamp(new Date());
        r = quizQuestionUserRoleRepo.save(r);

        r = new QuizQuestionUserRole();
        r.setUserId(u.getUserLoginId());
        r.setQuestionId(quizQuestion.getQuestionId());
        r.setRoleId(QuizQuestionUserRole.ROLE_VIEW);
        r.setCreatedStamp(new Date());
        r = quizQuestionUserRoleRepo.save(r);

        return quizQuestion;
    }

    @Override
    public List<QuizQuestion> findAll() {
        return quizQuestionRepo.findAll();
    }

    @Override
    public List<QuizQuestion> findQuizOfCourse(String courseId) {
        List<QuizCourseTopic> quizCourseTopics = quizCourseTopicService.findAllByEduCourse(courseId);
        //List<String> quizCourseTopicIds = quizCourseTopics.stream()
        //                                                  .map(quizCourseTopic -> quizCourseTopic.getQuizCourseTopicId()).collect(
        //    Collectors.toList());

//        for (QuizCourseTopic q : quizCourseTopics) {
//            log.info("findQuizOfCourse, courseId = " +
//                     courseId +
//                     ", topic = " +
//                     q.getQuizCourseTopicId() +
//                     ", " +
//                     q.getQuizCourseTopicName() +
//                     ", courseId = " +
//                     q.getEduCourse().getId());
//        }

        List<QuizQuestion> quizQuestions = quizQuestionRepo.findAllByQuizCourseTopicIn(quizCourseTopics);
//        log.info("findQuizOfCourse, courseId = " + courseId + ", quizCourseTopics.sz = " + quizCourseTopics.size()
//                 + " return quizQuestions.sz = " + quizQuestions.size());
        return quizQuestions;
    }

    @Override
    public List<QuizQuestion> findQuizOfCourseTopic(String quizCourseTopicId) {
        QuizCourseTopic quizCourseTopic = quizCourseTopicRepo.findById(quizCourseTopicId).orElse(null);
        if(quizCourseTopic == null) return null;
        List<QuizQuestion> quizQuestions = quizQuestionRepo.findAllByQuizCourseTopic(quizCourseTopic);

        return quizQuestions;
    }

    @Override
    public List<QuizQuestion> findAllQuizQuestionsByQuestionIdsIn(List<UUID> questionIds) {
        List<QuizQuestion> quizQuestions = quizQuestionRepo.findAllByQuestionIdIn(questionIds);
        return quizQuestions;
    }

    @Override
    public EduCourse findCourseOfQuizQuestion(UUID questionId) {
        QuizQuestion quizQuestion = quizQuestionRepo.findById(questionId).orElse(null);
        if (quizQuestion == null) {
            return null;
        }
        EduCourse eduCourse = quizQuestion.getQuizCourseTopic().getEduCourse();
        return eduCourse;
    }

    @Override
    public QuizQuestionDetailModel findQuizDetail(UUID questionId) {
        QuizQuestion quizQuestion = quizQuestionRepo.findById(questionId).orElse(null);
        QuizQuestionDetailModel quizQuestionDetailModel = new QuizQuestionDetailModel();
        quizQuestionDetailModel.setLevelId(quizQuestion.getLevelId());
        quizQuestionDetailModel.setStatement(quizQuestion.getQuestionContent());
        quizQuestionDetailModel.setQuizCourseTopic(quizQuestion.getQuizCourseTopic());
        quizQuestionDetailModel.setQuestionId(quizQuestion.getQuestionId());
        quizQuestionDetailModel.setStatusId(quizQuestion.getStatusId());
        quizQuestionDetailModel.setCreatedByUserLoginId(quizQuestion.getCreatedByUserLoginId());

        if (quizQuestion.getAttachment() != null) {
            String[] fileId = quizQuestion.getAttachment().split(";", -1);
            if (fileId.length != 0) {
                List<byte[]> fileArray = new ArrayList<>();
                for (String s : fileId) {
                    try {
                        GridFsResource content = mongoContentService.getById(s);
                        if (content != null) {
                            InputStream inputStream = content.getInputStream();
                            fileArray.add(IOUtils.toByteArray(inputStream));
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                quizQuestionDetailModel.setAttachment(fileArray);
            } else {
                quizQuestionDetailModel.setAttachment(null);
            }
        } else {
            quizQuestionDetailModel.setAttachment(null);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String tempDate = formatter.format(quizQuestion.getCreatedStamp());
            quizQuestionDetailModel.setCreatedStamp(tempDate);
        } catch (Exception e) {
            //  Block of code to handle errors
            e.printStackTrace();
        }
        List<QuizChoiceAnswer> quizChoiceAnswers = quizChoiceAnswerRepo.findAllByQuizQuestion(quizQuestion);
        //log.info("findQuizDetail, questionId = " +
        //         questionId +
        //         ", GOT quizChoideAnswers.sz = " +
        //         quizChoiceAnswers.size());

        //quizQuestionDetailModel.setQuizChoiceAnswerList(quizChoiceAnswers);
        List<QuizChoiceAnswerHideCorrectAnswer> ans = new ArrayList();
        for(QuizChoiceAnswer a: quizChoiceAnswers){
            ans.add(new QuizChoiceAnswerHideCorrectAnswer(a.getChoiceAnswerId(),a.getChoiceAnswerCode(),a.getChoiceAnswerContent()));
        }
        quizQuestionDetailModel.setQuizChoiceAnswerList(ans);

        return quizQuestionDetailModel;
    }

    @Override
    public QuizQuestion changeOpenCloseStatus(UserLogin u, UUID questionId) {
        QuizQuestion quizQuestion = quizQuestionRepo.findById(questionId).orElse(null);
        if (quizQuestion.getStatusId().equals(QuizQuestion.STATUS_PRIVATE)) {
            quizQuestion.setStatusId(QuizQuestion.STATUS_PUBLIC);
            List<String> userLoginIds = userService.findAllUserLoginIdOfGroup(
                "ROLE_EDUCATION_LEARNING_MANAGEMENT_STUDENT");

            for (String userLoginId : userLoginIds) {
                notificationsService.create(u.getUserLoginId(), userLoginId,
                                            u.getUserLoginId() + " vừa public quiz " +
                                            quizQuestion.getQuizCourseTopic().getQuizCourseTopicName() + " của môn "
                                            + quizQuestion.getQuizCourseTopic().getEduCourse().getName()
                    , "");
                log.info("changeOpenCloseStatus, push notif to " + userLoginId);
            }
        } else {
            quizQuestion.setStatusId(QuizQuestion.STATUS_PRIVATE);
        }
        quizQuestion = quizQuestionRepo.save(quizQuestion);
        return quizQuestion;
    }

    @Override
    public boolean checkAnswer(String userId, QuizChooseAnswerInputModel quizChooseAnswerInputModel) {
        //QuizQuestionDetailModel quizQuestionDetail = findQuizDetail(quizChooseAnswerInputModel.getQuestionId());
        QuizQuestion q = quizQuestionRepo.findById(quizChooseAnswerInputModel.getQuestionId()).orElse(null);
        if(q == null) return false;
        List<QuizChoiceAnswer> quizChoiceAnswers = quizChoiceAnswerRepo.findAllByQuizQuestion(q);


        String classCode = "";
        if(quizChooseAnswerInputModel.getClassId() != null) {
            EduClass eduClass = classRepo.findById(quizChooseAnswerInputModel.getClassId()).orElse(null);
            if(eduClass != null)
                classCode = eduClass.getClassCode();
        }

        String isCorrectAnswer = "Y";
        boolean ans = true;


        List<UUID> correctAns =
            //quizQuestionDetail.getQuizChoiceAnswerList()
            quizChoiceAnswers
            .stream()
            .filter(answer -> answer.getIsCorrectAnswer() == 'Y')
            .map(QuizChoiceAnswer::getChoiceAnswerId)
            .collect(Collectors.toList());

        if (!correctAns.containsAll(quizChooseAnswerInputModel.getChooseAnsIds())) {
            ans = false;
            isCorrectAnswer = "N";
        }

        // log information
        LogUserLoginQuizQuestion logUserLoginQuizQuestion = new LogUserLoginQuizQuestion();
        logUserLoginQuizQuestion.setUserLoginId(userId);
        logUserLoginQuizQuestion.setQuestionId(quizChooseAnswerInputModel.getQuestionId());
        logUserLoginQuizQuestion.setCreateStamp(new Date());

        logUserLoginQuizQuestion.setIsCorrectAnswer(isCorrectAnswer);
        //if (eduClass != null) {
        //    logUserLoginQuizQuestion.setClassCode(eduClass.getClassCode());
        //}
        logUserLoginQuizQuestion.setClassCode(classCode);

        logUserLoginQuizQuestion.setClassId(quizChooseAnswerInputModel.getClassId());


        //logUserLoginQuizQuestion.setQuestionTopicId(quizQuestionDetail.getQuizCourseTopic().getQuizCourseTopicId());
        //logUserLoginQuizQuestion.setQuestionTopicName(quizQuestionDetail.getQuizCourseTopic().getQuizCourseTopicName());
        logUserLoginQuizQuestion.setQuestionTopicId(q.getQuizCourseTopic().getQuizCourseTopicId());
        logUserLoginQuizQuestion.setQuestionTopicName(q.getQuizCourseTopic().getQuizCourseTopicName());

        logUserLoginQuizQuestion = logUserLoginQuizQuestionRepo.save(logUserLoginQuizQuestion);

        return ans;
    }

    @Override
    public QuizQuestionDetailModel findById(UUID questionId) {
        QuizQuestion quizQuestion = quizQuestionRepo.findById(questionId).orElse(null);
        QuizQuestionDetailModel quizQuestionDetailModel = new QuizQuestionDetailModel();
        quizQuestionDetailModel.setQuizCourseTopic(quizQuestion.getQuizCourseTopic());
        quizQuestionDetailModel.setCreatedByUserLoginId(quizQuestion.getCreatedByUserLoginId());
        quizQuestionDetailModel.setLevelId(quizQuestion.getLevelId());
        quizQuestionDetailModel.setQuestionContent(quizQuestion.getQuestionContent());
        quizQuestionDetailModel.setQuestionId(quizQuestion.getQuestionId());
        quizQuestionDetailModel.setStatusId(quizQuestion.getStatusId());
        quizQuestionDetailModel.setStatement(quizQuestion.getQuestionContent());
        quizQuestionDetailModel.setSolutionContent(quizQuestion.getSolutionContent());

        String solutionAttachment = quizQuestion.getSolutionAttachment();
        if (solutionAttachment != null && !solutionAttachment.trim().equals("")) {
            quizQuestionDetailModel.setSolutionAttachmentIds(quizQuestion.getSolutionAttachment().split(";"));
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String tempDate = formatter.format(quizQuestion.getCreatedStamp());
            quizQuestionDetailModel.setCreatedStamp(tempDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<QuizChoiceAnswer> quizChoiceAnswers = quizChoiceAnswerRepo.findAllByQuizQuestion(quizQuestion);
        List<QuizChoiceAnswerHideCorrectAnswer> ans = new ArrayList();
        for(QuizChoiceAnswer a: quizChoiceAnswers){
            ans.add(new QuizChoiceAnswerHideCorrectAnswer(a.getChoiceAnswerId(),a.getChoiceAnswerCode(),a.getChoiceAnswerContent()));
        }
        //quizQuestionDetailModel.setQuizChoiceAnswerList(quizChoiceAnswers);
        quizQuestionDetailModel.setQuizChoiceAnswerList(ans);


        if (quizQuestion.getAttachment() != null) {
            String[] fileId = quizQuestion.getAttachment().split(";", -1);
            if (fileId.length != 0) {
                List<byte[]> fileArray = new ArrayList<>();
                for (String s : fileId) {
                    try {
                        GridFsResource content = mongoContentService.getById(s);
                        if (content != null) {
                            InputStream inputStream = content.getInputStream();
                            fileArray.add(IOUtils.toByteArray(inputStream));
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                quizQuestionDetailModel.setAttachment(fileArray);
            } else {
                quizQuestionDetailModel.setAttachment(null);
            }
        } else {
            quizQuestionDetailModel.setAttachment(null);
        }
        return quizQuestionDetailModel;
    }

    @Override
    public QuizQuestion update(String userId, UUID questionId, String json, MultipartFile[] files, MultipartFile[] addedSolutionAttachments) {
//        Date now = new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        String prefixFileName = formatter.format(now);
//        ArrayList<String> attachmentPaths = new ArrayList<>();
//        Arrays.asList(files).forEach(file -> {
//            try {
//                Path path = Paths.get(properties.getFilesystemRoot() +"\\"+ properties.getClassManagementDataPath());
//                String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
//
//                if (file.isEmpty()) {
//                    throw new StorageException("Failed to store empty file " + originalFileName);
//                }
//
//                if (originalFileName.contains("..")) {
//                    // This is a security check
//                    throw new StorageException(
//                        "Cannot store file with relative path outside current directory "
//                        + originalFileName);
//                }
//
//                // Can throw IOExeption, e.g NoSuchFileException.
//                Files.copy(file.getInputStream(), path.resolve(prefixFileName + "-" + originalFileName), StandardCopyOption.REPLACE_EXISTING);
//                attachmentPaths.add(prefixFileName + "-" + file.getOriginalFilename());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
        //taskInput.setAttachmentPaths(attachmentPaths.toArray(new String[0]));

        Gson gson = new Gson();
        QuizQuestionUpdateInputModel input = gson.fromJson(json, QuizQuestionUpdateInputModel.class);
        List<String> attachmentId = new ArrayList<>();
        String[] fileId = input.getFileId();
        List<MultipartFile> fileArray = Arrays.asList(files);

        fileArray.forEach((file) -> {
            ContentModel model = new ContentModel(fileId[fileArray.indexOf(file)], file);
            log.info("createQuizQuestion, fileId: " + fileId[fileArray.indexOf(file)]);

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

        QuizQuestion quizQuestionTemp = quizQuestionRepo.findById(questionId).orElse(null);
        if (quizQuestionTemp == null) {
            return null;
        }
        QuizQuestion quizQuestion = new QuizQuestion();
        quizQuestion.setQuestionId(questionId);
        quizQuestion.setLevelId(input.getLevelId());
        quizQuestion.setQuestionContent(input.getQuestionContent());
        quizQuestion.setCreatedByUserLoginId(userId);
        //quizQuestion.setCreatedByUserLoginId(quizQuestionTemp.getCreatedByUserLoginId());

        QuizCourseTopic quizCourseTopic = quizCourseTopicRepo.findById(input.getQuizCourseTopicId()).orElse(null);
        if (quizCourseTopic == null) {
            return null;
        }
        quizQuestion.setQuizCourseTopic(quizCourseTopic);
        quizQuestion.setAttachment(String.join(";", attachmentId));
        quizQuestion.setLastUpdatedStamp(new Date());
        quizQuestion.setCreatedStamp(quizQuestionTemp.getCreatedStamp());
        quizQuestion.setStatusId(quizQuestionTemp.getStatusId());
        quizQuestion.setSolutionContent(input.getSolutionContent());

        List<String> addedSolutionAttachmentIds = mongoContentService.storeFiles(addedSolutionAttachments);
        String[] oldSolutionAttachments = {};
        if (quizQuestionTemp.getSolutionAttachment() != null) {
            oldSolutionAttachments = quizQuestionTemp.getSolutionAttachment().split(";");
        }
        List<String> solutionAttachmentIds = Arrays.stream(oldSolutionAttachments)
                                                   .collect(Collectors.toList());
        solutionAttachmentIds.removeAll(Arrays.asList(input.getDeletedAttachmentIds()));
        solutionAttachmentIds.addAll(addedSolutionAttachmentIds);
        String newSolutionAttachmentIds = solutionAttachmentIds.size() == 0 ? null : String.join(";", solutionAttachmentIds);
        quizQuestion.setSolutionAttachment(newSolutionAttachmentIds);

        for (String deletedAttachmentId : input.getDeletedAttachmentIds()) {
            mongoContentService.deleteFilesById(deletedAttachmentId);
        }

        quizQuestion = quizQuestionRepo.save(quizQuestion);
        return quizQuestion;
    }

    public QuizQuestionUserRole addQuizQuestionUserRole(ModelCreateQuizQuestionUserRole input){
        QuizQuestionUserRole quizQuestionUserRole = new QuizQuestionUserRole();
        quizQuestionUserRole.setUserId(input.getUserId());
        quizQuestionUserRole.setQuestionId(input.getQuestionId());
        quizQuestionUserRole.setRoleId(input.getRoleId());
        quizQuestionUserRole.setCreatedStamp(new Date());
        quizQuestionUserRole = quizQuestionUserRoleRepo.save(quizQuestionUserRole);
        return quizQuestionUserRole;
    }

    public boolean grantRoleToUserOnAllQuizQuestions(String roleId, String userId){
        List<QuizQuestion> questions = quizQuestionRepo.findAll();
        for(QuizQuestion q: questions){
            QuizQuestionUserRole r = new QuizQuestionUserRole();
            r.setRoleId(roleId);
            r.setUserId(userId);
            r.setQuestionId(q.getQuestionId());
            r.setCreatedStamp(new Date());
            r = quizQuestionUserRoleRepo.save(r);
        }
        return true;
    }
    public List<QuizQuestionUserRole> getUsersGranttedToQuizQuestion(UUID questionId){
        List<QuizQuestionUserRole> res = quizQuestionUserRoleRepo.findAllByQuestionId(questionId);
        return res;
    }

    @Override
    public int generateChoiceCodesForAllQuizQuestions() {
        int cnt = 0;
        List<QuizQuestion> questions = quizQuestionRepo.findAll();
        for(QuizQuestion q: questions){
            List<QuizChoiceAnswer> choices = quizChoiceAnswerRepo.findAllByQuizQuestion(q);
            int idx = 0;
            for(QuizChoiceAnswer a: choices){
                idx += 1;
                String code = PREFIX_CHOICE_CODE + Utils.stdCode(idx,3);
                a.setChoiceAnswerCode(code);
                a = quizChoiceAnswerRepo.save(a);
                cnt += 1;
            }
        }
        return cnt;
    }
}
