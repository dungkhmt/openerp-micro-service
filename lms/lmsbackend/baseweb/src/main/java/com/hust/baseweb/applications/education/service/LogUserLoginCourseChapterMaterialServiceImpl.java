package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.entity.*;
import com.hust.baseweb.applications.education.repo.ClassRepo;
import com.hust.baseweb.applications.education.repo.EduCourseChapterMaterialRepo;
import com.hust.baseweb.applications.education.repo.EduCourseChapterRepo;
import com.hust.baseweb.applications.education.repo.LogUserLoginCourseChapterMaterialRepo;
import com.hust.baseweb.applications.education.report.model.courseparticipation.StudentCourseParticipationModel;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LogUserLoginCourseChapterMaterialServiceImpl implements LogUserLoginCourseChapterMaterialService {

    private LogUserLoginCourseChapterMaterialRepo logUserLoginCourseChapterMaterialRepo;
    private EduCourseChapterMaterialRepo eduCourseChapterMaterialRepo;
    private EduCourseChapterRepo eduCourseChapterRepo;
    private UserService userService;
    private ClassRepo classRepo;

    //private UserCache userCache;
    public static HashMap<String, PersonModel> mUserLoginId2PersonModel = new HashMap();

    public PersonModel getPersonModel(String userLoginId){
        if(mUserLoginId2PersonModel.get(userLoginId) == null){
            // load data from DB
            List<UserLogin> userLoginList = userService.getAllUserLogins();
            log.info("UserCache, got list " + userLoginList.size() + " users");
            for(UserLogin u: userLoginList){
                PersonModel pm = userService.findPersonByUserLoginId(u.getUserLoginId());
                mUserLoginId2PersonModel.put(u.getUserLoginId(),pm);
            }
        }
        return mUserLoginId2PersonModel.get(userLoginId);
    }

    @Override
    public void logUserLoginMaterial(UserLogin userLogin, UUID eduCourseChapterMaterialId) {
        LogUserLoginCourseChapterMaterial logUserLoginCourseChapterMaterial = new LogUserLoginCourseChapterMaterial();
        logUserLoginCourseChapterMaterial.setUserLoginId(userLogin.getUserLoginId());
        logUserLoginCourseChapterMaterial.setEduCourseMaterialId(eduCourseChapterMaterialId);
        logUserLoginCourseChapterMaterial.setCreateStamp(new Date());
        logUserLoginCourseChapterMaterial = logUserLoginCourseChapterMaterialRepo.save(logUserLoginCourseChapterMaterial);

    }

    @Override
    public void logUserLoginMaterialV2(String userId, UUID classId, UUID eduCourseChapterMaterialId){
        EduClass eduClass = classRepo.findById(classId).orElse(null);

        LogUserLoginCourseChapterMaterial logUserLoginCourseChapterMaterial = new LogUserLoginCourseChapterMaterial();
        logUserLoginCourseChapterMaterial.setUserLoginId(userId);
        logUserLoginCourseChapterMaterial.setEduCourseMaterialId(eduCourseChapterMaterialId);
        logUserLoginCourseChapterMaterial.setEduClass(eduClass);
        logUserLoginCourseChapterMaterial.setCreateStamp(new Date());
        logUserLoginCourseChapterMaterialRepo.save(logUserLoginCourseChapterMaterial);
    }

    @Override
    public List<StudentCourseParticipationModel> findAllByClassId(UUID classId) {
        List<EduCourseChapterMaterial> eduCourseChapterMaterials = eduCourseChapterMaterialRepo.findAll();
        Map<UUID, String> mId2Name = new HashMap<UUID, String>();
        for (EduCourseChapterMaterial m : eduCourseChapterMaterials) {
            mId2Name.put(m.getEduCourseMaterialId(), m.getEduCourseMaterialName());
        }

        EduClass eduClass = classRepo.findById(classId).orElse(null);
        int classCode = 0;
        String courseId = "";
        String courseName = "";
        if (eduClass != null) {
            classCode = eduClass.getCode();
            courseId = eduClass.getEduCourse().getId();
            courseName = eduClass.getEduCourse().getName();
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<LogUserLoginCourseChapterMaterial> lst = logUserLoginCourseChapterMaterialRepo.findAll();
        List<StudentCourseParticipationModel> studentClassParticipationOutputModels = new ArrayList();
        for (LogUserLoginCourseChapterMaterial e : lst) {
            //PersonModel personModel = userService.findPersonByUserLoginId(e.getUserLoginId());
            // use cache
            PersonModel personModel = getPersonModel(e.getUserLoginId());
            String datetime = "";
            if(e.getCreateStamp() != null){
                datetime = df.format(e.getCreateStamp());
            }
            studentClassParticipationOutputModels.add(new StudentCourseParticipationModel(
                e.getUserLoginId(),
                personModel.getLastName() + " " + personModel.getMiddleName() + " " + personModel.getFirstName(),
                classCode + "",
                courseId,
                courseName,
                mId2Name.get(e.getEduCourseMaterialId()),
                datetime));
        }
        return studentClassParticipationOutputModels;
    }

    public Page<StudentCourseParticipationModel> findDataByClassIdAndPage(UUID classId, String userIdPattern, Pageable pageable) {
        // find log
        Page<LogUserLoginCourseChapterMaterial> logUserLoginCourseChapterMaterialPage = logUserLoginCourseChapterMaterialRepo
            .findByEduClass_IdAndUserLoginIdContaining(classId, userIdPattern, pageable);

        // log to data
        List<LogUserLoginCourseChapterMaterial> logUserLoginCourseChapterMaterials = logUserLoginCourseChapterMaterialPage.getContent();
        List<StudentCourseParticipationModel> data = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HashMap<String, PersonModel> personModelMap = new HashMap();
        EduClass eduClass = null;
        for(LogUserLoginCourseChapterMaterial l : logUserLoginCourseChapterMaterials){
            if(eduClass == null) eduClass = l.getEduClass();

            PersonModel personModel = null;
            String userLoginId = l.getUserLoginId();
            if(personModelMap.containsKey(userLoginId)){
                personModel = personModelMap.get(userLoginId);
            } else {
                personModel = userService.findPersonByUserLoginId(userLoginId);
                personModelMap.put(userLoginId, personModel);
            }

            Optional<EduCourseChapterMaterial> eduCourseChapterMaterial = eduCourseChapterMaterialRepo
                .findById(l.getEduCourseMaterialId());

            String fullName = personModel.getFirstName() + " " + personModel.getMiddleName() + " " + personModel.getLastName();
            String datetime = "";
            if(l.getCreateStamp() != null){
                datetime = df.format(l.getCreateStamp());
            }
            String classCourseMaterialName = "";
            if(eduCourseChapterMaterial.isPresent()){
                classCourseMaterialName = eduCourseChapterMaterial.get().getEduCourseMaterialName();
            }

            data.add(new StudentCourseParticipationModel(
                userLoginId,
                fullName,
                eduClass.getClassCode() + "",
                eduClass.getEduCourse().getId(),
                eduClass.getEduCourse().getName(),
                classCourseMaterialName,
                datetime)
            );
        }

        Page<StudentCourseParticipationModel> page = new PageImpl<>(data, pageable, logUserLoginCourseChapterMaterialPage.getTotalElements());
        return page;
    }
}
