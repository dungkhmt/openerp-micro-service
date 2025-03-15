package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckinoutPort;
import openerp.openerpresourceserver.application.port.out.checkinout.filter.IAttendancesFilter;
import openerp.openerpresourceserver.application.port.out.checkinout.filter.ICheckinoutFilter;
import openerp.openerpresourceserver.application.port.out.checkinout.usecase_data.Checkinout;
import openerp.openerpresourceserver.constant.CheckinoutType;
import openerp.openerpresourceserver.domain.model.CheckinoutModel;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckinoutEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.ICheckinoutRepo;
import openerp.openerpresourceserver.infrastructure.output.persistence.specification.AttendanceSpecification;
import openerp.openerpresourceserver.infrastructure.output.persistence.specification.CheckinoutSpecification;
import openerp.openerpresourceserver.application.port.out.checkinout.filter.impl.CheckinoutFilter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinoutAdapter implements ICheckinoutPort {
    private final ICheckinoutRepo checkinoutRepo;

    @Override
    public void checkinout(Checkinout checkinout) {
        var type = existCheckin(checkinout.getPointTime().toLocalDate())
                ? CheckinoutType.CHECKOUT : CheckinoutType.CHECKIN;
        var entity = new CheckinoutEntity();
        entity.setTimePoint(checkinout.getPointTime());
        entity.setCheckinout(type);
        checkinoutRepo.save(entity);
    }

    @Override
    public List<CheckinoutModel> getCheckinout(ICheckinoutFilter filter) {
        var spec = new CheckinoutSpecification(filter);
        Sort sort = Sort.by(Sort.Direction.DESC, "timePoint");
        var checkinoutList = checkinoutRepo.findAll(spec, sort);
        return checkinoutList.stream()
                .map(CheckinoutAdapter::toModel)
                .toList();
    }

    @Override
    public List<CheckinoutModel> getCheckinout(IAttendancesFilter filter) {
        var spec = new AttendanceSpecification(filter);
        var checkinoutList = checkinoutRepo.findAll(spec);
        return checkinoutList.stream()
                .map(CheckinoutAdapter::toModel)
                .toList();
    }

    private boolean existCheckin(LocalDate checkinDate) {
        var filter = CheckinoutFilter.builder()
                .date(checkinDate)
                .type(CheckinoutType.CHECKIN)
                .build();
        var spec = new CheckinoutSpecification(filter);
        return checkinoutRepo.exists(spec);
    }

    private static CheckinoutModel toModel(CheckinoutEntity entity){
        return CheckinoutModel.builder()
                .pointTime(entity.getTimePoint())
                .userId(entity.getUserId())
                .type(entity.getCheckinout())
                .build();
    }
}
