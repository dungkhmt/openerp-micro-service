package openerp.openerpresourceserver.generaltimetabling.service;

import java.io.InputStream;
import java.util.List;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.UpdateGeneralClassScheduleRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.V2UpdateClassScheduleRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.response.ModelResponseGeneralClass;
import org.springframework.transaction.annotation.Transactional;


public interface GeneralClassService {

    ModelResponseGeneralClass getClassDetailWithSubClasses(Long classId);
    public List<GeneralClass> getGeneralClasses(String semester, String groupName);

    void deleteAllGeneralClasses();

    public GeneralClass updateGeneralClassSchedule(String semester, UpdateGeneralClassScheduleRequest request);

    public GeneralClass updateGeneralClass(UpdateGeneralClassRequest request);

    List<GeneralClass> addClassesToGroup(List<Long> ids, String groupName) throws Exception;

    public void deleteClassesBySemester(String semester);

    void deleteClassesByIds(List<Long> ids);

    List<GeneralClass> resetSchedule(List<String> ids, String semester);

    List<GeneralClass> autoScheduleGroup(String semester, String groupName, int timeLimit);

    List<GeneralClass> autoScheduleTimeSlotRoom(List<Long> classIds, int timeLimit);
    List<GeneralClass> autoSchedule(String semester, int timeLimit);


    List<GeneralClass> autoScheduleRoom(String semester, String groupName, int timeLimit);

    List<GeneralClass> v2UpdateClassSchedule(String semester, List<V2UpdateClassScheduleRequest> request);

    GeneralClass deleteClassById(Long generalClassId);

    GeneralClass addRoomReservation(Long generalClassId, Long parentId, Integer duration);

    void deleteRoomReservation(Long generalClassId, Long roomReservationId);
}
