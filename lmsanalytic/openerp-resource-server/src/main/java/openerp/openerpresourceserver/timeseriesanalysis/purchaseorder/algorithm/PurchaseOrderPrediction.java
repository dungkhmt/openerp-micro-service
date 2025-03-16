package openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.algorithm;

import openerp.openerpresourceserver.timeseriesanalysis.MovingAverage;
import openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.model.ModelInputCreatePurchaseOrder;
import openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.model.ModelResponseCreatePurchaseOrder;
import openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.model.OrderItem;
import openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.model.SalesHistoryItem;
import openerp.openerpresourceserver.timeseriesanalysis.util.DateTimeUtil;

import java.util.*;

public class PurchaseOrderPrediction {
    public ModelResponseCreatePurchaseOrder genPurchaseOrder(ModelInputCreatePurchaseOrder I){
        Set<String> productIds = new HashSet<>();
        for(SalesHistoryItem shi: I.getSalesHistoryItemList()){
            for(OrderItem oi: shi.getOrderItems()){
                productIds.add(oi.getProductId());
            }
        }
        Map<String, Double[]> mProduct2QtySeq = new HashMap<>();
        Map<String, Integer> mDate2Int = new HashMap<>();
        int len = I.getSalesHistoryItemList().size();
        for(int i = 0; i < I.getSalesHistoryItemList().size(); i++){
            SalesHistoryItem shi = I.getSalesHistoryItemList().get(i);
            String date = shi.getDate();
            mDate2Int.put(date,i);
        }
        for(String pId: productIds){
            Double[] seq = new Double[len];
            for(int i = 0; i < len; i++) seq[i] = 0.0;
            mProduct2QtySeq.put(pId, seq);
        }
        for(SalesHistoryItem shi: I.getSalesHistoryItemList()){
            int idx = mDate2Int.get(shi.getDate());
            for(OrderItem oi: shi.getOrderItems()){
                String pId = oi.getProductId();
                int qty = oi.getQty();
                Double[] seq = mProduct2QtySeq.get(pId);
                seq[idx] = qty*1.0;
            }
        }
        Map<String, List<Double>> mProductId2ListSeq = new HashMap<>();
        for(String pid: productIds){
            Double[] s = mProduct2QtySeq.get(pid);
            List<Double> L = new ArrayList<>();
            for(int i = 0; i < s.length; i++) L.add(s[i]);
            mProductId2ListSeq.put(pid,L);
        }
        int wSz = Math.min(I.getSalesHistoryItemList().size(),10);
        List<SalesHistoryItem> predict = new ArrayList<>();
        String lastDate = I.getSalesHistoryItemList().get(0).getDate();
        for(int i = 1; i <= I.getLen(); i++) {
            lastDate = DateTimeUtil.nextDate(lastDate);
            List<OrderItem> orderItemList = new ArrayList<>();
            for (String pId : mProduct2QtySeq.keySet()) {
                List<Double> L = mProductId2ListSeq.get(pId);
                MovingAverage MA = new MovingAverage(wSz, L);
                double nextVal = MA.predictNextValue();
                int intNextVal = (int)nextVal;
                L.add(intNextVal*1.0);
                orderItemList.add(new OrderItem(pId,intNextVal));
            }
            predict.add(new SalesHistoryItem(lastDate,orderItemList));
        }
        return new ModelResponseCreatePurchaseOrder(predict);
    }
}
