package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class TestController {
    @PostMapping("/add-two-number")
    public ResponseEntity<?> addTwoNumber(Principal principal, @RequestBody ModelAddTwoNumber m){
        int res = m.getA() + m.getB();
        return ResponseEntity.ok().body(res);
    }
}

