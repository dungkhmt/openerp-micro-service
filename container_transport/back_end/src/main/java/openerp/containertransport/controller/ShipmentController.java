package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.service.ShipmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shipment")
public class ShipmentController {
    private final ShipmentService shipmentService;
}
