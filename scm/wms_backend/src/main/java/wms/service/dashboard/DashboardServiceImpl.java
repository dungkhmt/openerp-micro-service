package wms.service.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wms.common.enums.OrderStatus;
import wms.entity.Facility;
import wms.entity.InventoryItem;
import wms.entity.ProductEntity;
import wms.entity.PurchaseOrder;
import wms.repo.FacilityRepo;
import wms.repo.InventoryItemRepo;
import wms.repo.PurchaseOrderRepo;
import wms.service.BaseService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl extends BaseService implements IDashBoardService {

    @Autowired
    private FacilityRepo facilityRepo;
    @Autowired
    private InventoryItemRepo inventoryItemRepo;
    @Autowired
    private PurchaseOrderRepo purchaseOrderRepo;

    public Map<Integer, List<Integer>>  initQuarterMapping() {
        Map<Integer, List<Integer>> quarterMapping = new HashMap<>();
        List<Integer> monthInQuarter = new ArrayList<>();
        for (int i = 1; i < 13; i++) monthInQuarter.add(i);
        quarterMapping.put(1, monthInQuarter.subList(0, 3));
        quarterMapping.put(2, monthInQuarter.subList(3, 6));
        quarterMapping.put(3, monthInQuarter.subList(6, 9));
        quarterMapping.put(4, monthInQuarter.subList(9, 12));
        return quarterMapping;
    }
    @Override
    public List<Integer> newFacilityMonthly(int year) {
        List<Integer> facilityMonthlyOfYear = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            List<Facility> facilities = facilityRepo.getFacilityCreatedMonthly(i, year);
            facilityMonthlyOfYear.add(facilities.size());
        }
        return facilityMonthlyOfYear;
    }

    @Override
    public List<List<Object>> getImportProductList(int month, int year) {
        List<InventoryItem> inventoryItems = inventoryItemRepo.getAllItemsOfMonthAndYear(month, year);
        return inventoryItems.stream()
                .collect(Collectors.groupingBy((item)-> item.getProduct()))
                .entrySet().stream()
                .map(entry -> {
                    String productCode = entry.getKey().getName();
                    List<Object> innerList = new ArrayList<>();
                    innerList.add(productCode);
                    List<InventoryItem> tempInventory = entry.getValue();
                    int totalQuantity = tempInventory.stream().mapToInt(InventoryItem::getQuantity).sum();
                    innerList.add(totalQuantity);
                    return innerList;
                }).collect(Collectors.toList());

    }

    @Override
    public Map<String, List<List<Integer>>> getPurchaseOrderQuarterly(int quarter, int year) {
        Map<String, List<List<Integer>>> result = new HashMap<>();
        Map<Integer, List<Integer>> quarterMapping = initQuarterMapping();
        List<Integer> months = quarterMapping.get(quarter);
        for (int month : months) {
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepo.getOrderByMonth(month, year);

            result.put(OrderStatus.CREATED.getStatus(), );
            result.put(OrderStatus.ACCEPTED.getStatus(), );
            result.put(OrderStatus.DELIVERING.getStatus(), );
            result.put(OrderStatus.DELIVERED.getStatus(), );
        }
        return null;
    }
}
