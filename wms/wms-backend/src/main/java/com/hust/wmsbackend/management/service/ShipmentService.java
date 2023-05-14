package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.model.ShipmentDTO;

import java.security.Principal;
import java.util.List;

public interface ShipmentService {

    String create(Principal principal, ShipmentDTO request);

    List<ShipmentDTO> getAllShipments(Principal principal);

    ShipmentDTO getShipmentById(String shipmentId);

    boolean deleteByIds(String[] shipmentIds);

}
