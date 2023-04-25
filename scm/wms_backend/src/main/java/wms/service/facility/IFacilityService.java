package wms.service.facility;

import com.fasterxml.jackson.core.JsonProcessingException;
import wms.dto.ReturnPaginationDTO;
import wms.dto.facility.ExportFromFacilityDTO;
import wms.dto.facility.FacilityDTO;
import wms.dto.facility.FacilityUpdateDTO;
import wms.dto.facility.ImportToFacilityDTO;
import wms.entity.*;
import wms.exception.CustomException;

public interface IFacilityService {
    Facility createFacility(FacilityDTO facilityDTO) throws CustomException;
    ReturnPaginationDTO<Facility> getAllFacilities(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    Facility getFacilityById(long id);
    Facility getFacilityByCode(String code);
    ReturnPaginationDTO<ProductFacility> getInventoryItems(int page, int pageSize, String sortField, boolean isSortAsc, String facilityCode) throws JsonProcessingException;
    Facility updateFacility(FacilityUpdateDTO facilityDTO, long id) throws CustomException;
    void importToFacilityWithOrder(ImportToFacilityDTO importToFacilityDTO) throws CustomException;
    ReceiptBill getReceiptBillForOrderByCode(String orderCode, String code);
    void exportFromFacility(ExportFromFacilityDTO exportFromFacilityDTO) throws CustomException;
    void deleteFacilityById(long id);
}