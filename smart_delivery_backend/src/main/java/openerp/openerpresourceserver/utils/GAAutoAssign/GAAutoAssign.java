package openerp.openerpresourceserver.utils.GAAutoAssign;


import jakarta.ws.rs.NotFoundException;
import openerp.openerpresourceserver.dto.OrderResponseCollectorShipperDto;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.repository.AssignOrderCollectorRepository;
import openerp.openerpresourceserver.repository.SenderRepo;
import openerp.openerpresourceserver.utils.DistanceCalculator.GraphHopperCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GAAutoAssign {
    private static final int MAX_GENERATIONS = 50;
    private static final int POPULATION_SIZE = 50;
    private static final double CROSSOVER_RATE = 0.8;
    private static final double MUTATION_RATE = 0.1;
    @Autowired
    private SenderRepo senderRepo;
    @Autowired
    private AssignOrderCollectorRepository assignOrderCollectorRepository;
    @Autowired
    private GraphHopperCalculator graphHopperCalculator;  // T

    private Map<UUID, Sender> senderMap;
    private double[][] distanceMatrix; // Mảng 2 chiều lưu trữ khoảng cách



    private void initializeSenderMap(List<Order> orderList){
        Map<UUID, Sender> senderMap = new HashMap<>();
        for (Order order : orderList) {
            Sender sender = senderRepo.findById(order.getSenderId()).orElseThrow(() -> new NotFoundException("Sender not found"));
            senderMap.put(order.getSenderId(), sender);
        }
        this.senderMap = senderMap;
    }
    private void initializeDistanceMatrix(List<Order> orderList) {
        int size = orderList.size();
        distanceMatrix = new double[size][size]; // Khởi tạo mảng 2 chiều

        for (int i = 0; i < size; i++) {
            Sender sender1 = senderMap.get(orderList.get(i).getSenderId());
            for (int j = 0; j < size; j++) {
                Sender sender2 = senderMap.get(orderList.get(j).getSenderId());
                if (sender1.getLatitude() == null || sender1.getLongitude() == null ||
                        sender2.getLatitude() == null || sender2.getLongitude() == null) {
                    throw new NotFoundException("Location not found for sender");
                }
                // Tính khoảng cách và lưu vào mảng
                distanceMatrix[i][j] = graphHopperCalculator.calculateRealDistanceFromTo(
                        sender1.getLatitude(), sender1.getLongitude(),
                        sender2.getLatitude(), sender2.getLongitude()
                );
            }
        }
    }
    public Map<UUID, List<Order>> autoAssignOrderToEmployee(Hub hub, List<Order> orderList, List<Employee> employees) {
        initializeSenderMap(orderList);
        initializeDistanceMatrix(orderList); // Khởi tạo mảng khoảng cách

        Population population = new Population(senderMap, POPULATION_SIZE, CROSSOVER_RATE, MUTATION_RATE, orderList, employees, graphHopperCalculator, distanceMatrix);
        Individual bestIndividual = null;

        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            population.evaluateFitness();
            List<Individual> selectedParents = population.selectParents();
            List<Individual> offspring = population.reproduct(selectedParents);
            population.mutate(offspring);
            population = new Population(offspring, POPULATION_SIZE, CROSSOVER_RATE, MUTATION_RATE,graphHopperCalculator, distanceMatrix);
//            System.out.println("population" + population.getIndividuals().size());
            bestIndividual = population.findBestIndividual();
//            System.out.println("sad" + bestIndividual.getChromosome().toString());

        }

        for(Order order : orderList){
            order.setStatus(OrderStatus.ASSIGNED);
        }
//        System.out.println("sad" + bestIndividual.getChromosome().toString());
//        System.out.println("seb" + senderMap.size());

        return generateResponse(bestIndividual, orderList, employees);
    }

    private Map<UUID, List<Order>> generateResponse(Individual bestIndividual, List<Order> orderList, List<Employee> employees) {
            List<OrderResponseCollectorShipperDto> responses = new ArrayList<>();
            ArrayList<Integer> chromosome = bestIndividual.getChromosome(); // Lấy chromosome từ cá thể tốt nhất
//        System.out.println("Danh sách Schromosome:" + chromosome.toString());

            // Kiểm tra độ dài của chromosome và danh sách đơn hàng
            if (chromosome.size() != orderList.size()) {
                throw new IllegalStateException("Chromosome size does not match the number of orders.");
            }
//        System.out.println("Danh sách Collector:");
//        collectorList.forEach(collector ->
//                System.out.println("Collector ID: " + collector.getId() +
//                        " | Name: " + collector.getName()));

            // Tạo map Collector -> List<Order>
            Map<UUID, List<Order>> collectorOrderMap = new HashMap<>();
            for (int i = 0; i < chromosome.size(); i++) {
                int collectorIndex = chromosome.get(i);
                Employee employee = employees.get(collectorIndex);

                collectorOrderMap
                        .computeIfAbsent(employee.getId(), k -> new ArrayList<>())
                        .add(orderList.get(i));
            }
//            System.out.println("Danh sách Collector và hashCode:");
//        for (Collector collector : collectorList) {
////            System.out.println("Collector ID: " + collector.getId() +
////                    " | Name: " + collector.getName() +
////                    " | HashCode: " + collector.hashCode());
////        }

        // In ra danh sách đơn hàng của từng Collector theo cách dễ đọc hơn

            // Tạo danh sách AssignOrderCollector và lưu vào cơ sở dữ liệu
            List<AssignOrderCollector> assignments = new ArrayList<>();
            for (Map.Entry<UUID, List<Order>> entry : collectorOrderMap.entrySet()) {
                UUID collectorId = entry.getKey();
                int sequenceNumber = 1;
                for (Order order : entry.getValue()) {
                    // Lưu kết quả vào bảng AssignOrderCollector
                    AssignOrderCollector assignment = new AssignOrderCollector();
                    assignment.setOrderId(order.getId());
                    assignment.setSequenceNumber(sequenceNumber++);
                    assignment.setCollectorId(collectorId);
                    assignment.setCollectorName(getCollectorNameById(employees, collectorId));
                    // Set các trường khác nếu cần (như createdBy, approvedBy, v.v.)
                    assignment.setCreatedBy("admin"); // Example, bạn có thể thay đổi
                    assignment.setStatus(CollectorAssignmentStatus.ASSIGNED);
                    // Lưu vào database
                    assignments.add(assignment);
                }
            }
            // Lưu tất cả AssignOrderCollector vào cơ sở dữ liệu một lần
//            this.assignOrderCollectorRepository.saveAll(assignments);

//            // Tạo danh sách OrderResponse
//            for (Map.Entry<UUID, List<Order>> entry : collectorOrderMap.entrySet()) {
//                UUID collectorId = entry.getKey();
//                for (Order order : entry.getValue()) {
//                    responses.add(new OrderResponseCollectorShipperDto().builder()
//                            .id(order.getId())
//                            .collectorId(collectorId)
//                            .build());
//                }
//            }

            return collectorOrderMap;
        }

    private String getCollectorNameById(List<Employee> employees, UUID collectorId) {
        return employees.stream()
                .filter(collector -> collector.getId().equals(collectorId))
                .map(Employee::getName)
                .findFirst()
                .orElse(null); // hoặc trả về giá trị mặc định
    }
}


