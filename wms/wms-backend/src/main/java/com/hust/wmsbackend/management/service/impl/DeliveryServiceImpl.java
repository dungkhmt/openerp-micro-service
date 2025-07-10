package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.service.DeliveryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    @Override
    public BigDecimal calDeliveryFee(BigDecimal longitude, BigDecimal latitude) {
        return BigDecimal.ZERO;
    }
}
