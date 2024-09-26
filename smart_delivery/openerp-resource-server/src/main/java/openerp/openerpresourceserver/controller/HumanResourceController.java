package openerp.openerpresourceserver.controller;

import openerp.openerpresourceserver.entity.Collector;
import openerp.openerpresourceserver.entity.Driver;
import openerp.openerpresourceserver.entity.Shipper;
import openerp.openerpresourceserver.service.HumanResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/smdeli/humanresource")
public class HumanResourceController {

    @Autowired
    private HumanResourceService humanResourceService;

    @GetMapping("/shipper")
    public List<Shipper> getAllShipper() {
        List<Shipper> shippers = humanResourceService.getAllShipper();
        return shippers;
    }

    @GetMapping("/collector")
    public List<Collector> getAllCollector() {
        List<Collector> collectors = humanResourceService.getAllCollector();
        return collectors;
    }

    @GetMapping("/driver")
    public List<Driver> getAllDriver() {
        List<Driver> drivers = humanResourceService.getAllDriver();
        return drivers;
    }



}
