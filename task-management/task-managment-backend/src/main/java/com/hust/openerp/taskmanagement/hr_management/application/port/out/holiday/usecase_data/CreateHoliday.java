package com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.HolidayType;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.HolidayModel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class CreateHoliday implements UseCase {
    private String name;
    private HolidayType type;
    private List<LocalDate> dates;

    public HolidayModel toModel(){
        return HolidayModel.builder()
            .name(name)
            .type(type)
            .dates(dates)
            .build();
    }
}
