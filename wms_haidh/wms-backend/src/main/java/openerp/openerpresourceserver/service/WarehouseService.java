package openerp.openerpresourceserver.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.entity.Warehouse;
import openerp.openerpresourceserver.repository.WarehouseRepository;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    // Lấy tất cả warehouse
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }
}

