package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.response.SaleOrderItemDetailResponse;
import openerp.openerpresourceserver.dto.response.SaleOrderItemResponse;
import openerp.openerpresourceserver.service.SaleOrderItemService;

@RestController
@RequestMapping("/order-items")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SaleOrderItemController {

	private SaleOrderItemService saleOrderItemService;

	@Secured({"ROLE_WMS_WAREHOUSE_MANAGER","ROLE_WMS_ONLINE_CUSTOMER","ROLE_WMS_SALE_MANAGER"})
	@GetMapping
	public List<SaleOrderItemResponse> getAllSaleOrderItems(@RequestParam UUID orderId) {
		return saleOrderItemService.getSaleOrderItems(orderId);
	}
	
	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping("/{id}")
	public SaleOrderItemDetailResponse getSaleOrderItemDetail(@PathVariable UUID id) {
		return saleOrderItemService.getSaleOrderItemDetail(id);
	}

}
