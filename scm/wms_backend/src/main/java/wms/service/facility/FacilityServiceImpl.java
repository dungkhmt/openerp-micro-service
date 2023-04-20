package wms.service.facility;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.facility.FacilityDTO;
import wms.dto.facility.FacilityUpdateDTO;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.FacilityRepo;
import wms.repo.UserRepo;
import wms.service.BaseService;

@Service
@Slf4j
public class FacilityServiceImpl extends BaseService implements IFacilityService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private FacilityRepo facilityRepo;
    @Override
    public Facility createFacility(FacilityDTO facilityDTO) throws CustomException {
        if (facilityRepo.getFacilityByCode(facilityDTO.getCode()) != null) {
            throw caughtException(ErrorCode.ALREADY_EXIST.getCode(), "Exist customer with same code, can't create");
        }
        UserLogin user = userRepo.getUserByUserLoginId(facilityDTO.getCreatedBy());
        if (user == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this facility, can't create");
        }
        Facility newFacility = Facility.builder()
                .name(facilityDTO.getName())
                .code(facilityDTO.getCode().toUpperCase())
                .address(facilityDTO.getAddress())
                .status(facilityDTO.getStatus())
                .latitude(facilityDTO.getLatitude())
                .longitude(facilityDTO.getLongitude())
                .user(user)
                .build();
        return facilityRepo.save(newFacility);
    }

    @Override
    public ReturnPaginationDTO<Facility> getAllFacilities(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<Facility> facilities = facilityRepo.search(pageable);
        return getPaginationResult(facilities.getContent(), page, facilities.getTotalPages(), facilities.getTotalElements());
    }

    @Override
    public Facility getFacilityById(long id) {
        return facilityRepo.getFacilityById(id);
    }

    @Override
    public Facility getFacilityByCode(String code) {
        return facilityRepo.getFacilityByCode(code.toUpperCase());
    }

    @Override
    public Facility updateFacility(FacilityUpdateDTO facilityDTO, long id) throws CustomException {
        Facility facilityByCode = facilityRepo.getFacilityByCode(facilityDTO.getCode());
        if (facilityByCode != null && facilityByCode.getId() != id) {
            throw caughtException(ErrorCode.ALREADY_EXIST.getCode(), "Exist facility with same code, can't update");
        }
        Facility facilityToUpdate = facilityRepo.getFacilityById(id);
        facilityToUpdate.setName(facilityDTO.getName());
        facilityToUpdate.setCode(facilityToUpdate.getCode());
        facilityToUpdate.setAddress(facilityDTO.getAddress());
        facilityToUpdate.setStatus(facilityDTO.getStatus());
        facilityToUpdate.setLatitude(facilityToUpdate.getLatitude());
        facilityToUpdate.setLongitude(facilityToUpdate.getLongitude());
        // Don't update user_created
        return facilityRepo.save(facilityToUpdate);
    }

    @Override
    public void deleteFacilityById(long id) {
        facilityRepo.deleteById(id);
    }
}
