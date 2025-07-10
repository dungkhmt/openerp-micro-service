package com.hust.wmsbackend.vrp.delivery;

import com.graphhopper.ResponsePath;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RouteResponse {
    private double totalCost;
    private List<DeliveryAddressDTO> order;
    private List<ResponsePath> paths;
}
