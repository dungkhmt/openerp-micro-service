package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Application;

import java.util.List;

public interface ApplicationService {

    Application createApplication(Application application);

    List<Application> getMyApplications(String userId);

    List<Application> getApplicationByClassId(int classCallId);

    List<Application> getUniqueApplicator();
}
