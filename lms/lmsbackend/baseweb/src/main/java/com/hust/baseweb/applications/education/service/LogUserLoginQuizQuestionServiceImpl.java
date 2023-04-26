package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.classmanagement.service.ClassService;
import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.entity.LogUserLoginQuizQuestion;
import com.hust.baseweb.applications.education.repo.ClassRepo;
import com.hust.baseweb.applications.education.repo.LogUserLoginQuizQuestionRepo;
import com.hust.baseweb.applications.education.report.model.quizparticipation.StudentQuizParticipationModel;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LogUserLoginQuizQuestionServiceImpl implements LogUserLoginQuizQuestionService {

    private final LogUserLoginQuizQuestionRepo logUserLoginQuizQuestionRepo;

    private final ClassRepo classRepo;

    private UserService userService;

    private ClassService classService;

    @Override
    public Page<StudentQuizParticipationModel> findByClassId(UUID classId, Integer page, Integer size) {
        Pageable sortedByCreatedStampDsc =
            PageRequest.of(page, size, Sort.by("createStamp").descending());
        Page<LogUserLoginQuizQuestion> logs = logUserLoginQuizQuestionRepo.findAll(sortedByCreatedStampDsc);

        EduClass eduClass = classRepo.findById(classId).orElse(null);
        String courseId = "";
        String courseName = "";

        if (eduClass != null) {
            courseId = eduClass.getEduCourse().getId();
            courseName = eduClass.getEduCourse().getName();
        }

        HashMap<String, PersonModel> mUserLoginId2PersonModel =
            LogUserLoginCourseChapterMaterialServiceImpl.mUserLoginId2PersonModel;
        //CacheQuizCourseTopic cacheQuizCourseTopic = QuizCourseTopicServiceImpl.cacheQuizCourseTopic;

        // Copy instance to use in stream
        String finalCourseId = courseId;
        String finalCourseName = courseName;

        Page<StudentQuizParticipationModel> studentQuizParticipationModels = logs.map(logi -> {
            //PersonModel personModel = userService.findPersonByUserLoginId(log.getUserLoginId());
            //PersonModel personModel = mUserLoginId2PersonModel.get(logi.getUserLoginId());
            PersonModel personModel = userService.findPersonByUserLoginId(logi.getUserLoginId());

            //QuizCourseTopic q = cacheQuizCourseTopic.get(log.getQuestionId());
            int grade = 0;
            //if(log.getIsCorrectAnswer() == 'Y')
            if (logi.getIsCorrectAnswer() != null) {
                if (logi.getIsCorrectAnswer().equals("Y")) {
                    grade = 1;
                }
            }

            String fullName = "";
            String affiliations = "";
            if (personModel != null) {
                fullName = personModel.getLastName() +
                           " " +
                           personModel.getMiddleName() +
                           " " +
                           personModel.getFirstName();
                log.info("findByClassId, fullName = " + fullName);
                affiliations = personModel.getAffiliations();
            }else{
                log.info("findByClassId, personModel NULL!");

            }

            return new StudentQuizParticipationModel(
                logi.getUserLoginId(),
                fullName, affiliations,
                (logi.getClassId() != null ? logi.getClassId().toString() : ""),
                logi.getClassCode() + "",
                (logi.getQuestionId() != null ? logi.getQuestionId().toString() : ""),
                finalCourseId,
                finalCourseName,
                logi.getQuestionTopicName(),
                logi.getQuestionTopicId(),
                //q.getQuizCourseTopicName(),
                //q.getQuizCourseTopicId(),
                grade,
                logi.getCreateStamp()
            );
        });

        return studentQuizParticipationModels;
    }
    @Override
    public Page<StudentQuizParticipationModel> getPageLogStudentQuizOfAStudent(String userLoginId, Integer page, Integer size){
        log.info("getPageLogStudentQuizOfAStudent, studentId = " + userLoginId);
        Pageable sortedByCreatedStampDsc =
            PageRequest.of(page, size, Sort.by("createStamp").descending());
        Page<LogUserLoginQuizQuestion> logs = logUserLoginQuizQuestionRepo.findByUserLoginId(userLoginId, sortedByCreatedStampDsc);
        log.info("getPageLogStudentQuizOfAStudent, studentId = " + userLoginId + " sz = " + logs.getSize());

        Page<StudentQuizParticipationModel> studentQuizParticipationModels = logs.map(logi -> {
            //PersonModel personModel = userService.findPersonByUserLoginId(log.getUserLoginId());
            //PersonModel personModel = mUserLoginId2PersonModel.get(logi.getUserLoginId());
            PersonModel personModel = userService.findPersonByUserLoginId(logi.getUserLoginId());

            //QuizCourseTopic q = cacheQuizCourseTopic.get(log.getQuestionId());
            int grade = 0;
            //if(log.getIsCorrectAnswer() == 'Y')
            if (logi.getIsCorrectAnswer() != null) {
                if (logi.getIsCorrectAnswer().equals("Y")) {
                    grade = 1;
                }
            }

            EduCourse course = classService.getCourseOfClassCode(logi.getClassCode());
            String courseId = "";
            String courseName = "";
            if(course != null){
                courseId = course.getId();
                courseName = course.getName();
            }
            String fullName = "";
            String affiliations = "";
            if (personModel != null) {
                fullName = personModel.getLastName() +
                           " " +
                           personModel.getMiddleName() +
                           " " +
                           personModel.getFirstName();
                affiliations = personModel.getAffiliations();
                //log.info("findByClassId, fullName = " + fullName);
            }else{
                //log.info("findByClassId, personModel NULL!");

            }
            return new StudentQuizParticipationModel(
                logi.getUserLoginId(),
                fullName,affiliations,
                (logi.getClassId() != null ? logi.getClassId().toString() : ""),
                logi.getClassCode() + "",
                (logi.getQuestionId() != null ? logi.getQuestionId().toString() : ""),
                courseId,
                courseName,
                logi.getQuestionTopicName(),
                logi.getQuestionTopicId(),
                //q.getQuizCourseTopicName(),
                //q.getQuizCourseTopicId(),
                grade,
                logi.getCreateStamp()
            );
        });

        return studentQuizParticipationModels;

    }

    @Override
    public Page<StudentQuizParticipationModel> getPageLogStudentQuiz(Integer page, Integer size) {
        Pageable sortedByCreatedStampDsc =
            PageRequest.of(page, size, Sort.by("createStamp").descending());
        Page<LogUserLoginQuizQuestion> logs = logUserLoginQuizQuestionRepo.findAll(sortedByCreatedStampDsc);

        /*
        EduClass eduClass = classRepo.findById(classId).orElse(null);
        String courseId = "";
        String courseName = "";

        if (eduClass != null) {
            courseId = eduClass.getEduCourse().getId();
            courseName = eduClass.getEduCourse().getName();
        }
        // Copy instance to use in stream
        String finalCourseId = courseId;
        String finalCourseName = courseName;
        */

        //HashMap<String, PersonModel> mUserLoginId2PersonModel =
        //    LogUserLoginCourseChapterMaterialServiceImpl.mUserLoginId2PersonModel;
        //CacheQuizCourseTopic cacheQuizCourseTopic = QuizCourseTopicServiceImpl.cacheQuizCourseTopic;


        Page<StudentQuizParticipationModel> studentQuizParticipationModels = logs.map(logi -> {
            //PersonModel personModel = userService.findPersonByUserLoginId(log.getUserLoginId());
            //PersonModel personModel = mUserLoginId2PersonModel.get(logi.getUserLoginId());
            PersonModel personModel = userService.findPersonByUserLoginId(logi.getUserLoginId());

            //QuizCourseTopic q = cacheQuizCourseTopic.get(log.getQuestionId());
            int grade = 0;
            //if(log.getIsCorrectAnswer() == 'Y')
            if (logi.getIsCorrectAnswer() != null) {
                if (logi.getIsCorrectAnswer().equals("Y")) {
                    grade = 1;
                }
            }

            EduCourse course = classService.getCourseOfClassCode(logi.getClassCode());
            String courseId = "";
            String courseName = "";
            if(course != null){
                courseId = course.getId();
                courseName = course.getName();
            }
            String fullName = "";
            String affiliations = "";
            if (personModel != null) {
                fullName = personModel.getLastName() +
                           " " +
                           personModel.getMiddleName() +
                           " " +
                           personModel.getFirstName();
                affiliations = personModel.getAffiliations();
                //log.info("findByClassId, fullName = " + fullName);
            }else{
                //log.info("findByClassId, personModel NULL!");

            }
            return new StudentQuizParticipationModel(
                logi.getUserLoginId(),
                fullName,affiliations,
                (logi.getClassId() != null ? logi.getClassId().toString() : ""),
                logi.getClassCode() + "",
                (logi.getQuestionId() != null ? logi.getQuestionId().toString() : ""),
                courseId,
                courseName,
                logi.getQuestionTopicName(),
                logi.getQuestionTopicId(),
                //q.getQuizCourseTopicName(),
                //q.getQuizCourseTopicId(),
                grade,
                logi.getCreateStamp()
            );
        });

        return studentQuizParticipationModels;
    }
}
