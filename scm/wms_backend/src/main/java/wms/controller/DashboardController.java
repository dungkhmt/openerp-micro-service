package wms.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wms.common.constant.DefaultConst;
import wms.entity.ResultEntity;
import wms.service.dashboard.IDashBoardService;

@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController extends BaseController{
    @Autowired
    private IDashBoardService dashBoardService;
    @ApiOperation(value = "Thong ke kho hang tao moi moi thang trong 1 nam duong lich") // tuong tu voi thong ke so khach hang moi
    @GetMapping(path = "/facility/new-facility")
    public ResponseEntity<?> getNewFacilityMonthly(
            @RequestParam(value = "year", required = true, defaultValue = DefaultConst.NUMBER) int year
    ) {
        try {
            return response(new ResultEntity(1, "Get facilities list successfully", dashBoardService.newFacilityMonthly(year)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Thong ke khach hang tao moi moi thang trong 1 nam duong lich")
    @GetMapping(path = "/customer/new-customer")
    public ResponseEntity<?> getNewCustomerMonthly(
            @RequestParam(value = "year", required = true, defaultValue = DefaultConst.NUMBER) int year
    ) {
        try {
            return response(new ResultEntity(1, "Get customers list successfully", dashBoardService.newCustomerMonthly(year)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Thong ke ti le san pham nhap") // tuong tu voi ti le san pham xuat
    @GetMapping(path = "/facility/import-product-category")
    public ResponseEntity<?> getImportProductCategory(
            @RequestParam(value = "month", required = true, defaultValue = DefaultConst.NUMBER) int month,
            @RequestParam(value = "year", required = true, defaultValue = DefaultConst.NUMBER) int year
    ) {
        try {
            return response(new ResultEntity(1, "Get import products list successfully", dashBoardService.getImportProductList(month, year)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Thong ke don hang theo quy (doanh so mua vao)") // tuong tu voi doanh so ban ra
    @GetMapping(path = "/sell-in/purchase-order-quarter")
    public ResponseEntity<?> getPurchaseOrderQuarterly(
            @RequestParam(value = "quarter", required = true, defaultValue = DefaultConst.NUMBER) int quarter,
            @RequestParam(value = "year", required = true, defaultValue = DefaultConst.NUMBER) int year
    ) {
        try {
            return response(new ResultEntity(1, "Get  order quarterly successfully", dashBoardService.getPurchaseOrderQuarterly(quarter, year)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Top 5 customer trong thang") // tuong tu voi top 5 kho nhap hang trong thang
    @GetMapping(path = "/sell-out/top-buying-customer")
    public ResponseEntity<?> getTopFiveBuyingCustomer(
            @RequestParam(value = "month", required = true, defaultValue = DefaultConst.NUMBER) int month,
            @RequestParam(value = "year", required = true, defaultValue = DefaultConst.NUMBER) int year
    ) {
        try {
            return response(new ResultEntity(1, "Get top 5 customers successfully", dashBoardService.getTopFiveOrderedCustomer(month, year)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "So chuyen giao hang o cac tinh thanh")
    @GetMapping(path = "/delivery/trip-customer-of-province")
    public ResponseEntity<?> getTripCustomerOfProvincePerMonth(
            @RequestParam(value = "month", required = true, defaultValue = DefaultConst.NUMBER) int month,
            @RequestParam(value = "year", required = true, defaultValue = DefaultConst.NUMBER) int year
    ) {
        try {
            return response(new ResultEntity(1, "Get all trip customer of province successfully", dashBoardService.getTripCustomerOfEveryProvince(month, year)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Ti trong danh muc san pham") // tuong tu voi ti le san pham xuat
    @GetMapping(path = "/category/product-category-rate")
    public ResponseEntity<?> getProductCategoryRate(
    ) {
        try {
            return response(new ResultEntity(1, "Get product category rate successfully", dashBoardService.getProductCategoryRate()));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Doanh so ban hang ca nam")
    @GetMapping(path = "/sell-out/sale-annual")
    public ResponseEntity<?> getSaleAnnual(
    ) {
        try {
            return response(new ResultEntity(1, "Get sale annual successfully", dashBoardService.getSaleAnnual()));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
