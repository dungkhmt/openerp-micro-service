package openerp.openerpresourceserver.controller;

import jakarta.ws.rs.BadRequestException;
import openerp.openerpresourceserver.entity.Collector;
import openerp.openerpresourceserver.entity.Driver;
import openerp.openerpresourceserver.entity.Shipper;
import openerp.openerpresourceserver.dto.EmployeeRequestDto;
import openerp.openerpresourceserver.dto.EmployeeDetailDTO;
import openerp.openerpresourceserver.service.HumanResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/humanresource")
public class HumanResourceController {

    @Autowired
    private HumanResourceService humanResourceService;

    //Endpoint for shipper

    @GetMapping("/shipper")
    public List<Shipper> getAllShipper() {
        List<Shipper> shippers = humanResourceService.getAllShipper();
        return shippers;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @GetMapping("/shipper/hub/{hubId}")
    public List<Shipper> getShippersByHubId(@PathVariable UUID hubId) {
        List<Shipper> shippers = humanResourceService.getAllShippersByHubId(hubId);
        return shippers;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @GetMapping("/shipper/{id}")
    public EmployeeDetailDTO getShipperById(@PathVariable UUID id) {
        return humanResourceService.getShipperById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @PostMapping("/shiper/{id}")
    public Shipper updateShipper(@PathVariable UUID id, @RequestBody Shipper shipper) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @DeleteMapping("/shipper/{id}")
    public void deleteShipper(@PathVariable UUID id) {
        humanResourceService.deleteShipper(id);
    }


    //Endpoint for collector
    @GetMapping("/collector")
    public List<Collector> getAllCollector() {
        List<Collector> collectors = humanResourceService.getAllCollector();
        return collectors;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @GetMapping("/collector/hub/{hubId}")
    public List<Collector> getCollectorsByHubId(@PathVariable UUID hubId) {
        return humanResourceService.getAllCollectorsByHubId(hubId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @GetMapping("/collector/{id}")
    public EmployeeDetailDTO getCollectorById(@PathVariable UUID id) {
        return humanResourceService.getCollectorById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @PostMapping("/collector/{id}")
    public Collector updateCollector(@PathVariable UUID id, @RequestBody Collector collector) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @DeleteMapping("/collector/{id}")
    public void deleteCollector(@PathVariable UUID id) {
        humanResourceService.deleteCollector(id);
    }



    //Endpoint for driver
    @GetMapping("/driver")
    public List<Driver> getAllDriver() {
        List<Driver> drivers = humanResourceService.getAllDriver();
        return drivers;
    }

    @GetMapping("/driver/hub/{hubId}")
    public List<Driver> getDriversByHubId(@PathVariable UUID hubId) {
        List<Driver> drivers = humanResourceService.getAllDriversByHubId(hubId);
        return drivers;
    }

    @GetMapping("/driver/{id}")
    public EmployeeDetailDTO getDriverById(@PathVariable UUID id) {
        return humanResourceService.getDriverById(id);
    }

    @PostMapping("/driver/{id}")
    public Driver updateDriver(@PathVariable UUID id, @RequestBody Driver driver) {
        return null;
    }

    @DeleteMapping("/driver/{id}")
    public void deleteDriver(@PathVariable UUID id) {
        humanResourceService.deleteDriver(id);
    }



    //Create and Update employee
    @PostMapping("/add")
    public void addEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
         humanResourceService.addEmployee(employeeRequestDto);
    }


    @PutMapping("/update")
    public void updateEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
        try {
            if (employeeRequestDto.getId() != null) {
                // Gọi service để thực hiện cập nhật
                humanResourceService.updateEmployee(employeeRequestDto);
            }
           else throw new BadRequestException("id is null");
        } catch (Exception e) {
        }
    }

}
