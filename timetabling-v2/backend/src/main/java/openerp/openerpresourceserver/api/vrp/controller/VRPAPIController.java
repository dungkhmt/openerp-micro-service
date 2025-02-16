package openerp.openerpresourceserver.api.vrp.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.api.vrp.model.cvrp.ModelInputCVRP;
import openerp.openerpresourceserver.api.vrp.model.cvrp.ModelResonseCVRP;
import openerp.openerpresourceserver.api.vrp.service.CVRPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class VRPAPIController {
    private CVRPService cvrpService;
    public ResponseEntity<?> cvrp(Principal principal, @RequestBody ModelInputCVRP I){
        ModelResonseCVRP res = cvrpService.solve(I);
        return ResponseEntity.ok().body(res);
    }
}
