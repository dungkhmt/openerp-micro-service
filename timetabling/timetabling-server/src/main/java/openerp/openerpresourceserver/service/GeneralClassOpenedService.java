package openerp.openerpresourceserver.service;

import java.util.List;

import openerp.openerpresourceserver.model.dto.request.general.UpdateClassesToNewGroupRequest;
import openerp.openerpresourceserver.model.dto.request.general.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.model.dto.request.general.UpdateGeneralClassScheduleRequest;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;

public interface GeneralClassOpenedService {
    public List<GeneralClassOpened> getGeneralClasses(String semester);
    
    public void deleteAllGeneralClasses();

    public GeneralClassOpened updateGeneralClassSchedule(UpdateGeneralClassScheduleRequest request);

    public GeneralClassOpened updateGeneralClass(UpdateGeneralClassRequest request);

    List<GeneralClassOpened> addClassesToNewGroup(List<String> ids, String groupName, String priorityBuilding) throws Exception;

    List<GeneralClassOpened> addClassesToCreatedGroup(List<String> ids, String groupName) throws Exception;

    public void deleteClassesBySemester(String semester);
}
