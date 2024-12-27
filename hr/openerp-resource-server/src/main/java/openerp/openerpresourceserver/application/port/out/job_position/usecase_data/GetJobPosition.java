package openerp.openerpresourceserver.application.port.out.job_position.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.job_position.filter.IJobPositionFilter;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.IPageableRequest;

@Data
@Builder
@Getter
@Setter
public class GetJobPosition implements IJobPositionFilter, UseCase {
    private String code;
    private String name;
    private IPageableRequest pageableRequest;
}
