package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollDetailPort;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.PayrollDetailEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.PayrollDetailRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class PayrollDetailAdapter implements IPayrollDetailPort {
    private final PayrollDetailRepo payrollDetailRepo;

    @Override
    public List<PayrollDetailModel> getDetailsByPayrollId(UUID payrollId) {
        return payrollDetailRepo.findByPayrollId(payrollId)
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public PayrollDetailModel saveDetail(PayrollDetailModel model) {
        var entity = toEntity(model);
        var saved = payrollDetailRepo.save(entity);
        return toModel(saved);
    }

    private PayrollDetailModel toModel(PayrollDetailEntity entity) {
        return PayrollDetailModel.builder()
                .id(entity.getId())
                .payrollId(entity.getPayrollId())
                .userId(entity.getUserId())
                .salary(entity.getSalary())
                .salaryType(entity.getSalaryType())
                .isPaidHoliday(entity.getIsPaidHoliday())
                .workHours(entity.getWorkHours())
                .pairLeaveHours(entity.getPairLeaveHours())
                .unpairLeaveHours(entity.getUnpairLeaveHours())
                .payrollAmount(entity.getPayrollAmount())
                .build();
    }

    private PayrollDetailEntity toEntity(PayrollDetailModel model) {
        var entity = new PayrollDetailEntity();
        entity.setId(model.getId());
        entity.setPayrollId(model.getPayrollId());
        entity.setUserId(model.getUserId());
        entity.setSalary(model.getSalary());
        entity.setSalaryType(model.getSalaryType());
        entity.setIsPaidHoliday(model.getIsPaidHoliday());
        entity.setWorkHours(model.getWorkHours());
        entity.setPairLeaveHours(model.getPairLeaveHours());
        entity.setUnpairLeaveHours(model.getUnpairLeaveHours());
        entity.setPayrollAmount(model.getPayrollAmount());
        return entity;
    }
}
