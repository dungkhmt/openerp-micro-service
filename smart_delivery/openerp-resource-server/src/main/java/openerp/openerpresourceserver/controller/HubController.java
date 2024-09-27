package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.request.HubWithBays;
import openerp.openerpresourceserver.response.HubGeneral;
import openerp.openerpresourceserver.service.HubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hub")
    public ResponseEntity<List<HubGeneral>> getAllHubGeneral(){
        return ResponseEntity.ok(hubService.getAllHubGeneral());
    }

    @GetMapping("/hub/{id}")
    public ResponseEntity<HubWithBays> getHubById(@PathVariable String id){
        return ResponseEntity.ok(hubService.getHubById(id));
    }

//    @GetMapping("/hub")
//    public ResponseEntity<Hub> getAllHubGeneral(){
//        return ResponseEntity.ok(hubService.getAllHubGeneral());
//    }

    @PostMapping("/hub/add")
    public ResponseEntity<Hub> createHub(@Valid @RequestBody HubWithBays request){
        return ResponseEntity.ok(hubService.createHub(request));
    }


    @PutMapping("/hub/update")
    public ResponseEntity<Hub> updateHub(@Valid @RequestBody HubWithBays request){
        return ResponseEntity.ok(hubService.updateHub(request));
    }

    @DeleteMapping("/hub/delete")
    public ResponseEntity<Hub> deleteHub(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        Hub deletedHub = hubService.deleteHub(id);
        return ResponseEntity.ok(deletedHub);
    }

}
