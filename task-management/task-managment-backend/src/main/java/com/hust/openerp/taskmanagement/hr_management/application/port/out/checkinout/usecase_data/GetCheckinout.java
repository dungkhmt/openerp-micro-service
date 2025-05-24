package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.filter.ICheckinoutFilter;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckinoutType;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckinoutModel;
import com.hust.openerp.taskmanagement.hr_management.processor.AutoMapped;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Builder
@Getter
@Setter
@AutoMapped(target = CheckinoutModel.class)
public class GetCheckinout implements ICheckinoutFilter, UseCase {
    private String userId;
    private LocalDate date;
    private CheckinoutType type;
}
