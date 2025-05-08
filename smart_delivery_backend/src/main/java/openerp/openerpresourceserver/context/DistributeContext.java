package openerp.openerpresourceserver.context;

import openerp.openerpresourceserver.entity.Employee;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.strategy.DistributeStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DistributeContext {

    private DistributeStrategy distributeStrategy;
    private final Map<String, DistributeStrategy> strategyMap;

    @Value("${delivery.strategy.default}")
    private String defaultStrategyName;

    public DistributeContext(Map<String, DistributeStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    @PostConstruct
    public void init() {
        this.distributeStrategy = strategyMap.getOrDefault(defaultStrategyName, null);
    }

    public synchronized void setDistributeStrategy(String strategyName) {
        if (strategyMap.containsKey(strategyName)) {
            this.distributeStrategy = strategyMap.get(strategyName);
        } else {
            throw new IllegalArgumentException("Không tìm thấy strategy: " + strategyName);
        }
    }

    public Map<UUID, List<Order>> assignOrderToEmployees(Hub hub, List<Order> orders, List< Employee > employees) {
        if (distributeStrategy == null) {
            throw new IllegalStateException("Strategy chưa được cấu hình.");
        }
       return distributeStrategy.assignOrderToEmployees( hub,  orders, employees);
    }
}
