package openerp.openerpresourceserver.service;

import java.util.List;

import openerp.openerpresourceserver.model.dto.request.general.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.model.dto.request.general.UpdateGeneralClassScheduleRequest;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;


public interface GeneralClassOpenedService {
    public List<GeneralClassOpened> getGeneralClasses(String semester);
    
    public void deleteAllGeneralClasses();

    public GeneralClassOpened updateGeneralClassSchedule(String semester, UpdateGeneralClassScheduleRequest request);

    public GeneralClassOpened updateGeneralClass(UpdateGeneralClassRequest request);

    List<GeneralClassOpened> addClassesToNewGroup(List<String> ids, String groupName, String priorityBuilding) throws Exception;

    List<GeneralClassOpened> addClassesToCreatedGroup(List<String> ids, String groupName) throws Exception;

    public void deleteClassesBySemester(String semester);

    List<GeneralClassOpened> resetSchedule(List<String> ids, String semester);

    List<GeneralClassOpened> autoSchedule(String semester, String groupName);
}
