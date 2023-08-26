package wms.service.shipment;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.product.ProductDTO;
import wms.dto.shipment.*;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.ShipmentItemRepo;
import wms.repo.ShipmentRepo;
import wms.repo.UserRepo;
import wms.service.BaseService;
import wms.service.delivery_bill.IDeliveryBillService;
import wms.service.delivery_trip.IDeliveryTripService;
import wms.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShipmentServiceImpl extends BaseService implements IShipmentService {
    private final ShipmentItemRepo shipmentItemRepo;
    private final UserRepo userRepo;
    private final ShipmentRepo shipmentRepo;

    @Autowired
    private IDeliveryBillService deliveryBillService;

    @Autowired
    private IDeliveryTripService deliveryTripService;

    public ShipmentServiceImpl(ShipmentRepo shipmentRepo,
                               UserRepo userRepo,
                               ShipmentItemRepo shipmentItemRepo) {
        this.shipmentRepo = shipmentRepo;
        this.userRepo = userRepo;
        this.shipmentItemRepo = shipmentItemRepo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Shipment createShipment(ShipmentDTO shipmentDTO, JwtAuthenticationToken token) throws CustomException {
        UserRegister createdBy = userRepo.getUserByUserLoginId(token.getName());
        if (createdBy == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this shipment, can't create");
        }
        Shipment newShipment = Shipment.builder()
                .code("SHIP" + GeneralUtils.generateCodeFromSysTime())
                .title(shipmentDTO.getTitle())
                .maxSize(shipmentDTO.getMaxSize())
                .startedDate(GeneralUtils.convertFromStringToDate(shipmentDTO.getStartedDate()))
                .endedDate(GeneralUtils.convertFromStringToDate(shipmentDTO.getEndedDate()))
                .user(createdBy)
                .build();
        return shipmentRepo.save(newShipment);
    }

    @Override
    public ShipmentItem createShipmentItem(ShipmentItemDTO shipmentItemDTO) throws CustomException {
        DeliveryBill deliveryBill =  deliveryBillService.getBillByCode(shipmentItemDTO.getDeliveryBillCode());
        if (deliveryBill == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Can't find referenced bill, can't create");
        }
        ShipmentItem newShipmentItem = ShipmentItem.builder()
                .code("SHIT" + GeneralUtils.generateCodeFromSysTime())
                .quantity(shipmentItemDTO.getQuantity())
                .deliveryBill(deliveryBill)
                .deliveryBillItemSeqId(shipmentItemDTO.getDeliveryBillItemSeqId())
                .build();
        return shipmentItemRepo.save(newShipmentItem);
    }

    @Override
    public Shipment updateShipment(ShipmentDTO shipmentDTO, Long id) throws CustomException {
        Shipment shipment = shipmentRepo.getShipmentById(id);
        if (shipment == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(),  "Can not find shipment with this id: " + id);
        }
        shipment.setTitle(shipmentDTO.getTitle());
        shipment.setMaxSize(shipmentDTO.getMaxSize());
        shipment.setStartedDate(GeneralUtils.convertFromStringToDate(shipmentDTO.getStartedDate()));
        shipment.setEndedDate(GeneralUtils.convertFromStringToDate(shipmentDTO.getEndedDate()));
        return shipmentRepo.save(shipment);
    }

    @Override
    @Transactional
    public void assignShipmentItem(AssignedItemDTO assignedItemDTO) throws CustomException {
        ShipmentItem shipmentItem = getShipmentItemByCode(assignedItemDTO.getShipmentItemCode());
        DeliveryTrip assignedTrip = deliveryTripService.getDeliveryTripByCode(assignedItemDTO.getTripCode());
        if (shipmentItem == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Shipment item not exists");
        }
        if (assignedTrip == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Can't find assigned trip item not exists");
        }
        Shipment mappedShipment = assignedTrip.getShipment();
        shipmentItem.setShipment(mappedShipment);
        shipmentItem.setDeliveryTrip(assignedTrip);
        shipmentItem.setTripSeqId(GeneralUtils.generateCodeFromSysTime());
        shipmentItemRepo.save(shipmentItem);
    }
    @Override
    @Transactional
    public void unassignShipmentItem(AssignedItemDTO assignedItemDTO) throws CustomException {
        ShipmentItem shipmentItem = getShipmentItemByCode(assignedItemDTO.getShipmentItemCode());
        DeliveryTrip assignedTrip = deliveryTripService.getDeliveryTripByCode(assignedItemDTO.getTripCode());
        if (shipmentItem == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Shipment item not exists");
        }
        if (assignedTrip == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Can't find assigned trip item not exists");
        }
        shipmentItem.setShipment(null);
        shipmentItem.setDeliveryTrip(null);
        shipmentItem.setTripSeqId(null);
        shipmentItemRepo.save(shipmentItem);
    }
    @Override
    public ReturnPaginationDTO<Shipment> getAllShipments(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<Shipment> shipments = shipmentRepo.search(pageable);
        return getPaginationResult(shipments.getContent(), page, shipments.getTotalPages(), shipments.getTotalElements());
    }

    @Override
    public ReturnPaginationDTO<ShipmentItem> getAllShipmentItems(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<ShipmentItem> shipmentItems = shipmentItemRepo.search(pageable);
        return getPaginationResult(shipmentItems.getContent(), page, shipmentItems.getTotalPages(), shipmentItems.getTotalElements());
    }

    @Override
    public ReturnPaginationDTO<ReturnShipmentItemDTO> getAllItemOfTrip(int page, int pageSize, String sortField, boolean isSortAsc, String tripCode) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<ShipmentItem> shipmentItems = shipmentItemRepo.getShipmentItemOfATrip(pageable, tripCode);
        Page<ReturnShipmentItemDTO> shipmentItemDTOS = mapPageToDTO(shipmentItems);
        return getPaginationResult(shipmentItemDTOS.getContent(), page, shipmentItemDTOS.getTotalPages(), shipmentItemDTOS.getTotalElements());
    }
    public Page<ReturnShipmentItemDTO> mapPageToDTO(Page<ShipmentItem> entityPage) {
        List<ShipmentItem> entityList = entityPage.getContent();
        List<ReturnShipmentItemDTO> dtoList = new ArrayList<>();

        for (ShipmentItem entity : entityList) {
            ReturnShipmentItemDTO dto = mapEntityToDTO(entity);
            dtoList.add(dto);
        }

        Pageable pageable = entityPage.getPageable();
        long totalElements = entityPage.getTotalElements();
        int pageNumber = entityPage.getNumber();
        int pageSize = entityPage.getSize();

        return new PageImpl<>(dtoList, pageable, totalElements);
    }
    public ReturnShipmentItemDTO mapEntityToDTO(ShipmentItem entity) {
         ReturnShipmentItemDTO dto = new ReturnShipmentItemDTO();
         dto.setShipment(entity.getShipment());
         dto.setCode(entity.getCode());
         dto.setDeliveryBill(entity.getDeliveryBill());
         dto.setDeliveryTrip(entity.getDeliveryTrip());
         dto.setQuantity(entity.getQuantity());
         dto.setTripSeqId(entity.getTripSeqId());
         List<DeliveryBillItem> dbillItem = entity.getDeliveryBill().getDeliveryBillItems().stream().filter(item ->
                 item.getSeqId().equals(entity.getDeliveryBillItemSeqId())).collect(Collectors.toList());
         dto.setProductName(dbillItem.isEmpty() ? "" : dbillItem.get(0).getProduct().getName());
         dto.setCreatedDate(entity.getCreatedDate());
         dto.setId(entity.getId());
         dto.setIsDeleted(entity.getDeleted());
         return dto;
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
    public ShipmentItem getShipmentItemById(long id) {
        return shipmentItemRepo.getShipmentItemById(id);
    }

    @Override
    public ShipmentItem getShipmentItemByCode(String code) {
        return shipmentItemRepo.getShipmentItemByCode(code);
    }

    @Override
    public Shipment updateShipment(ProductDTO productDTO, long id) throws CustomException {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteShipmentById(long id) {
        Shipment currShipment = shipmentRepo.getShipmentById(id);
        currShipment.setDeleted(1);
        shipmentRepo.save(currShipment);
    }
}
