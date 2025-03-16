package openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.model;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelInputCreatePurchaseOrder {
    List<SalesHistoryItem> salesHistoryItemList;
    int len;
    public static String std(int v, int L){
        String s = "" + v;
        while(s.length() < L) s = s + "0";
        return s;
    }
    public static void main(String[] args){
        String[] productIds = {"PRD001","PRD002","PRD003","PRD004","PRD005","PRD006","PRD007","PRD008","PRD009","PRD010"};
        List<SalesHistoryItem> salesHistoryItemList = new ArrayList<>();
        Random R = new Random();
        int len = 50;
        int year = 2025;
        int month = 1;
        int day = 1;
        int maxQty = 1000;
        for(int i = 1; i <= len; i++){
            String date = "" + year + "-" + std(month,2) + "-" + std(day,2);
            day = day + 1;
            if(day > 30){
                month ++; day = 1;
            }
            List<OrderItem> orders = new ArrayList<>();
            int nbProducts = R.nextInt(productIds.length/2) + productIds.length/2;
            for(int j = 1; j <= nbProducts; j++){
                int idx = R.nextInt(productIds.length);
                String productId = productIds[idx];
                int qty = R.nextInt(maxQty)+1;
                orders.add(new OrderItem(productId,qty));
            }
            salesHistoryItemList.add(new SalesHistoryItem(date,orders));
        }

        ModelInputCreatePurchaseOrder model = new ModelInputCreatePurchaseOrder(salesHistoryItemList,3);
        Gson gson = new Gson();
        String json = gson.toJson(model);
        try{
            PrintWriter out = new PrintWriter("po-input.json");
            out.print(json);
            out.close();
        }catch (Exception e){ e.printStackTrace();}
    }
}

