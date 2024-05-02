package openerp.openerpresourceserver.tarecruitment.service;

import openerp.openerpresourceserver.tarecruitment.dto.PaginationDTO;
import openerp.openerpresourceserver.tarecruitment.entity.Application;

import java.io.IOException;
import java.util.List;

public interface ApplicationService {

    Application createApplication(Application application);

    PaginationDTO<Application> getMyApplications(String userId, int page, int limit);

    PaginationDTO<Application> getApplicationByClassId(int classCallId, int page, int limit);

    List<Application> getUniqueApplicator();

    PaginationDTO<Application> getApplicationBySemester(String semester, String search, String applicationStatus, int page, int limit);

    PaginationDTO<Application> getApplicationByApplicationStatusAndSemester(String applicationStatus,
                                                                            String semester, String search, String assignStatus, int page, int limit);

    Application updateApplicationStatus(int id, String status);

    Application updateAssignStatus(int id, String status);

    int[][] autoAssignApplication(String semester);

    byte[] generateExcelFile(String semester) throws IOException;
}
