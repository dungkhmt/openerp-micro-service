package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.entity.projection.ReceiptItemProjection;
import openerp.openerpresourceserver.repository.ReceiptItemRepository;

@Service
public class ReceiptItemService {

	@Autowired
    private  ReceiptItemRepository repository;


    public List<ReceiptItemProjection> getItemsByRequestId(UUID receiptItemRequestId) {
        return repository.findByReceiptItemRequestId(receiptItemRequestId);
    }
}

