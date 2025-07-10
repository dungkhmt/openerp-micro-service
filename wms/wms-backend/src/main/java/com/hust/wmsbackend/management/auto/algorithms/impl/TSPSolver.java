package com.hust.wmsbackend.management.auto.algorithms.impl;


import com.graphhopper.ResponsePath;
import com.hust.wmsbackend.management.auto.algorithms.DistanceCalculator2;
import com.hust.wmsbackend.vrp.delivery.DeliveryAddressDTO;
import com.hust.wmsbackend.vrp.delivery.RouteRequest;
import com.hust.wmsbackend.vrp.delivery.RouteResponse;
import com.hust.wmsbackend.management.auto.algorithms.TSP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TSPSolver implements TSP {

    DistanceCalculator2 distanceCalculator;

    private final Double INFINITY_VALUE = Double.MAX_VALUE;
    public RouteResponse solveTSP(RouteRequest request) {
        log.info("Start TSP algo");
        int customerCount = request.getAddressDTOs().size();
        List<DeliveryAddressDTO> initOrder = request.getAddressDTOs();

        double totalCost = 0.0;
        List<ResponsePath> responsePath = new ArrayList<>();
        List<DeliveryAddressDTO> order = new ArrayList<>();


        List<Integer> checkVisitedAddress = new ArrayList<>();
        checkVisitedAddress.add(1); // checkVisitedAddress[0] => warehouse => 1
        for (int i = 1; i <= customerCount; i++) {
            checkVisitedAddress.add(0);
        }

        // chon dia diem 1
        List<ResponsePath> tmpResponsePath = new ArrayList<>();
        double shortestDistance = Double.MAX_VALUE;
        ResponsePath shortestPath = null;
        int currentIndex = -1;
        for (int i = 0; i < customerCount; i++) { // index = stt - 1
            ResponsePath currentPath = distanceCalculator.calculate(request.getWarehouseLat(), request.getWarehouseLon(),
                    initOrder.get(i).getLatitude(), initOrder.get(i).getLongitude());
            tmpResponsePath.add(currentPath);
            if (currentPath != null && currentPath.getDistance() < shortestDistance) {
                shortestPath = currentPath; // Cập nhật đường đi ngắn nhất
                shortestDistance = currentPath.getDistance(); // Cập nhật khoảng cách ngắn nhất
                currentIndex = i;
            }
        }
        responsePath.add(shortestPath);
        totalCost += shortestDistance;
        order.add(initOrder.get(currentIndex));
        checkVisitedAddress.set(currentIndex, 1);

        // chon cac dia diem tiep theo
        for (int i = 2; i <= customerCount; i++) {
            double minDistance = Double.MAX_VALUE;
            ResponsePath minPath = null;
            int minIndex = -1;

            for (int j = 0; j < customerCount; j++) {
                if (checkVisitedAddress.get(j) == 0) {
                    ResponsePath currentPath = distanceCalculator.calculate(
                            order.get(i - 2).getLatitude(), order.get(i - 2).getLongitude(),
                            initOrder.get(j).getLatitude(), initOrder.get(j).getLongitude());

                    if (currentPath != null && currentPath.getDistance() < minDistance) {
                        minPath = currentPath;
                        minDistance = currentPath.getDistance();
                        minIndex = j;
                    }
                }
            }

            if (minIndex == -1) {
                break;
            }

            responsePath.add(minPath);
            totalCost += minDistance;
            order.add(initOrder.get(minIndex));
            checkVisitedAddress.set(minIndex, 1);
        }

        RouteResponse response = new RouteResponse();
        response.setTotalCost(totalCost);
        response.setPaths(responsePath);
        response.setOrder(order);
        return response;
    }
}
