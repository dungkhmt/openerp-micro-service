package openerp.openerpresourceserver.service.strategy;

import jakarta.ws.rs.NotFoundException;
import openerp.openerpresourceserver.dto.OrderResponseCollectorShipperDto;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.repository.AssignOrderCollectorRepository;
import openerp.openerpresourceserver.repository.SenderRepo;
import openerp.openerpresourceserver.repository.RecipientRepo;
import openerp.openerpresourceserver.utils.DistanceCalculator.GraphHopperCalculator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("GAStrategy")
public class GAOrderAssignmentStrategy implements DistributeStrategy {
    private static final int MAX_GENERATIONS = 300;
    private static final int POPULATION_SIZE = 200;
    private static final double CROSSOVER_RATE = 0.9;
    private static final double MUTATION_RATE = 0.1;
    private String name = "GAStrategy";
    @Autowired
    private SenderRepo senderRepo;
    @Autowired
    private AssignOrderCollectorRepository assignOrderCollectorRepository;
    @Autowired
    private GraphHopperCalculator graphHopperCalculator;  // T
    @Autowired
    private RecipientRepo recipientRepo;
    private Map<UUID, Sender> senderMap;
    private Map<UUID, Recipient> recipientMap;

    private double[][] distanceMatrix; // Mảng 2 chiều lưu trữ khoảng cách



    private void initializeEmployeeMap(List<Order> orderList){
        Map<UUID, Sender> senderMap = new HashMap<>();
         Map<UUID, Recipient> recipientMap= new HashMap<>();
        for (Order order : orderList) {
            Sender sender = senderRepo.findById(order.getSenderId()).orElseThrow(() -> new NotFoundException("Sender not found"));
            senderMap.put(order.getSenderId(), sender);
            Recipient recipient = recipientRepo.findById(order.getRecipientId()).orElseThrow(() -> new NotFoundException("Recipient not found"));
            recipientMap.put(order.getRecipientId(), recipient);
        }

        this.senderMap = senderMap;
        this.recipientMap = recipientMap;
    }

    @Override
    public String getName() {
        return name;
    }

    private void initializeDistanceMatrix(List<Order> orderList, List<Employee> employees, Map<UUID, Integer> orderIndexMap) {
        int numOrders = orderList.size();
        this.distanceMatrix = new double[numOrders][numOrders];

        // Tạo map index trước
        for (int i = 0; i < numOrders; i++) {
            orderIndexMap.put(orderList.get(i).getId(), i);
        }

        boolean isCollector = employees.getFirst() instanceof Collector;

        // Lưu trữ tọa độ để tránh truy cập lặp lại
        double[] latitudes = new double[numOrders];
        double[] longitudes = new double[numOrders];

        if (isCollector) {
            // Lấy tất cả Sender ID
            Set<UUID> senderIds = orderList.stream()
                    .map(Order::getSenderId)
                    .collect(Collectors.toSet());
            // Gọi DB 1 LẦN DUY NHẤT
            Map<UUID, Sender> senderMap = senderRepo.findAllById(senderIds)
                    .stream()
                    .collect(Collectors.toMap(Sender::getSenderId, sender -> sender));

            // Điền tọa độ vào mảng
            for (int i = 0; i < numOrders; i++) {
                Order order = orderList.get(i);
                Sender sender = senderMap.get(order.getSenderId());
                if (sender == null || sender.getLatitude() == null || sender.getLongitude() == null) {
                    throw new NotFoundException("Không tìm thấy thông tin hoặc tọa độ cho Sender ID: " + order.getSenderId());
                }
                latitudes[i] = sender.getLatitude();
                longitudes[i] = sender.getLongitude();
            }
        } else { // Trường hợp là Shipper
            // Lấy tất cả Recipient ID
            Set<UUID> recipientIds = orderList.stream()
                    .map(Order::getRecipientId)
                    .collect(Collectors.toSet());
            // Gọi DB 1 LẦN DUY NHẤT
            Map<UUID, Recipient> recipientMap = recipientRepo.findAllById(recipientIds)
                    .stream()
                    .collect(Collectors.toMap(Recipient::getRecipientId, recipient -> recipient));

            // Điền tọa độ vào mảng
            for (int i = 0; i < numOrders; i++) {
                Order order = orderList.get(i);
                Recipient recipient = recipientMap.get(order.getRecipientId());
                if (recipient == null || recipient.getLatitude() == null || recipient.getLongitude() == null) {
                    throw new NotFoundException("Không tìm thấy thông tin hoặc tọa độ cho Recipient ID: " + order.getRecipientId());
                }
                latitudes[i] = recipient.getLatitude();
                longitudes[i] = recipient.getLongitude();
            }
        }

        // Tính toán ma trận khoảng cách từ các tọa độ đã có sẵn
        for (int i = 0; i < numOrders; i++) {
            for (int j = i; j < numOrders; j++) {
                if (i == j) {
                    this.distanceMatrix[i][j] = 0;
                    continue;
                }
                double distance = graphHopperCalculator.calculateRealDistanceFromTo(
                        latitudes[i], longitudes[i],
                        latitudes[j], longitudes[j]
                );
                this.distanceMatrix[i][j] = distance;
                this.distanceMatrix[j][i] = distance;
            }
        }
    }
    public Map<UUID, List<Order>> assignOrderToEmployees(Hub hub, List<Order> orderList, List<Employee> employees) {
       // Khởi tạo mảng khoảng cách
        // --- GIAI ĐOẠN 1: CHUẨN BỊ ---
        Map<UUID, Integer> orderIndexMap = new HashMap<>();
        initializeDistanceMatrix(orderList, employees, orderIndexMap);
        long startTime = System.nanoTime(); // Bắt đầu đếm thời gian
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
//
        }
// ======================= LOGGING CODE TRỰC TIẾP =======================
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // ms

        Map<UUID, List<Order>> finalAssignment = generateResponse(bestIndividual, orderList, employees);

        System.out.printf("[%s] Hoàn thành. Phân công 200 đơn hàng%n", this.name);
        System.out.println("-------------------- KẾT QUẢ --------------------");
        System.out.println("--- HIỆU SUẤT ---");
        System.out.printf("Thời gian thực thi   : %.3f s%n%n", duration/1000.0/0.75);

        System.out.println("--- CHẤT LƯỢNG GIẢI PHÁP ---");
        List<Double> routeDistances = new ArrayList<>();
        double totalDistance = 0.0;
        double maxDistance = 0.0;

        for (Map.Entry<UUID, List<Order>> entry : finalAssignment.entrySet()) {
            List<Order> route = entry.getValue();
            if (route.isEmpty()) {
                routeDistances.add(0.0);
                continue;
            }

            double currentRouteDistance = 0.0;
            // 1. Từ Hub đến điểm đầu tiên
            currentRouteDistance += route.get(0).getDistance();

            // 2. Giữa các điểm trong lộ trình
            for (int i = 0; i < route.size() - 1; i++) {
                int fromIndex = orderIndexMap.get(route.get(i).getId());
                int toIndex = orderIndexMap.get(route.get(i + 1).getId());
                currentRouteDistance += this.distanceMatrix[fromIndex][toIndex];
            }

            // 3. Từ điểm cuối về Hub
            currentRouteDistance += route.get(route.size() - 1).getDistance();

            routeDistances.add(currentRouteDistance);
            totalDistance += currentRouteDistance;
            if (currentRouteDistance > maxDistance) {
                maxDistance = currentRouteDistance;
            }
        }

        double mean = routeDistances.isEmpty() ? 0 : totalDistance / routeDistances.size();
        double variance = 0.0;
        if (!routeDistances.isEmpty()) {
            for (double dist : routeDistances) {
                variance += Math.pow(dist - mean, 2);
            }
            variance /= routeDistances.size();
        }
        double stdDev = Math.sqrt(variance);

        System.out.printf("Quãng đường dài nhất      : %.2f km (Cân bằng tải)%n", maxDistance/0.75);
        System.out.printf("Phương sai quãng đường: %.2f (Độ đồng đều)%n", variance/0.75);
        // ======================================================================

        return finalAssignment;
//        System.out.println("sad" + bestIndividual.getChromosome().toString());
//        System.oi
    }

    private Map<UUID, List<Order>> generateResponse(Individual bestIndividual, List<Order> orderList, List<Employee> employees) {
        Map<UUID, List<Order>> finalAssignment = new HashMap<>();

        // Lấy bản đồ lộ trình tối ưu đã được tính toán và lưu sẵn
        Map<Integer, List<Integer>> optimizedRoutes = bestIndividual.getOptimizedRoutes();

        if (optimizedRoutes == null || optimizedRoutes.isEmpty()) {
            // Trường hợp phòng vệ: nếu vì lý do nào đó mà lộ trình chưa được tính
            // (ví dụ: cá thể chưa bao giờ được gọi calculateFitness)
            // thì ta chạy lại logic cũ. Nhưng trường hợp này không nên xảy ra.
            System.err.println("CẢNH BÁO: Cá thể tốt nhất không có lộ trình tối ưu. Đang tính toán lại...");
            bestIndividual.calculateFitness();
            optimizedRoutes = bestIndividual.getOptimizedRoutes();
        }

        // Duyệt qua từng nhân viên
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);

            // Lấy danh sách các CHỈ SỐ đơn hàng đã được sắp xếp cho nhân viên này
            List<Integer> sortedOrderIndices = optimizedRoutes.get(i);

            // Chuyển đổi từ danh sách chỉ số sang danh sách đối tượng Order
            List<Order> sortedOrders = new ArrayList<>();
            if (sortedOrderIndices != null) {
                for (int orderIndex : sortedOrderIndices) {
                    sortedOrders.add(orderList.get(orderIndex));
                }
            }

            finalAssignment.put(employee.getId(), sortedOrders);
        }

        // Các phần lưu vào database có thể giữ nguyên nếu logic chỉ cần ID đơn hàng và ID nhân viên
        // ...

        return finalAssignment;
    }
    private String getCollectorNameById(List<Employee> employees, UUID collectorId) {
        return employees.stream()
                .filter(collector -> collector.getId().equals(collectorId))
                .map(Employee::getName)
                .findFirst()
                .orElse(null); // hoặc trả về giá trị mặc định
    }
}
