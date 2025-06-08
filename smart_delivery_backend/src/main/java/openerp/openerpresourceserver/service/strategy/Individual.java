package openerp.openerpresourceserver.service.strategy;

import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.Sender;
import openerp.openerpresourceserver.utils.DistanceCalculator.GraphHopperCalculator;

import java.util.*;

public class Individual {
    private final GraphHopperCalculator graphHopperCalculator;


    private ArrayList<Integer> chromosome;
    private double fitness;
    private int numOfOrder;
    private int numOfCollector;
    private List<Order> orderList;
    private Map<UUID, Sender> senderMap;
    private double[][] distanceMatrix; // Mảng 2 chiều lưu trữ khoảng cách

    public Individual(Map<UUID, Sender> senderMap, int numOfOrder, int numOfCollector, List<Order> orderList, GraphHopperCalculator graphHopperCalculator, double[][] distanceMatrix) {
        this.fitness = -1;
        this.numOfOrder = numOfOrder;
        this.numOfCollector = numOfCollector;
        this.orderList = orderList;
        this.chromosome = new ArrayList<>();
        this.senderMap = senderMap;
        this.graphHopperCalculator = graphHopperCalculator;
        this.distanceMatrix = distanceMatrix; // Nhận mảng khoảng cách
    }

    public void randomInit() {
        Random rand = new Random();
        for (int i = 0; i < numOfOrder; i++) {
            chromosome.add(rand.nextInt(numOfCollector));
        }
    }

    public void calculateFitness() {
        Double maxSumDistance = -1.0;
        List<List<Integer>> ordersOfEachCollector = new ArrayList<>();

//        // Tạo mảng đếm số đơn của mỗi collector
//        int[] orderCountPerCollector = new int[numOfCollector];
//
//        // Đếm số đơn của từng collector
//        for (int gene : chromosome) {
//            orderCountPerCollector[gene]++;
//        }
//
//        // Kiểm tra nếu có collector nào xử lý quá 50 đơn
//        boolean hasOverloadedCollector = false;
//        for (int count : orderCountPerCollector) {
//            if (count > 50) {
//                hasOverloadedCollector = true;
//                break;
//            }
//        }
//
//        // Nếu có collector bị quá tải, gán fitness rất thấp
//        if (hasOverloadedCollector) {
//            this.fitness = 0.0000001; // Giá trị rất nhỏ
//            return;
//        }

        for (int i = 0; i < numOfCollector; i++) {
            List<Integer> ordersOfCollector = new ArrayList<>();
            for (int j = 0; j < numOfOrder; j++) {
                if (this.chromosome.get(j) == i) {
                    ordersOfCollector.add(j);
                }
            }
            ordersOfEachCollector.add(ordersOfCollector);
        }

        for (List<Integer> orderNumbers : ordersOfEachCollector) {
            if (orderNumbers.isEmpty()) continue;

            Integer currentOrderNumber = orderNumbers.getFirst();
            Double totalDistance = 0.0 + orderList.get(currentOrderNumber).getDistance();
//            System.out.println("from hub " + totalDistance);
            for (int i = 1; i < orderNumbers.size(); i++) {
                Integer nextOrderNumber = orderNumbers.get(i);

//                System.out.println("distance  current " + currentOrderNumber + " next " +  nextOrderNumber + " "+  distance );
                totalDistance += distanceMatrix[currentOrderNumber][nextOrderNumber];
                currentOrderNumber = nextOrderNumber;
                if(i== orderNumbers.size()-1){
                    totalDistance +=  orderList.get(nextOrderNumber).getDistance();
                }
            }
            if (totalDistance > maxSumDistance) {
                maxSumDistance = totalDistance;
            }
        }

        this.fitness =  1 / maxSumDistance;
//        System.out.println("maxsumdistance " + maxSumDistance);
//        System.out.println(ordersOfEachCollector.toString());

        if(maxSumDistance == 0.0){
        System.out.println(this.chromosome.toString());
        }
//        System.out.println("fitness " + this.fitness);
    }

    public Individual crossoverWith(Individual other) {
        Individual child = new Individual(this.senderMap,this.numOfOrder, this.numOfCollector, this.orderList, this.graphHopperCalculator, this.distanceMatrix);
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