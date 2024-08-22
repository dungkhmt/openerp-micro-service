package com.hust.wmsbackend.management.auto.algorithms;

import com.hust.wmsbackend.vrp.delivery.RouteRequest;
import com.hust.wmsbackend.vrp.delivery.RouteResponse;

public interface TSP {
    public RouteResponse solveTSP(RouteRequest request);
}
