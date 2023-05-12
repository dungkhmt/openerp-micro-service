package wms.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wms.common.constant.DefaultConst;
import wms.entity.ResultEntity;
import wms.service.receipt_bill.IReceiptBillService;

@RestController
@RequestMapping("/receipt-bill")
@Slf4j
public class ReceiptBillController extends BaseController {
    @Autowired
    private IReceiptBillService receiptBillService;
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllBills(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sortAsc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "orderCode", required = false, defaultValue = DefaultConst.STRING) String orderCode
    ) {
        try {
            return response(new ResultEntity(1, "Get bills successfully", receiptBillService.getAllBills(page, pageSize, sortField, isSortAsc, orderCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-bill-items-of-order")
    public ResponseEntity<?> getBillsOfOrder(
            @RequestParam(value = "orderCode", required = false, defaultValue = DefaultConst.STRING) String orderCode
    ) {
        try {
            return response(new ResultEntity(1, "Get bill items successfully", receiptBillService.getBillItemsOfOrder( orderCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-bill-items-of-order-paging")
    public ResponseEntity<?> getBillsOfOrderPaging(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sortAsc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "orderCode", required = false, defaultValue = DefaultConst.STRING) String orderCode
    ) {
        try {
            return response(new ResultEntity(1, "Get bill items successfully", receiptBillService.getBillItemsOfOrder(page, pageSize, sortField, isSortAsc, orderCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
