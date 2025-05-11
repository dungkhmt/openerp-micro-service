/*
package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollPort;
import com.hust.openerp.taskmanagement.hr_management.constant.PayrollStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.PayrollEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.PayrollRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class PayrollAdapter implements IPayrollPort {
    private final PayrollRepo PayrollRepo;

    @Override
    public PayrollModel createPayroll(PayrollModel payrollModel) {
        var payrollEntity = new PayrollEntity();
        payrollEntity.setStatus(PayrollStatus.ACTIVE);
        return toModel(PayrollRepo.save(payrollEntity));
    }


    @Override
    public void cancelPayroll(UUID id) {
        var payrollEntity = getPayrollEntity(id);
        var startDateTime = LocalDateTime.of(payrollEntity.getDate(), payrollEntity.getStartTime());
        if(startDateTime.isBefore(LocalDateTime.now())){
            throw new ApplicationException(ResponseCode.CANCEL_ABSENCE_ERROR, "Payroll must cancel before start time");
        }
        payrollEntity.setStatus(PayrollStatus.INACTIVE);
        PayrollRepo.save(payrollEntity);
    }

    @Override
    public PayrollModel getPayroll(UUID id) {
        return toModel(getPayrollEntity(id));
    }

    @Override
    public List<PayrollModel> getPayrolls(List<String> userIds, LocalDate startDate, LocalDate endDate) {
        var payrolls = PayrollRepo.findPayrollsWithDatesInRange(userIds, startDate, endDate);
        return toModels(payrolls);
    }

    private PayrollEntity getPayrollEntity(UUID id) {
        return PayrollRepo.findById(id).orElseThrow(
            () -> new ApplicationException(
                ResponseCode.VALIDATE_ABSENCE_ERROR,
                "Payroll with id " + id + " already exists"
            )
        );
    }

    private PayrollModel toModel(PayrollEntity payrollEntity){
        return PayrollModel.builder()
            .build();
    }

    private List<PayrollModel> toModels(List<PayrollEntity> entities){
        return entities.stream()
            .map(this::toModel)
            .toList();
    }
}
*/
