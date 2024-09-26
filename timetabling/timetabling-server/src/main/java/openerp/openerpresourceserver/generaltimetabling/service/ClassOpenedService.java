package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.AutoMakeScheduleDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.FilterClassOpenedDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.MakeScheduleDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.UpdateClassOpenedDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassOpened;

import java.util.List;

import jakarta.validation.Valid;

public interface ClassOpenedService {

    List<ClassOpened> getAll();

    void deleteByIds(List<Long> ids);

    List<ClassOpened> updateClassOpenedList(UpdateClassOpenedDto requestDto);

    List<ClassOpened> getBySemester(String semester);

    void setSeparateClass(Long id, Boolean isSeparateClass);

    List<ClassOpened> searchClassOpened(FilterClassOpenedDto requestDto);

    void makeSchedule(MakeScheduleDto requestDto);

    void automationMakeScheduleForCTTT(AutoMakeScheduleDto autoMakeScheduleDto);

    void autoMakeGeneralSchedule(@Valid AutoMakeScheduleDto autoMakeScheduleDto);
}
