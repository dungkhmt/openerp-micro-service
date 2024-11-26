package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.repo.EduCourseRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CourseServiceImpl implements CourseService {

    private EduCourseRepo courseRepo;

    @Override
    public EduCourse createCourse(String Id, String courseName, short credit){
        EduCourse eduCourse = courseRepo.findById(Id).orElse(null);
        if (eduCourse == null) {
            EduCourse newEduCourse = new EduCourse();
            newEduCourse.setId(Id);
            newEduCourse.setName(courseName);
            newEduCourse.setCredit(credit);
            newEduCourse.setCreatedStamp(new Date());
            newEduCourse.setLastUpdatedStamp(new Date());
            return courseRepo.save(newEduCourse);
        }
        return null;
    }

    @Override
    public List<EduCourse> findAll() {
        //return null;// TODO
        return courseRepo.findAll();
    }
}
