package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.domain.model.ShiftModel;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IShiftPort {
    ShiftModel createShift(ShiftModel shiftModel);
    List<ShiftModel> createShifts(List<ShiftModel> shiftModels);
    ShiftModel updateShift(ShiftModel shiftModel);
    void deleteShifts(List<UUID> ids);
    ShiftModel getShift(UUID id);
    List<ShiftModel> getShifts(List<String> userIds, LocalDate startDate, LocalDate endDate, boolean hasUnassigned);
}
