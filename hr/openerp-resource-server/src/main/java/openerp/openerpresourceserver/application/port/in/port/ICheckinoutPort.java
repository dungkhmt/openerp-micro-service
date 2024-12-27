package openerp.openerpresourceserver.application.port.in.port;

import openerp.openerpresourceserver.application.port.out.checkinout.filter.IAttendancesFilter;
import openerp.openerpresourceserver.application.port.out.checkinout.filter.ICheckinoutFilter;
import openerp.openerpresourceserver.application.port.out.checkinout.usecase_data.Checkinout;
import openerp.openerpresourceserver.domain.model.CheckinoutModel;

import java.util.List;

public interface ICheckinoutPort {
    void checkinout(Checkinout checkinout);
    List<CheckinoutModel> getCheckinout(ICheckinoutFilter filter);
    List<CheckinoutModel> getCheckinout(IAttendancesFilter filter);
}
