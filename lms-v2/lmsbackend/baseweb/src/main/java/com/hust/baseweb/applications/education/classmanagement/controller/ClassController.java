package com.hust.baseweb.applications.education.classmanagement.controller;

import com.google.gson.Gson;
import com.hust.baseweb.applications.contentmanager.model.ContentModel;
import com.hust.baseweb.applications.contentmanager.repo.MongoContentService;
import com.hust.baseweb.applications.education.classmanagement.enumeration.RegistStatus;
import com.hust.baseweb.applications.education.classmanagement.model.ModelAddUser2ClassInput;
import com.hust.baseweb.applications.education.classmanagement.model.ModelInputAddStudentToClassViaExcelUpload;
import com.hust.baseweb.applications.education.classmanagement.model.ModelResponseEduClassDetail;
import com.hust.baseweb.applications.education.classmanagement.service.ClassServiceImpl;
import com.hust.baseweb.applications.education.classmanagement.service.EduClassSessionService;
import com.hust.baseweb.applications.education.content.Video;
import com.hust.baseweb.applications.education.content.VideoService;
import com.hust.baseweb.applications.education.entity.*;
import com.hust.baseweb.applications.education.exception.SimpleResponse;
import com.hust.baseweb.applications.education.model.*;
import com.hust.baseweb.applications.education.model.educlassuserloginrole.AddEduClassUserLoginRoleIM;
import com.hust.baseweb.applications.education.model.educlassuserloginrole.ClassOfUserOM;
import com.hust.baseweb.applications.education.model.educlassuserloginrole.EduClassUserLoginRoleType;
import com.hust.baseweb.applications.education.repo.ClassRegistrationRepo;
import com.hust.baseweb.applications.education.repo.ClassRepo;
import com.hust.baseweb.applications.education.report.model.courseparticipation.StudentCourseParticipationModel;
import com.hust.baseweb.applications.education.report.model.quizparticipation.StudentQuizParticipationModel;
import com.hust.baseweb.applications.education.service.*;
import com.hust.baseweb.applications.notifications.service.NotificationsService;
import com.hust.baseweb.applications.programmingcontest.callexternalapi.model.LmsLogModelCreate;
import com.hust.baseweb.applications.programmingcontest.callexternalapi.service.ApiService;
import com.hust.baseweb.applications.programmingcontest.model.ModelUpdateContest;
import com.hust.baseweb.config.FileSystemStorageProperties;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.*;

@Log4j2
@Controller
@Validated
@RequestMapping("/edu/class")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ClassController {

    private ClassServiceImpl classService;
    private CourseService courseService;
    private SemesterService semesterService;
    private UserService userService;
    private EduDepartmentService eduDepartmentService;
    private EduCourseChapterService eduCourseChapterService;
    private EduCourseChapterMaterialService eduCourseChapterMaterialService;
    private EduClassMaterialService eduClassMaterialService;
    private LogUserLoginCourseChapterMaterialService logUserLoginCourseChapterMaterialService;
    private LogUserLoginQuizQuestionService logUserLoginQuizQuestionService;
    private VideoService videoService;
    private NotificationsService notificationsService;
    private ClassRepo classRepo;
    private FileSystemStorageProperties properties;
    private ClassRegistrationRepo classRegistrationRepo;
    private EduClassSessionService eduClassSessionService;

    ApiService apiService;

    @Autowired
    private MongoContentService mongoContentService;

    @PostMapping
    public ResponseEntity<?> getClassesOfCurrSemester(
        Principal principal,
        @RequestParam
        @Min(value = 0, message = "Số trang có giá trị không âm") Integer page,
        @RequestParam
        @Min(value = 0, message = "Kích thước trang có giá trị không âm") Integer size,
        @RequestBody GetClassesIM filterParams
    ) {
        log.info("getClassesOfCurrSemester");

        if (null == page) {
            page = 0;
        }

        if (null == size) {
            size = 20;
        }

        return ResponseEntity
            .ok()
            .body(classService.getClassesOfCurrentSemester(
                principal.getName(),
                filterParams,
                PageRequest.of(page, size)));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistIM im, Principal principal) {
        SimpleResponse res = classService.register(im.getClassId(), principal.getName());
        return ResponseEntity.status(res.getStatus()).body(res.getMessage());
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/add-class-user-login-role")
    public ResponseEntity addEduClassUserLoginRole(
        Principal principal,
        @RequestBody AddEduClassUserLoginRoleIM input
    ) {
        log.info("addEduClassUserLoginRole, classId = " + input.getClassId()
                 + " userlogin = " + input.getUserLoginId() + " roleId = " + input.getRoleId());

        EduClassUserLoginRole eduClassUserLoginRole = classService.addEduClassUserLoginRole(input);

        return ResponseEntity.ok().body(eduClassUserLoginRole);
    }

    @GetMapping("/{classId}/user-login-roles")
    public ResponseEntity getEduUserLoginRolesOfClass(@PathVariable UUID classId) {
        return ResponseEntity.ok(classService.getUserLoginRolesOfClass(classId));
    }

    @DeleteMapping("/class-user-login-roles")
    public ResponseEntity deleteEduClassUserLoginRole(@RequestBody AddEduClassUserLoginRoleIM deletedPermission) {
        classService.deleteEduClassUserLoginRole(deletedPermission);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-all-class")
    public ResponseEntity<?> getAllClass(Principal principal) {
        List<ModelResponseEduClassDetail> res = classService.getAllClass();
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-classes-of-user/{userLoginId}")
    public ResponseEntity getClassesOfUser(Principal principal, @PathVariable String userLoginId) {
        String currentUserLoginId = principal.getName();
        log.info("getClassesOfUser, currentUserLoginId = " + currentUserLoginId + " userLoginId = " + userLoginId);

        if (userLoginId.equals("null")) {
            userLoginId = currentUserLoginId;
        }

        List<ClassOfUserOM> eduClasses = classService.getClassOfUser(userLoginId);
        return ResponseEntity.ok().body(eduClasses);
    }

    @GetMapping("/get-role-list-educlass-userlogin")
    public ResponseEntity<?> getRoleListEduClassUserLogin() {
        List<EduClassUserLoginRoleType> lst = new ArrayList();
        lst.add(new EduClassUserLoginRoleType(EduClassUserLoginRole.ROLE_PARTICIPANT, "Người tham gia"));
        lst.add(new EduClassUserLoginRoleType(EduClassUserLoginRole.ROLE_OWNER, "Người tạo và sở hữu"));
        lst.add(new EduClassUserLoginRoleType(EduClassUserLoginRole.ROLE_MANAGER, "Người quản lý"));
        return ResponseEntity.ok().body(lst);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/update-class-status")
    public ResponseEntity updateClassStatus(
        Principal principal, @RequestParam UUID classId,
        @RequestParam String status
    ) {
        log.info("updateClassStatus, classId = " + classId + ", status = " + status);
        EduClass eduClass = classRepo.findById(classId).orElse(null);
        if (eduClass != null) {
            eduClass.setStatusId(status);
            eduClass = classRepo.save(eduClass);
        } else {
            log.info("updateClassStatus, classId = " + classId + ", status = " + status + " class NOT FOUND??");
        }
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<GetStudentsOfClassOM>> getStudentsOfClass(@PathVariable UUID id) {
        return ResponseEntity.ok().body(classService.getStudentsOfClass(id));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/add-all-users-to-class")
    public ResponseEntity<?> addAllUsers2Class(Principal principal, @RequestBody ModelAddUser2ClassInput I) {
        int cnt = classService.addAllUser2Class(I.getClassCode());
        return ResponseEntity.ok().body(cnt);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/{id}/registered-students")
    public ResponseEntity<List<GetStudentsOfClassOM>> getRegistStudentsOfClass(@PathVariable UUID id) {
        List<GetStudentsOfClassOM> lst = classService.getRegistStudentsOfClass(id);
        log.info("getRegistStudentsOfClass, lst.sz = " + lst.size());
        return ResponseEntity.ok().body(lst);
    }

    @Secured("ROLE_TEACHER")
    @PutMapping("/registration-status")
    public ResponseEntity<?> updateRegistStatus(@Valid @RequestBody UpdateRegistStatusIM im) {
        return ResponseEntity
            .ok()
            .body(classService.updateRegistStatus(im.getClassId(), im.getStudentIds(), im.getStatus()));
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/list/teacher")
    public ResponseEntity<?> getClassesOfTeacher(Principal principal) {
        return ResponseEntity.ok().body(classService.getClassesOfTeacher(principal.getName()));
    }

    @Async
    public void logUserGetRegisteredClasses(String userId){
        if (true) return;
        LmsLogModelCreate logM = new LmsLogModelCreate();
        logM.setUserId(userId);
        log.info("logUserGetRegisteredClasses, userId = " + logM.getUserId());


        logM.setActionType("USER_GET_REGISTERED_CLASSES_LIST");
        logM.setDescription("an user get list of registered classes");
        apiService.callLogAPI("https://analytics.soict.ai/api/log/create-log",logM);
    }

    @GetMapping("/list/student")
    public ResponseEntity<?> getClassesOfStudent(Principal principal) {
        logUserGetRegisteredClasses(principal.getName());

        return ResponseEntity.ok().body(classService.getClassesOfStudent(principal.getName()));
    }

    @Async
    public void logUserGetClassDetailForLearning(String userId, String courseName){
        if (true) return;
        LmsLogModelCreate logM = new LmsLogModelCreate();
        logM.setUserId(userId);
        log.info("logUserGetClassDetailForLearning, userId = " + logM.getUserId());
        logM.setParam1(courseName);

        logM.setActionType("USER_GET_CLASS_DETAIL_FOR_LEARNING");
        logM.setDescription("an user get a class detail for learning");
        apiService.callLogAPI("https://analytics.soict.ai/api/log/create-log",logM);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassDetail(Principal principal, @PathVariable UUID id) {
        String registrationStatus = classRegistrationRepo.checkRegistration(id, principal.getName());
        EduClass cls = classRepo.findById(id).orElse(null);
        boolean auth = false;
        if("APPROVED".equals(registrationStatus)) auth = true;

        String courseName = "";
        if(cls!=null){
            if(cls.getTeacher()!=null){
                if(cls.getEduCourse() != null){
                    courseName = cls.getEduCourse().getName();
                }
                if(principal.getName().equals(cls.getTeacher().getUserLoginId()))
                    auth = true;
            }
        }

        logUserGetClassDetailForLearning(principal.getName(),courseName);

        //log.info("getClassDetail, FOR TESTING, registrationStatus = " + registrationStatus + " auth = " + auth);

        //if (auth) {
        if (true){ 
            return ResponseEntity.ok().body(classService.getClassDetail(id));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/{id}/assignments/teacher")
    public ResponseEntity<?> getAssignOfClass4Teacher(@PathVariable UUID id) {
        return ResponseEntity.ok().body(classService.getAssign4Teacher(id));
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/{id}/all-student-assignments/teacher")
    public ResponseEntity<?> getAllStuAssignOfClass4Teacher(@PathVariable UUID id) {
        return ResponseEntity.ok().body(classService.getAllStuAssign4Teacher(id));
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/{id}/assignmentsSubmission/teacher")
    public ResponseEntity<?> getAssignSubmitOfClass4Teacher(@PathVariable UUID id) {
        return ResponseEntity.ok().body(classService.getAssignSubmit4Teacher(id));
    }

    @GetMapping("/{id}/assignments/student")
    public ResponseEntity<?> getAssignOfClass4Student(@PathVariable UUID id) {
        return ResponseEntity.ok().body(classService.getAssign4Student(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEduClass(Principal principal, @RequestBody AddClassModel addClassModel) {
        log.info("addEduClass, start....");
        UserLogin userLogin = userService.findById(principal.getName());
        EduClass aClass = classService.save(userLogin, addClassModel);
        eduClassSessionService.addCourseSessionToClass(aClass);
        // eduClassMaterialService.addCourseMaterialsToClass(aClass);
        return ResponseEntity.ok().body(aClass);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/get-all-courses")
    public ResponseEntity<?> getAllCourses(Principal principal) {
        log.info("getAllCourses start...");
        List<EduCourse> courses = courseService.findAll();
        log.info("getAllCourses, GOT " + courses.size());
        return ResponseEntity.ok().body(courses);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/get-chapters-of-course/{courseId}")
    public ResponseEntity<?> getChaptersOfCourse(Principal principal, @PathVariable String courseId) {
        log.info("getChaptersOfCourse start... courseId = " + courseId);
        //List<EduCourseChapter> eduCourseChapters = eduCourseChapterService.findAll();
        List<EduCourseChapter> eduCourseChapters = eduCourseChapterService.findAllByCourseId(courseId);
        log.info("getChaptersOfCourse, GOT " + eduCourseChapters.size());
        return ResponseEntity.ok().body(eduCourseChapters);
    }

    @GetMapping("/get-chapters-of-class/{classId}")
    public ResponseEntity<?> getChaptersOfClass(Principal principal, @PathVariable UUID classId) {
        String registrationStatus = classRegistrationRepo.checkRegistration(classId, principal.getName());
        if ("APPROVED".equals(registrationStatus)) {

            GetClassDetailOM eduClass = classService.getClassDetail(classId);
            String courseId = eduClass.getCourseId();
            
            List<EduCourseChapter> eduCourseChapters = eduCourseChapterService.findAllByCourseId(courseId);
            //        log.info("getChaptersOfClass, classId = " + classId + ", courseId = " + courseId
            //                 + " RETURN list.sz = " + eduCourseChapters.size());
            
            return ResponseEntity.ok().body(eduCourseChapters);
        } else return ResponseEntity.status(403).build();

    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/create-chapter-of-course")
    public ResponseEntity<?> createChapterOfCourse(
        Principal principal,
        @RequestBody EduCourseChapterModelCreate eduCourseChapterModelCreate
    ) {
        log.info("createChapterOfCourse, courseId = " + eduCourseChapterModelCreate.getCourseId() + " chapterName = " +
                 eduCourseChapterModelCreate.getChapterName());
        EduCourseChapter eduCourseChapter = eduCourseChapterService.save(eduCourseChapterModelCreate);
        return ResponseEntity.ok().body(eduCourseChapter);
    }

    @GetMapping("/change-chapter-status/{chapterId}")
    public ResponseEntity<?> changeChapterStatus(Principal principal, @PathVariable UUID chapterId) {
        log.info("changeChapterStatus, chapterId = " + chapterId);
        String statusId = eduCourseChapterService.changeOpenCloseChapterStatus(chapterId);
        return ResponseEntity.ok().body(statusId);
    }

    @GetMapping("/get-course-chapter-material-type-list")
    public ResponseEntity<?> getCourseChapterMaterialTypeList(Principal principal) {
        log.info("getCourseChapterMaterialTypeList");
        List<String> types = new ArrayList<>();
        types.add(EduCourseChapterMaterial.EDU_COURSE_MATERIAL_TYPE_SLIDE);
        types.add(EduCourseChapterMaterial.EDU_COURSE_MATERIAL_TYPE_VIDEO);
        return ResponseEntity.ok().body(types);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/create-chapter-material-of-course")
    //public ResponseEntity<?> createChapterMaterialOfCourse(Principal principal, @RequestBody
    //  EduCourseChapterMaterialModelCreate eduCourseChapterMaterialModelCreate){
    public ResponseEntity<?> createChapterMaterialOfCourse(
        Principal principal, @RequestParam("inputJson") String inputJson,
        @RequestParam("files") MultipartFile[] files
    ) {
        UserLogin u = userService.findById(principal.getName());

        Gson gson = new Gson();
        EduCourseChapterMaterialModelCreate eduCourseChapterMaterialModelCreate = gson.fromJson(
            inputJson,
            EduCourseChapterMaterialModelCreate.class);

        log.info("createChapterMaterialOfCourse, chapterId = " + eduCourseChapterMaterialModelCreate.getChapterId()
                 + " materialName = " + eduCourseChapterMaterialModelCreate.getMaterialName() + " materialType = " +
                 eduCourseChapterMaterialModelCreate.getMaterialType());
        EduCourseChapterMaterial eduCourseChapterMaterial = null;
        try {

            log.info("file type = " + eduCourseChapterMaterialModelCreate.getMaterialType());
            if (eduCourseChapterMaterialModelCreate.getMaterialType().equals("EDU_COURSE_MATERIAL_TYPE_SLIDE")) {
//                List<String> attachmentId = new ArrayList<>();
//
//                PDDocument document = PDDocument.load(files[0].getInputStream());
//                PDFRenderer pdfRenderer = new PDFRenderer(document);
//
//                //check if folder slides is existing
//                File slidesDir = new File(properties.getFilesystemRoot() + "/slides/");
//                if (!slidesDir.exists()){
//                    slidesDir.mkdirs();
//                }
//
//                //change pdf format to png and hash image
//                int numberOfPages = document.getNumberOfPages();
//                System.out.println("Total files to be converting -> "+ numberOfPages);
//
//                String fileName = files[0].getName().replace(".pdf", "");
//                String fileExtension= "png";
//
//                int dpi = 150;  //for less store hard disk
//                for (int i = 0; i < numberOfPages; ++i) {
//                    File outPutFile = new File(properties.getFilesystemRoot() + "/slides/" + fileName +"_"+ (i+1) +"."+ fileExtension);
//                    BufferedImage bImage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
//                    ImageIO.write(bImage, fileExtension, outPutFile);
//
//                    // change type File to type MultipartFile
//                    FileInputStream input = new FileInputStream(outPutFile);
//                    MultipartFile multipartFileImage = new MockMultipartFile(
//                        "file",
//                        outPutFile.getName(),
//                        "text/plain",
//                        IOUtils.toByteArray(input)
//                    );
//                    //hash image to binary
//                    ObjectId id = null;
//
//                    long imageId = new Date().getTime();
//                    ContentModel model = new ContentModel( String.valueOf(imageId), multipartFileImage);
//
//                    //store image to mongodb
//                    try {
//                        id = mongoContentService.storeFileToGridFs(model);
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    // add list id to array attachmentId
//                    if (id != null) {
//                        ContentHeaderModel rs = new ContentHeaderModel(id.toHexString());
//                        attachmentId.add(rs.getId());
//                    }
//
//                    //delete file image
//                    try  {
//                        outPutFile.delete();
//                    }
//                    catch (Exception e){
//                     e.printStackTrace();
//                    }
//                }
//                String stringIdList = String.join(";", attachmentId);

                // add list id to array attachmentId
//                if (id != null) {
//                    ContentHeaderModel rs = new ContentHeaderModel(id.toHexString());
//                    attachmentId.add(rs.getId());
//                }
                ContentModel model = new ContentModel(files[0].getOriginalFilename(), files[0]);
                ObjectId id = null;
                try {
                    id = mongoContentService.storeFileToGridFs(model);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                eduCourseChapterMaterial = eduCourseChapterMaterialService.saveSlide(
                    eduCourseChapterMaterialModelCreate,
                    id.toString());
            } else if (eduCourseChapterMaterialModelCreate.getMaterialType().equals("EDU_COURSE_MATERIAL_TYPE_VIDEO")) {
                Video video = videoService.create(files[0]);
                log.info("createChapterMaterialOfCourse, videoId = " + video.getId());
                eduCourseChapterMaterial = eduCourseChapterMaterialService.save(
                    eduCourseChapterMaterialModelCreate,
                    video);

                log.info("*************************");
                log.info("slide content mongo db " + eduCourseChapterMaterial.getSlideId());

            }

        } catch (Exception e) {
            //return error to frontend
            e.printStackTrace();
            Map<String, Boolean> temp = new HashMap<>();
            temp.put("error", true);
            return ResponseEntity.ok().body(temp);
        }

        // push notification
        List<String> userLoginIds = userService
            .findAllUserLoginIdOfGroup("ROLE_STUDENT");
        for (String userLoginId : userLoginIds) {
            log.info("createChapterMaterialOfCourse, push notif to " + userLoginId);
            notificationsService.create(u.getUserLoginId(), userLoginId,
                                        u.getUserLoginId() +
                                        " vừa upload bài giảng "
                                        +
                                        eduCourseChapterMaterialModelCreate.getMaterialName()
                                        +
                                        ", chương " +
                                        eduCourseChapterMaterial.getEduCourseChapter().getChapterName()
                                        +
                                        ", môn học" +
                                        eduCourseChapterMaterial.getEduCourseChapter().getEduCourse().getName(),
                                        "");
        }
        log.info("create successful");
        return ResponseEntity.ok().body(eduCourseChapterMaterial);
    }

    @Secured("ROLE_TEACHER")
    @DeleteMapping("/delete-course-chapter-material-detail-slide-video/{chapterMarialId}")
    public ResponseEntity<?> deleteCourseChapterMaterialSlideOrVideo(
        Principal principal,
        @PathVariable UUID chapterMarialId
    ) {
        log.info("run here");
        EduCourseChapterMaterial eduCourseChapterMaterial = null;
        try {
            eduCourseChapterMaterial = eduCourseChapterMaterialService.findById(chapterMarialId);
            log.info("find successs");

            String materialType = eduCourseChapterMaterial.getEduCourseMaterialType();
            if (materialType.equals("EDU_COURSE_MATERIAL_TYPE_SLIDE")) {
                log.info("delete slide");

                String[] fileIds = eduCourseChapterMaterial.getSlideId().split(";");
                if (fileIds.length != 0) {
                    for (String fileId : fileIds) {
                        try {
                            mongoContentService.deleteFilesById(fileId);
                            log.info(fileId);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                log.info("delete slide successful");
                eduCourseChapterMaterialService.updateMaterial(
                    eduCourseChapterMaterial.getEduCourseMaterialId(),
                    eduCourseChapterMaterial.getEduCourseMaterialName(),
                    null,
                    null,
                    eduCourseChapterMaterial.getSourceId()
                );
            } else if (materialType.equals("EDU_COURSE_MATERIAL_TYPE_VIDEO")) {
                videoService.deleteVideo(eduCourseChapterMaterial.getSourceId());
                eduCourseChapterMaterialService.updateMaterial(
                    eduCourseChapterMaterial.getEduCourseMaterialId(),
                    eduCourseChapterMaterial.getEduCourseMaterialName(),
                    null,
                    null,
                    null
                );
                log.info("delete video");
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Boolean> temp = new HashMap<>();
            temp.put("error", true);
            ResponseEntity.ok().body(eduCourseChapterMaterial);
        }

        return ResponseEntity.ok().body(eduCourseChapterMaterial);
    }

    @Secured("ROLE_TEACHER")
    @PutMapping("/update-course-chapter-material-detail/{chapterMarialId}")
    public ResponseEntity<?> updateCourseChapterMaterial(
        Principal principal,
        @PathVariable UUID chapterMarialId,
        @RequestBody EduCourseChapterMaterial eduCourseChapterMaterial
    ) {
        log.info(eduCourseChapterMaterial);
        try {
            eduCourseChapterMaterial = eduCourseChapterMaterialService.findById(chapterMarialId);
            log.info("find successs");

            eduCourseChapterMaterialService.updateMaterial(
                eduCourseChapterMaterial.getEduCourseMaterialId(),
                eduCourseChapterMaterial.getEduCourseMaterialName(),
                eduCourseChapterMaterial.getEduCourseMaterialType(),
                eduCourseChapterMaterial.getSlideId(),
                eduCourseChapterMaterial.getSourceId()
            );
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Boolean> temp = new HashMap<>();
            temp.put("error", true);
            ResponseEntity.ok().body(eduCourseChapterMaterial);
        }

        return ResponseEntity.ok().body(eduCourseChapterMaterial);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/update-chapter-material-of-course")
    //public ResponseEntity<?> createChapterMaterialOfCourse(Principal principal, @RequestBody
    //  EduCourseChapterMaterialModelCreate eduCourseChapterMaterialModelCreate){
    public ResponseEntity<?> updateChapterMaterialOfCourse(
        Principal principal, @RequestParam("inputJson") String inputJson,
        @RequestParam("files") MultipartFile[] files
    ) {
        UserLogin u = userService.findById(principal.getName());

        Gson gson = new Gson();


        EduCourseChapterMaterial eduCourseChapterMaterial = gson.fromJson(
            inputJson,
            EduCourseChapterMaterial.class);

        EduCourseChapterMaterial oldEduCourseChapterMaterial = eduCourseChapterMaterialService.findById(
            eduCourseChapterMaterial.getEduCourseMaterialId());
        String oldListStringId = oldEduCourseChapterMaterial.getSlideId();
        UUID oldSourceId = oldEduCourseChapterMaterial.getSourceId();
        try {

            log.info("file type = " + eduCourseChapterMaterial.getEduCourseMaterialType());
            if (eduCourseChapterMaterial.getEduCourseMaterialType().equals("EDU_COURSE_MATERIAL_TYPE_SLIDE")) {
//                List<String> attachmentId = new ArrayList<>();
//
//                PDDocument document = PDDocument.load(files[0].getInputStream());
//                PDFRenderer pdfRenderer = new PDFRenderer(document);
//
//                //check if folder slides is existing
//                File slidesDir = new File(properties.getFilesystemRoot() + "/slides/");
//                if (!slidesDir.exists()){
//                    slidesDir.mkdirs();
//                }
//
//                //change pdf format to png and hash image
//                int numberOfPages = document.getNumberOfPages();
//                System.out.println("Total files to be converting -> "+ numberOfPages);
//
//                String fileName = files[0].getName().replace(".pdf", "");
//                String fileExtension= "png";
//
//                int dpi = 150;  //for less store hard disk
//                for (int i = 0; i < numberOfPages; ++i) {
//                    File outPutFile = new File(properties.getFilesystemRoot() + "/slides/" + fileName +"_"+ (i+1) +"."+ fileExtension);
//                    BufferedImage bImage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
//                    ImageIO.write(bImage, fileExtension, outPutFile);
//
//                    // change type File to type MultipartFile
//                    FileInputStream input = new FileInputStream(outPutFile);
//                    MultipartFile multipartFileImage = new MockMultipartFile(
//                        "file",
//                        outPutFile.getName(),
//                        "text/plain",
//                        IOUtils.toByteArray(input)
//                    );
//                    //hash image to binary
//                    ObjectId id = null;
//
//                    long imageId = new Date().getTime();
//                    ContentModel model = new ContentModel( String.valueOf(imageId), multipartFileImage);
//
//                    //store image to mongodb
//                    try {
//                        id = mongoContentService.storeFileToGridFs(model);
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    // add list id to array attachmentId
//                    if (id != null) {
//                        ContentHeaderModel rs = new ContentHeaderModel(id.toHexString());
//                        attachmentId.add(rs.getId());
//                    }
//
//                    //delete file image
//                    try  {
//                        outPutFile.delete();
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//                String stringIdList = String.join(";", attachmentId);
                ContentModel model = new ContentModel(files[0].getOriginalFilename(), files[0]);
                ObjectId id = null;
                try {
                    id = mongoContentService.storeFileToGridFs(model);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                EduCourseChapterMaterial newEduCourseChapterMaterial = eduCourseChapterMaterialService.updateMaterial(
                    eduCourseChapterMaterial.getEduCourseMaterialId(),
                    eduCourseChapterMaterial.getEduCourseMaterialName(),
                    eduCourseChapterMaterial.getEduCourseMaterialType(),
                    id.toString(),
                    null
                );
//                document.close();
            } else if (eduCourseChapterMaterial.getEduCourseMaterialType().equals("EDU_COURSE_MATERIAL_TYPE_VIDEO")) {
                Video video = videoService.create(files[0]);
                log.info("createChapterMaterialOfCourse, videoId = " + video.getId());

                EduCourseChapterMaterial newEduCourseChapterMaterial = eduCourseChapterMaterialService.updateMaterial(
                    eduCourseChapterMaterial.getEduCourseMaterialId(),
                    eduCourseChapterMaterial.getEduCourseMaterialName(),
                    eduCourseChapterMaterial.getEduCourseMaterialType(),
                    null,
                    video.getId()
                );
                log.info("*************************");
                log.info("slide content mongo db " + eduCourseChapterMaterial.getSlideId());

            }

            //delete old file
            //delete slide
            if (oldListStringId != null) {
                String[] fileIds = oldListStringId.split(";");
                if (fileIds.length != 0) {
                    for (String fileId : fileIds) {
                        try {
                            mongoContentService.deleteFilesById(fileId);
                            log.info(fileId);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                log.info("delete old slide successful");
            }
            //delete video
            if (oldSourceId != null) {
                videoService.deleteVideo(oldSourceId);
                log.info("delete old video successful");
            }
            //delete video

        } catch (Exception e) {
            //return error to frontend
            e.printStackTrace();
            Map<String, Boolean> temp = new HashMap<>();
            temp.put("error", true);
            return ResponseEntity.ok().body(temp);
        }

        log.info("change successful");
        return ResponseEntity.ok().body(eduCourseChapterMaterial);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/get-course-chapter-material-detail/{id}")
    public ResponseEntity<?> getCourseChapterMaterialDetail(Principal principal, @PathVariable UUID id) {
        //log.info("getCourseChapterMaterialDetail, id = " + id);
        UserLogin userLogin = userService.findById(principal.getName());
        logUserLoginCourseChapterMaterialService.logUserLoginMaterial(userLogin, id);
        //log.info("getCourseChapterMaterialDetail, id = " + id);
        EduCourseChapterMaterial eduCourseChapterMaterial = eduCourseChapterMaterialService.findById(id);
        return ResponseEntity.ok().body(eduCourseChapterMaterial);
    }

    @Async
    public void logUserViewCourseMaterial(String userId, String courseName,
                                          String chapterName, String materialName){
        if (true) return;
        LmsLogModelCreate logM = new LmsLogModelCreate();
        logM.setUserId(userId);
        log.info("logUpdateContest, userId = " + logM.getUserId());
        logM.setParam1(courseName);
        logM.setParam2(chapterName);
        logM.setParam3(materialName);

        logM.setActionType("USER_VIEW_COURSE_MATERIAL_DETAIL");
        logM.setDescription("an user views detail about a course material");
        apiService.callLogAPI("https://analytics.soict.ai/api/log/create-log",logM);
    }


    @GetMapping("/get-course-chapter-material-detail/{courseId}/{classId}")
    public ResponseEntity<?> getCourseChapterMaterialDetailV2(
        Principal principal, @PathVariable UUID courseId, @PathVariable UUID classId
    ) {
        log.info("getCourseChapterMaterialDetail, id = " + courseId);
        String userId = principal.getName();
        logUserLoginCourseChapterMaterialService.logUserLoginMaterialV2(userId, classId, courseId);
        //log.info("getCourseChapterMaterialDetail, id = " + courseId);
        EduCourseChapterMaterial eduCourseChapterMaterial = eduCourseChapterMaterialService.findById(courseId);
        String materialName = eduCourseChapterMaterial.getEduCourseMaterialName();
        String chapterName = "";
        String courseName = "";

        if(eduCourseChapterMaterial.getEduCourseChapter() != null){
            chapterName = eduCourseChapterMaterial.getEduCourseChapter().getChapterName();
            if(eduCourseChapterMaterial.getEduCourseChapter().getEduCourse() != null)
                eduCourseChapterMaterial.getEduCourseChapter().getEduCourse().getName();
        }

        logUserViewCourseMaterial(principal.getName(),courseName,chapterName,materialName);

        return ResponseEntity.ok().body(eduCourseChapterMaterial);
    }

    @GetMapping("/get-chapter-materials-of-course/{chapterId}")
    public ResponseEntity<?> getChapterMaterialsOfCourse(Principal principal, @PathVariable UUID chapterId) {
        //List<EduCourseChapterMaterial> eduCourseChapterMaterials = eduCourseChapterMaterialService.findAll();
        List<EduCourseChapterMaterial> eduCourseChapterMaterials = eduCourseChapterMaterialService.findAllByChapterId(
            chapterId);
        return ResponseEntity.ok().body(eduCourseChapterMaterials);
    }

    @GetMapping("/get-chapter-materials-of-class/{classId}/{chapterId}")
    public ResponseEntity<?> getChapterMaterialsOfClass(Principal principal, @PathVariable UUID classId, @PathVariable UUID chapterId) {
        //List<EduCourseChapterMaterial> eduCourseChapterMaterials = eduCourseChapterMaterialService.findAll();

        List<EduClassMaterial> eduClassChapterMaterials = eduClassMaterialService.getMaterialByClassIdAndChapterId(
            classId, chapterId);
        return ResponseEntity.ok().body(eduClassChapterMaterials);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/get-all-semesters")
    public ResponseEntity<?> getAllSemesters(Principal principal) {
        log.info("getAllSemester start...");
        List<Semester> semesters = semesterService.findAll();
        log.info("getAllSemester GOT " + semesters.size());
        return ResponseEntity.ok().body(semesters);
    }

    @GetMapping("/get-all-departments")
    public ResponseEntity<?> getAllEduDepartments(Principal principal) {

        List<EduDepartment> eduDepartments = eduDepartmentService.findAll();
        log.info("getAllEduDepartments, GOT sz = " + eduDepartments.size());
        return ResponseEntity.ok().body(eduDepartments);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/get-log-user-course-chapter-material/{classId}")
    public ResponseEntity<?> getLogUserCourseChapterMaterial(Principal principal, @PathVariable UUID classId) {
        log.info("getLogUserCourseChapterMaterial, classId = " + classId);
        UserLogin userLogin = userService.findById(principal.getName());

        List<StudentCourseParticipationModel> studentCourseParticipationModels =
            logUserLoginCourseChapterMaterialService.findAllByClassId(classId);
        return ResponseEntity.ok().body(studentCourseParticipationModels);
    }

    @GetMapping("/get-log-user-course-chapter-material-by-page")
    public ResponseEntity<?> getLogUserCourseChapterMaterialByPage(
        @RequestParam(name = "classId") UUID classId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
        @RequestParam(name = "sortType", defaultValue = "desc") String sortType,
        @RequestParam(name = "keyword", defaultValue = "_") String keyword
    ) {
        log.info("getLogUserCourseChapterMaterial, classId = " + classId);

        Sort sort = Sort.by("createStamp");
        if (sortType.equals("desc")) {
            sort = sort.descending();
        }
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<StudentCourseParticipationModel> studentCourseParticipationModels =
            logUserLoginCourseChapterMaterialService.findDataByClassIdAndPage(classId, keyword, pageable);
        return ResponseEntity.ok().body(studentCourseParticipationModels);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/{classId}/user-quiz/log")
    public ResponseEntity<?> getLogUserQuiz(
        @PathVariable UUID classId,
        @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
        @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("getLogUserQuiz, classId = " + classId);
        Page<StudentQuizParticipationModel> studentQuizParticipationModels = logUserLoginQuizQuestionService.findByClassId(
            classId,
            page,
            size);

        return ResponseEntity.ok().body(studentQuizParticipationModels);
    }

    @PostMapping("/add-students-to-class-excel-upload")
    public ResponseEntity<?> addStudentToClassViaExcelUpload(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelInputAddStudentToClassViaExcelUpload modelUpload = gson.fromJson(
            inputJson, ModelInputAddStudentToClassViaExcelUpload.class);
        log.info("addStudentToClassViaExcelUpload, classId = " + modelUpload.getClassId());

        List<String> uploadedUsers = new ArrayList();
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            //System.out.println("uploadExcelStudentListOfQuizTest, lastRowNum = " + lastRowNum);
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                Cell c = row.getCell(0);

                String userId = c.getStringCellValue();
                log.info("addStudentToClassViaExcelUpload, extract userId " + userId);

                UserLogin u = userService.findById(userId);
                if (u == null) {
                    log.info("addStudentToClassViaExcelUpload, user " + userId + " NOT EXISTS");
                    continue;
                }


                SimpleResponse res = classService.register(modelUpload.getClassId(), userId);

                Set<String> ids = new HashSet();
                ids.add(userId);
                classService.updateRegistStatus(modelUpload.getClassId(), ids, RegistStatus.APPROVED);

                uploadedUsers.add(userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(uploadedUsers);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/update-material-status")
    public ResponseEntity<?> updateMaterialStatus(@RequestBody EduClassMaterial eduClassMaterial){
        EduClassMaterial response = eduClassMaterialService.update(eduClassMaterial.getClassId(), eduClassMaterial.getChapterId(), eduClassMaterial.getMaterialId(), eduClassMaterial.getStatus());

        return ResponseEntity.ok().body(response);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/get-material/{classId}")
    public ResponseEntity<?> getMaterialByClassId(@PathVariable UUID classId){
        List<EduClassMaterial> response = eduClassMaterialService.getMaterialByClassId(classId);

        return ResponseEntity.ok().body(response);
    }


}
