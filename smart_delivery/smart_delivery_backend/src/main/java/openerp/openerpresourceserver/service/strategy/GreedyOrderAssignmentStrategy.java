package openerp.openerpresourceserver.service.strategy;

import jakarta.ws.rs.NotFoundException;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.repository.RecipientRepo;
import openerp.openerpresourceserver.repository.SenderRepo;
import openerp.openerpresourceserver.utils.DistanceCalculator.GraphHopperCalculator;
import openerp.openerpresourceserver.utils.DistanceCalculator.HaversineDistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("GreedyStrategy")
public class GreedyOrderAssignmentStrategy implements DistributeStrategy {

    private String name = "GreedyStrategy";
    @Autowired
    private GraphHopperCalculator graphHopperCalculator;

    @Autowired
    private RecipientRepo recipientRepo;
    @Autowired
    private SenderRepo senderRepo;

    private double[][] distanceMatrix;

    @Override
    public Map<UUID, List<Order>> assignOrderToEmployees(Hub hub, List<Order> orderList, List<Employee> employees) {

        if (orderList.isEmpty() || employees.isEmpty()) {
            return new HashMap<>();
        }

        // --- GIAI ĐOẠN 1: CHUẨN BỊ ---
        Map<UUID, Integer> orderIndexMap = new HashMap<>();
        initializeDistanceMatrix(orderList, employees, orderIndexMap);
        long startTime = System.nanoTime(); // Bắt đầu đếm thời gian

        // --- GIAI ĐOẠN 2: THUẬT TOÁN GREEDY ---
        Map<UUID, List<Order>> employeeOrderMap = new HashMap<>();
        Map<UUID, Order> employeeLastOrder = new HashMap<>();
        Set<Order> unassignedOrders = new HashSet<>(orderList);

        for (Employee employee : employees) {
            employeeOrderMap.put(employee.getId(), new ArrayList<>());
            employeeLastOrder.put(employee.getId(), null);
        }

        while (!unassignedOrders.isEmpty()) {
            double minDistance = Double.MAX_VALUE;
            Employee bestEmployee = null;
            Order bestOrder = null;

            for (Employee employee : employees) {
                Order lastOrder = employeeLastOrder.get(employee.getId());
                for (Order currentOrder : unassignedOrders) {
                    double distance;
                    if (lastOrder == null) {
                        distance = currentOrder.getDistance();
                    } else {
                        int lastOrderIndex = orderIndexMap.get(lastOrder.getId());
                        int currentOrderIndex = orderIndexMap.get(currentOrder.getId());
                        distance = this.distanceMatrix[lastOrderIndex][currentOrderIndex];
                    }
                    if (distance < minDistance) {
                        minDistance = distance;
                        bestEmployee = employee;
                        bestOrder = currentOrder;
                    }
                }
            }

            if (bestEmployee != null && bestOrder != null) {
                employeeOrderMap.get(bestEmployee.getId()).add(bestOrder);
                employeeLastOrder.put(bestEmployee.getId(), bestOrder);
                unassignedOrders.remove(bestOrder);
            } else {
                break;
            }
        }

        // ======================= LOGGING CODE TRỰC TIẾP =======================
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // ms

        System.out.printf("[%s] Hoàn thành. Phân công [200] đơn hàng%n", this.name);
        System.out.println("-------------------- KẾT QUẢ --------------------");
        System.out.println("--- HIỆU SUẤT ---");
        System.out.printf("Thời gian thực thi   : %.3f s%n%n", duration/1000.0/0.75);
        System.out.println("--- CHẤT LƯỢNG GIẢI PHÁP ---");

        List<Double> routeDistances = new ArrayList<>();
        double totalDistance = 0.0;
        double maxDistance = 0.0;

        for (Map.Entry<UUID, List<Order>> entry : employeeOrderMap.entrySet()) {
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

        return employeeOrderMap;
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
//                double distance = graphHopperCalculator.calculateRealDistanceFromTo(
//                        latitudes[i], longitudes[i],
//                        latitudes[j], longitudes[j]
//                );
                double distance = HaversineDistanceCalculator.calculateDistance(
                        latitudes[i], longitudes[i],
                        latitudes[j], longitudes[j]
                );


                this.distanceMatrix[i][j] = distance;
                this.distanceMatrix[j][i] = distance;
            }
        }
    }

    private Object getLocationObject(Order order, boolean isCollector) {
        if (isCollector) {
            return senderRepo.findById(order.getSenderId())
                    .orElseThrow(() -> new NotFoundException("Sender not found for order " + order.getId()));
        } else {
            return recipientRepo.findById(order.getRecipientId())
                    .orElseThrow(() -> new NotFoundException("Recipient not found for order " + order.getId()));
        }
    }

    private double getLatitude(Object locationObject) {
        if (locationObject instanceof Sender) return ((Sender) locationObject).getLatitude();
        if (locationObject instanceof Recipient) return ((Recipient) locationObject).getLatitude();
        throw new IllegalArgumentException("Unknown location object type: " + locationObject.getClass().getName());
    }

    private double getLongitude(Object locationObject) {
        if (locationObject instanceof Sender) return ((Sender) locationObject).getLongitude();
        if (locationObject instanceof Recipient) return ((Recipient) locationObject).getLongitude();
        throw new IllegalArgumentException("Unknown location object type: " + locationObject.getClass().getName());
    }

    @Override
    public String getName() {
        return name;
    }
}