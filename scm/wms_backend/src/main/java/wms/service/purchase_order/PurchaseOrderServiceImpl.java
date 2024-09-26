package wms.service.purchase_order;

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
import wms.dto.purchase_order.PurchaseOrderDTO;
import wms.dto.purchase_order.PurchaseOrderItemDTO;
import wms.dto.purchase_order.UpdatePurchaseOrderDTO;
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
public class PurchaseOrderServiceImpl extends BaseService implements IPurchaseOrderService {
    @Autowired
    private PurchaseOrderItemRepo purchaseOrderItemRepo;
    @Autowired
    private ProductUnitRepo productUnitRepo;
    @Autowired
    private PurchaseOrderRepo purchaseOrderRepo;
    @Autowired
    private FacilityRepo facilityRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ExportPDFService exportPDFService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrder createOrder(PurchaseOrderDTO purchaseOrderDTO, JwtAuthenticationToken token) throws CustomException {
        Facility boughtBy = facilityRepo.getFacilityByCode(purchaseOrderDTO.getBoughtBy());
        UserRegister createdBy = userRepo.getUserByUserLoginId(token.getName());
        if (boughtBy== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Order belongs to no facility, can't create");
        }
        if (createdBy == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Order created by unknown person, can't create");
        }
        PurchaseOrder newOrder = PurchaseOrder.builder()
                .code("PO" + GeneralUtils.generateCodeFromSysTime())
                .supplierCode(purchaseOrderDTO.getSupplierCode())
                .status(OrderStatus.CREATED.getStatus())
                .facility(boughtBy)
                .user(createdBy)
                .vat(purchaseOrderDTO.getVat())
                .build();
        purchaseOrderRepo.save(newOrder);

        double total = 0.0;
        int seq = 0;
        for (PurchaseOrderItemDTO orderItem : purchaseOrderDTO.getOrderItems()) {
            seq++;
            ProductEntity product = productRepo.getProductByCode(orderItem.getProductCode().toUpperCase());
            if (product == null) {
                log.error("Product with code {} not found", orderItem.getProductCode().toUpperCase());
                throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product not found");
            }
            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(newOrder)
                    .priceUnit(orderItem.getPriceUnit())
                    .product(product)
                    .seqId("00" + seq)
                    .quantity(orderItem.getQuantity())
                    .build();
            total += orderItem.getPriceUnit() * orderItem.getQuantity();
            purchaseOrderItemRepo.save(item);
        }
        newOrder.setTotalPayment(total + total * purchaseOrderDTO.getVat() / 100);
        newOrder.setTotalMoney(total);
        return purchaseOrderRepo.save(newOrder);
    }

    @Override
    public ReturnPaginationDTO<PurchaseOrder> getAllOrders(int page, int pageSize, String sortField, boolean isSortAsc, String orderStatus
    ,String facilityName, String createdBy, String supplierCode, String textSearch
    ) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<PurchaseOrder> purchaseOrders = purchaseOrderRepo.search(pageable, orderStatus.toUpperCase(),
                facilityName, createdBy, supplierCode, textSearch);
        return getPaginationResult(purchaseOrders.getContent(), page, purchaseOrders.getTotalPages(), purchaseOrders.getTotalElements());
    }

    @Override
    public ReturnPaginationDTO<PurchaseOrderItem> getOrderItems(int page, int pageSize, String sortField, boolean isSortAsc, String orderCode) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<PurchaseOrderItem> purchaseOrderItems = purchaseOrderItemRepo.search(pageable, orderCode.toUpperCase());
        return getPaginationResult(purchaseOrderItems.getContent(), page, purchaseOrderItems.getTotalPages(), purchaseOrderItems.getTotalElements());
    }

    @Override
    public PurchaseOrder getOrderById(long id) {
        return purchaseOrderRepo.getOrderById(id);
    }

    @Override
    public PurchaseOrder getOrderByCode(String code) {
        return purchaseOrderRepo.getOrderByCode(code.toUpperCase());
    }

    @Override
    public PurchaseOrderItem getOrderItemByProduct(String orderCode, String productCode) {
        return purchaseOrderItemRepo.getItemByProductCode(orderCode, productCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrder updateOrder(UpdatePurchaseOrderDTO updatePurchaseOrderDTO) throws CustomException {
        double totalMoney = 0.0;
        PurchaseOrder currOrder = getOrderByCode(updatePurchaseOrderDTO.getCreatedOrderCode());
        if (currOrder == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown order, can't update");
        }
        List<Long> lstPreviousOrderItemId = currOrder.getPurchaseOrderItems().stream().map(PurchaseOrderItem::getId).collect(Collectors.toList());
        purchaseOrderItemRepo.deleteAllByIds(lstPreviousOrderItemId);
        Facility boughtBy = facilityRepo.getFacilityByCode(updatePurchaseOrderDTO.getBoughtBy());
        int seq = 0;
        List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();
        for (PurchaseOrderItemDTO orderItem : updatePurchaseOrderDTO.getOrderItems()) {
            seq++;
            ProductEntity product = productRepo.getProductByCode(orderItem.getProductCode().toUpperCase());
            if (product == null) {
                log.error("Product with code {} not found", orderItem.getProductCode().toUpperCase());
                throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product not found");
            }
            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(currOrder)
                    .priceUnit(orderItem.getPriceUnit())
                    .product(product)
                    .seqId("00" + seq)
                    .quantity(orderItem.getQuantity())
                    .build();
            totalMoney += orderItem.getPriceUnit() * orderItem.getQuantity();
            purchaseOrderItemRepo.save(item);
            purchaseOrderItems.add(item);
        }
        currOrder.setPurchaseOrderItems(purchaseOrderItems);
        currOrder.setSupplierCode(updatePurchaseOrderDTO.getSupplierCode());
        currOrder.setFacility(boughtBy);
        currOrder.setVat(updatePurchaseOrderDTO.getVat());
        currOrder.setTotalMoney(totalMoney);
        currOrder.setTotalPayment(totalMoney + totalMoney * updatePurchaseOrderDTO.getVat() / 100);
        return purchaseOrderRepo.save(currOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrder updateOrderStatus(String status, String orderCode) throws CustomException {
        PurchaseOrder currOrder = getOrderByCode(orderCode.toUpperCase());
        currOrder.setStatus(status.toUpperCase());
        return purchaseOrderRepo.save(currOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrderById(long id) {
        PurchaseOrder order = getOrderById(id);
        order.setStatus(OrderStatus.DELETED.getStatus());
        purchaseOrderRepo.save(order);
    }

    @Override
    public ResponseEntity<InputStreamResource> exportOrderPdf(String orderCode) throws IOException {
        PurchaseOrder order = purchaseOrderRepo.getOrderByCode(orderCode);
        String dest = "don_mua_hang_" + orderCode + ".pdf";
        return exportPDFService.createPdfOrder(order, dest);
    }
}
