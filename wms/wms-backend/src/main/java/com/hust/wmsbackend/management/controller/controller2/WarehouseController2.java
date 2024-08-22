package com.hust.wmsbackend.management.controller.controller2;

import com.hust.wmsbackend.management.entity.Warehouse;
import com.hust.wmsbackend.management.model.WarehouseWithBays;
import com.hust.wmsbackend.management.model.response.ProductWarehouseResponse;
import com.hust.wmsbackend.management.model.response.WarehouseDetailsResponse;
import com.hust.wmsbackend.management.model.response.WarehouseGeneral;
import com.hust.wmsbackend.management.model.model2.response.WarehouseDetailWithProduct2;
import com.hust.wmsbackend.management.service.service2.WarehouseService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/wmsv3/admin/warehouse")
@AllArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class WarehouseController2 {

    private WarehouseService warehouseService;

    @PutMapping()
    public ResponseEntity<Warehouse> createWarehouse(@Valid @RequestBody WarehouseWithBays request) {
        return ResponseEntity.ok(warehouseService.createWarehouse(request));
    }

    @GetMapping()
    public ResponseEntity<List<WarehouseGeneral>> getAllWarehouseGeneral() {
        return ResponseEntity.ok(warehouseService.getAllWarehouseGeneral());
    }

    @GetMapping(path = "/detail")
    public ResponseEntity<List<WarehouseWithBays>> getAllWarehouseDetail() {
        return ResponseEntity.ok(warehouseService.getAllWarehouseDetail());
    }

    @GetMapping(path = "/detail-with-products/{orderId}")
    public ResponseEntity<List<WarehouseDetailsResponse>> getAllWarehouseDetailWithProducts(@PathVariable String orderId) {
        return ResponseEntity.ok(warehouseService.getAllWarehouseDetailWithProducts(orderId));
    }


    @DeleteMapping()
    public ResponseEntity<List<String>> delete(@RequestBody List<String> warehouseIds) {
        return warehouseService.delete(warehouseIds) ?
            ResponseEntity.ok(warehouseIds) :
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(warehouseIds);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseWithBays> getByWarehouseId(@PathVariable String id) {
        return ResponseEntity.ok(warehouseService.getById(id));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductWarehouseResponse> getProductInWarehouse(@PathVariable String id) {
        return ResponseEntity.ok(warehouseService.getProductInWarehouse(id));
    }

    @PutMapping("/detail-with-products-2")
    public ResponseEntity<List<WarehouseDetailWithProduct2>> getAllWarehouseDetailHavingProducts(@RequestBody List<String> productIds) {
        return ResponseEntity.ok(warehouseService.getAllWarehouseDetailHavingProducts(productIds));
    }

}
