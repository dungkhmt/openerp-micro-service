package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.entity.ReceiptBill;
import openerp.openerpresourceserver.projection.ReceiptBillProjection;
import openerp.openerpresourceserver.repository.ReceiptBillRepository;

@Service
@RequiredArgsConstructor
public class ReceiptBillService {

	private final ReceiptBillRepository receiptBillRepository;
	private final ReceiptItemRequestService receiptItemRequestService;

	public Page<ReceiptBillProjection> getAllReceiptBills(Pageable pageable) {
		return receiptBillRepository.findAllReceiptBills(pageable);
	}

	public List<String> getAllReceiptBillIds(UUID receiptItemRequestId) {
		// Lấy receiptId từ receiptItemRequestId
		UUID receiptId = receiptItemRequestService.getReceiptIdByRequestId(receiptItemRequestId);

		// Lấy tất cả các receiptBillId thuộc về receiptId
		return receiptBillRepository.findReceiptBillIdsByReceiptId(receiptId);
	}

	public ReceiptBill createReceiptBill(String receiptBillId, String description, String createdBy,
			UUID receiptItemRequestId) {
		// Lấy receiptId từ receiptItemRequestId
		UUID receiptId = receiptItemRequestService.getReceiptIdByRequestId(receiptItemRequestId);

		// Tạo đối tượng ReceiptBill
		ReceiptBill receiptBill = ReceiptBill.builder().receiptBillId(receiptBillId).description(description)
				.receiptId(receiptId).createdBy(createdBy).createdStamp(LocalDateTime.now())
				.lastUpdateStamp(LocalDateTime.now()).totalPrice(0).build();

		// Lưu vào cơ sở dữ liệu
		return receiptBillRepository.save(receiptBill);
	}

	public void updateTotalPrice(double importPrice, int quantity, String receiptBillId) {
		// Tính toán giá trị cần thêm vào totalPrice
		double additionalPrice = importPrice * quantity;
		// Lấy ReceiptBill tương ứng
		ReceiptBill receiptBill = receiptBillRepository.findById(receiptBillId)
				.orElseThrow(() -> new RuntimeException("ReceiptBill not found with ID: " + receiptBillId));

		// Cập nhật totalPrice
		receiptBill.setTotalPrice(receiptBill.getTotalPrice() + additionalPrice);
		receiptBill.setLastUpdateStamp(LocalDateTime.now());

		// Lưu ReceiptBill với totalPrice đã được cập nhật
		receiptBillRepository.save(receiptBill);
	}
}
