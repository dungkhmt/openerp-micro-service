package openerp.openerpresourceserver.tarecruitment.service;

import openerp.openerpresourceserver.tarecruitment.entity.dto.ChartDTO;
import openerp.openerpresourceserver.tarecruitment.entity.dto.PaginationDTO;
import openerp.openerpresourceserver.tarecruitment.entity.Application;

import java.io.IOException;
import java.util.List;

public interface ApplicationService {

    Application createApplication(Application application);

    Application updateApplication(int id, Application application);

    boolean deleteApplication(int id);

    boolean deleteMultiApplication(List<Integer> idList);

    Application getApplicationById(int id);

    PaginationDTO<Application> getMyApplications(String userId, int page, int limit);

    PaginationDTO<Application> getApplicationByClassId(int classCallId, int page, int limit);

    List<Application> getUniqueApplicator();

    PaginationDTO<Application> getApplicationBySemester(String semester, String search, String applicationStatus, int page, int limit);

    PaginationDTO<Application> getApplicationByApplicationStatusAndSemester(String applicationStatus,
                                                                            String semester, String search, String assignStatus, int page, int limit);

    Application updateApplicationStatus(int id, String status);

    String updateMultipleApplicationStatus(List<Integer> idList, String status);

    Application updateAssignStatus(int id, String status);

    void autoAssignApplication(String semester);

    void oldAutoAssignApplication(String semester);

    byte[] generateExcelFile(String semester) throws IOException;

    PaginationDTO<Application> getTABySemester(String semester, String search, int page, int limit);

    List<ChartDTO> getApplicatorEachSemesterData();

    List<ChartDTO> getNumbApplicationEachSemesterData();

    List<ChartDTO> getNumbApplicationApproveEachSemesterData();

    List<ChartDTO> dataApplicationEachCourseThisSemester();
}
