package openerp.openerpresourceserver.generaltimetabling.service;

import java.io.InputStream;
import java.util.List;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.UpdateGeneralClassScheduleRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.V2UpdateClassScheduleRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;


public interface GeneralClassOpenedService {
    public List<GeneralClass> getGeneralClasses(String semester, String groupName);
    
    public void deleteAllGeneralClasses();

    public GeneralClass updateGeneralClassSchedule(String semester, UpdateGeneralClassScheduleRequest request);

    public GeneralClass updateGeneralClass(UpdateGeneralClassRequest request);

    List<GeneralClass> addClassesToNewGroup(List<String> ids, String groupName, String priorityBuilding) throws Exception;

    List<GeneralClass> addClassesToCreatedGroup(List<String> ids, String groupName) throws Exception;

    public void deleteClassesBySemester(String semester);

    List<GeneralClass> resetSchedule(List<String> ids, String semester);

    List<GeneralClass> autoSchedule(String semester, String groupName, int timeLimit);
    List<GeneralClass> autoScheduleRoom(String semester, String groupName, int timeLimit);

    InputStream exportExcel(String semester);

    List<GeneralClass> v2UpdateClassSchedule(String semester, List<V2UpdateClassScheduleRequest> request);
}
