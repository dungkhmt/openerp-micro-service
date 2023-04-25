package wms.service.sale_order;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import wms.dto.ReturnPaginationDTO;
import wms.dto.purchase_order.PurchaseOrderDTO;
import wms.dto.purchase_order.UpdatePurchaseOrderDTO;
import wms.dto.sale_order.SaleOrderDTO;
import wms.entity.PurchaseOrder;
import wms.entity.PurchaseOrderItem;
import wms.entity.SaleOrder;
import wms.entity.SaleOrderItem;
import wms.exception.CustomException;

public interface ISaleOrderService {
    SaleOrder createOrder(SaleOrderDTO saleOrderDTO, JwtAuthenticationToken token) throws CustomException;
    ReturnPaginationDTO<SaleOrder> getAllOrders(int page, int pageSize, String sortField, boolean isSortAsc, String orderStatus) throws JsonProcessingException;
    ReturnPaginationDTO<SaleOrderItem> getOrderItems(int page, int pageSize, String sortField, boolean isSortAsc, String orderCode) throws JsonProcessingException;
    SaleOrder getOrderById(long id);
    SaleOrder getOrderByCode(String code);
    SaleOrderItem getOrderItemByProduct(String orderCode, String productCode);
//    SaleOrder updateOrder(UpdateSaleOrderDTO updateSaleOrderDTO, long id) throws CustomException;
    SaleOrder updateOrderStatus(String status, String orderCode) throws CustomException;
    void deleteOrderById(long id);
}
