package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.containertransport.dto.FacilityModel;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.repo.FacilityRepo;
import openerp.containertransport.service.FacilityService;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepo facilityRepo;

    @Override
    public Facility createFacility(FacilityModel facilityModel) {
        Facility facility = new Facility();
        return facility;
    }
}
