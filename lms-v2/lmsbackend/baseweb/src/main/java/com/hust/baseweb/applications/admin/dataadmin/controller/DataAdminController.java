package com.hust.baseweb.applications.admin.dataadmin.controller;

import com.hust.baseweb.applications.admin.dataadmin.education.service.ProgrammingContestSubmissionServiceImpl;
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
import com.hust.baseweb.applications.programmingcontest.entity.ProblemEntity;
import com.hust.baseweb.applications.programmingcontest.repo.ContestSubmissionRepo;
import com.hust.baseweb.applications.programmingcontest.repo.ProblemRepo;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.PrintWriter;
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

    @Autowired
    private ProblemRepo problemRepo;

    private final ProgrammingContestSubmissionServiceImpl contestSubmissionService;

    @GetMapping("/admin/data/notifications")
    public ResponseEntity<?> getPageNotifications(
        Principal principal, @RequestParam int page, @RequestParam int size,
        Pageable pageable
    ) {

        List<Notifications> lst = notificationRepo.getPage(page * size, size);
        int count = notificationRepo.countNotifications();
        Page<Notifications> aPage = new PageImpl<>(lst, pageable, count);
        return ResponseEntity.ok().body(aPage);
    }

    @GetMapping("/admin/data/view-users-do-pratice-quiz")
    public ResponseEntity<?> getPageLogUsersDoQuiz(
        Principal principal,
        @RequestParam int page,
        int size,
        Pageable pageable
    ) {
        Pageable sortedByCreatedStampDsc =
            PageRequest.of(page, size, Sort.by("createStamp").descending());
        Page<StudentQuizParticipationModel> studentQuizParticipationModels =
            logUserLoginQuizQuestionService.getPageLogStudentQuiz(
                page,
                size);

        return ResponseEntity.ok().body(studentQuizParticipationModels);

    }

    @GetMapping("/admin/data/view-users-do-pratice-quiz/{studentId}")
    public ResponseEntity<?> getPageLogUsersDoQuizOfAStudent(
        Principal principal,
        @PathVariable String studentId,
        @RequestParam int page, int size, Pageable pageable
    ) {
        Pageable sortedByCreatedStampDsc =
            PageRequest.of(page, size, Sort.by("createStamp").descending());
        Page<StudentQuizParticipationModel> studentQuizParticipationModels =
            logUserLoginQuizQuestionService.getPageLogStudentQuizOfAStudent(
                studentId,
                page,
                size);

        return ResponseEntity.ok().body(studentQuizParticipationModels);

    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/data/view-contest-submission")
    public ResponseEntity<?> getPageContestSubmission(
        @RequestParam("page") int page,
        @RequestParam("size") int size,
        ContestSubmissionEntity filter
    ) {
        Pageable sortedByCreatedStampDsc = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok().body(contestSubmissionService.search(filter, sortedByCreatedStampDsc));
//        Pageable sortedByCreatedStampDsc =
//            PageRequest.of(page, size, Sort.by("createdAt").descending());
//        Page<ContestSubmissionEntity> lst = contestSubmissionRepo.findAll(sortedByCreatedStampDsc);
//        List<ContestSubmission> L = new ArrayList<ContestSubmission>();
//        for (ContestSubmissionEntity e : lst) {
//            ContestSubmission cs = new ContestSubmission();
//            cs.setContestId(e.getContestId());
//            cs.setProblemId(e.getProblemId());
//            cs.setUserId(e.getUserId());
////            PersonModel person = userService.findPersonByUserLoginId(e.getUserId());
////            if (person != null) {
////                cs.setFullname(person.getLastName() + " " + person.getMiddleName() + " " + person.getFirstName());
////                cs.setAffiliation(person.getAffiliations());
////            }
//            cs.setFullname(userService.getUserFullName(e.getUserId()));
//
//            cs.setPoint(e.getPoint());
//            cs.setStatus(e.getStatus());
//            cs.setSubmissionDate(e.getCreatedAt());
//            cs.setTestCasePass(e.getTestCasePass());
//            L.add(cs);
//        }
//        int count = contestSubmissionRepo.countTotal();
//        Page<ContestSubmission> aPage = new PageImpl<>(L, pageable, count);
//        return ResponseEntity.ok().body(aPage);
    }

    @GetMapping("/admin/data/view-course-video")
    public ResponseEntity<?> getPageLogUserLoginCourseChapterMaterial(
        Principal principal, @RequestParam int page, int size, Pageable pageable
    ) {
        Pageable sortedByCreatedStampDsc =
            PageRequest.of(page, size, Sort.by("createStamp").descending());
        /*
        Get Chapter List -> tobe improved, e.g., by Caching
         */
        List<EduCourseChapterMaterial> chapterMaterials = eduCourseChapterMaterialRepo.findAll();
        List<EduCourseChapter> chapters = eduCourseChapterRepo.findAll();
        Map<UUID, EduCourseChapterMaterial> mId2ChapterMaterial = new HashMap();
        Map<UUID, EduCourseChapter> mId2Chapter = new HashMap();
        for (EduCourseChapterMaterial c : chapterMaterials) {
            mId2ChapterMaterial.put(c.getEduCourseMaterialId(), c);
        }
        for (EduCourseChapter c : chapters) {
            mId2Chapter.put(c.getChapterId(), c);
        }

        //List<LogUserLoginCourseChapterMaterial> lst = dataAdminLogUserLoginCourseChapterMaterial.getPage(page * size, size);
        Page<LogUserLoginCourseChapterMaterial> lst = dataAdminLogUserLoginCourseChapterMaterial.findAll(
            sortedByCreatedStampDsc);
        int count = dataAdminLogUserLoginCourseChapterMaterial.countTotal();

        List<LogUserLoginCourseChapterMaterialOutputModel> lstModel = new ArrayList();
        for (LogUserLoginCourseChapterMaterial e : lst) {
            LogUserLoginCourseChapterMaterialOutputModel m = new LogUserLoginCourseChapterMaterialOutputModel();
            m.setUserLoginId(e.getUserLoginId());
            String classId = null;
            String courseId = null;
            String courseName = null;
            String chapterName = null;
            String materialName = null;
            EduClass eduClass = e.getEduClass();
            if (eduClass != null) {
                classId = eduClass.getClassCode();
                courseId = eduClass.getEduCourse().getId();
                courseName = eduClass.getEduCourse().getName();
            }
            EduCourseChapterMaterial chapterMaterial = mId2ChapterMaterial.get(e.getEduCourseMaterialId());

            if (chapterMaterial != null) {
                EduCourseChapter chapter = chapterMaterial.getEduCourseChapter();
                materialName = chapterMaterial.getEduCourseMaterialName();
                if (chapter != null) {
                    chapterName = chapter.getChapterName();
                }
            }

//            PersonModel person = userService.findPersonByUserLoginId(e.getUserLoginId());
//            if (person != null) {
//                m.setFullname(person.getLastName() + " " + person.getMiddleName() + " " + person.getFirstName());
//                m.setAffiliations(person.getAffiliations());
//            }
            m.setFullname(userService.getUserFullName(e.getUserLoginId()));
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
    ) {
        Pageable sortedByCreatedStampDsc =
            PageRequest.of(page, size, Sort.by("createStamp").descending());
        /*
        Get Chapter List -> tobe improved, e.g., by Caching
         */
        List<EduCourseChapterMaterial> chapterMaterials = eduCourseChapterMaterialRepo.findAll();
        List<EduCourseChapter> chapters = eduCourseChapterRepo.findAll();
        Map<UUID, EduCourseChapterMaterial> mId2ChapterMaterial = new HashMap();
        Map<UUID, EduCourseChapter> mId2Chapter = new HashMap();
        for (EduCourseChapterMaterial c : chapterMaterials) {
            mId2ChapterMaterial.put(c.getEduCourseMaterialId(), c);
        }
        for (EduCourseChapter c : chapters) {
            mId2Chapter.put(c.getChapterId(), c);
        }

        List<LogUserLoginCourseChapterMaterial> lst = dataAdminLogUserLoginCourseChapterMaterial.getPageOfUserLogin(
            page *
            size,
            size,
            studentId);
        //Page<LogUserLoginCourseChapterMaterial> lst = dataAdminLogUserLoginCourseChapterMaterial.findAll(sortedByCreatedStampDsc);
        int count = dataAdminLogUserLoginCourseChapterMaterial.countTotal();

        List<LogUserLoginCourseChapterMaterialOutputModel> lstModel = new ArrayList();
        for (LogUserLoginCourseChapterMaterial e : lst) {
            LogUserLoginCourseChapterMaterialOutputModel m = new LogUserLoginCourseChapterMaterialOutputModel();
            m.setUserLoginId(e.getUserLoginId());
            String classId = null;
            String courseId = null;
            String courseName = null;
            String chapterName = null;
            String materialName = null;
            EduClass eduClass = e.getEduClass();
            if (eduClass != null) {
                classId = eduClass.getClassCode();
                courseId = eduClass.getEduCourse().getId();
                courseName = eduClass.getEduCourse().getName();
            }
            EduCourseChapterMaterial chapterMaterial = mId2ChapterMaterial.get(e.getEduCourseMaterialId());

            if (chapterMaterial != null) {
                EduCourseChapter chapter = chapterMaterial.getEduCourseChapter();
                materialName = chapterMaterial.getEduCourseMaterialName();
                if (chapter != null) {
                    chapterName = chapter.getChapterName();
                }
            }

//            PersonModel person = userService.findPersonByUserLoginId(e.getUserLoginId());
//            if (person != null) {
//                m.setFullname(person.getLastName() + " " + person.getMiddleName() + " " + person.getFirstName());
//                m.setAffiliations(person.getAffiliations());
//            }
            m.setFullname(userService.getUserFullName(e.getUserLoginId()));
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
    @GetMapping("/admin/data/get-accepted-submission")
    public ResponseEntity<?> getAcceptedSubmissions(Principal principal){
        log.info("getAcceptedSubmissions");
        int count = contestSubmissionRepo.countTotalAccept();
        log.info("getAcceptedSubmissions, subs.size = " + count);
        try {
            PrintWriter out = new PrintWriter("log.txt");
            Map<String, List<String>> mProblemId2SolutionCode = new HashMap<String, List<String>>();
            Map<String, ProblemEntity> mID2Problem = new HashMap<String, ProblemEntity>();
            List<ProblemEntity> problems = problemRepo.findAll();
            for (ProblemEntity p : problems) {
                mID2Problem.put(p.getProblemId(), p);
            }

            int limit = 1000;
            int offset = 0;
            while(offset + limit <= count) {
                log.info("getAcceptedSubmissions, offset = " + offset + " count = " + count);
                //List<ContestSubmissionEntity> subs = contestSubmissionRepo.findAllByStatus(ContestSubmissionEntity.SUBMISSION_STATUS_ACCEPTED);

                List<ContestSubmissionEntity> subs = contestSubmissionRepo.getPageContestSubmission(
                    offset,
                    limit,
                    "Accepted");


                for (ContestSubmissionEntity s : subs) {
                    String problemId = s.getProblemId();
                    String solutionCode = s.getSourceCode();
                    if (mProblemId2SolutionCode.get(problemId) == null) {
                        mProblemId2SolutionCode.put(problemId, new ArrayList<String>());
                    }
                    mProblemId2SolutionCode.get(problemId).add(solutionCode);
                }

                offset += limit;
            }

            int cnt = 0;
            for (String problemID : mProblemId2SolutionCode.keySet()) {
                ProblemEntity p = mID2Problem.get(problemID);
                List<String> sourceCodes = mProblemId2SolutionCode.get(problemID);
                out.println("ProblemID: " + problemID);

                if (p != null) {
                    out.println(p.getProblemDescription());
                    out.println("solution codes");
                    log.info("problem " + problemID + " has " + sourceCodes.size() + " correct solution");
                    for (String s : sourceCodes) {
                        out.println(s);
                        out.println("--------");
                    }

                }
                log.info("getAcceptedSubmissions, finished " + cnt + "/" + problems.size());
                out.println("---------------------------");
            }

            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("submissions");
    }
}
