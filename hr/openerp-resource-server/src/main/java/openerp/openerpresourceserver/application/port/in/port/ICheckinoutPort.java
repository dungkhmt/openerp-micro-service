package openerp.openerpresourceserver.application.port.in.port;

import openerp.openerpresourceserver.application.port.out.checkinout.usecase_data.Checkinout;
import openerp.openerpresourceserver.domain.model.CheckinoutModel;

public interface ICheckinoutPort {
    void checkinout(Checkinout checkinout);
}
