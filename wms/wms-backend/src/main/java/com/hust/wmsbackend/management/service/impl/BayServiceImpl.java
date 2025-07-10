package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.Bay;
import com.hust.wmsbackend.management.repository.BayRepository;
import com.hust.wmsbackend.management.service.BayService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class BayServiceImpl implements BayService {

    private BayRepository bayRepository;

    @Override
    public Map<UUID, String> getBayCodeMap() {
        List<Bay> bays = bayRepository.findAll();
        Map<UUID, String> map = new HashMap<>();
        for (Bay bay : bays) {
            map.put(bay.getBayId(), bay.getCode());
        }
        return map;
    }
}
