package wms.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wms.common.constant.DefaultConst;
import wms.dto.bill.SplitBillDTO;
import wms.entity.ResultEntity;
import wms.service.delivery_bill.IDeliveryBillService;

import javax.validation.Valid;

@RestController
@RequestMapping("/delivery-bill")
@Slf4j
public class DeliveryBillController extends BaseController {
    @Autowired
    private IDeliveryBillService deliveryBillService;
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllBills(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sortAsc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "orderCode", required = false, defaultValue = DefaultConst.STRING) String orderCode
    ) {
        try {
            return response(new ResultEntity(1, "Get bills successfully", deliveryBillService.getAllBills(page, pageSize, sortField, isSortAsc, orderCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-bill-items-of-order")
    public ResponseEntity<?> getBillItemsOfOrder(
            @RequestParam(value = "orderCode", required = false, defaultValue = DefaultConst.STRING) String orderCode
    ) {
        try {
            return response(new ResultEntity(1, "Get bill items successfully", deliveryBillService.getBillItemsOfOrder( orderCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

    @GetMapping("/get-bill-items-of-bill")
    public ResponseEntity<?> getBillItemsOfBill(
            @RequestParam(value = "bill_code", required = false, defaultValue = DefaultConst.STRING) String billCode
    ) {
        try {
            return response(new ResultEntity(1, "Get bill items successfully", deliveryBillService.getBillItemsOfBill( billCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-bill-items-of-bill-with-seq")
    public ResponseEntity<?> getBillItemsOfBill(
            @RequestParam(value = "bill_code", required = false, defaultValue = DefaultConst.STRING) String billCode,
            @RequestParam(value = "seq_id", required = false, defaultValue = DefaultConst.STRING) String seqId
    ) {
        try {
            return response(new ResultEntity(1, "Get bill items successfully", deliveryBillService.getBillItemsOfBillBySeq( billCode, seqId)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Phân đơn giao hàng thành các lô nhỏ hơn để chia cho các trip")
    @PostMapping("/split-bill")
    public ResponseEntity<?> splitBills(
            @Valid @RequestBody SplitBillDTO splitBillDTO
            ) {
        try {
            deliveryBillService.splitBills(splitBillDTO);
            return response(new ResultEntity(1, "Split bills successfully", splitBillDTO.getDeliveryBillCode()));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Lấy các lô giao hàng dự kiến")
    @GetMapping("/get-split-bill")
    public ResponseEntity<?> getSplitBillFromDeliveryBill(
            @RequestParam(value = "deliveryBillCode", required = false, defaultValue = DefaultConst.STRING) String deliveryBillCode
    ) {
        try {
            return response(new ResultEntity(1, "Get splitted bills successfully", deliveryBillService.getSplitBillByCode(deliveryBillCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
//    @GetMapping("/get-bill-can-deliver")
//    public ResponseEntity<?> getBillsCanDeliver(
//            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
//            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
//            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
//            @RequestParam(value = "sortAsc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
//    ) {
//        try {
//            return response(new ResultEntity(1, "Get all bills can be delivered successfully", deliveryBillService.getBillsCanDeliver(page, pageSize, sortField, isSortAsc)));
//        } catch (Exception ex) {
//            return response(error(ex));
//        }
//    }
}
