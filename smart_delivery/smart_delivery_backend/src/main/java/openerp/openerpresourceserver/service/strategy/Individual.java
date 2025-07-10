package openerp.openerpresourceserver.service.strategy;

import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.Sender;
import openerp.openerpresourceserver.utils.DistanceCalculator.GraphHopperCalculator;

import java.util.*;

@Getter
@Setter
public class Individual {
    private final GraphHopperCalculator graphHopperCalculator;


    private ArrayList<Integer> chromosome;
    private double fitness;
    private int numOfOrder;
    private int numOfEmployee;
    private List<Order> orderList;
    private double[][] distanceMatrix; // Mảng 2 chiều lưu trữ khoảng cách
    private Map<Integer, List<Integer>> optimizedRoutes;
    public Individual(int numOfOrder, int numOfEmployee, List<Order> orderList, GraphHopperCalculator graphHopperCalculator, double[][] distanceMatrix) {
        this.fitness = -1;
        this.numOfOrder = numOfOrder;
        this.numOfEmployee = numOfEmployee;
        this.orderList = orderList;
        this.chromosome = new ArrayList<>();
        this.graphHopperCalculator = graphHopperCalculator;
        this.distanceMatrix = distanceMatrix; // Nhận mảng khoảng cách
        this.optimizedRoutes = new HashMap<>(); // Khởi tạo map
    }
    public Individual(Individual other) {
        // 1. Sao chép nông (Shallow Copy) cho các đối tượng không đổi và được chia sẻ
        this.graphHopperCalculator = other.graphHopperCalculator;
        this.orderList = other.orderList;
        this.distanceMatrix = other.distanceMatrix;

        // 2. Sao chép các giá trị nguyên thủy
        this.fitness = other.getFitness();
        this.numOfOrder = other.getNumOfOrder();
        this.numOfEmployee = other.getNumOfEmployee();

        // 3. Sao chép sâu (Deep Copy) cho các thuộc tính cần độc lập
        // Sao chép chromosome để đảm bảo đột biến không ảnh hưởng đến bản gốc
        this.chromosome = new ArrayList<>(other.getChromosome());

        // Sao chép optimizedRoutes để đảm bảo độc lập
        if (other.getOptimizedRoutes() != null) {
            this.optimizedRoutes = new HashMap<>();
            for (Map.Entry<Integer, List<Integer>> entry : other.getOptimizedRoutes().entrySet()) {
                // Key là Integer (bất biến), Value là List (cần tạo mới)
                this.optimizedRoutes.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        } else {
            this.optimizedRoutes = new HashMap<>(); // Hoặc null tùy logic
        }
    }
    public void randomInit() {
        Random rand = new Random();
        for (int i = 0; i < numOfOrder; i++) {
            chromosome.add(rand.nextInt(numOfEmployee));
        }
    }

    public void calculateFitness() {
        this.optimizedRoutes.clear();
        Map<Integer, List<Integer>> employeeOrderIndicesMap = new HashMap<>();
        for (int i = 0; i < numOfEmployee; i++) {
            employeeOrderIndicesMap.put(i, new ArrayList<>());
        }
        for (int j = 0; j < numOfOrder; j++) {
            employeeOrderIndicesMap.get(this.chromosome.get(j)).add(j);
        }

        double maxRouteDistance = 0.0;

        for (Map.Entry<Integer, List<Integer>> entry : employeeOrderIndicesMap.entrySet()) {
            int employeeIndex = entry.getKey();
            List<Integer> assignedOrderIndices = entry.getValue();

            RouteCalculationResult result = calculateOptimalRouteAndSequence(assignedOrderIndices);
            this.optimizedRoutes.put(employeeIndex, result.getOptimalSequence());

            if (result.getDistance() > maxRouteDistance) {
                maxRouteDistance = result.getDistance();
            }
        }

        if (maxRouteDistance > 0) {
            this.fitness = 1.0 / maxRouteDistance;
        } else {
            this.fitness = -1;
        }
    }
    @Getter
    private static class RouteCalculationResult {
        private final double distance;
        private final List<Integer> optimalSequence;

        public RouteCalculationResult(double distance, List<Integer> optimalSequence) {
            this.distance = distance;
            this.optimalSequence = optimalSequence;
        }
    }

    /**
     * Hàm helper mới, trả về cả quãng đường và thứ tự lộ trình.
     * Đây là phiên bản nâng cấp của hàm calculateOptimalRouteForEmployee cũ.
     */
    private RouteCalculationResult calculateOptimalRouteAndSequence(List<Integer> orderIndices) {
        if (orderIndices.isEmpty()) {
            return new RouteCalculationResult(0.0, new ArrayList<>());
        }

        List<Integer> optimalRoute = new ArrayList<>();
        Set<Integer> unvisitedOrderIndices = new HashSet<>(orderIndices);
        double totalDistance = 0.0;

        if (orderIndices.size() == 1) {
            int singleOrderIndex = orderIndices.get(0);
            totalDistance = orderList.get(singleOrderIndex).getDistance() * 2;
            optimalRoute.add(singleOrderIndex);
            return new RouteCalculationResult(totalDistance, optimalRoute);
        }

        // 1. Tìm điểm bắt đầu
        int startNodeIndex = -1;
        double minStartDistance = Double.MAX_VALUE;
        for (int index : unvisitedOrderIndices) {
            double distFromHub = orderList.get(index).getDistance();
            if (distFromHub < minStartDistance) {
                minStartDistance = distFromHub;
                startNodeIndex = index;
            }
        }

        totalDistance += minStartDistance;
        optimalRoute.add(startNodeIndex);
        unvisitedOrderIndices.remove(startNodeIndex);
        int currentNodeIndex = startNodeIndex;

        // 2. Lặp để tìm các điểm tiếp theo
        while (!unvisitedOrderIndices.isEmpty()) {
            int nearestNextNodeIndex = -1;
            double minNextDistance = Double.MAX_VALUE;
            for (int nextIndex : unvisitedOrderIndices) {
                double distance = distanceMatrix[currentNodeIndex][nextIndex];
                if (distance < minNextDistance) {
                    minNextDistance = distance;
                    nearestNextNodeIndex = nextIndex;
                }
            }
            totalDistance += minNextDistance;
            optimalRoute.add(nearestNextNodeIndex);
            currentNodeIndex = nearestNextNodeIndex;
            unvisitedOrderIndices.remove(nearestNextNodeIndex);
        }

        // 3. Quay về Hub
        totalDistance += orderList.get(currentNodeIndex).getDistance();

        return new RouteCalculationResult(totalDistance, optimalRoute);
    }
    /**
     * Tính toán lộ trình tối ưu (xấp xỉ) cho một nhân viên dựa trên các đơn hàng được giao.
     * Sử dụng thuật toán Nearest Neighbor Heuristic.
     * @param orderIndices Danh sách chỉ số của các đơn hàng được giao cho nhân viên.
     * @return Tổng quãng đường của lộ trình tốt nhất tìm được.
     */
    private double calculateOptimalRouteForEmployee(List<Integer> orderIndices) {
        if (orderIndices.isEmpty()) {
            return 0.0;
        }
        if (orderIndices.size() == 1) {
            // Nếu chỉ có 1 đơn hàng, lộ trình là Hub -> Order -> Hub
            return orderList.get(orderIndices.get(0)).getDistance() * 2;
        }

        Set<Integer> unvisitedOrderIndices = new HashSet<>(orderIndices);
        double totalDistance = 0.0;
        int currentNodeIndex;

        // 1. Tìm điểm bắt đầu: Đơn hàng gần Hub nhất
        int startNodeIndex = -1;
        double minStartDistance = Double.MAX_VALUE;
        for (int index : unvisitedOrderIndices) {
            double distFromHub = orderList.get(index).getDistance();
            if (distFromHub < minStartDistance) {
                minStartDistance = distFromHub;
                startNodeIndex = index;
            }
        }

        totalDistance += minStartDistance; // Cộng khoảng cách từ Hub -> điểm bắt đầu
        currentNodeIndex = startNodeIndex;
        unvisitedOrderIndices.remove(currentNodeIndex);

        // 2. Lặp để đi đến các điểm gần nhất tiếp theo
        while (!unvisitedOrderIndices.isEmpty()) {
            int nearestNextNodeIndex = -1;
            double minNextDistance = Double.MAX_VALUE;

            for (int nextIndex : unvisitedOrderIndices) {
                double distance = distanceMatrix[currentNodeIndex][nextIndex];
                if (distance < minNextDistance) {
                    minNextDistance = distance;
                    nearestNextNodeIndex = nextIndex;
                }
            }

            totalDistance += minNextDistance;
            currentNodeIndex = nearestNextNodeIndex;
            unvisitedOrderIndices.remove(currentNodeIndex);
        }

        // 3. Cộng khoảng cách từ điểm cuối cùng quay về Hub
        totalDistance += orderList.get(currentNodeIndex).getDistance();

        return totalDistance;
    }

    public Individual crossoverWith(Individual other) {
        Individual child = new Individual(this.numOfOrder, this.numOfEmployee, this.orderList, this.graphHopperCalculator, this.distanceMatrix);
        Random random = new Random();
        ArrayList<Integer> childChromosome = new ArrayList<>();

        for (int i = 0; i < this.chromosome.size(); i++) {
            if (random.nextBoolean()) {
                childChromosome.add(this.chromosome.get(i));
            } else {
                childChromosome.add(other.chromosome.get(i));
            }
        }

        child.chromosome = childChromosome;
        child.calculateFitness();
        return child;
    }

    public void mutate() {
        Random random = new Random();
        // Tỷ lệ 50/50 để chọn một trong hai loại đột biến
            // Loại 1: Swap Mutation (cũ) - Tốt cho việc tinh chỉnh cục bộ
            int index1 = random.nextInt(chromosome.size());
            int index2 = random.nextInt(chromosome.size());
            Collections.swap(chromosome, index1, index2);

    }

    public double getFitness() {
        return fitness;
    }

    public ArrayList<Integer> getChromosome() {
        return chromosome;
    }

}