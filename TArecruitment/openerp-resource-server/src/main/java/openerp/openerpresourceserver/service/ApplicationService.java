package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.PaginationDTO;
import openerp.openerpresourceserver.entity.Application;

import java.util.List;

public interface ApplicationService {

    Application createApplication(Application application);

    PaginationDTO<Application> getMyApplications(String userId, int page, int limit);

    PaginationDTO<Application> getApplicationByClassId(int classCallId, int page, int limit);

    List<Application> getUniqueApplicator();

    PaginationDTO<Application> getApplicationBySemester(String semester, int page, int limit);

    PaginationDTO<Application> getApplicationByApplicationStatusAndSemester(String applicationStatus,
                                                                            String semester, int page, int limit);

    Application updateApplicationStatus(int id, String status);

    Application updateAssignStatus(int id, String status);

    int[][] autoAssignApplication(String semester);
}
