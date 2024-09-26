package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.entity.CustomerAddress;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerAddressService {

    BigDecimal getDistanceToCluster(CustomerAddress from, List<CustomerAddress> cluster);

}
