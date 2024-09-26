package openerp.containertransport.service;

import openerp.containertransport.dto.FacilityFilterRequestDTO;
import openerp.containertransport.dto.FacilityFilterRes;
import openerp.containertransport.dto.FacilityModel;
import openerp.containertransport.entity.Facility;

import java.util.List;

public interface FacilityService {
    Facility createFacility(FacilityModel facilityModel, String username) throws Exception;
    FacilityModel getFacilityByUid(String uid);
    FacilityFilterRes filterFacility(FacilityFilterRequestDTO facilityFilterRequestDTO);
    FacilityModel updateFacility(FacilityModel facilityModel, String uid);
    FacilityModel deleteFacility(String uid);
}
