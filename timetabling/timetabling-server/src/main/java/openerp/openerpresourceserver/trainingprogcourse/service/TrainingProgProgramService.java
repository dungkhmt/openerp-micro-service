package openerp.openerpresourceserver.trainingprogcourse.service;

import openerp.openerpresourceserver.trainingprogcourse.dto.*;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.*;

import java.util.List;
import java.util.Map;

public interface TrainingProgProgramService {
    void create(RequestTrainingProgProgramDTO trainingProgProgram);

    void update( List<TrainingProgScheduleUpdateRequest> scheduleUpdates);

    PaginationDTO<TrainingProgProgramInfo> getAllTrainingProgPrograms(int page, int size, String keyword);

    ResponseTrainingProgProgramDTO getTrainingProgProgramById(String id);

    void addCoursesToProgram(String programId, List<String> courseIds);

    List<String> getListCourseProgram(String programId);

    List<ResponseTrainingProgCourse> getAvailableCourse(String programId);

    List<ResponseProgramAlterDTO> courseScheduler(String programId);

    Boolean updateSemesterCount(RequestSemesterCountDTO requestSemesterCountDTO);

    Boolean deleteCourse(RequestDeleteTrainingProgProgramDTO request);

    List<ResponseCourseChangeDTO> changeCourse(RequestChangeCourseDTO request);

    int deletePrograms(List<String> ids);
}



