package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.projection.SaleOrderItemDetailProjection;
import openerp.openerpresourceserver.entity.projection.SaleOrderItemProjection;
import openerp.openerpresourceserver.service.SaleOrderItemService;

@RestController
@RequestMapping("/order-items")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SaleOrderItemController {

	private SaleOrderItemService saleOrderItemService;
	
	@GetMapping
	public List<SaleOrderItemProjection> getAllSaleOrderItems(@RequestParam UUID orderId) {
		return saleOrderItemService.getSaleOrderItems(orderId);
	}
	
	@GetMapping("/{id}")
	public SaleOrderItemDetailProjection getSaleOrderItemDetail(@PathVariable UUID id) {
		return saleOrderItemService.getSaleOrderItemDetail(id);
	}

}
