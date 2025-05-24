package com.hust.openerp.taskmanagement.hr_management.application.port.out.config.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.ConfigGroup;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetConfigs implements UseCase {
    private ConfigGroup configGroup;
}
