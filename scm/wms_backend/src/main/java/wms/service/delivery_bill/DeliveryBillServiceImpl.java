package wms.service.delivery_bill;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import wms.dto.ReturnPaginationDTO;
import wms.entity.DeliveryBill;
import wms.entity.DeliveryBillItem;
import wms.repo.DeliveryBillItemRepo;
import wms.repo.DeliveryBillRepo;
import wms.service.BaseService;

import java.util.List;

@Service
@Slf4j
public class DeliveryBillServiceImpl extends BaseService implements IDeliveryBillService {
    private final DeliveryBillItemRepo deliveryBillItemRepo;
    private final DeliveryBillRepo deliveryBillRepo;

    public DeliveryBillServiceImpl(DeliveryBillRepo deliveryBillRepo,
                                   DeliveryBillItemRepo deliveryBillItemRepo) {
        this.deliveryBillRepo = deliveryBillRepo;
        this.deliveryBillItemRepo = deliveryBillItemRepo;
    }

    @Override
    public ReturnPaginationDTO<DeliveryBill> getAllBills(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<DeliveryBill> deliveryBills = deliveryBillRepo.search(pageable);
        return getPaginationResult(deliveryBills.getContent(), page, deliveryBills.getTotalPages(), deliveryBills.getTotalElements());
    }

    @Override
    public List<DeliveryBillItem> getBillItemsOfOrder(String orderCode) throws JsonProcessingException {
        return deliveryBillItemRepo.search(orderCode);
    }

    @Override
    public DeliveryBill getBillById(long id) {
        return null;
    }

    @Override
    public DeliveryBill getBillByCode(String code) {
        return null;
    }

    @Override
    public DeliveryBillItem getBillItemsOfOrder(String billCode, String billItemSeq) {
        return null;
    }

    @Override
    public void deleteBillItem(long id) {

    }
}
