package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    @PostMapping("/order-by-month")
    public ResponseEntity<?> getOrderByMonth(@PathVariable int year) {
        return null;
    }

    @PostMapping("/truck-rate")
    public ResponseEntity<?> getRateUsingTruck() {
        return null;
    }
}
