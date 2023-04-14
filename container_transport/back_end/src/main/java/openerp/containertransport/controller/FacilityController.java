package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.FacilityModel;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.service.FacilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/facility")
public class FacilityController {
    private FacilityService facilityService;

    @PostMapping("/create")
    public ResponseEntity<?> createFacility (FacilityModel facilityModel){
        Facility facility = facilityService.createFacility(facilityModel);
        return ResponseEntity.status(HttpStatus.OK).body(facility);
    }
}
