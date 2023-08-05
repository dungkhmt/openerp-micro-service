package com.hust.wmsbackend.vrp.delivery;

import java.util.List;

public interface DeliveryRouteService {

    RouteResponse getRoute(RouteRequest r);

    void tsp(int level, int totalNode, List<Integer> path, List<Double[]> costMatrix, ReducedMatrix reducedMatrix, List<List<Integer>> totalPaths);

    void buildNormAddressList(RouteRequest r);
}
