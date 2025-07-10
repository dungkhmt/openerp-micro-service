package openerp.openerpresourceserver.controller;

import openerp.openerpresourceserver.entity.UsernameHubId;
import openerp.openerpresourceserver.service.UsernameHubIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/hub")
public class UsernameHubIdController {

    private final UsernameHubIdService usernameHubIdService;

    @Autowired
    public UsernameHubIdController(UsernameHubIdService usernameHubIdService) {
        this.usernameHubIdService = usernameHubIdService;
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UsernameHubId> getHubIdByUsername(@PathVariable String username) {
        Optional<UsernameHubId> hubId = usernameHubIdService.getHubIdByUsername(username);
        return hubId.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 