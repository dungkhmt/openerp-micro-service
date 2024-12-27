package openerp.openerpresourceserver.application.port.out.staff.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.staff.filter.IStaffFilter;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.IPageableRequest;

@Data
@Builder
@Getter
@Setter
public class FindStaff implements IStaffFilter, UseCase {
    private String staffCode;
    private String staffName;
    private String staffEmail;
    private IPageableRequest pageableRequest;
    //private String departmentId;
}
