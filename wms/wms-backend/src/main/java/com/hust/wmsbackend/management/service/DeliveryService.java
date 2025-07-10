package com.hust.wmsbackend.management.service;

import java.math.BigDecimal;

public interface DeliveryService {

    BigDecimal calDeliveryFee(BigDecimal longitude, BigDecimal latitude);

}
