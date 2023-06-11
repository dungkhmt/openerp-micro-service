package wms.service.dashboard;

import wms.entity.Facility;
import wms.entity.ProductEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IDashBoardService {
    List<Integer> newFacilityMonthly(int year);
    List<List<Object>> getImportProductList(int month, int year);
    Map<String, List<List<Integer>>> getPurchaseOrderQuarterly(int quarter, int year);
}
