package com.hust.wmsbackend.vrp.delivery;

import com.hust.wmsbackend.WmsBackendApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = WmsBackendApplication.class)
class DeliveryRouteServiceImplTest {

    @Autowired
    DeliveryRouteService service;
    private final Double INFINITY_VALUE = Double.MAX_VALUE;

    @Test
    public void tspTest() {
        List<Integer> path = new ArrayList<>();
        List<List<Integer>> totalPaths = new ArrayList<>();
        List<Double[]> matrix = new ArrayList<>();
        Double[] row1 = {INFINITY_VALUE, 20.0, 30.0, 10.0, 11.0};
        Double[] row2 = {15.0, INFINITY_VALUE, 16.0, 4.0, 2.0};
        Double[] row3 = {3.0, 5.0, INFINITY_VALUE, 2.0, 4.0};
        Double[] row4 = {19.0, 6.0, 18.0, INFINITY_VALUE, 3.0};
        Double[] row5 = {16.0, 4.0, 7.0, 16.0, INFINITY_VALUE};

        matrix.add(row1);
        matrix.add(row2);
        matrix.add(row3);
        matrix.add(row4);
        matrix.add(row5);

        service.tsp(0, matrix.size(), path, matrix, ReducedMatrix.builder().matrix(matrix).build(), totalPaths);

        System.out.printf("All paths: %s%n", totalPaths);
    }

    @Test
    public void buildNormAddTest() {
        List<DeliveryAddressDTO> addressDTOs = new ArrayList<>();
        addressDTOs.add(DeliveryAddressDTO.builder().deliveryTripItemId("TRP_ITEM_00075")
                .longitude(new BigDecimal("105.85915783364125"))
                .latitude(new BigDecimal("20.99565866383600"))
                .build());
        addressDTOs.add(DeliveryAddressDTO.builder().deliveryTripItemId("TRP_ITEM_00076")
                .longitude(new BigDecimal("105.85915783364125"))
                .latitude(new BigDecimal("20.99565866383600"))
                .build());
        addressDTOs.add(DeliveryAddressDTO.builder().deliveryTripItemId("TRP_ITEM_00062")
                .longitude(new BigDecimal("105.81587820000000"))
                .latitude(new BigDecimal("21.15872680000000"))
                .build());
        addressDTOs.add(DeliveryAddressDTO.builder().deliveryTripItemId("TRP_ITEM_00073")
                .longitude(new BigDecimal("105.85915783364125"))
                .latitude(new BigDecimal("20.99565866383600"))
                .build());
        addressDTOs.add(DeliveryAddressDTO.builder().deliveryTripItemId("TRP_ITEM_00074")
                .longitude(new BigDecimal("105.85915783364125"))
                .latitude(new BigDecimal("20.99565866383600"))
                .build());
        RouteRequest request = RouteRequest.builder().addressDTOs(addressDTOs).build();
        service.buildNormAddressList(request);
    }
}