package openerp.openerpresourceserver.application.port.out.example.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.example.filter.IExampleFilter;
import openerp.openerpresourceserver.constant.CheckinoutType;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.CheckinoutModel;
import openerp.openerpresourceserver.processor.AutoMapped;

import java.time.LocalDate;

@Data
@Builder
@Getter
@Setter
public class IterableUseCaseExample implements IExampleFilter, UseCase {

}
