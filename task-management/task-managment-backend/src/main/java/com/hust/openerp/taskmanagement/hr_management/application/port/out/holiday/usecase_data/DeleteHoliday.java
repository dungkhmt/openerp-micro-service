package com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteHoliday implements UseCase {
    private UUID id;
}
