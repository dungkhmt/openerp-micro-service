package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.dto.request.AutoMakeScheduleDto;
import openerp.openerpresourceserver.model.dto.request.FilterClassOpenedDto;
import openerp.openerpresourceserver.model.dto.request.MakeScheduleDto;
import openerp.openerpresourceserver.model.dto.request.UpdateClassOpenedDto;
import openerp.openerpresourceserver.model.entity.ClassOpened;
import openerp.openerpresourceserver.model.entity.Schedule;

import java.util.List;

public interface ClassOpenedService {

    List<ClassOpened> getAll();

    void deleteByIds(List<Long> ids);

    List<ClassOpened> updateClassOpenedList(UpdateClassOpenedDto requestDto);

    List<ClassOpened> getBySemester(String semester);

    void setSeparateClass(Long id, Boolean isSeparateClass);

    List<ClassOpened> searchClassOpened(FilterClassOpenedDto requestDto);

    void makeSchedule(MakeScheduleDto requestDto);

    void automationMakeScheduleForCTTT(AutoMakeScheduleDto autoMakeScheduleDto);
}
