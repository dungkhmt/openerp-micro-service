package openerp.openerpresourceserver.generaltimetabling.service.impl;

import openerp.openerpresourceserver.generaltimetabling.exception.CourseNotFoundException;
import openerp.openerpresourceserver.generaltimetabling.exception.CourseUsedException;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.CourseDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Course;
import openerp.openerpresourceserver.generaltimetabling.repo.CourseRepo;
import openerp.openerpresourceserver.generaltimetabling.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepo courseRepo;

    @Override
    public List<Course> getCourse() {
        return courseRepo.findAll();
    }


    @Override
    public void updateCourse(CourseDto requestDto) {
//        String oldId = requestDto.getOldId();
//        String newId = requestDto.getNewId();
        String id = requestDto.getId();
        String courseName = requestDto.getCourseName();
        Short credit = requestDto.getCredit();

        Course course = courseRepo.findById(id).orElse(null);

        if (course == null) {
            throw new CourseNotFoundException("Mã môn học không tồn tại");
        }

//        if (!newId.equals(oldId)) {
//            Course newCourse = courseRepo.findById(newId).orElse(null);
//            if (newCourse != null) {
//                throw new CourseUsedException("Mã môn học đã tồn tại!");
//            }
//        }


        if (!courseName.equals(course.getCourseName())) {
            List<Course> courseList = courseRepo.getAllByCourseName(courseName);
            if (!courseList.isEmpty()) {
                throw new CourseUsedException("Tên môn học đã tồn tại!");
            }
        }

//        course.setId(newId);
        course.setCourseName(courseName);
        course.setCredit(credit);

        courseRepo.save(course);
    }

    @Override
    public Course create(CourseDto courseDto) {
        String id = courseDto.getId();
        String courseName = courseDto.getCourseName();
        Short credit = courseDto.getCredit();

        Course course = courseRepo.findById(id).orElse(null);

        if (course != null) {
            throw new CourseUsedException("Mã môn học đã tồn tại");
        }

        List<Course> courseList = courseRepo.getAllByCourseName(courseName);
        if (!courseList.isEmpty()) {
            throw new CourseUsedException("Tên môn học đã tồn tại!");
        }

        Course newCourse = new Course();
        newCourse.setId(id);
        newCourse.setCourseName(courseName);
        newCourse.setCredit(credit);

        courseRepo.save(newCourse);

        return newCourse;
    }

    @Override
    public void deleteById(String id) {
        Course course = courseRepo.findById(id).orElse(null);
        if (course == null) {
            throw new RuntimeException("Không tồn tại môn học " + id);
        }

        courseRepo.deleteById(id);
    }
}
