package com.hust.baseweb.applications.admin.dataadmin.controller;

import com.hust.baseweb.applications.admin.dataadmin.model.education.LogUserLoginCourseChapterMaterialOutputModel;
import com.hust.baseweb.applications.admin.dataadmin.repo.DataAdminLogUserLoginCourseChapterMaterialRepo;
import com.hust.baseweb.applications.admin.dataadmin.repo.NotificationRepo;
import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.EduCourseChapter;
import com.hust.baseweb.applications.education.entity.EduCourseChapterMaterial;
import com.hust.baseweb.applications.education.entity.LogUserLoginCourseChapterMaterial;
import com.hust.baseweb.applications.education.repo.EduCourseChapterMaterialRepo;
import com.hust.baseweb.applications.education.repo.EduCourseChapterRepo;
import com.hust.baseweb.applications.education.report.model.quizparticipation.StudentQuizParticipationModel;
import com.hust.baseweb.applications.education.service.LogUserLoginQuizQuestionService;
import com.hust.baseweb.applications.notifications.entity.Notifications;
import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import com.hust.baseweb.applications.programmingcontest.model.ContestSubmission;
import com.hust.baseweb.applications.programmingcontest.repo.ContestSubmissionRepo;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Log4j2
@Controller
@Validated
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DataAdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private DataAdminLogUserLoginCourseChapterMaterialRepo dataAdminLogUserLoginCourseChapterMaterial;

    @Autowired
    private EduCourseChapterMaterialRepo eduCourseChapterMaterialRepo;

    @Autowired
    private EduCourseChapterRepo eduCourseChapterRepo;

    @Autowired
    private LogUserLoginQuizQuestionService logUserLoginQuizQuestionService;

    @Autowired
    private ContestSubmissionRepo contestSubmissionRepo;

    @GetMapping("/admin/data/notifications")
    public ResponseEntity<?> getPageNotifications(Principal principal, @RequestParam int page, @RequestParam int size,
                                Pageable pageable ) {

        List<Notifications> lst = notificationRepo.getPage(page*size,size);
        int count = notificationRepo.countNotifications();
        Page<Notifications> aPage = new PageImpl<>(lst, pageable, count);
        return ResponseEntity.ok().body(aPage);
    }
    @GetMapping("/admin/data/view-users-do-pratice-quiz")
    public ResponseEntity<?> getPageLogUsersDoQuiz(Principal principal, @RequestParam int page, int size, Pageable pageable){
        Pageable sortedByCreatedStampDsc =
            PageRequest.of(page, size, Sort.by("createStamp").descending());
        Page<StudentQuizParticipationModel> studentQuizParticipationModels =
            logUserLoginQuizQuestionService.getPageLogStudentQuiz(
            page,
            size);

        return ResponseEntity.ok().body(studentQuizParticipationModels);

    }
    @GetMapping("/admin/data/view-users-do-pratice-quiz/{studentId}")
    public ResponseEntity<?> getPageLogUsersDoQuizOfAStudent(Principal principal,
                                                             @PathVariable String studentId,
                                                             @RequestParam int page, int size, Pageable pageable){
        Pageable sortedByCreatedStampDsc =
            PageRequest.of(page, size, Sort.by("createStamp").descending());
        Page<StudentQuizParticipationModel> studentQuizParticipationModels =
            logUserLoginQuizQuestionService.getPageLogStudentQuizOfAStudent(studentId,
                page,
                size);

        return ResponseEntity.ok().body(studentQuizParticipationModels);

    }

    @GetMapping("/admin/data/view-contest-submission")
    public ResponseEntity<?> getPageContestSubmission(
        Principal principal, @RequestParam int page, int size, Pageable pageable
    ) {
        Pageable sortedByCreatedStampDsc =
            PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ContestSubmissionEntity> lst = contestSubmissionRepo.findAll(sortedByCreatedStampDsc);
        List<ContestSubmission> L = new ArrayList<ContestSubmission>();
        for(ContestSubmissionEntity e: lst){
            ContestSubmission cs = new ContestSubmission();
            cs.setContestId(e.getContestId());
            cs.setProblemId(e.getProblemId());
            cs.setUserId(e.getUserId());
            PersonModel person= userService.findPersonByUserLoginId(e.getUserId());
            if(person != null){
                cs.setFullname(person.getLastName() + " " + person.getMiddleName() + " " + person.getFirstName());
                cs.setAffiliation(person.getAffiliations());
            }

            cs.setPoint(e.getPoint());
            cs.setStatus(e.getStatus());
            cs.setSubmissionDate(e.getCreatedAt());
            cs.setTestCasePass(e.getTestCasePass());
            L.add(cs);
        }
        int count = contestSubmissionRepo.countTotal();
        Page<ContestSubmission> aPage = new PageImpl<>(L, pageable, count);
        return ResponseEntity.ok().body(aPage);

    }
        @GetMapping("/admin/data/view-course-video")
    public ResponseEntity<?> getPageLogUserLoginCourseChapterMaterial(
        Principal principal, @RequestParam int page, int size, Pageable pageable
    ){
        Pageable sortedByCreatedStampDsc =
            PageRequest.of(page, size, Sort.by("createStamp").descending());
        /*
        Get Chapter List -> tobe improved, e.g., by Caching
         */
        List<EduCourseChapterMaterial> chapterMaterials = eduCourseChapterMaterialRepo.findAll();
        List<EduCourseChapter> chapters = eduCourseChapterRepo.findAll();
        Map<UUID, EduCourseChapterMaterial> mId2ChapterMaterial = new HashMap();
        Map<UUID, EduCourseChapter> mId2Chapter = new HashMap();
        for(EduCourseChapterMaterial c: chapterMaterials){
            mId2ChapterMaterial.put(c.getEduCourseMaterialId(),c);
        }
        for(EduCourseChapter c: chapters){
            mId2Chapter.put(c.getChapterId(),c);
        }

        //List<LogUserLoginCourseChapterMaterial> lst = dataAdminLogUserLoginCourseChapterMaterial.getPage(page * size, size);
        Page<LogUserLoginCourseChapterMaterial> lst = dataAdminLogUserLoginCourseChapterMaterial.findAll(sortedByCreatedStampDsc);
        int count = dataAdminLogUserLoginCourseChapterMaterial.countTotal();

        List<LogUserLoginCourseChapterMaterialOutputModel> lstModel = new ArrayList();
        for(LogUserLoginCourseChapterMaterial e: lst){
            LogUserLoginCourseChapterMaterialOutputModel m = new LogUserLoginCourseChapterMaterialOutputModel();
            m.setUserLoginId(e.getUserLoginId());
            String classId = null;
            String courseId = null;
            String courseName = null;
            String chapterName = null;
            String materialName = null;
            EduClass eduClass = e.getEduClass();
            if(eduClass != null) {
                classId = eduClass.getClassCode();
                courseId = eduClass.getEduCourse().getId();
                courseName = eduClass.getEduCourse().getName();
            }
            EduCourseChapterMaterial chapterMaterial = mId2ChapterMaterial.get(e.getEduCourseMaterialId());

            if(chapterMaterial != null){
                EduCourseChapter chapter = chapterMaterial.getEduCourseChapter();
                materialName = chapterMaterial.getEduCourseMaterialName();
                if(chapter != null){
                    chapterName = chapter.getChapterName();
                }
            }

            PersonModel person= userService.findPersonByUserLoginId(e.getUserLoginId());
            if(person != null){
                m.setFullname(person.getLastName() + " " + person.getMiddleName() + " " + person.getFirstName());
                m.setAffiliations(person.getAffiliations());
            }
            m.setClassId(classId);
            m.setCourseId(courseId);
            m.setCourseName(courseName);
            m.setChapterName(chapterName);
            m.setMaterialName(materialName);
            m.setDate(e.getCreateStamp());
            lstModel.add(m);
        }
        //Page<LogUserLoginCourseChapterMaterial> aPage = new PageImpl<>(lst, pageable, count);
        Page<LogUserLoginCourseChapterMaterialOutputModel> aPage = new PageImpl<>(lstModel, pageable, count);

        return ResponseEntity.ok().body(aPage);

    }

    @GetMapping("/admin/data/view-course-video-of-a-student/{studentId}")
    public ResponseEntity<?> getPageLogUserLoginCourseChapterMaterialOfAStudent(
        Principal principal, @PathVariable String studentId,
        @RequestParam int page, int size, Pageable pageable
    ){
        Pageable sortedByCreatedStampDsc =
            PageRequest.of(page, size, Sort.by("createStamp").descending());
        /*
        Get Chapter List -> tobe improved, e.g., by Caching
         */
        List<EduCourseChapterMaterial> chapterMaterials = eduCourseChapterMaterialRepo.findAll();
        List<EduCourseChapter> chapters = eduCourseChapterRepo.findAll();
        Map<UUID, EduCourseChapterMaterial> mId2ChapterMaterial = new HashMap();
        Map<UUID, EduCourseChapter> mId2Chapter = new HashMap();
        for(EduCourseChapterMaterial c: chapterMaterials){
            mId2ChapterMaterial.put(c.getEduCourseMaterialId(),c);
        }
        for(EduCourseChapter c: chapters){
            mId2Chapter.put(c.getChapterId(),c);
        }

        List<LogUserLoginCourseChapterMaterial> lst = dataAdminLogUserLoginCourseChapterMaterial.getPageOfUserLogin(page * size, size, studentId);
        //Page<LogUserLoginCourseChapterMaterial> lst = dataAdminLogUserLoginCourseChapterMaterial.findAll(sortedByCreatedStampDsc);
        int count = dataAdminLogUserLoginCourseChapterMaterial.countTotal();

        List<LogUserLoginCourseChapterMaterialOutputModel> lstModel = new ArrayList();
        for(LogUserLoginCourseChapterMaterial e: lst){
            LogUserLoginCourseChapterMaterialOutputModel m = new LogUserLoginCourseChapterMaterialOutputModel();
            m.setUserLoginId(e.getUserLoginId());
            String classId = null;
            String courseId = null;
            String courseName = null;
            String chapterName = null;
            String materialName = null;
            EduClass eduClass = e.getEduClass();
            if(eduClass != null) {
                classId = eduClass.getClassCode();
                courseId = eduClass.getEduCourse().getId();
                courseName = eduClass.getEduCourse().getName();
            }
            EduCourseChapterMaterial chapterMaterial = mId2ChapterMaterial.get(e.getEduCourseMaterialId());

            if(chapterMaterial != null){
                EduCourseChapter chapter = chapterMaterial.getEduCourseChapter();
                materialName = chapterMaterial.getEduCourseMaterialName();
                if(chapter != null){
                    chapterName = chapter.getChapterName();
                }
            }

            PersonModel person= userService.findPersonByUserLoginId(e.getUserLoginId());
            if(person != null){
                m.setFullname(person.getLastName() + " " + person.getMiddleName() + " " + person.getFirstName());
                m.setAffiliations(person.getAffiliations());
            }
            m.setClassId(classId);
            m.setCourseId(courseId);
            m.setCourseName(courseName);
            m.setChapterName(chapterName);
            m.setMaterialName(materialName);
            m.setDate(e.getCreateStamp());
            lstModel.add(m);
        }
        //Page<LogUserLoginCourseChapterMaterial> aPage = new PageImpl<>(lst, pageable, count);
        Page<LogUserLoginCourseChapterMaterialOutputModel> aPage = new PageImpl<>(lstModel, pageable, count);

        return ResponseEntity.ok().body(aPage);

    }

}
