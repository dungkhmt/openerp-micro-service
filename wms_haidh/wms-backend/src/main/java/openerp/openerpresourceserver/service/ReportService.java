package openerp.openerpresourceserver.service;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.dto.response.CategoryProfitDatapoint;
import openerp.openerpresourceserver.dto.response.ProfitDatapoint;
import openerp.openerpresourceserver.dto.response.RevenueProfitDatapoint;
import openerp.openerpresourceserver.repository.OrderRepository;

@Service
public class ReportService {

    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<RevenueProfitDatapoint> getMonthlyStats() {
        Map<String, Double> profitMap = orderRepository.getMonthlyProfit().stream()
            .collect(Collectors.toMap(ProfitDatapoint::date, ProfitDatapoint::profit));

        return orderRepository.getMonthlyRevenue().stream()
            .map(r -> new RevenueProfitDatapoint(
                r.date(),
                r.revenue(),
                profitMap.getOrDefault(r.date(), 0.0)
            ))
            .sorted(Comparator.comparing(RevenueProfitDatapoint::date))
            .toList();
    }
    
    public List<ProfitDatapoint> getMonthlyProfit() {
        return orderRepository.getMonthlyProfit();
    }
    
    public List<CategoryProfitDatapoint> getProfitByCategoryForMonth(String month) {
        return orderRepository.getMonthlyProfitByCategory(month);
    }



}

