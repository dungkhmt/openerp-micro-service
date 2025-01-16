package openerp.openerpresourceserver.trainingprogcourse.service;

import openerp.openerpresourceserver.trainingprogcourse.dto.ResponseTrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.RequestTrainingProgCourse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TrainingProgCourseService {

    void create(RequestTrainingProgCourse requestTrainingProgCourse);

    void update(String CourseID, RequestTrainingProgCourse requestTrainingProgCourse);

    List<ResponseTrainingProgCourse> getAll();

    ResponseTrainingProgCourse getDetail(String id);

    void delete(String id);

    int importTrainingProgCourse(MultipartFile file);
}
