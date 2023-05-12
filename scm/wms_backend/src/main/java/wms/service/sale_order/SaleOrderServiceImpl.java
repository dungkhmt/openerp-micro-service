package wms.service.sale_order;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wms.common.enums.ErrorCode;
import wms.common.enums.OrderStatus;
import wms.dto.ReturnPaginationDTO;
import wms.dto.sale_order.SaleOrderDTO;
import wms.dto.sale_order.SaleOrderItemDTO;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.*;
import wms.service.BaseService;
import wms.utils.GeneralUtils;

@Service
@Slf4j
public class SaleOrderServiceImpl extends BaseService implements ISaleOrderService {
    @Autowired
    private SaleOrderItemRepo saleOrderItemRepo;

    @Autowired
    private SaleOrderRepo saleOrderRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private ProductRepo productRepo;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleOrder createOrder(SaleOrderDTO saleOrderDTO, JwtAuthenticationToken token) throws CustomException {
        Customer boughtBy = customerRepo.getCustomerByCode(saleOrderDTO.getBoughtBy());
        UserLogin createdBy = userRepo.getUserByUserLoginId(token.getName());

        if (boughtBy== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Order belongs to no customer, can't create");
        }
        if (createdBy == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Order created by unknown person, can't create");
        }
        SaleOrder newOrder = SaleOrder.builder()
                .code("SO" + GeneralUtils.generateCodeFromSysTime())
                .customer(boughtBy)
                .status(OrderStatus.CREATED.getStatus())
                .user(createdBy)
                .discount(saleOrderDTO.getDiscount())
                .build();
        saleOrderRepo.save(newOrder);

        double total = 0.0;
        int seq = 0;
        for (SaleOrderItemDTO orderItem : saleOrderDTO.getOrderItems()) {
            seq++;
            ProductEntity product = productRepo.getProductByCode(orderItem.getProductCode().toUpperCase());
            if (product == null) {
                log.error("Product with code {} not found", orderItem.getProductCode().toUpperCase());
                throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product not found");
            }
            SaleOrderItem item = SaleOrderItem.builder()
                    .saleOrder(newOrder)
                    .priceUnit(orderItem.getPriceUnit())
                    .product(product)
                    .seqId("00" + seq)
                    .quantity(orderItem.getQuantity())
                    .build();
            total += orderItem.getPriceUnit() * orderItem.getQuantity();
            saleOrderItemRepo.save(item);
        }
        newOrder.setTotalMoney(total);
        newOrder.setTotalPayment(total - total * saleOrderDTO.getDiscount() / 100);
        return saleOrderRepo.save(newOrder);
    }

    @Override
    public ReturnPaginationDTO<SaleOrder> getAllOrders(int page, int pageSize, String sortField, boolean isSortAsc, String orderStatus) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<SaleOrder> saleOrders = saleOrderRepo.search(pageable, orderStatus.toUpperCase());
        return getPaginationResult(saleOrders.getContent(), page, saleOrders.getTotalPages(), saleOrders.getTotalElements());
    }

    @Override
    public ReturnPaginationDTO<SaleOrderItem> getOrderItems(int page, int pageSize, String sortField, boolean isSortAsc, String orderCode) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<SaleOrderItem> purchaseOrderItems = saleOrderItemRepo.search(pageable, orderCode.toUpperCase());
        return getPaginationResult(purchaseOrderItems.getContent(), page, purchaseOrderItems.getTotalPages(), purchaseOrderItems.getTotalElements());
    }

    @Override
    public SaleOrder getOrderById(long id) {
        return null;
    }

    @Override
    public SaleOrder getOrderByCode(String code) {
        return saleOrderRepo.getOrderByCode(code.toUpperCase());
    }

    @Override
    public SaleOrderItem getOrderItemByProduct(String orderCode, String productCode) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleOrder updateOrderStatus(String status, String orderCode) throws CustomException {
        SaleOrder currOrder = getOrderByCode(orderCode.toUpperCase());
        currOrder.setStatus(status.toUpperCase());
        return saleOrderRepo.save(currOrder);
    }

    @Override
    public void deleteOrderById(long id) {

    }
}
