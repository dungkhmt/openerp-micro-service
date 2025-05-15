package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.filter.IPayrollFilter;
import com.hust.openerp.taskmanagement.hr_management.constant.PayrollStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.PayrollEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.PayrollRepo;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification.PayrollSpecification;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.utils.PageableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        payrollEntity.setId(UUID.randomUUID());
        payrollEntity.setName(payrollModel.getName());
        payrollEntity.setCreatedBy(payrollModel.getCreatedBy());
        payrollEntity.setFromdate(payrollModel.getFromDate());
        payrollEntity.setThruDate(payrollModel.getThruDate());
        payrollEntity.setTotalHolidayDays(payrollModel.getTotalHolidayDays());
        payrollEntity.setWorkHoursPerDay(payrollModel.getWorkHoursPerDay());
        payrollEntity.setTotalWorkDays(payrollModel.getTotalWorkDays());
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
    public PageWrapper<PayrollModel> getPayrolls(IPayrollFilter filter, IPageableRequest pageableRequest) {
        var pageable = PageableUtils.getPageable(pageableRequest);
        var spec = new PayrollSpecification(filter);
        var page = payrollRepo.findAll(spec ,pageable);
        return new PageWrapper<>(PageableUtils.getPageInfo(page), toModels(page.getContent()));
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
            .fromDate(payrollEntity.getFromdate())
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
