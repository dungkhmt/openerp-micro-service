package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IShiftPort;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ShiftModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.ShiftEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.ShiftRepo;
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
public class ShiftAdapter implements IShiftPort {
    private final ShiftRepo shiftRepo;

    @Override
    public ShiftModel createShift(ShiftModel shiftModel) {
        return toModel(shiftRepo.save(toEntity(shiftModel)));
    }

    @Override
    public List<ShiftModel> createShifts(List<ShiftModel> shiftModels) {
        return toModels(shiftRepo.saveAll(toEntities(shiftModels)));
    }

    @Override
    public ShiftModel updateShift(ShiftModel shiftModel) {
        var shiftEntity = getShiftEntity(shiftModel.getId());
        shiftEntity.setUserId(shiftModel.getUserId());
        if(shiftModel.getSlots() != null || shiftModel.getUserId() != null){
            shiftEntity.setSlots(shiftModel.getSlots());
        }

        if(shiftModel.getDate() != null){
            shiftEntity.setDate(shiftModel.getDate());
        }
        if(shiftModel.getStartTime() != null){
            shiftEntity.setStartTime(shiftModel.getStartTime());
        }
        if(shiftModel.getEndTime() != null){
            shiftEntity.setEndTime(shiftModel.getEndTime());
        }
        return toModel(shiftRepo.save(shiftEntity));
    }

    @Override
    public void deleteShifts(List<UUID> ids) {
        shiftRepo.deleteAllByIdInBatch(ids);
    }

    @Override
    public ShiftModel getShift(UUID id) {
        return toModel(getShiftEntity(id));
    }

    @Override
    public List<ShiftModel> getShifts(List<String> userIds, LocalDate startDate, LocalDate endDate, boolean hasUnassigned) {
        var shifts = shiftRepo.findShiftsWithDatesInRange(userIds, startDate, endDate, hasUnassigned);
        return toModels(shifts);
    }


    private ShiftEntity getShiftEntity(UUID id) {
        return shiftRepo.findById(id).orElseThrow(
            () -> new ApplicationException(
                ResponseCode.VALIDATE_ABSENCE_ERROR,
                "Shift with id " + id + " already exists"
            )
        );
    }

    private ShiftEntity toEntity(ShiftModel shiftModel){
        var shiftEntity = new ShiftEntity();
        shiftEntity.setUserId(shiftModel.getUserId());
        shiftEntity.setNote(shiftModel.getNote());
        shiftEntity.setSlots(shiftModel.getSlots());
        shiftEntity.setStartTime(shiftModel.getStartTime());
        shiftEntity.setEndTime(shiftModel.getEndTime());
        shiftEntity.setDate(shiftModel.getDate());
        return shiftEntity;
    }

    private List<ShiftEntity> toEntities(List<ShiftModel> models){
        return models.stream()
            .map(this::toEntity)
            .toList();
    }

    private ShiftModel toModel(ShiftEntity shiftEntity){
        return ShiftModel.builder()
            .id(shiftEntity.getId())
            .userId(shiftEntity.getUserId())
            .note(shiftEntity.getNote())
            .date(shiftEntity.getDate())
            .slots(shiftEntity.getSlots())
            .endTime(shiftEntity.getEndTime())
            .startTime(shiftEntity.getStartTime())
            .build();
    }

    private List<ShiftModel> toModels(List<ShiftEntity> entities){
        return entities.stream()
            .map(this::toModel)
            .toList();
    }
}
