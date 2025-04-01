package openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.algorithm;

import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;
import openerp.openerpresourceserver.timeseriesanalysis.MovingAverage;
import openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.model.*;
import openerp.openerpresourceserver.timeseriesanalysis.util.DateTimeUtil;

import java.util.*;

public class PurchaseOrderPrediction {
    public static double[] predict(int forecastSz, List<Double> data){
        //int forecastSz = 7;
        int p = 3;
        int d = 0;
        int q = 1;
        int P = 1;
        int D = 0;
        int Q = 1;
        int m = 12;
        final double[] a = new double[data.size()];
        for(int i = 0; i < data.size(); i++) a[i] = data.get(i);
        final ForecastResult res = Arima
                .forecast_arima(a, forecastSz, new ArimaParams(p, d, q, P, D, Q, m));
        //double[] res_p = new double[res.getForecast().length];
        //for(double v: res.getForecast()) System.out.println(v);
        return res.getForecast();
    }
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
        String lastDate = I.getSalesHistoryItemList().get(len-1).getDate();

        int wSz = Math.min(I.getSalesHistoryItemList().size(),10);
        List<SalesHistoryItem> predict = new ArrayList<>();

        PurchaseOrder po = null;
        int distance = DateTimeUtil.distance(lastDate,I.getOrderDate());
        int forecastSize = distance + I.getDeliveryDays();
        System.out.println("Distance from " + lastDate + " to " + I.getOrderDate() + " = " + distance);

        Map<String, double[]> mProductId2Forecasts = new HashMap<>();
        List<OrderItem> orderItemList = new ArrayList<>();
            for (String pId : mProduct2QtySeq.keySet()) {
                List<Double> L = mProductId2ListSeq.get(pId);
                double[] p = predict(forecastSize,L);
                System.out.println("Predict of pId " + pId + " = ");for(double v: p) System.out.print(v + ","); System.out.println();
                mProductId2Forecasts.put(pId,p);
                double nextVal = p[p.length-1];
                int intNextVal = (int)nextVal;
                L.add(intNextVal*1.0);
                int qty = intNextVal - I.getInventoryRemain().get(pId);// subtract inventory remain
                if(qty < 0) qty = 0;
                orderItemList.add(new OrderItem(pId,qty));
            }
        po = new PurchaseOrder(I.getOrderDate(), orderItemList);
            predict = new ArrayList<>();
            for(int i = 0; i < forecastSize; i++){
                lastDate = DateTimeUtil.nextDate(lastDate);
                List<OrderItem> orderItems = new ArrayList<>();
                for (String pId : mProduct2QtySeq.keySet()) {
                    double nextVal = mProductId2Forecasts.get(pId)[i];
                    int intNextVal = (int)nextVal;

                    int qty = intNextVal - I.getInventoryRemain().get(pId);// subtract inventory remain
                    if(qty < 0) qty = 0;
                    orderItems.add(new OrderItem(pId,qty));
                }
                predict.add(new SalesHistoryItem(lastDate,orderItems));
            }
        /*
        for(int i = 1; i <= n+1; i++) {
            lastDate = DateTimeUtil.nextDate(lastDate);
            List<OrderItem> orderItemList = new ArrayList<>();
            for (String pId : mProduct2QtySeq.keySet()) {
                List<Double> L = mProductId2ListSeq.get(pId);
                MovingAverage MA = new MovingAverage(wSz, L);
                double nextVal = MA.predictNextValue();
                int intNextVal = (int)nextVal;
                L.add(intNextVal*1.0);
                int qty = intNextVal - I.getInventoryRemain().get(pId);// subtract inventory remain
                if(qty < 0) qty = 0;
                orderItemList.add(new OrderItem(pId,qty));
            }
            if(i == I.getDeliveryDays() + 1){
                po = new PurchaseOrder(lastDate,orderItemList);
            }
            predict.add(new SalesHistoryItem(lastDate,orderItemList));
        }
         */
        return new ModelResponseCreatePurchaseOrder(po,predict);
    }

    public static void main(String[] args){
        int n = 1000;
        double[] data = new double[n];
        Random R = new Random();
        for(int i = 0; i < n; i++){
            int v = R.nextInt(1000) + 1;
            data[i] = (double)v*1.0/5;
        }
        int forecastSz = 7;
        int p = 3;
        int d = 0;
        int q = 1;
        int P = 1;
        int D = 0;
        int Q = 1;
        int m = 12;

        final ForecastResult res = Arima
                .forecast_arima(data, forecastSz, new ArimaParams(p, d, q, P, D, Q, m));
        for(double v: res.getForecast()) System.out.println(v);
    }
}
