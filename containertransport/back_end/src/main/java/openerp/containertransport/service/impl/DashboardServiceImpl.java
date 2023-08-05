package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.Constants;
import openerp.containertransport.dto.dashboard.*;
import openerp.containertransport.dto.TypeContainerFilterReqDTO;
import openerp.containertransport.dto.TypeContainerModel;
import openerp.containertransport.repo.TrailerRepo;
import openerp.containertransport.repo.TruckRepo;
import openerp.containertransport.service.DashboardService;
import openerp.containertransport.service.OrderService;
import openerp.containertransport.service.TypeContainerService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        float countAvailable = truckRepo.countTruckByStatus(Constants.TruckStatus.AVAILABLE.getStatus());
        float countScheduler = truckRepo.countTruckByStatus(Constants.TruckStatus.SCHEDULED.getStatus());
        float countExecuting = truckRepo.countTruckByStatus(Constants.TruckStatus.EXECUTING.getStatus());
        float total = countAvailable + countScheduler + countExecuting;

        RateUseEntityDTO rateAvailable = new RateUseEntityDTO();
        rateAvailable.setName(Constants.TruckStatus.AVAILABLE.getStatus());
        rateAvailable.setY(new BigDecimal(countAvailable/total*100).setScale(2, RoundingMode.HALF_DOWN));

        RateUseEntityDTO rateScheduler = new RateUseEntityDTO();
        rateScheduler.setName(Constants.TruckStatus.SCHEDULED.getStatus());
        rateScheduler.setY(new BigDecimal(countScheduler/total*100).setScale(2, RoundingMode.HALF_DOWN));

        RateUseEntityDTO rateExecuting = new RateUseEntityDTO();
        rateExecuting.setName(Constants.TruckStatus.EXECUTING.getStatus());
        rateExecuting.setY(new BigDecimal(countExecuting/total*100).setScale(2, RoundingMode.HALF_DOWN));

        List<RateUseEntityDTO> rateUseEntityDTOList = new ArrayList<>();
        rateUseEntityDTOList.add(rateAvailable);
        rateUseEntityDTOList.add(rateExecuting);
        rateUseEntityDTOList.add(rateScheduler);

        dashboardUseTruck.setRateUseTruck(rateUseEntityDTOList);
        return dashboardUseTruck;
    }

    @Override
    public DashboardUseTrailer getRateUseTrailer() {
        DashboardUseTrailer dashboardUseTrailer = new DashboardUseTrailer();

        float countAvailable = trailerRepo.countTrailerByStatus(Constants.TrailerStatus.AVAILABLE.getStatus());
        float countScheduler = trailerRepo.countTrailerByStatus(Constants.TrailerStatus.SCHEDULED.getStatus());
        float countExecuting = trailerRepo.countTrailerByStatus(Constants.TrailerStatus.EXECUTING.getStatus());
        float total = countAvailable + countScheduler + countExecuting;

        RateUseEntityDTO rateAvailable = new RateUseEntityDTO();
        rateAvailable.setName(Constants.TrailerStatus.AVAILABLE.getStatus());
        rateAvailable.setY(new BigDecimal(countAvailable/total*100).setScale(2, RoundingMode.HALF_DOWN));

        RateUseEntityDTO rateScheduler = new RateUseEntityDTO();
        rateScheduler.setName(Constants.TrailerStatus.SCHEDULED.getStatus());
        rateScheduler.setY(new BigDecimal(countScheduler/total*100).setScale(2, RoundingMode.HALF_DOWN));

        RateUseEntityDTO rateExecuting = new RateUseEntityDTO();
        rateExecuting.setName(Constants.TrailerStatus.EXECUTING.getStatus());
        rateExecuting.setY(new BigDecimal(countExecuting/total*100).setScale(2, RoundingMode.HALF_DOWN));

        List<RateUseEntityDTO> rateUseEntityDTOList = new ArrayList<>();
        rateUseEntityDTOList.add(rateAvailable);
        rateUseEntityDTOList.add(rateExecuting);
        rateUseEntityDTOList.add(rateScheduler);

        dashboardUseTrailer.setRateUseTrailer(rateUseEntityDTOList);

        return dashboardUseTrailer;
    }

    @Override
    public DashboardUseTypeContainer getRateUseTypeContainer() {
        DashboardUseTypeContainer dashboardUseTypeContainer = new DashboardUseTypeContainer();

        float countSize20 = typeContainerService.countContainer(20);
        float countSize40 = typeContainerService.countContainer(40);
        float total = countSize20 + countSize40;

        RateUseEntityDTO rateSize20 = new RateUseEntityDTO();
        rateSize20.setName("20ft");
        rateSize20.setY(new BigDecimal(countSize20/total*100).setScale(2, RoundingMode.HALF_DOWN));

        RateUseEntityDTO rateSize40 = new RateUseEntityDTO();
        rateSize40.setName("40ft");
        rateSize40.setY(new BigDecimal(countSize40/total*100).setScale(2, RoundingMode.HALF_DOWN));

        List<RateUseEntityDTO> rateUseEntityDTOList = new ArrayList<>();
        rateUseEntityDTOList.add(rateSize20);
        rateUseEntityDTOList.add(rateSize40);

        dashboardUseTypeContainer.setRateUseTypeContainer(rateUseEntityDTOList);

        return dashboardUseTypeContainer;
    }
}
