package wms.service.facility;

import com.fasterxml.jackson.core.JsonProcessingException;
import wms.dto.ReturnPaginationDTO;
import wms.dto.facility.FacilityDTO;
import wms.dto.facility.FacilityUpdateDTO;
import wms.dto.product.ProductDTO;
import wms.entity.Facility;
import wms.entity.ProductEntity;
import wms.exception.CustomException;

public interface IFacilityService {
    Facility createFacility(FacilityDTO facilityDTO) throws CustomException;
    ReturnPaginationDTO<Facility> getAllFacilities(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    Facility getFacilityById(long id);
    Facility getFacilityByCode(String code);
    Facility updateFacility(FacilityUpdateDTO facilityDTO, long id) throws CustomException;
    void deleteFacilityById(long id);
}
