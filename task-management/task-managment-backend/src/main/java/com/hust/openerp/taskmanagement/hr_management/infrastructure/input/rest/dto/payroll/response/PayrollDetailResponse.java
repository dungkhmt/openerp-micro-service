package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.payroll.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.PayrollStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.SalaryType;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PayrollDetailResponse {
    private UUID id;
    private UUID payrollId;
    private String userId;
    private Integer salary;
    private SalaryType salaryType;
    private Boolean isPaidHoliday;
    private Float workHours;
    private Float pairLeaveHours;
    private Float unpairLeaveHours;
    private Integer payrollAmount;

    public static PayrollDetailResponse fromModel(PayrollDetailModel model) {
        return PayrollDetailResponse.builder()

            .build();
    }
}

