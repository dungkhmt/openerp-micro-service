package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.CustomerAddress;
import com.hust.wmsbackend.management.service.CustomerAddressService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CustomerAddressServiceImpl implements CustomerAddressService {

    @Override
    public BigDecimal getDistanceToCluster(CustomerAddress from, List<CustomerAddress> cluster) {
        BigDecimal minDistance = BigDecimal.valueOf(2000000); // chieu dai Viet Nam
        for (CustomerAddress address : cluster) {
            // Euclidean distance
            BigDecimal lonGap = from.getLongitude().subtract(address.getLongitude()).abs();
            BigDecimal latGap = from.getLatitude().subtract(address.getLatitude()).abs();
            BigDecimal distance = lonGap.multiply(lonGap).add(latGap.multiply(latGap));
            minDistance = distance.min(minDistance);
        }
        return minDistance;
    }

}
