package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Application;

import java.util.List;

public interface ApplicationService {

    Application createApplication(Application application);

    List<Application> getMyApplications(String userId);
}
