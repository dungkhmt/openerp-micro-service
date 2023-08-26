package wms.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import wms.common.constant.DefaultConst;
import wms.dto.purchase_order.PurchaseOrderDTO;
import wms.dto.purchase_order.UpdatePurchaseOrderStatusDTO;
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
    public ResponseEntity<?> create(@Valid @RequestBody PurchaseOrderDTO purchaseOrderDTO,  JwtAuthenticationToken token) {
        try {
            return response(new ResultEntity(1, "Create order successfully", purchaseOrderService.createOrder(purchaseOrderDTO, token)));
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
            @RequestParam(value = "sortAsc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "orderStatus", required = false, defaultValue = DefaultConst.STRING) String orderStatus,
            @RequestParam(value = "facilityName", required = false, defaultValue = DefaultConst.STRING) String facilityName,
            @RequestParam(value = "createdBy", required = false, defaultValue = DefaultConst.STRING) String createdBy,
            @RequestParam(value = "supplierCode", required = false, defaultValue = DefaultConst.STRING) String supplierCode,
            @RequestParam(value = "textSearch", required = false, defaultValue = DefaultConst.STRING) String textSearch
    ) {
        try {
            return response(new ResultEntity(1, "Get list orders successfully", purchaseOrderService.getAllOrders(page, pageSize, sortField, isSortAsc, orderStatus
            , facilityName, createdBy, supplierCode, textSearch
            )));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-order-items")
    public ResponseEntity<?> getOrderItemsFromOrder(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sortAsc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "orderCode", required = false, defaultValue = DefaultConst.STRING) String orderCode
    ) {
        try {
            return response(new ResultEntity(1, "Get order items successfully", purchaseOrderService.getOrderItems(page, pageSize, sortField, isSortAsc, orderCode)));
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
    @PostMapping("/update")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody UpdatePurchaseOrderDTO updatePurchaseOrderDTO
                                         ) {
        try {
            return response(new ResultEntity(1, "Update order successfully", purchaseOrderService.updateOrder(updatePurchaseOrderDTO)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

    @ApiOperation(value = "Approve created order. Only created order can be approved")
    @PutMapping("/update-status")
    public ResponseEntity<?> updateOrderStatus(@RequestBody UpdatePurchaseOrderStatusDTO status,
                                               @RequestParam(value = "orderCode", required = true) String orderCode) {
        try {
            return response(new ResultEntity(1, "Update order status successfully", purchaseOrderService.updateOrderStatus(status.getStatus(), orderCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteOrderById(@RequestParam(value = "id", required = true) Long id) {
        try {
            purchaseOrderService.deleteOrderById(id);
            return response(new ResultEntity(1, "Delete order successfully", id));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

    @GetMapping("/export-order-pdf")
    public ResponseEntity<?> exportOrderPdf(
            @RequestParam(value = "orderCode", required = true) String orderCode
    ) {
        try {
            return purchaseOrderService.exportOrderPdf(orderCode);
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
