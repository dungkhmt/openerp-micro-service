package com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetShiftList implements UseCase {
    private List<String> userIds;
    private LocalDate startDate;
    private LocalDate endDate;
}
