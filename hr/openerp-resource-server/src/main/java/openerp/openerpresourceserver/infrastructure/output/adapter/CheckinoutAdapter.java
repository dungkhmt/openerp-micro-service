package openerp.openerpresourceserver.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckinoutPort;
import openerp.openerpresourceserver.application.port.out.checkinout.usecase_data.Checkinout;
import openerp.openerpresourceserver.constant.CheckinoutType;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckinoutEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.ICheckinoutRepo;
import openerp.openerpresourceserver.infrastructure.output.persistence.specification.CheckinoutSpecification;
import openerp.openerpresourceserver.infrastructure.output.persistence.specification.filter.impl.CheckinoutFilter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinoutAdapter implements ICheckinoutPort {
    private final ICheckinoutRepo checkinoutRepo;

    @Override
    public void checkinout(Checkinout usecase) {
        var type = existCheckin(usecase.getPointTime().toLocalDate()) ? CheckinoutType.CHECKOUT : CheckinoutType.CHECKIN;
        var entity = new CheckinoutEntity();
        entity.setTimePoint(usecase.getPointTime());
        entity.setCheckinout(type);
        checkinoutRepo.save(entity);
    }

    private boolean existCheckin(LocalDate checkinDate) {
        var filter = CheckinoutFilter.builder()
                .date(checkinDate)
                .type(CheckinoutType.CHECKIN)
                .build();
        var spec = new CheckinoutSpecification(filter);
        return checkinoutRepo.exists(spec);
    }
}
