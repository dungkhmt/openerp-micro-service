package openerp.containertransport.service;

import openerp.containertransport.dto.FacilityFilterRequestDTO;
import openerp.containertransport.dto.FacilityModel;
import openerp.containertransport.entity.Facility;

import java.util.List;

public interface FacilityService {
    Facility createFacility(FacilityModel facilityModel) throws Exception;
    FacilityModel getFacilityById(long id);
    List<FacilityModel> filterFacility(FacilityFilterRequestDTO facilityFilterRequestDTO);
    FacilityModel updateFacility(FacilityModel facilityModel);
}
