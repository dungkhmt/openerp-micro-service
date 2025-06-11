package openerp.openerpresourceserver.utils;

import openerp.openerpresourceserver.entity.Employee;
import openerp.openerpresourceserver.entity.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.text.DecimalFormat;

public class PerformanceLogger {

    private final String strategyName;
    private final int orderCount;
    private final long startTime;

    // Sử dụng DecimalFormat để làm tròn số cho đẹp
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public PerformanceLogger(String strategyName, int orderCount) {
        this.strategyName = strategyName;
        this.orderCount = orderCount;
        this.startTime = System.nanoTime();
        System.out.println("\n==========================================================");
        System.out.printf("[%s] Bắt đầu thực thi cho %d đơn hàng...\n", strategyName, orderCount);
    }

    public void logResults(Map<UUID, List<Order>> assignmentResult, double[][] distanceMatrix, Map<UUID, Integer> orderIndexMap) {
        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        // 1. Tính toán các thông số chất lượng
        List<Double> routeDistances = new ArrayList<>();
        double totalDistance = 0.0;

        for (Map.Entry<UUID, List<Order>> entry : assignmentResult.entrySet()) {
            List<Order> assignedOrders = entry.getValue();
            if (assignedOrders.isEmpty()) {
                routeDistances.add(0.0);
                continue;
            }

            double currentRouteDistance = 0.0;
            // Khoảng cách từ Hub đến đơn đầu tiên
            Order firstOrder = assignedOrders.get(0);
            currentRouteDistance += firstOrder.getDistance();

            // Khoảng cách giữa các đơn hàng
            for (int i = 0; i < assignedOrders.size() - 1; i++) {
                Order order1 = assignedOrders.get(i);
                Order order2 = assignedOrders.get(i + 1);
                int index1 = orderIndexMap.get(order1.getId());
                int index2 = orderIndexMap.get(order2.getId());
                currentRouteDistance += distanceMatrix[index1][index2];
            }

            // Giả sử sau khi hoàn thành, nhân viên không cần quay về Hub
            // Nếu cần quay về Hub, hãy bỏ comment dòng dưới
            // currentRouteDistance += assignedOrders.get(assignedOrders.size() - 1).getDistance();

            routeDistances.add(currentRouteDistance);
            totalDistance += currentRouteDistance;
        }

        // 2. Tính các chỉ số thống kê
        double maxRouteDistance = routeDistances.stream().max(Double::compare).orElse(0.0);
        double avgRouteDistance = routeDistances.stream().mapToDouble(d -> d).average().orElse(0.0);

        // Tính phương sai
        double variance = routeDistances.stream()
                .mapToDouble(d -> Math.pow(d - avgRouteDistance, 2))
                .average().orElse(0.0);
        double stdDeviation = Math.sqrt(variance);

        // 3. In kết quả
        System.out.printf("[%s] Hoàn thành. Phân công [%d] đơn hàng\n", strategyName, orderCount);
        System.out.println("-------------------- KẾT QUẢ --------------------");
        System.out.println("--- HIỆU SUẤT ---");
        System.out.printf("Thời gian thực thi   : %d ms\n", durationMs);
        System.out.println("\n--- CHẤT LƯỢNG GIẢI PHÁP ---");
        System.out.printf("Tổng quãng đường     : %s km\n", df.format(totalDistance));
        System.out.printf("Quãng đường dài nhất      : %s km (Cân bằng tải)\n", df.format(maxRouteDistance));
        System.out.printf("Phương sai quãng đường: %s (Độ đồng đều)\n", df.format(variance));
        System.out.printf("Độ lệch chuẩn        : %s\n", df.format(stdDeviation));
        System.out.println("==========================================================");
    }
}