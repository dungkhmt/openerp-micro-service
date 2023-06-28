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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/facility")
public class FacilityController {
    private final FacilityService facilityService;

    @PostMapping("/create")
    public ResponseEntity<?> createFacility (@RequestBody FacilityModel facilityModel, JwtAuthenticationToken token) throws Exception {
        String username = token.getName();
        Facility facility = facilityService.createFacility(facilityModel, username);
        return ResponseEntity.status(HttpStatus.OK).body(facility);
    }

    @GetMapping("/{uid}")
    public ResponseEntity<?> getFacilityById(@PathVariable String uid) {
        FacilityModel facilityModel = facilityService.getFacilityByUid(uid);
        return ResponseEntity.status(HttpStatus.OK).body(facilityModel);
    }

    @PostMapping("/owner")
    public ResponseEntity<?> filterFacilityOwner(@RequestBody FacilityFilterRequestDTO facilityFilterRequestDTO, JwtAuthenticationToken token) {
        String username = token.getName();
        List<String> roleIds = token
                .getAuthorities()
                .stream()
                .filter(grantedAuthority -> !grantedAuthority
                        .getAuthority()
                        .startsWith("ROLE_GR")) // remove all composite roles
                .map(grantedAuthority -> { // convert role to permission
                    String roleId = grantedAuthority.getAuthority().substring(5); // remove prefix "ROLE_"
                    return roleId;
                })
                .collect(Collectors.toList());
        if(roleIds.contains("TMS_CUSTOMER")) {
            facilityFilterRequestDTO.setOwner(username);
        }
        FacilityFilterRes facilityModels = facilityService.filterFacility(facilityFilterRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), facilityModels));
    }

    @PostMapping("/")
    public ResponseEntity<?> filterFacility(@RequestBody FacilityFilterRequestDTO facilityFilterRequestDTO) {
        FacilityFilterRes facilityModels = facilityService.filterFacility(facilityFilterRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), facilityModels));
    }

    @PutMapping("/update/{uid}")
    public ResponseEntity<?> updateFacility(@PathVariable String uid, @RequestBody FacilityModel facilityModel) {
        FacilityModel facilityModelUpdate = facilityService.updateFacility(facilityModel, uid);
        return ResponseEntity.status(HttpStatus.OK).body(facilityModelUpdate);
    }

    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<?> deleteFacility(@PathVariable String uid) {
        FacilityModel facilityDelete = facilityService.deleteFacility(uid);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), facilityDelete));
    }
}
