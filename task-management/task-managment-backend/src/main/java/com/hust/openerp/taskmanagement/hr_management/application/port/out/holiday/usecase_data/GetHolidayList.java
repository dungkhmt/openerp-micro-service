package com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetHolidayList implements UseCase {
    private LocalDate startDate;
    private LocalDate endDate;
}
