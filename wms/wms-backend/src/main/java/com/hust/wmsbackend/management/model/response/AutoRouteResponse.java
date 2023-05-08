package com.hust.wmsbackend.management.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AutoRouteResponse {
    private String deliveryTripId;
    private List<Point> points;
    private List<Marker> customers;
    private Marker warehouse;

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Marker {
        private String name;
        private List<BigDecimal> position; // [lat, lon]
    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Point {
        private BigDecimal lat;
        private BigDecimal lon;
    }
}
