package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IAbsencePort;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.AbsenceEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.AbsenceRepo;
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
public class AbsenceAdapter implements IAbsencePort {
    private final AbsenceRepo absenceRepo;

    @Override
    public AbsenceModel createAbsence(AbsenceModel absenceModel) {
        var absenceEntity = new AbsenceEntity();
        absenceEntity.setType(absenceModel.getType());
        absenceEntity.setStartTime(absenceModel.getStartTime());
        absenceEntity.setEndTime(absenceModel.getEndTime());
        absenceEntity.setDate(absenceModel.getDate());
        absenceEntity.setReason(absenceModel.getReason());
        absenceEntity.setUserId(absenceModel.getUserId());
        absenceEntity.setStatus(AbsenceStatus.ACTIVE);
        return toModel(absenceRepo.save(absenceEntity));
    }

    @Override
    public AbsenceModel updateAbsence(AbsenceModel absenceModel) {
        var absenceEntity = getAbsenceEntity(absenceModel.getId());
        if(absenceModel.getDate() != null){
            absenceEntity.setDate(absenceModel.getDate());
        }
        if(absenceModel.getReason() != null){
            absenceEntity.setReason(absenceModel.getReason());
        }
        if(absenceModel.getStartTime() != null){
            absenceEntity.setStartTime(absenceModel.getStartTime());
        }
        if(absenceModel.getEndTime() != null){
            absenceEntity.setEndTime(absenceModel.getEndTime());
        }
        if(absenceModel.getStatus() != null){
            absenceEntity.setStatus(absenceModel.getStatus());
        }
        if(absenceModel.getType() != null){
            absenceEntity.setType(absenceModel.getType());
        }
        return toModel(absenceRepo.save(absenceEntity));
    }

    @Override
    public void cancelAbsence(UUID id) {
        var absenceEntity = getAbsenceEntity(id);
        var startDateTime = LocalDateTime.of(absenceEntity.getDate(), absenceEntity.getStartTime());
        if(startDateTime.isBefore(LocalDateTime.now())){
            throw new ApplicationException(ResponseCode.CANCEL_ABSENCE_ERROR, "Absence must cancel before start time");
        }
        absenceEntity.setStatus(AbsenceStatus.INACTIVE);
    }

    @Override
    public AbsenceModel getAbsence(UUID id) {
        return toModel(getAbsenceEntity(id));
    }

    @Override
    public List<AbsenceModel> getAbsences(List<String> userIds, LocalDate startDate, LocalDate endDate) {
        var absences = absenceRepo.findAbsencesWithDatesInRange(userIds, startDate, endDate);
        return toModels(absences);
    }

    private AbsenceEntity getAbsenceEntity(UUID id) {
        return absenceRepo.findById(id).orElseThrow(
            () -> new ApplicationException(
                ResponseCode.VALIDATE_ABSENCE_ERROR,
                "Absence with id " + id + " already exists"
            )
        );
    }

    private AbsenceModel toModel(AbsenceEntity absenceEntity){
        return AbsenceModel.builder()
            .id(absenceEntity.getId())
            .date(absenceEntity.getDate())
            .reason(absenceEntity.getReason())
            .endTime(absenceEntity.getEndTime())
            .startTime(absenceEntity.getStartTime())
            .status(absenceEntity.getStatus())
            .userId(absenceEntity.getUserId())
            .build();
    }

    private List<AbsenceModel> toModels(List<AbsenceEntity> entities){
        return entities.stream()
            .map(this::toModel)
            .toList();
    }
}
