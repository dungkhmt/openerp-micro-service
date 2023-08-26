package openerp.containertransport.service;

import openerp.containertransport.dto.dashboard.DashboardOrderByMonthRes;
import openerp.containertransport.dto.dashboard.DashboardUseTrailer;
import openerp.containertransport.dto.dashboard.DashboardUseTruck;
import openerp.containertransport.dto.dashboard.DashboardUseTypeContainer;

public interface DashboardService {
    DashboardOrderByMonthRes getOrderByMonth(int year);
    DashboardUseTruck getRateUseTruck();
    DashboardUseTrailer getRateUseTrailer();
    DashboardUseTypeContainer getRateUseTypeContainer();
}
