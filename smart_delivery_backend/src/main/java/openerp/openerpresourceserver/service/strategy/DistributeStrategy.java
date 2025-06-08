package openerp.openerpresourceserver.service.strategy;

import openerp.openerpresourceserver.entity.Employee;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.entity.Order;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DistributeStrategy {
    Map<UUID, List<Order>> assignOrderToEmployees(Hub hub, List<Order> orders, List<Employee> employees);
    String getName();
}
