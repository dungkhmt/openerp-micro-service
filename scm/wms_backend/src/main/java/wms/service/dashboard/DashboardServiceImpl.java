package wms.service.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wms.common.enums.OrderStatus;
import wms.entity.*;
import wms.repo.*;
import wms.service.BaseService;

import java.time.Year;
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
    @Autowired
    private SaleOrderRepo saleOrderRepo;
    @Autowired
    private ProductCategoryRepo productCategoryRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ShipmentItemRepo shipmentItemRepo;
    @Autowired
    private CustomerRepo customerRepo;

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
    // TODO: Methods should have verb or verb phrase names
    @Override
    public List<Integer> newFacilityMonthly(int year) {
        List<Integer> facilityMonthlyOfYear = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            List<Facility> facilities = facilityRepo.getFacilityCreatedMonthly(i, year);
            facilityMonthlyOfYear.add(facilities.size());
        }
        return facilityMonthlyOfYear;
    }
    // TODO: Methods should have verb or verb phrase names
    @Override
    public List<Integer> newCustomerMonthly(int year) {
        List<Integer> customersMonthly = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            List<Customer> facilities = customerRepo.getCustomerCreatedMonthly(i, year);
            customersMonthly.add(facilities.size());
        }
        return customersMonthly;
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
    public Map<Integer, List<Map<String, Integer>>> getPurchaseOrderQuarterly(int quarter, int year) {
        Map<Integer, List<Map<String, Integer>>> result = new HashMap<>();
        Map<Integer, List<Integer>> quarterMapping = initQuarterMapping();
        List<Integer> months = quarterMapping.get(quarter);

        String createdState = OrderStatus.CREATED.getStatus();
        String acceptedState = OrderStatus.ACCEPTED.getStatus();
        String delivering = OrderStatus.DELIVERING.getStatus();
        String delivered = OrderStatus.DELIVERED.getStatus();


        for (int month : months) {
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepo.getOrderByMonth(month, year);
            int totalCreatedOrderPayment = 0;
            int totalAccepteadOrderPayment = 0;
            int totalDeliveringOrderPayment = 0;
            int totalDeliveredOrderPayment = 0;
            for (PurchaseOrder order : purchaseOrders) {
                if (order.getStatus().equals(createdState)) {
                    totalCreatedOrderPayment += order.getTotalPayment();
                }
                else if (order.getStatus().equals(acceptedState)) {
                    totalAccepteadOrderPayment += order.getTotalPayment();
                }
                else if (order.getStatus().equals(delivering)) {
                    totalDeliveringOrderPayment += order.getTotalPayment();
                } else if (order.getStatus().equals(delivered)) {
                    totalDeliveredOrderPayment += order.getTotalPayment();
                }
            }
            HashMap<String, Integer> createdOrderMap = new HashMap<>();
            HashMap<String, Integer> acceptedOrderMap = new HashMap<>();
            HashMap<String, Integer> deliveringOrderMap = new HashMap<>();
            HashMap<String, Integer> deliveredOrderMap = new HashMap<>();
            createdOrderMap.put(createdState, totalCreatedOrderPayment);
            acceptedOrderMap.put(acceptedState, totalAccepteadOrderPayment);
            deliveringOrderMap.put(delivering, totalDeliveringOrderPayment);
            deliveredOrderMap.put(delivered, totalDeliveredOrderPayment);
            List<Map<String, Integer>> orderResultList = new ArrayList<>();
            orderResultList.add(createdOrderMap);
            orderResultList.add(acceptedOrderMap);
            orderResultList.add(deliveringOrderMap);
            orderResultList.add(deliveredOrderMap);
            result.put(month, orderResultList);
        }
        return result;
    }

    @Override
    public List<List<Object>> getTopFiveOrderedCustomer(int month, int year) {
        List<SaleOrder> saleOrders = saleOrderRepo.getOrderByMonth(month, year);
        Map<Customer, List<SaleOrder>> groupedCustomers = saleOrders.stream().collect(Collectors.groupingBy(SaleOrder::getCustomer));
        List<Integer> topFiveBuying = new ArrayList<>();
        List<Customer> topFiveCustomer = new ArrayList<>();
        for (Map.Entry<Customer, List<SaleOrder>> entry : groupedCustomers.entrySet()) {
            Customer customer = entry.getKey();
            List<SaleOrder> orders = entry.getValue();
            int totalBuying = 0;
            for (SaleOrder order: orders) {
                totalBuying += order.getTotalPayment();
            }
            if (topFiveCustomer.size() < 5) {
                topFiveCustomer.add(customer);
                topFiveBuying.add(totalBuying);
            } else {
                if (topFiveBuying.get(4) < totalBuying) {
                    topFiveBuying.remove(4);
                    topFiveCustomer.remove(4);
                    for (int i = 3; i >=0; i--) {
                        if (topFiveBuying.get(i) > totalBuying) {
                            topFiveBuying.add(i+1, totalBuying);
                            topFiveCustomer.add(i+1, customer);
                        }
                    }
                }
            }
        }
        List<List<Object>> results = new ArrayList<>();
        for (int i = 0; i < topFiveBuying.size(); i++) {
            List<Object> result = new ArrayList<>();
            result.add(topFiveCustomer.get(i));
            result.add(topFiveBuying.get(i));
            results.add(result);
        }
        return results;
    }

    // This function is inefficent.
    public List<List<Object>> getTripCustomerOfEveryProvince(int month, int year) {
        List<ShipmentItem> shipmentItems = shipmentItemRepo.getShipmentItemsOfMonth(month, year);
        List<Customer> customers = shipmentItems.stream().map(item -> item.getDeliveryBill().getSaleOrder().getCustomer()).collect(Collectors.toList());
        Map<String, List<Customer>> customerMapping = customers.stream().filter(cus -> {
            if (cus.getProvince() != null) {
                return true;
            } else return false;
        }).collect(Collectors.groupingBy(customer -> customer.getProvince().toUpperCase()));
        List<List<Object>> results = new ArrayList<>();
        for (Map.Entry<String, List<Customer>> entry : customerMapping.entrySet()) {
            String province = entry.getKey();
            List<Customer> listCustomers = entry.getValue();
            List<Object> resultList = new ArrayList<>();
            resultList.add(province);
            resultList.add(listCustomers.size());
            results.add(resultList);
        }
        return results;
    }

    @Override
    public List<List<Object>> getProductCategoryRate() {
        List<List<Object>> results = new ArrayList<>();
        List<ProductEntity> productEntities = productRepo.getAllProduct();
        Map<ProductCategory, List<ProductEntity>> groupCategories = productEntities.stream().collect(Collectors.groupingBy(ProductEntity::getProductCategory));
        for (Map.Entry<ProductCategory, List<ProductEntity>> entry : groupCategories.entrySet()) {
            ProductCategory productCategory = entry.getKey();
            List<ProductEntity> currProducts = entry.getValue();
            List<Object> resultList = new ArrayList<>();
            resultList.add(productCategory);
            resultList.add(currProducts.size());
            results.add(resultList);
        }
        return results;
    }

    @Override
    public List<Integer> getSaleAnnual() {
        Year currentYear = Year.now();
        int yearValue = currentYear.getValue();
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            List<SaleOrder> saleOrders = saleOrderRepo.getOrderByMonth(i, yearValue);
            result.add(saleOrders.size());
        }
        return result;
    }
}
