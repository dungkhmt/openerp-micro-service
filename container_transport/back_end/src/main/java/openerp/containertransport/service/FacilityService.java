package openerp.containertransport.service;

import openerp.containertransport.dto.FacilityModel;
import openerp.containertransport.entity.Facility;

public interface FacilityService {
    Facility createFacility(FacilityModel facilityModel);
}
