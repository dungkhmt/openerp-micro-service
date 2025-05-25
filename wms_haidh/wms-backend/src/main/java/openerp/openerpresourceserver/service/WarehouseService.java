package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import openerp.openerpresourceserver.entity.Warehouse;
import openerp.openerpresourceserver.repository.WarehouseRepository;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }
    
    public Warehouse getWarehouseById(UUID warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with ID: " + warehouseId));
    }
    
    public Page<Warehouse> getWarehouses(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return warehouseRepository.findAll(pageable);
        }
        return warehouseRepository.searchByName(search, pageable);
    }
    
    public List<Warehouse> getWarehousesWithPickedOrders() {
        return warehouseRepository.findWarehousesWithPickedOrders();
    }

}

