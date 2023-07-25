package wms.service.facility;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.multipart.MultipartFile;
import wms.dto.ReturnPaginationDTO;
import wms.dto.facility.ExportFromFacilityDTO;
import wms.dto.facility.FacilityDTO;
import wms.dto.facility.FacilityUpdateDTO;
import wms.dto.facility.ImportToFacilityDTO;
import wms.entity.*;
import wms.exception.CustomException;

import java.io.IOException;
import java.util.List;

public interface IFacilityService {
    void createFacilityFromFile(MultipartFile file, JwtAuthenticationToken token) throws IOException, CustomException;
    Facility createFacility(FacilityDTO facilityDTO, JwtAuthenticationToken token) throws CustomException;
    ReturnPaginationDTO<Facility> getAllFacilities(int page, int pageSize, String sortField, boolean isSortAsc,
    String facilityName, String status, String createdBy, String managedBy, String textSearch
    ) throws JsonProcessingException;
    List<Facility> getAllWithoutPaging();
    Facility getFacilityById(long id);
    Facility getFacilityByCode(String code);
    ReturnPaginationDTO<ProductFacility> getInventoryItems(int page, int pageSize, String sortField, boolean isSortAsc, String facilityCode) throws JsonProcessingException;
    Facility updateFacility(FacilityUpdateDTO facilityDTO, Long id) throws CustomException;
    void importToFacilityWithOrder(ImportToFacilityDTO importToFacilityDTO) throws CustomException;
    ReceiptBill getReceiptBillForOrderByCode(String orderCode, String code);
    void exportFromFacility(ExportFromFacilityDTO exportFromFacilityDTO) throws CustomException;
    void deleteFacilityById(long id);
    void assignStaff(String staffCode, String facilityCode) throws CustomException;
    List<Customer> getFacilityCustomer(String facilityCode);
    ReturnPaginationDTO<Customer> getAllFacilityCustomer(int page, int pageSize, String sortField, boolean isSortAsc, String facilityCode) throws JsonProcessingException;
}
