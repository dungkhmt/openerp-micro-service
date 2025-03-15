package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;

@Data
@Builder
@Getter
@Setter
@Slf4j
public class GetStaffInfo implements UseCase {
    private String userLoginId;
    private String staffCode;
}
