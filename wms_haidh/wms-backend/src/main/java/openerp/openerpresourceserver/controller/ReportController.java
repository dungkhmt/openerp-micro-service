package openerp.openerpresourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.response.CategoryProfitDatapoint;
import openerp.openerpresourceserver.dto.response.ProfitDatapoint;
import openerp.openerpresourceserver.dto.response.RevenueProfitDatapoint;
import openerp.openerpresourceserver.service.ReportService;

@RestController
@RequestMapping("/reports")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ReportController {

    private final ReportService reportService;

    @Secured("ROLE_WMS_DIRECTOR")
    @GetMapping("/monthly-revenue")
    public List<RevenueProfitDatapoint> getMonthlyRevenueAndProfit() {
        return reportService.getMonthlyStats();
    }
    
    @Secured("ROLE_WMS_DIRECTOR")
    @GetMapping("/monthly-profit")
    public List<ProfitDatapoint> getMonthlyProfit() {
        return reportService.getMonthlyProfit();
    }
    
    @Secured("ROLE_WMS_DIRECTOR")
    @GetMapping("/monthly-profit-by-category")
    public List<CategoryProfitDatapoint> getMonthlyProfitByCategory(@RequestParam("month") String month) {
        return reportService.getProfitByCategoryForMonth(month);
    }


}
