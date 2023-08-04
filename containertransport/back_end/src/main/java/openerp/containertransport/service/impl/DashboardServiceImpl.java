package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.Constants;
import openerp.containertransport.dto.dashboard.DashboardOrderByMonthRes;
import openerp.containertransport.dto.TypeContainerFilterReqDTO;
import openerp.containertransport.dto.TypeContainerModel;
import openerp.containertransport.dto.dashboard.DashboardTimeOrderDTO;
import openerp.containertransport.dto.dashboard.DashboardUseTruck;
import openerp.containertransport.dto.dashboard.RateUseEntityDTO;
import openerp.containertransport.repo.TrailerRepo;
import openerp.containertransport.repo.TruckRepo;
import openerp.containertransport.service.DashboardService;
import openerp.containertransport.service.OrderService;
import openerp.containertransport.service.TypeContainerService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final OrderService orderService;
    private final TypeContainerService typeContainerService;
    private final TruckRepo truckRepo;
    private final TrailerRepo trailerRepo;
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

        DashboardOrderByMonthRes dashboardOrderByMonthRes = new DashboardOrderByMonthRes();
        dashboardOrderByMonthRes.setOrderByMonth(orderByMonth);

        return dashboardOrderByMonthRes;
    }

    @Override
    public DashboardUseTruck getRateUseTruck() {
        DashboardUseTruck dashboardUseTruck = new DashboardUseTruck();

        long countAvailable = truckRepo.countTruckByStatus(Constants.TruckStatus.AVAILABLE.getStatus());
        long countScheduler = truckRepo.countTruckByStatus(Constants.TruckStatus.SCHEDULED.getStatus());
        long countExecuting = truckRepo.countTruckByStatus(Constants.TruckStatus.EXECUTING.getStatus());
        long total = countAvailable + countScheduler + countExecuting;

        RateUseEntityDTO rateAvailable = new RateUseEntityDTO();
        rateAvailable.setName(Constants.TruckStatus.AVAILABLE.getStatus());
        rateAvailable.setRate(new BigDecimal(countAvailable/total).setScale(2));

        RateUseEntityDTO rateScheduler = new RateUseEntityDTO();
        rateScheduler.setName(Constants.TruckStatus.SCHEDULED.getStatus());
        rateScheduler.setRate(new BigDecimal(countScheduler/total).setScale(2));

        RateUseEntityDTO rateExecuting = new RateUseEntityDTO();
        rateExecuting.setName(Constants.TruckStatus.EXECUTING.getStatus());
        rateExecuting.setRate(new BigDecimal(countExecuting/total).setScale(2));

        List<RateUseEntityDTO> rateUseEntityDTOList = new ArrayList<>();
        rateUseEntityDTOList.add(rateAvailable);
        rateUseEntityDTOList.add(rateExecuting);
        rateUseEntityDTOList.add(rateScheduler);

        dashboardUseTruck.setRateUseTruck(rateUseEntityDTOList);
        return dashboardUseTruck;
    }

//    @Override
//    public DashboardUseTruck getRateTypeContainer() {
//        DashboardUseTruck dashboardUseTruck = new DashboardUseTruck();
//        Map<String, BigDecimal> rateUseTruck = new HashMap<>();
//
//        TypeContainerFilterReqDTO typeContainerFilterReqDTO = new TypeContainerFilterReqDTO();
//        List<TypeContainerModel> typeContainerModels = typeContainerService.filterTypeContainer(typeContainerFilterReqDTO).getTypeContainers();
//
//        Long countContainer = typeContainerService.countContainer();
//
//        typeContainerModels.forEach((item) -> {
//
//            BigDecimal rate = BigDecimal.valueOf((item.getTotal()/countContainer)).setScale(2);
//            rateTypeContainer.put(item.getSize(), rate);
//        });
//        dashboardUseTruck.setRateTypeContainer(rateTypeContainer);
//
//        return dashboardUseTruck;
//    }
}
