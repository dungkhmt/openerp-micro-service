package wms.service.dashboard;

import wms.entity.ProductCategory;

import java.util.List;
import java.util.Map;

public interface IDashBoardService {
    List<Integer> newFacilityMonthly(int year);
    List<Integer> newCustomerMonthly(int year);
    List<List<Object>> getImportProductList(int month, int year);
    Map<Integer, List<Map<String, Integer>>> getPurchaseOrderQuarterly(int quarter, int year);
    List<List<Object> > getTopFiveOrderedCustomer(int month, int year);
    List<List<Object>> getTripCustomerOfEveryProvince(int month, int year);
    List<List<Object>> getProductCategoryRate();
    List<Integer> getSaleAnnual();
}
