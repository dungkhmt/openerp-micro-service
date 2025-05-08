package openerp.openerpresourceserver.strategy;

import jakarta.ws.rs.NotFoundException;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import openerp.openerpresourceserver.repository.AssignOrderCollectorRepository;
import openerp.openerpresourceserver.repository.RecipientRepo;
import openerp.openerpresourceserver.repository.SenderRepo;
import openerp.openerpresourceserver.utils.DistanceCalculator.GraphHopperCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("GreedyStrategy")
public class GreedyOrderAssignmentStrategy implements DistributeStrategy {

    @Autowired
    private AssignOrderCollectorRepository assignOrderCollectorRepository;

    @Autowired
    private GraphHopperCalculator graphHopperCalculator;

    @Autowired
    private RecipientRepo recipentRepo;
    @Autowired
    private SenderRepo senderRepo;
    public Map<UUID, List<Order>> assignOrderToEmployees(Hub hub, List<Order> orderList, List<Employee> employees) {
        Map<UUID, Double> driverDistanceMap = new HashMap<>();
        Map<UUID, List<Order>> driverOrderMap = new HashMap<>();

        // Initialize distance and order map for each driver
        for (Employee employee : employees) {
            driverDistanceMap.put(employee.getId(), 0.0);
            driverOrderMap.put(employee.getId(), new ArrayList<>());
        }
        UUID closestDriverId = null;
        // Assign each order to the driver with the least total distance
        if(employees.get(0) instanceof Collector) {

            for (Order order : orderList) {
                Recipient recipient = recipentRepo.findById(order.getRecipientId())
                        .orElseThrow(() -> new NotFoundException("Recipient not found"));

            double minDistance = Double.MAX_VALUE;
                // Calculate distance from hub to order
                double hubToOrderDistance = graphHopperCalculator.calculateRealDistanceFromTo(
                        hub.getLatitude(), hub.getLongitude(),
                        recipient.getLatitude(), recipient.getLongitude()
                );

                // Find the closest driver
                for (Employee employee : employees) {
                    double totalDistance = driverDistanceMap.get(employee.getId()) + hubToOrderDistance;
                    if (totalDistance < minDistance) {
                        minDistance = totalDistance;
                        closestDriverId = employee.getId();
                    }
                }
                // Update the driver's total distance and assign the order
                driverDistanceMap.put(closestDriverId, driverDistanceMap.get(closestDriverId) + minDistance);
                driverOrderMap.get(closestDriverId).add(order);
            }
        }

        else if(employees.getFirst() instanceof Shipper) {
            for (Order order : orderList) {
                Recipient recipient = recipentRepo.findById(order.getRecipientId())
                        .orElseThrow(() -> new NotFoundException("Recipient not found"));

                double minDistance = Double.MAX_VALUE;
                // Calculate distance from hub to order
                double hubToOrderDistance = graphHopperCalculator.calculateRealDistanceFromTo(
                        hub.getLatitude(), hub.getLongitude(),
                        recipient.getLatitude(), recipient.getLongitude()
                );

                // Find the closest driver
                for (Employee employee : employees) {
                    double totalDistance = driverDistanceMap.get(employee.getId()) + hubToOrderDistance;
                    if (totalDistance < minDistance) {
                        minDistance = totalDistance;
                        closestDriverId = employee.getId();
                    }
                }
                // Update the driver's total distance and assign the order
                driverDistanceMap.put(closestDriverId, driverDistanceMap.get(closestDriverId) + minDistance);
                driverOrderMap.get(closestDriverId).add(order);
            }
        }

        // Save assignments to the database
        List<AssignOrderCollector> assignments = new ArrayList<>();
        for (Map.Entry<UUID, List<Order>> entry : driverOrderMap.entrySet()) {
            UUID driverId = entry.getKey();
            int sequenceNumber = 1;
            for (Order order : entry.getValue()) {
                AssignOrderCollector assignment = new AssignOrderCollector();
                assignment.setOrderId(order.getId());
                assignment.setSequenceNumber(sequenceNumber++);
                assignment.setCollectorId(driverId);
                assignment.setCollectorName(getEmployeeNameById(employees, driverId));
                assignment.setCreatedBy("admin");
                assignment.setStatus(CollectorAssignmentStatus.ASSIGNED);
                assignments.add(assignment);
            }
        }
        assignOrderCollectorRepository.saveAll(assignments);

        return driverOrderMap;
    }

    private String getEmployeeNameById(List<Employee> employees, UUID employeeId) {
        return employees.stream()
                .filter(employee -> employee.getId().equals(employeeId))
                .map(Employee::getName)
                .findFirst()
                .orElse(null);
    }
}