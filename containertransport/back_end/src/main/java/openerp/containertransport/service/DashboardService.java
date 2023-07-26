package openerp.containertransport.service;

import openerp.containertransport.dto.DashboardOrderByMonthRes;
import openerp.containertransport.dto.dashboard.DashboardTypeContainer;

public interface DashboardService {
    DashboardOrderByMonthRes getOrderByMonth(int year);
    DashboardTypeContainer getRateTypeContainer();
}
