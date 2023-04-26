package com.hust.baseweb.utils.algorithm;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class DistanceUtils {

    public static <T> DirectionSolution<T> calculateGreedyTotalDistance(
        List<T> clientPoint,
        T facilityPoint,
        BiFunction<T, T, Integer> distanceFunction
    ) {
        if (clientPoint.isEmpty()) {
            return new DirectionSolution<>(0.0, new ArrayList<>());
        }
        double totalDistance = 0;
        Set<Integer> candidates = new HashSet<>();  //  T in ts can be duplicated
        for (int i = 0; i < clientPoint.size(); i++) {
            candidates.add(i);
        }
        List<T> tour = new ArrayList<>();
        tour.add(facilityPoint);

        for (int i = 0; i < clientPoint.size(); i++) {
            // greedy get minimum neighborhood distance
            int minDistance = Integer.MAX_VALUE;
            int selectedId = -1;
            for (int candidateId : candidates) {
                Integer distance = distanceFunction.apply(tour.get(tour.size() - 1), clientPoint.get(candidateId));
                if (distance < minDistance) {
                    minDistance = distance;
                    selectedId = candidateId;
                }
            }
            totalDistance += minDistance;
            tour.add(clientPoint.get(selectedId));
            candidates.remove(selectedId);
        }

        totalDistance += distanceFunction.apply(tour.get(tour.size() - 1), facilityPoint);
        return new DirectionSolution<>(totalDistance, tour);
    }

    @AllArgsConstructor
    @Getter
    public static class DirectionSolution<T> {

        private Double distance;
        private List<T> tour;
    }
}
