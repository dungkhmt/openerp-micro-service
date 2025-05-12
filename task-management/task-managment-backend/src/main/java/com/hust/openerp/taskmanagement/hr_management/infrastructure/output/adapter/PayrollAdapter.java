package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollPort;
import com.hust.openerp.taskmanagement.hr_management.constant.PayrollStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.PayrollEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.PayrollRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class PayrollAdapter implements IPayrollPort {
    private final PayrollRepo payrollRepo;

    @Override
    public PayrollModel createPayroll(PayrollModel payrollModel) {
        var payrollEntity = new PayrollEntity();
        payrollEntity.setStatus(PayrollStatus.ACTIVE);
        return toModel(payrollRepo.save(payrollEntity));
    }

    @Override
    public void cancelPayroll(UUID id) {
        var payrollEntity = getPayrollEntity(id);
        payrollEntity.setStatus(PayrollStatus.INACTIVE);
        payrollRepo.save(payrollEntity);
    }

    @Override
    public PayrollModel getPayroll(UUID id) {
        return toModel(getPayrollEntity(id));
    }

    @Override
    public List<PayrollModel> getPayrolls(List<String> userIds, LocalDate startDate, LocalDate endDate) {
        var payrolls = payrollRepo.findAll();
        return toModels(payrolls);
    }

    private PayrollEntity getPayrollEntity(UUID id) {
        return payrollRepo.findById(id).orElseThrow(
            () -> new ApplicationException("Payroll with id " + id + " not found")
        );
    }

    private PayrollModel toModel(PayrollEntity payrollEntity) {
        return PayrollModel.builder()
            .id(payrollEntity.getId())
            .name(payrollEntity.getName())
            .createdBy(payrollEntity.getCreatedBy())
            .fromdate(payrollEntity.getFromdate())
            .thruDate(payrollEntity.getThruDate())
            .workHoursPerDay(payrollEntity.getWorkHoursPerDay())
            .totalWorkDays(payrollEntity.getTotalWorkDays())
            .totalHolidayDays(payrollEntity.getTotalHolidayDays())
            .status(payrollEntity.getStatus())
            .build();
    }

    private List<PayrollModel> toModels(List<PayrollEntity> entities) {
        return entities.stream()
            .map(this::toModel)
            .toList();
    }
}
