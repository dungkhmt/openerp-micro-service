package openerp.openerpresourceserver.service;

import java.util.List;

import openerp.openerpresourceserver.controller.general.GeneralClassOpenedController;
import openerp.openerpresourceserver.model.dto.request.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.model.dto.request.UpdateGeneralClassScheduleRequest;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;

public interface GeneralClassOpenedService {
    public List<GeneralClassOpened> getGeneralClasses(String semester);
    
    public void deleteAllGeneralClasses();

    public GeneralClassOpened updateGeneralClassSchedule(UpdateGeneralClassScheduleRequest request);

    public GeneralClassOpened updateGeneralClass(UpdateGeneralClassRequest request);
}
