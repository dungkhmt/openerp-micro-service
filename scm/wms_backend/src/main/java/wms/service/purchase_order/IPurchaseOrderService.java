package wms.service.purchase_order;

import com.fasterxml.jackson.core.JsonProcessingException;
import wms.common.enums.OrderStatus;
import wms.dto.ReturnPaginationDTO;
import wms.dto.purchase_order.PurchaseOrderDTO;
import wms.dto.purchase_order.UpdatePurchaseOrderDTO;
import wms.entity.PurchaseOrder;
import wms.entity.PurchaseOrderItem;
import wms.exception.CustomException;

public interface IPurchaseOrderService {
    PurchaseOrder createOrder(PurchaseOrderDTO purchaseOrderDTO) throws CustomException;
    ReturnPaginationDTO<PurchaseOrder> getAllOrders(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    PurchaseOrder getOrderById(long id);
    PurchaseOrder getOrderByCode(String code);
    PurchaseOrderItem getOrderItemByProduct(String orderCode, String productCode);
    PurchaseOrder updateOrder(UpdatePurchaseOrderDTO updatePurchaseOrderDTO, long id) throws CustomException;
    PurchaseOrder updateOrderStatus(OrderStatus status, long id) throws CustomException;
    void deleteOrderById(long id);
}
