package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.dto.request.FilterClassOpenedDto;
import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.model.dto.request.UpdateClassOpenedDto;
import openerp.openerpresourceserver.model.entity.ClassOpened;
import openerp.openerpresourceserver.model.entity.Schedule;

import java.util.List;

public interface ClassOpenedService {

    List<ClassOpened> getAll();

    List<ClassOpened> updateClassOpenedList(UpdateClassOpenedDto requestDto);

    List<ClassOpened> getBySemester(String semester);

    List<ClassOpened> getByGroupName(String groupName);

    List<Schedule> searchClassOpened(FilterClassOpenedDto requestDto);
}