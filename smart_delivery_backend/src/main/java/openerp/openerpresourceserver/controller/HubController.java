package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.dto.HubWithBaysDto;
import openerp.openerpresourceserver.dto.HubGeneral;
import openerp.openerpresourceserver.service.HubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/smdeli/hubmanager")
public class HubController {

    private HubService hubService;

    @Autowired
    public HubController(HubService hubService) {
        this.hubService = hubService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'ROUTE_MANAGER')")
    @GetMapping("/hub")
    public ResponseEntity<List<HubGeneral>> getAllHubGeneral(){
        return ResponseEntity.ok(hubService.getAllHubGeneral());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'COLLECTOR','DRIVER')")
    @GetMapping("/hub/{id}")
    public ResponseEntity<HubWithBaysDto> getHubById(@PathVariable String id){
        return ResponseEntity.ok(hubService.getHubById(id));
    }

//    @GetMapping("/hub")
//    public ResponseEntity<Hub> getAllHubGeneral(){
//        return ResponseEntity.ok(hubService.getAllHubGeneral());
//    }

    @PostMapping("/hub/add")
    public ResponseEntity<Hub> createHub(@Valid @RequestBody HubWithBaysDto request){
        return ResponseEntity.ok(hubService.createHub(request));
    }


    @PutMapping("/hub/update")
    public ResponseEntity<Hub> updateHub(@Valid @RequestBody HubWithBaysDto request){
        return ResponseEntity.ok(hubService.updateHub(request));
    }

    @DeleteMapping("/hub/delete")
    public ResponseEntity<Hub> deleteHub(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        Hub deletedHub = hubService.deleteHub(id);
        return ResponseEntity.ok(deletedHub);
    }

}
