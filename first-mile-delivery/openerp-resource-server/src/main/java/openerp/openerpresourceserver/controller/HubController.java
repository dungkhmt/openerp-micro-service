package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.HubDTO;
import openerp.openerpresourceserver.dto.HubUpdateDTO;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.service.HubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/hub")
public class HubController {

    private HubService hubService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {

        return ResponseEntity.ok().body(hubService.getAllHubs());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(Principal principal, @RequestBody HubDTO hub) {
        System.out.println(hub);
        return ResponseEntity.ok().body(hubService.saveHub(principal.getName(),hub));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam UUID id) {
        return ResponseEntity.ok().body(hubService.deleteHub(id));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(Principal principal, @RequestBody HubUpdateDTO hub) {
        return ResponseEntity.ok().body(hubService.editHub(principal.getName(),hub));
    }

}

