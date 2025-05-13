package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.filter.IPayrollFilter;
import com.hust.openerp.taskmanagement.hr_management.constant.PayrollStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GetPayrollList implements UseCase, IPayrollFilter {
    private String searchName;
    private IPageableRequest pageableRequest;

    @Override
    public PayrollStatus getStatus() {
        return PayrollStatus.ACTIVE;
    }
}
