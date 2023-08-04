package openerp.containertransport.service;

import openerp.containertransport.dto.dashboard.DashboardOrderByMonthRes;
import openerp.containertransport.dto.dashboard.DashboardUseTruck;

public interface DashboardService {
    DashboardOrderByMonthRes getOrderByMonth(int year);
    DashboardUseTruck getRateUseTruck();
}
