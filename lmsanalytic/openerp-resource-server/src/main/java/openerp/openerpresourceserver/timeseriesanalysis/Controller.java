package openerp.openerpresourceserver.timeseriesanalysis;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.algorithm.PurchaseOrderPrediction;
import openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.model.ModelInputCreatePurchaseOrder;
import openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.model.ModelResponseCreatePurchaseOrder;
import openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.model.OrderItem;
import org.apache.poi.ss.formula.functions.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class Controller {
    @PostMapping("/time-series-predict")
    public ResponseEntity<?> timeSeriesPredict(Principal principal, @RequestBody ModelInputTimeSeriesPrediction I){
        log.info("timeSeriesPredict, user = " + principal.getName());
        MovingAverage MA = new MovingAverage(I.getWindowsSize(),I.getSequences());
        double val = MA.predictNextValue();
        log.info("timeSeriesPredict, res = " + val);
        ModelResponseTimeSeriesPrediction res = new ModelResponseTimeSeriesPrediction(val);
        return ResponseEntity.ok().body(res);
    }
    @PostMapping("/gen-purchase-order")
    public ResponseEntity<?> genPurchaseOrder(Principal principal, @RequestBody ModelInputCreatePurchaseOrder model){
        //List<OrderItem> orderItemList = new ArrayList<>();
        //ModelResponseCreatePurchaseOrder res = new ModelResponseCreatePurchaseOrder(orderItemList);
        PurchaseOrderPrediction algo = new PurchaseOrderPrediction();
        ModelResponseCreatePurchaseOrder res = algo.genPurchaseOrder(model);
        return ResponseEntity.ok().body(res);
    }
}
