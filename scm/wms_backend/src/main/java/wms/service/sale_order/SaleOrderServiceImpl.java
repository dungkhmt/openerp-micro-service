package wms.service.sale_order;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wms.common.enums.ErrorCode;
import wms.common.enums.OrderStatus;
import wms.dto.ReturnPaginationDTO;
import wms.dto.purchase_order.PurchaseOrderItemDTO;
import wms.dto.sale_order.SaleOrderDTO;
import wms.dto.sale_order.SaleOrderItemDTO;
import wms.dto.sale_order.UpdateSaleOrderDTO;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.*;
import wms.service.BaseService;
import wms.service.files.ExportPDFService;
import wms.utils.GeneralUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SaleOrderServiceImpl extends BaseService implements ISaleOrderService {
    @Autowired
    private ProductSalePriceRepo productSalePriceRepo;
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
    @Autowired
    private ExportPDFService exportPDFService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleOrder createOrder(SaleOrderDTO saleOrderDTO, JwtAuthenticationToken token) throws CustomException {
        Customer boughtBy = customerRepo.getCustomerByCode(saleOrderDTO.getBoughtBy());
        UserRegister createdBy = userRepo.getUserByUserLoginId(token.getName());

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
            // TODO: Add distribution channel as one of the discount factors here
            ProductSalePrice salePrice = productSalePriceRepo.getByProductAndContract(orderItem.getProductCode(), boughtBy.getContractType().getCode());
            double massDiscount = salePrice != null && orderItem.getQuantity() >= product.getMassQuantity() ? salePrice.getMassDiscount() : 0;
            double contractDiscount = salePrice != null ? salePrice.getContractDiscount() : 0;
            double promotionDiscount = salePrice != null ? salePrice.getContractType().getChannel().getPromotion() : 0;
            SaleOrderItem item = SaleOrderItem.builder()
                    .saleOrder(newOrder)
                    .priceUnit(orderItem.getPriceUnit() * (100 - massDiscount - contractDiscount - promotionDiscount) / 100)
                    .product(product)
                    .seqId("00" + seq)
                    .quantity(orderItem.getQuantity())
                    .build();
            total += item.getPriceUnit() * orderItem.getQuantity();
            saleOrderItemRepo.save(item);
        }
        newOrder.setTotalMoney(total);
        newOrder.setTotalPayment(total - total * saleOrderDTO.getDiscount() / 100);
        return saleOrderRepo.save(newOrder);
    }

    @Override
    public ReturnPaginationDTO<SaleOrder> getAllOrders(int page, int pageSize, String sortField, boolean isSortAsc, String orderStatus, String createdBy, String customerName, String textSearch) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<SaleOrder> saleOrders = saleOrderRepo.search(pageable, orderStatus.toUpperCase(), createdBy, customerName, textSearch);
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
        return saleOrderRepo.getOrderById(id);
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
    public SaleOrder updateOrder(UpdateSaleOrderDTO updateSaleOrderDTO) throws CustomException {
        double totalMoney = 0.0;
        SaleOrder currOrder = getOrderByCode(updateSaleOrderDTO.getCreatedOrderCode());
        if (currOrder == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown order, can't update");
        }
        List<Long> lstPreviousOrderItemId = currOrder.getSaleOrderItems().stream().map(SaleOrderItem::getId).collect(Collectors.toList());
        saleOrderItemRepo.deleteAllByIds(lstPreviousOrderItemId);
        Customer boughtBy = customerRepo.getCustomerByCode(updateSaleOrderDTO.getBoughtBy());
        if (boughtBy== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Order belongs to no customer, can't update");
        }
        int seq = 0;

        List<SaleOrderItem> saleOrderItems = new ArrayList<>();
        for (SaleOrderItemDTO orderItem : updateSaleOrderDTO.getOrderItems()) {
            seq++;
            ProductEntity product = productRepo.getProductByCode(orderItem.getProductCode().toUpperCase());
            if (product == null) {
                log.error("Product with code {} not found", orderItem.getProductCode().toUpperCase());
                throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product not found");
            }
            ProductSalePrice salePrice = productSalePriceRepo.getByProductAndContract(orderItem.getProductCode(), boughtBy.getContractType().getCode());
            double massDiscount = salePrice != null ? salePrice.getMassDiscount() : 0;
            double contractDiscount = salePrice != null ? salePrice.getContractDiscount() : 0;
            double promotionDiscount = salePrice != null ? salePrice.getContractType().getChannel().getPromotion() : 0;
            SaleOrderItem item = SaleOrderItem.builder()
                    .saleOrder(currOrder)
                    .priceUnit(orderItem.getPriceUnit() * (100 - massDiscount - contractDiscount - promotionDiscount) / 100)
                    .product(product)
                    .seqId("00" + seq)
                    .quantity(orderItem.getQuantity())
                    .build();
            totalMoney += orderItem.getPriceUnit() * orderItem.getQuantity();
            saleOrderItemRepo.save(item);
            saleOrderItems.add(item);
        }
        currOrder.setSaleOrderItems(saleOrderItems);
        currOrder.setCustomer(boughtBy);
        currOrder.setDiscount(updateSaleOrderDTO.getDiscount());
        currOrder.setTotalMoney(totalMoney);
        currOrder.setTotalPayment(totalMoney - totalMoney * updateSaleOrderDTO.getDiscount() / 100);
        return saleOrderRepo.save(currOrder);
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
        SaleOrder order = getOrderById(id);
        order.setStatus(OrderStatus.DELETED.getStatus());
        saleOrderRepo.save(order);
    }

    @Override
    public ResponseEntity<InputStreamResource> exportOrderPdf(String orderCode) throws IOException {
        SaleOrder order = saleOrderRepo.getOrderByCode(orderCode);
        String dest = "don_mua_hang_" + orderCode + ".pdf";
        return exportPDFService.createPdfOrder(order, dest);
    }
}
