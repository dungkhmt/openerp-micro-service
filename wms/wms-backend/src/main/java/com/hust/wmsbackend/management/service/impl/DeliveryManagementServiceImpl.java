package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.DeliveryPerson;
import com.hust.wmsbackend.management.repository.DeliveryPersonRepository;
import com.hust.wmsbackend.management.service.DeliveryManagementService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DeliveryManagementServiceImpl implements DeliveryManagementService {

    private DeliveryPersonRepository deliveryPersonRepository;

    @Override
    public List<DeliveryPerson> getAllDeliveryPersons() {
        return deliveryPersonRepository.findAll();
    }

    @Override
    public DeliveryPerson create(DeliveryPerson deliveryPerson) {
        if (deliveryPerson.getFullName() == null || deliveryPerson.getUserLoginId() == null) {
            log.warn("Delivery person full name or user login id must not be null");
            return null;
        }
        deliveryPersonRepository.save(deliveryPerson);
        return deliveryPerson;
    }

    @Override
    @Transactional
    public boolean delete(String[] deliveryPersonIds) {
        try {
            for (String deliveryPersonId : deliveryPersonIds) {
                Optional<DeliveryPerson> deliveryPersonOpt = deliveryPersonRepository.findById(deliveryPersonId);
                if (!deliveryPersonOpt.isPresent()) {
                    log.warn(String.format("Delivery person with id %s is not exist", deliveryPersonId));
                    return false;
                }
                deliveryPersonRepository.delete(deliveryPersonOpt.get());
            }
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, String> getDeliveryPersonNameMap() {
        Map<String, String> nameMap = new HashMap<>();
        List<DeliveryPerson> persons = getAllDeliveryPersons();
        for (DeliveryPerson person : persons) {
            nameMap.put(person.getUserLoginId(), person.getFullName());
        }
        return nameMap;
    }
}
