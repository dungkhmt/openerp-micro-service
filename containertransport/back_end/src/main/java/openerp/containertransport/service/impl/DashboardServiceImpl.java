package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.Constants;
import openerp.containertransport.dto.DashboardOrderByMonthRes;
import openerp.containertransport.dto.dashboard.DashboardTimeOrderDTO;
import openerp.containertransport.dto.dashboard.DashboardTypeContainer;
import openerp.containertransport.service.DashboardService;
import openerp.containertransport.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final OrderService orderService;
    @Override
    public DashboardOrderByMonthRes getOrderByMonth(int year) {
        Map<Integer, DashboardTimeOrderDTO> timeMonth = new HashMap<>();

        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i <= 11; i++) {
            DashboardTimeOrderDTO dashboardTimeOrderDTO = new DashboardTimeOrderDTO();
            calendar.set(year, i, 1);
            long startTime = calendar.getTimeInMillis();
            dashboardTimeOrderDTO.setStartTime(startTime);

            int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(year, i, lastDay);
            long endTime = calendar.getTimeInMillis();
            dashboardTimeOrderDTO.setEndTime(endTime);
            timeMonth.put(i, dashboardTimeOrderDTO);
        }

        for (Map.Entry<Integer, DashboardTimeOrderDTO> entry : timeMonth.entrySet()) {
            long countNewOrder = orderService.countOrderByMonth(entry.getValue(), Constants.OrderStatus.ORDERED.getStatus());
            long countDoneOrder = orderService.countOrderByMonth(entry.getValue(), Constants.OrderStatus.DONE.getStatus());
            DashboardTimeOrderDTO dashboardTimeOrderDTO = entry.getValue();
            dashboardTimeOrderDTO.setCountNewOrder(countNewOrder);
            dashboardTimeOrderDTO.setCountDoneOrder(countDoneOrder);
        }

        Map<String, List<Long>> orderByMonth = new HashMap<>();
        List<Long> listNewOrder = new ArrayList<>();
        List<Long> listDoneOrder = new ArrayList<>();
        for (Map.Entry<Integer, DashboardTimeOrderDTO> entry : timeMonth.entrySet()) {
            listNewOrder.add(entry.getValue().getCountNewOrder());
            listDoneOrder.add(entry.getValue().getCountDoneOrder());
        }
        orderByMonth.put("New", listNewOrder);
        orderByMonth.put("Done", listDoneOrder);

        return null;
    }

    @Override
    public DashboardTypeContainer getRateTypeContainer() {
        return null;
    }
}
