package openerp.openerpresourceserver.controller;

import openerp.openerpresourceserver.context.DistributeContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final DistributeContext distributeContext;

    public AdminController(DistributeContext distributeContext) {
        this.distributeContext = distributeContext;
    }

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

