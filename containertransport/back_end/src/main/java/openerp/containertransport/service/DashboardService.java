package openerp.containertransport.service;

import openerp.containertransport.dto.DashboardOrderByMonthRes;

public interface DashboardService {
    DashboardOrderByMonthRes getOrderByMonth(Long year);
}
