package wms.service.shipment;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.product.ProductDTO;
import wms.dto.shipment.ShipmentDTO;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.ShipmentRepo;
import wms.repo.UserRepo;
import wms.service.BaseService;
import wms.utils.GeneralUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class ShipmentServiceImpl extends BaseService implements IShipmentService {
    private final UserRepo userRepo;
    private final ShipmentRepo shipmentRepo;

    public ShipmentServiceImpl(ShipmentRepo shipmentRepo,
                               UserRepo userRepo) {
        this.shipmentRepo = shipmentRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Shipment createShipment(ShipmentDTO shipmentDTO, JwtAuthenticationToken token) throws CustomException {
        UserLogin createdBy = userRepo.getUserByUserLoginId(token.getName());
        if (createdBy == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this shipment, can't create");
        }
        Shipment newShipment = Shipment.builder()
                .code("SHIP" + GeneralUtils.generateCodeFromSysTime())
                .title(shipmentDTO.getTitle())
                .maxSize(shipmentDTO.getMaxSize())
                .startedDate(ZonedDateTime.parse(shipmentDTO.getStartedDate(), DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                .endedDate(ZonedDateTime.parse(shipmentDTO.getEndedDate(), DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                .user(createdBy)
                .build();
        return shipmentRepo.save(newShipment);
    }

    @Override
    public ReturnPaginationDTO<Shipment> getAllShipments(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        return null;
    }

    @Override
    public Shipment getShipmentById(long id) {
        return shipmentRepo.getShipmentById(id);
    }

    @Override
    public Shipment getShipmentByCode(String code) {
        return shipmentRepo.getShipmentByCode(code);
    }

    @Override
    public Shipment updateShipment(ProductDTO productDTO, long id) throws CustomException {
        return null;
    }

    @Override
    public void deleteShipmentById(long id) {
        shipmentRepo.deleteById(id);
    }
}
