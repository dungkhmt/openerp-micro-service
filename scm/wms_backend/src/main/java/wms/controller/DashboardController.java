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
    @ApiOperation(value = "Thong ke kho hang tao moi moi thang")
    @GetMapping(path = "/facility/new-facility")
    public ResponseEntity<?> getNewFacilityMonthly(
//            @RequestParam(value = "month", required = true, defaultValue = DefaultConst.NUMBER) int month,
            @RequestParam(value = "year", required = true, defaultValue = DefaultConst.NUMBER) int year
    ) {
        try {
            return response(new ResultEntity(1, "Get facilities list successfully", dashBoardService.newFacilityMonthly(year)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Thong ke ti le san pham nhap ")
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
    @ApiOperation(value = "Thong ke don hang theo quy (doanh so mua vao)")
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
}
