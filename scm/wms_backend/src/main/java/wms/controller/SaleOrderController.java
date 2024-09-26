package wms.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import wms.common.constant.DefaultConst;
import wms.dto.purchase_order.UpdatePurchaseOrderDTO;
import wms.dto.sale_order.SaleOrderDTO;
import wms.dto.sale_order.UpdateSaleOrderDTO;
import wms.dto.sale_order.UpdateSaleOrderStatusDTO;
import wms.entity.ResultEntity;
import wms.service.sale_order.ISaleOrderService;

import javax.validation.Valid;

@RestController
@RequestMapping("/sale-order")
@Slf4j
public class SaleOrderController extends BaseController {
    @Autowired
    private ISaleOrderService saleOrderService;
    @ApiOperation(value = "Thêm mới 1 order bán hàng")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody SaleOrderDTO saleOrderDTO, JwtAuthenticationToken token) {
        try {
            return response(new ResultEntity(1, "Create order successfully", saleOrderService.createOrder(saleOrderDTO, token)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all sale order with pagination and sorting and some conditions")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sortAsc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "orderStatus", required = false, defaultValue = DefaultConst.STRING) String orderStatus,
            @RequestParam(value = "createdBy", required = false, defaultValue = DefaultConst.STRING) String createdBy,
            @RequestParam(value = "customerName", required = false, defaultValue = DefaultConst.STRING) String customerName,
            @RequestParam(value = "textSearch", required = false, defaultValue = DefaultConst.STRING) String textSearch
    ) {
        try {
            return response(new ResultEntity(1, "Get list orders successfully", saleOrderService.getAllOrders(page, pageSize, sortField, isSortAsc, orderStatus,
                    createdBy, customerName, textSearch)));
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
            return response(new ResultEntity(1, "Get order items successfully", saleOrderService.getOrderItems(page, pageSize, sortField, isSortAsc, orderCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Approve created order. Only created order can be approved")
    @PutMapping("/update-status")
    public ResponseEntity<?> updateOrderStatus(@RequestBody UpdateSaleOrderStatusDTO status,
                                               @RequestParam(value = "orderCode", required = true) String orderCode) {
        try {
            return response(new ResultEntity(1, "Update order status successfully", saleOrderService.updateOrderStatus(status.getStatus(), orderCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/export-order-pdf")
    public ResponseEntity<?> exportOrderPdf(
            @RequestParam(value = "orderCode", required = true) String orderCode
    ) {
        try {
            return saleOrderService.exportOrderPdf(orderCode);
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Update sale order")
    @PostMapping("/update")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody UpdateSaleOrderDTO updateSaleOrderDTO
    ) {
        try {
            return response(new ResultEntity(1, "Update order successfully", saleOrderService.updateOrder(updateSaleOrderDTO)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteOrderById(@RequestParam(value = "id", required = true) Long id) {
        try {
            saleOrderService.deleteOrderById(id);
            return response(new ResultEntity(1, "Delete order successfully", id));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
