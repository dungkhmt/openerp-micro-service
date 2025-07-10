package openerp.openerpresourceserver.controller;

import openerp.openerpresourceserver.service.context.DistributeContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/smdeli/admin")
public class AdminController {

    private final DistributeContext distributeContext;

    public AdminController(DistributeContext distributeContext) {
        this.distributeContext = distributeContext;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/current-strategy")
    public ResponseEntity<String> getCurrentStrategy() {
        try {
            String currentStrategy = distributeContext.getCurrentStrategy();
            return ResponseEntity.ok(currentStrategy);
        } catch (Exception e) {
            // Return the default strategy if there's an issue
            return ResponseEntity.ok(e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/change-strategy")
    public ResponseEntity<String> changeStrategy(@RequestParam String strategy) {
        try {
            distributeContext.setDistributeStrategy(strategy);
            return ResponseEntity.ok("Đã đổi strategy thành: " + strategy);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

