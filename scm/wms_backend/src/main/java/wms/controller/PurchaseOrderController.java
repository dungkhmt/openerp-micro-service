package wms.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wms.common.constant.DefaultConst;
import wms.common.enums.OrderStatus;
import wms.dto.purchase_order.PurchaseOrderDTO;
import wms.dto.purchase_order.UpdatePurchaseOrderDTO;
import wms.entity.ResultEntity;
import wms.service.purchase_order.IPurchaseOrderService;

import javax.validation.Valid;

@RestController
@RequestMapping("/purchase-order")
@Slf4j
public class PurchaseOrderController extends BaseController {
    @Autowired
    private IPurchaseOrderService purchaseOrderService;
    @ApiOperation(value = "Thêm mới 1 order mua hàng")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        try {
            return response(new ResultEntity(1, "Create order successfully", purchaseOrderService.createOrder(purchaseOrderDTO)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all purchase order with pagination and sorting and some conditions")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
    ) {
        try {
            return response(new ResultEntity(1, "Get list orders successfully", purchaseOrderService.getAllOrders(page, pageSize, sortField, isSortAsc)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<?> getOrderByID(@PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Get order by id successfully", purchaseOrderService.getOrderById(id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-by-code")
    public ResponseEntity<?> getOrderByCode(
            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code) {
        try {
            return response(new ResultEntity(1, "Get order by code successfully", purchaseOrderService.getOrderByCode(code)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Should only update products and vat")
    // TODO: Reconsider this apis
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody UpdatePurchaseOrderDTO updatePurchaseOrderDTO, @PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Update order successfully", purchaseOrderService.updateOrder(updatePurchaseOrderDTO, id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

    @ApiOperation(value = "Approve created order. Only created order can be approved")
    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateOrderStatus(@RequestParam(value = "status", required = true) OrderStatus status, @PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Update order successfully", purchaseOrderService.updateOrderStatus(status, id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrderById(@PathVariable("id") long id) {
        try {
            purchaseOrderService.deleteOrderById(id);
            return response(new ResultEntity(1, "Delete order successfully", id));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
