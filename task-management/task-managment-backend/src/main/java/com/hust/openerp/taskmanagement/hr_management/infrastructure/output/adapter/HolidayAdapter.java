package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IHolidayPort;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.HolidayModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.HolidayEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.HolidayRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class HolidayAdapter implements IHolidayPort {
    private final HolidayRepo holidayRepo;

    @Override
    public HolidayModel createHoliday(HolidayModel holidayModel) {
        var holidayEntity = new HolidayEntity();
        holidayEntity.setName(holidayModel.getName());
        holidayEntity.setType(holidayModel.getType());
        holidayEntity.setDates(holidayModel.getDates());
        return toModel(holidayRepo.save(holidayEntity));
    }

    @Override
    public HolidayModel updateHoliday(HolidayModel holidayModel) {
        var holidayEntity = getHolidayEntity(holidayModel.getId());
        if(holidayModel.getType() != null){
            holidayEntity.setType(holidayModel.getType());
        }
        if(holidayModel.getDates() != null){
            holidayEntity.setDates(holidayModel.getDates());
        }
        if(holidayModel.getName() != null){
            holidayEntity.setName(holidayModel.getName());
        }
        return toModel(holidayRepo.save(holidayEntity));
    }

    @Override
    public void deleteHoliday(UUID id) {
        try {
            holidayRepo.deleteById(id);
        }
        catch (Exception e) {
            throw new ApplicationException(ResponseCode.HOLIDAY_DELETE_ERROR, "Holiday deletion failed " + e.getMessage());
        }
    }

    @Override
    public HolidayModel getHoliday(UUID id) {
        return toModel(getHolidayEntity(id));
    }

    @Override
    public List<HolidayModel> getHolidays(LocalDate startDate, LocalDate endDate) {
        var holidays = holidayRepo.findHolidaysWithDatesInRange(startDate, endDate);
        return toModels(holidays);
    }

    private HolidayEntity getHolidayEntity(UUID id) {
        return holidayRepo.findById(id).orElseThrow(
            () -> new ApplicationException(
                ResponseCode.STAFF_EXISTED,
                "Holiday with id " + id + " already exists"
            )
        );
    }

    private HolidayModel toModel(HolidayEntity holidayEntity){
        return HolidayModel.builder()
            .id(holidayEntity.getId())
            .name(holidayEntity.getName())
            .type(holidayEntity.getType())
            .dates(holidayEntity.getDates())
            .build();
    }

    private List<HolidayModel> toModels(List<HolidayEntity> staffEntities){
        return staffEntities.stream()
            .map(this::toModel)
            .toList();
    }
}
