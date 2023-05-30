package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.MetaData;
import openerp.containertransport.dto.FacilityFilterRequestDTO;
import openerp.containertransport.dto.FacilityFilterRes;
import openerp.containertransport.dto.FacilityModel;
import openerp.containertransport.dto.metaData.MetaDTO;
import openerp.containertransport.dto.metaData.ResponseMetaData;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.service.FacilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/facility")
public class FacilityController {
    private final FacilityService facilityService;

    @PostMapping("/create")
    public ResponseEntity<?> createFacility (@RequestBody FacilityModel facilityModel) throws Exception {
        Facility facility = facilityService.createFacility(facilityModel);
        return ResponseEntity.status(HttpStatus.OK).body(facility);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFacilityById(@PathVariable long id) {
        FacilityModel facilityModel = facilityService.getFacilityById(id);
        return ResponseEntity.status(HttpStatus.OK).body(facilityModel);
    }

    @PostMapping("/")
    public ResponseEntity<?> filterFacility(@RequestBody FacilityFilterRequestDTO facilityFilterRequestDTO) {
        FacilityFilterRes facilityModels = facilityService.filterFacility(facilityFilterRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), facilityModels));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateFacility(@RequestBody FacilityModel facilityModel) {
        FacilityModel facilityModelUpdate = facilityService.updateFacility(facilityModel);
        return ResponseEntity.status(HttpStatus.OK).body(facilityModelUpdate);
    }
}
