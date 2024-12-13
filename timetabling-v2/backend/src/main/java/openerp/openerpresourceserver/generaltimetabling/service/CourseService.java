package openerp.openerpresourceserver.generaltimetabling.service;


import openerp.openerpresourceserver.generaltimetabling.model.dto.request.CourseDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Course;

import java.util.List;

public interface CourseService {
    List<Course> getCourse();

    void updateCourse(CourseDto courseDto);

    Course create(CourseDto courseDto);

    void deleteById(String id);
}
