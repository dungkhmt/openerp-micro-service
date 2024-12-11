package openerp.openerpresourceserver.application.port.out.checkinout.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkinout.filter.ICheckinoutFilter;
import openerp.openerpresourceserver.constant.CheckinoutType;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.CheckinoutModel;
import openerp.openerpresourceserver.processor.AutoMapped;

import java.time.LocalDate;

@Data
@Builder
@Getter
@Setter
@AutoMapped(target = CheckinoutModel.class)
public class GetCheckinout implements ICheckinoutFilter, UseCase {
    private String userId;
    private LocalDate date;
    private CheckinoutType type;
}
