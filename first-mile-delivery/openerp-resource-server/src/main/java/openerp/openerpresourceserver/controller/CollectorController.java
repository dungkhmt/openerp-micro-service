package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.CollectorDTO;
import openerp.openerpresourceserver.service.CollectorService;
import openerp.openerpresourceserver.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/collector")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CollectorController {

    private CollectorService collectorService;

//    @PostMapping("/create")
//    public void createCollector(@P("userId") String userId, @P("hubId") String hubId) {
//        collectorService.createCollector(userId, hubId);
//    }

    @PostMapping("/create")
    public ResponseEntity<?> registerCollector(Principal principal, @RequestBody CollectorDTO collectorDTO) {

        String userId = collectorDTO.getUserId() != null ? collectorDTO.getUserId() : principal.getName();


        return ResponseEntity.ok().body(collectorService.createCollector(collectorDTO, userId));
    }

    @DeleteMapping("/delete/{collectorId}")
    public ResponseEntity<?> deleteCollector(@PathVariable UUID collectorId){
        return ResponseEntity.ok().body(collectorService.deleteCollector(collectorId));
    }


    //PUT /update?collectorId=abcd-1234-efgh-5678
    @PutMapping("/update")
    public ResponseEntity<?> updateCollector(@RequestParam("collectorId") UUID collectorId, @RequestBody CollectorDTO collectorDTO){
        return ResponseEntity.ok().body(collectorService.updateCollector(collectorId, collectorDTO));
    }

    @GetMapping("/get/{collectorId}")
    public ResponseEntity<?> getCollector(@PathVariable UUID collectorId){
        return ResponseEntity.ok().body(collectorService.getCollector(collectorId));
    }
    @GetMapping("/get-all/{hubId}")
    public ResponseEntity<?> getAllCollectorsInHub(@PathVariable UUID hubId){
        return ResponseEntity.ok().body(collectorService.getAllCollectorsInHub(hubId));
    }



}
