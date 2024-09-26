package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.MetaData;
import openerp.containertransport.dto.dashboard.DashboardOrderByMonthRes;
import openerp.containertransport.dto.dashboard.DashboardUseTrailer;
import openerp.containertransport.dto.dashboard.DashboardUseTruck;
import openerp.containertransport.dto.dashboard.DashboardUseTypeContainer;
import openerp.containertransport.dto.metaData.MetaDTO;
import openerp.containertransport.dto.metaData.ResponseMetaData;
import openerp.containertransport.service.DashboardService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/order-by-month")
    public ResponseEntity<?> getOrderByMonth(@RequestParam int year) {
        DashboardOrderByMonthRes dashboardOrderByMonthRes = dashboardService.getOrderByMonth(year);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), dashboardOrderByMonthRes));
    }

    @PostMapping("/truck-rate")
    public ResponseEntity<?> getRateUsingTruck() {
        DashboardUseTruck dashboardUseTruck = dashboardService.getRateUseTruck();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), dashboardUseTruck));
    }

    @PostMapping("/trailer-rate")
    public ResponseEntity<?> getRateUsingTrailer() {
        DashboardUseTrailer dashboardUseTrailer = dashboardService.getRateUseTrailer();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), dashboardUseTrailer));
    }

    @PostMapping("/type-container-rate")
    public ResponseEntity<?> getRateTypeContainer() {
        DashboardUseTypeContainer dashboardUseTypeContainer = dashboardService.getRateUseTypeContainer();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), dashboardUseTypeContainer));
    }
}
