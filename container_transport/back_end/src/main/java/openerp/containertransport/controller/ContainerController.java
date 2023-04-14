package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.service.ContainerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/container")
public class ContainerController {
    private final ContainerService containerService;
}
