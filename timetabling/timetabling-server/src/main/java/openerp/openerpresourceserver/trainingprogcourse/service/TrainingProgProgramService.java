package openerp.openerpresourceserver.trainingprogcourse.service;

import openerp.openerpresourceserver.trainingprogcourse.dto.PaginationDTO;
import openerp.openerpresourceserver.trainingprogcourse.dto.ResponseTrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.dto.ResponseTrainingProgProgramDTO;
import openerp.openerpresourceserver.trainingprogcourse.dto.TrainingProgProgramInfo;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.RequestTrainingProgProgramDTO;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.TrainingProgScheduleUpdateRequest;

import java.util.List;

public interface TrainingProgProgramService {
    void create(RequestTrainingProgProgramDTO trainingProgProgram);

    void update( List<TrainingProgScheduleUpdateRequest> scheduleUpdates);

    PaginationDTO<TrainingProgProgramInfo> getAllTrainingProgPrograms(int page, int size, String keyword);

    ResponseTrainingProgProgramDTO getTrainingProgProgramById(String id);

    void addCoursesToProgram(String programId, List<String> courseIds);

    List<String> getListCourseProgram(String programId);

    List<ResponseTrainingProgCourse> getAvailableCourse(String programId);

}



