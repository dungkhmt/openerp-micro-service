package com.hust.wmsbackend.management.auto.impl;

import com.graphhopper.ResponsePath;
import com.hust.wmsbackend.WmsBackendApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = WmsBackendApplication.class)
class GraphHopperCalculatorTest {

    @Autowired
    GraphHopperCalculator graphHopperCalculator;

    @Test
    public void calculateTest() {
        ResponsePath path = graphHopperCalculator.calculate(
                BigDecimal.valueOf(21.10794210000000),
                BigDecimal.valueOf(106.38573390000000),
                BigDecimal.valueOf(21.69345523485924),
                BigDecimal.valueOf(104.29547823476348));
    }

}