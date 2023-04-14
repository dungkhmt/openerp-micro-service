package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
}
