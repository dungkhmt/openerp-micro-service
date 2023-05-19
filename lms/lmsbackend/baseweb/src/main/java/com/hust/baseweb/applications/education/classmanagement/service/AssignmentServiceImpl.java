package com.hust.baseweb.applications.education.classmanagement.service;

import com.hust.baseweb.applications.contentmanager.model.ContentModel;
import com.hust.baseweb.applications.contentmanager.repo.MongoContentService;
import com.hust.baseweb.applications.education.classmanagement.service.storage.FileSystemStorageServiceImpl;
import com.hust.baseweb.applications.education.classmanagement.service.storage.exception.StorageException;
import com.hust.baseweb.applications.education.classmanagement.utils.ZipOutputStreamUtils;
import com.hust.baseweb.applications.education.entity.Assignment;
import com.hust.baseweb.applications.education.entity.AssignmentSubmission;
import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.exception.ResponseFirstType;
import com.hust.baseweb.applications.education.exception.SimpleResponse;
import com.hust.baseweb.applications.education.model.CreateAssignmentIM;
import com.hust.baseweb.applications.education.model.GetSubmissionsOM;
import com.hust.baseweb.applications.education.model.getassignmentdetail.GetAssignmentDetailOM;
import com.hust.baseweb.applications.education.model.getassignmentdetail4teacher.GetAssignmentDetail4TeacherOM;
import com.hust.baseweb.applications.education.model.getassignmentdetail4teacher.Submission;
import com.hust.baseweb.applications.education.repo.AssignmentRepo;
import com.hust.baseweb.applications.education.repo.AssignmentSubmissionRepo;
import com.hust.baseweb.applications.education.repo.ClassRepo;
import com.hust.baseweb.config.FileSystemStorageProperties;
import com.hust.baseweb.entity.UserLogin;
import lombok.extern.log4j.Log4j2;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Log4j2
@Service
public class AssignmentServiceImpl implements AssignmentService {

    private AssignmentRepo assignRepo;

    private AssignmentSubmissionRepo submissionRepo;

    private ClassRepo classRepo;

    private FileSystemStorageServiceImpl storageService;

    private final String dataPath;

    private RedisTemplate<String, Object> redisTemplate;

    private final String storageFilesDownloadPath;

    @Autowired
    private MongoContentService mongoContentService;

    public AssignmentServiceImpl(
        AssignmentRepo assignRepo,
        AssignmentSubmissionRepo submissionRepo,
        ClassRepo classRepo,
        FileSystemStorageServiceImpl storageService,
        FileSystemStorageProperties properties,
        RedisTemplate<String, Object> redisTemplate
    ) {
        this.assignRepo = assignRepo;
        this.submissionRepo = submissionRepo;
        this.classRepo = classRepo;
        this.storageService = storageService;
        this.dataPath = properties.getFilesystemRoot() + properties.getClassManagementDataPath();
        this.redisTemplate = redisTemplate;
        this.storageFilesDownloadPath = properties.getFilesystemRoot() + "/filesDownload/";
    }

    private String getUserNameOfAuthUserWithRole(String token, String role) {
        if (null == token) {
            return null;
        }

        HashOperations<String, String, SecurityContextImpl> hashOperations = redisTemplate.opsForHash();
        SecurityContextImpl context = hashOperations.get(
            "spring:session:sessions:" + token,
            "sessionAttr:SPRING_SECURITY_CONTEXT");

        if (null == context) {
            return null;
        } else {
            Authentication authentication = context.getAuthentication();

            if (authentication.isAuthenticated()) {
                User user = (User) authentication.getPrincipal();

                if (user.getAuthorities().contains(new SimpleGrantedAuthority(role))) {
                    return user.getUsername();
                }
            }
        }

        return null;
    }

    @Override
    public String verifyDownloadPermission(UUID assignId, String token) {
        String username = getUserNameOfAuthUserWithRole(token, "ROLE_TEACHER");

        if (null != username) {
            if (0 == assignRepo.hasDownloadingPermission(username, assignId)) {
                return "Require downloading permission";
            }

            return null;
        } else {
            return "Invalid token";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GetAssignmentDetailOM getAssignmentDetail(UUID id, String studentId) {
        return new GetAssignmentDetailOM(
            assignRepo.getAssignmentDetail(id),
            submissionRepo.getSubmitedFilenameOf(id, studentId));
    }

    @Override
    @Transactional(readOnly = true)
    public GetAssignmentDetail4TeacherOM getAssignmentDetail4Teacher(UUID assignmentId) {
        UUID classId = UUID.fromString(assignRepo.getClassIdOf(assignmentId));
        List<Submission> submissions = assignRepo.getStudentSubmissionsOf(assignmentId);
        int noOfStudents = classRepo.getNoStudentsOf(classId);

        String noSubmissions = noOfStudents == 0 ? "0/0" : submissions.size() +
                                                           "/" +
                                                           noOfStudents +
                                                           " (" +
                                                           100 * submissions.size() / noOfStudents +
                                                           "%)";

        return new GetAssignmentDetail4TeacherOM(
            assignRepo.getAssignmentDetail(assignmentId),
            submissions,
            noSubmissions);
    }

    @Override
    @Transactional(readOnly = true)
    public void downloadSubmmissions(
        String assignmentId,
        List<String> studentIds,
        OutputStream outputStream
    ) throws IOException {
        List<File> fileToAdd = new ArrayList<>();
        List<GetSubmissionsOM> submissions = assignRepo.getSubmissionsOf(
            UUID.fromString(assignmentId),
            new HashSet<>(studentIds));

//        for (GetSubmissionsOM submission : submissions) {
//            fileToAdd.add(new File(dataPath +
//                                   assignmentId +
//                                   "/" +
//                                   submission.getStudentId() +
//                                   storageService.getFileExtension(submission.getOriginalFileName())));
//        }
        AssignmentSubmission sub = null;

        for (GetSubmissionsOM submission : submissions) {

            sub = submissionRepo.findByAssignmentIdAndStudentUserLoginId(
                UUID.fromString(assignmentId),
                submission.getStudentId());
            GridFsResource content = mongoContentService.getById(sub.getMaterialSourceMongoId());
            if (content != null) {
                //check if folder slides is existing
                File fileDownloadsDir = new File(storageFilesDownloadPath);
                if (!fileDownloadsDir.exists()) {
                    fileDownloadsDir.mkdirs();
                }
                File outputFile = new File(storageFilesDownloadPath + content.getFilename());
                try (OutputStream outputFileStream = new FileOutputStream(outputFile)) {
                    IOUtils.copy(content.getInputStream(), outputFileStream);
                    fileToAdd.add(outputFile);
                } catch (FileNotFoundException e) {
                    // handle exception here
                } catch (IOException e) {
                    // handle exception here
                }
//                    files.add(IOUtils.toByteArray(inputStream));
            }
        }

        // Zip files.
        ZipOutputStreamUtils.zip(
            outputStream,
            fileToAdd,
            CompressionMethod.DEFLATE,
            null,
            EncryptionMethod.AES,
            AesKeyStrength.KEY_STRENGTH_256);

        //delete files
        for (File file : fileToAdd) {
            file.delete();
        }
    }

    @Override
    @Transactional
    public SimpleResponse deleteAssignment(UUID id) {
        /*int isAssignExist = assignRepo.isAssignExist(id);

        if (0 == isAssignExist) {
            return new SimpleResponse(
                404,
                "not exist",
                "Bài tập không tồn tại");
        } else {
            // Delete meta-data.
            assignRepo.deleteAssignment(id);

            // Delete folder.
            try {
                storageService.deleteIfExists("", id.toString());
            } catch (IOException e) {
            *//*return new SimpleResponse(
                500,
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                null);*//*
            }

            return new SimpleResponse(200, null, null);
        }*/

        assignRepo.deleteAssignment(id);

        return new SimpleResponse(200, null, null);
    }

    @Override
    @Transactional
    public ResponseFirstType createAssignment(CreateAssignmentIM im) {
        ResponseFirstType res;

        // Save meta-data.
        res = validateTime(im.getOpenTime(), im.getCloseTime());

        if (res.getErrors().size() > 0) {
            return res;
        }

        EduClass eduClass;

        if (1 == classRepo.isClassExist(im.getClassId())) {
            eduClass = new EduClass();
            eduClass.setId(im.getClassId());
        } else {
            res.addError("classId", "not exist", "Lớp không tồn tại");
            return res;
        }

        Assignment assignment = new Assignment();

        assignment.setName(StringUtils.normalizeSpace(im.getName()));
        assignment.setSubject(im.getSubject());
        assignment.setOpenTime(im.getOpenTime());
        assignment.setCloseTime(im.getCloseTime());
        assignment.setEduClass(eduClass);

        assignment = assignRepo.save(assignment);

        // Create a folder for storing file.
        try {
            storageService.createFolder(assignment.getId().toString());
        } catch (IOException e) {
            log.info("ERROR in method createAssignment()");
            e.printStackTrace();
            throw new StorageException("Could not initialize storage", e);
        }

        return new ResponseFirstType(200);
    }

    @Override
    @Transactional
    public ResponseFirstType updateAssignment(UUID id, CreateAssignmentIM im) {
        ResponseFirstType res = new ResponseFirstType(400);

        Assignment assignment = assignRepo.findByIdAndDeletedFalse(id);

        if (null == assignment) {
            res.addError("id", "not exist", "Bài tập không tồn tại");
        } else {
            // Validate open and close time.
            Date currTime = new Date();

            // Error: open time> close time.
            if (im.getOpenTime().compareTo(im.getCloseTime()) > 0) {
                res.addError("closeTime", "require subsequent date", "Vui lòng chọn thời điểm sau ngày giao");
            }

            // Validate open time.
            // Old open time < current, only close time modification is allowed.
            if (assignment.getOpenTime().compareTo(currTime) < 0) {
                // Error: modify open time is not allowed.
                if (assignment.getOpenTime().compareTo(im.getOpenTime()) != 0) {
                    res.addError(
                        "openTime",
                        "not allowed changing",
                        "Vui lòng chọn thời điểm ban đầu vì bài tập đã được giao");
                    return res;
                }
            } else { // Allow to modify both open and close time.
                // Error: new open time < current.
                if (im.getOpenTime().compareTo(currTime) < 0) {
                    res.addError("openTime", "require future date", "Vui lòng chọn thời điểm trong tương lai");
                    return res;
                }
            }

            // Validate close time.
            if (assignment.getCloseTime().compareTo(im.getCloseTime()) != 0) {
                if (im.getCloseTime().compareTo(currTime) < 0) {
                    res.addError(
                        "closeTime",
                        "invalid change",
                        "Vui lòng chọn thời điểm ban đầu hoặc trong tương lai");
                }
            }

            if (res.getErrors().size() > 0) {
                return res;
            }

            // Valid update.
            assignment.setName(StringUtils.normalizeSpace(im.getName()));
            assignment.setSubject(im.getSubject());
            assignment.setOpenTime(im.getOpenTime());
            assignment.setCloseTime(im.getCloseTime());

            assignRepo.save(assignment);

            res = new ResponseFirstType(200);
        }

        return res;
    }

    @Override
    @Transactional
    public SimpleResponse saveSubmission(String studentId, UUID assignmentId, MultipartFile file) {
        SimpleResponse res;

        // Get file name.
        String originalFileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());

        //save file
        ObjectId materialSourceMongoId = null;
        try {
            ContentModel model = new ContentModel(file.getOriginalFilename(), file);

            //store image to mongodb
            materialSourceMongoId = mongoContentService.storeFileToGridFs(model);
        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException("Failed to store file " + originalFileName, e);
        }
        res = saveSubmissionMetaData(originalFileName, assignmentId, studentId, materialSourceMongoId.toString());

        if (200 != res.getStatus()) {
            return res;
        }

        // Save file.
//        try {
//            if (res.getMessage() != null) {
//                storageService.deleteIfExists(
//                    assignmentId.toString(),
//                    studentId + storageService.getFileExtension(res.getMessage()));
//            }
//
//            storageService.store(file, assignmentId.toString(), studentId);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new StorageException("Failed to store file " + originalFileName, e);
//        }

        res.setMessage("Tải lên thành công tệp '" + originalFileName + "'");
        return res;
    }

    @Transactional
    private SimpleResponse saveSubmissionMetaData(
        String originalFileName,
        UUID assignmentId,
        String studentId,
        String materialSourceMongoId
    ) {
        Date closeTime = assignRepo.getCloseTime(assignmentId);

        if (null == closeTime) {
            return new SimpleResponse(
                400,
                "not exist",
                "Bài tập không tồn tại");
        }

        if (closeTime.compareTo(new Date()) < 0) {
            return new SimpleResponse(
                400,
                "deadline exceeded",
                "Đã quá hạn nộp bài");
        }

        UserLogin student = new UserLogin();
        Assignment assignment = new Assignment();
        AssignmentSubmission submission = submissionRepo.findByAssignmentIdAndStudentUserLoginId(
            assignmentId,
            studentId);

        if (null == submission) {
            submission = new AssignmentSubmission();
        }

        //delete file if update file
        if (null != submission.getMaterialSourceMongoId()) {
            mongoContentService.deleteFilesById(submission.getMaterialSourceMongoId());
        }

        String submitedFileName = submission.getOriginalFileName();

        student.setUserLoginId(studentId);
        assignment.setId(assignmentId);

        submission.setAssignment(assignment);
        submission.setOriginalFileName(originalFileName);
        submission.setStudent(student);
        submission.setLastUpdatedStamp(new Date());
        submission.setMaterialSourceMongoId(materialSourceMongoId);

        submissionRepo.save(submission);

        return new SimpleResponse(200, null, submitedFileName);
    }

    private ResponseFirstType validateTime(Date openTime, Date closeTime) {
        ResponseFirstType res = new ResponseFirstType(400);

        if (openTime.compareTo(new Date()) < 0) {
            res.addError("openTime", "require future date", "Vui lòng chọn thời điểm trong tương lai");
        }

        if (openTime.compareTo(closeTime) > 0) {
            res.addError("closeTime", "require subsequent date", "Vui lòng chọn thời điểm sau ngày giao");
        }

        return res;
    }
}
