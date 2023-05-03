package wms.service.delivery_trip;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wms.algorithms.TruckDroneDeliverySolver;
import wms.algorithms.entity.Drone;
import wms.algorithms.entity.Truck;
import wms.algorithms.entity.TruckDroneDeliveryInput;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.delivery_trip.DeliveryTripDTO;
import wms.dto.product.ProductDTO;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.*;
import wms.service.BaseService;
import wms.service.delivery_bill.IDeliveryBillService;
import wms.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DeliveryTripServiceImpl extends BaseService implements IDeliveryTripService {
    private final ShipmentItemRepo shipmentItemRepo;
    private final FacilityRepo facilityRepo;
    private final DroneRepo droneRepo;
    private final TruckRepo truckRepo;
    private final SaleOrderRepo saleOrderRepo;
    private final DeliveryBillRepo deliveryBillRepo;
    private final ShipmentRepo shipmentRepo;
    private final DeliveryTripRepo deliveryTripRepo;
    private final UserRepo userRepo;

    @Autowired
    private IDeliveryBillService deliveryBillService;

    public DeliveryTripServiceImpl(UserRepo userRepo,
                                   DeliveryTripRepo deliveryTripRepo,
                                   ShipmentRepo shipmentRepo,
                                   DeliveryBillRepo deliveryBillRepo,
                                   SaleOrderRepo saleOrderRepo,
                                   TruckRepo truckRepo,
                                   DroneRepo droneRepo,
                                   FacilityRepo facilityRepo,
                                   ShipmentItemRepo shipmentItemRepo) {
        this.userRepo = userRepo;
        this.deliveryTripRepo = deliveryTripRepo;
        this.shipmentRepo = shipmentRepo;
        this.deliveryBillRepo = deliveryBillRepo;
        this.saleOrderRepo = saleOrderRepo;
        this.truckRepo = truckRepo;
        this.droneRepo = droneRepo;
        this.facilityRepo = facilityRepo;
        this.shipmentItemRepo = shipmentItemRepo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliveryTrip createDeliveryTrip(DeliveryTripDTO deliveryTripDTO, JwtAuthenticationToken token) throws CustomException {
        UserLogin createdBy = userRepo.getUserByUserLoginId(token.getName());
        UserLogin userInCharge = userRepo.getUserByUserLoginId(deliveryTripDTO.getUserInCharge());
        Shipment shipment = shipmentRepo.getShipmentByCode(deliveryTripDTO.getShipmentCode());
        if (createdBy == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this shipment, can't create");
        }
        if (shipment == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Not found shipment for this trip, can't create");
        }
        DeliveryTrip newDeliveryTrip = DeliveryTrip.builder()
                .code("TRIP" + GeneralUtils.generateCodeFromSysTime())
                .startedDate(GeneralUtils.convertFromStringToDate(deliveryTripDTO.getCreatedDate()))
                .creator(createdBy)
                .userInCharge(userInCharge)
                .shipment(shipment)
                .build();
        return deliveryTripRepo.save(newDeliveryTrip);
    }

    @Override
    public ReturnPaginationDTO<DeliveryTrip> getAllDeliveryTrips(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<DeliveryTrip> deliveryTrips = deliveryTripRepo.search(pageable);
        return getPaginationResult(deliveryTrips.getContent(), page, deliveryTrips.getTotalPages(), deliveryTrips.getTotalElements());
    }

    @Override
    public List<DeliveryTrip> getTripToAssignBill(String billCode) throws JsonProcessingException {
        DeliveryBill deliveryBill = deliveryBillRepo.getBillWithCode(billCode);
        SaleOrder saleOrder = deliveryBill.getSaleOrder();
        Customer customer = saleOrder.getCustomer();
        Facility facility = customer.getFacility();
        UserLogin facilityManager = facility.getManager();
        return deliveryTripRepo.getDeliveryTripsByStaff(facilityManager.getId());
    }

    @Override
    public DeliveryTrip getDeliveryTripById(long id) {
        return deliveryTripRepo.getDeliveryTripById(id);
    }

    @Override
    public DeliveryTrip getDeliveryTripByCode(String code) {
        return deliveryTripRepo.getDeliveryTripByCode(code);
    }

    @Override
    public DeliveryTrip updateDeliveryTrip(ProductDTO productDTO, long id) throws CustomException {
        return null;
    }

    @Override
    public void deleteDeliveryTripById(long id) {

    }

    @Override
    public ShipmentItem assignBillToTrip(DeliveryTripDTO deliveryTripDTO, JwtAuthenticationToken token) throws CustomException {
        return null;
    }

    @Override
    public void createTripRoute(String tripCode) throws CustomException {
        TruckDroneDeliveryInput input = new TruckDroneDeliveryInput();
        DeliveryTrip trip = deliveryTripRepo.getDeliveryTripByCode(tripCode);
        List<ShipmentItem> shipmentItems = shipmentItemRepo.getShipmentItemOfATrip(tripCode);
        UserLogin user = trip.getUserInCharge();
        // Set truck properties
        TruckEntity truckEntity = truckRepo.getTruckFromUser(user.getId());
        Truck truck = new Truck();
        truck.setID(truckEntity.getCode());
        truck.setCapacity(truckEntity.getCapacity());
        truck.setSpeed(truckEntity.getSpeed());
        truck.setWaitingCost(truckEntity.getWaitingCost());
        truck.setTransportCostPerUnit(truckEntity.getTransportCostPerUnit());
        input.setTruck(truck);
        // Set drone properties
        DroneEntity droneEntity = droneRepo.getDroneFromUser(user.getId());
        Drone drone = new Drone();
        drone.setCapacity(droneEntity.getCapacity());
        drone.setWaitingCost(droneEntity.getWaitingCost());
        drone.setDurationCapacity(droneEntity.getDurationTime());
        drone.setTransportCostPerUnit(droneEntity.getTransportCostPerUnit());
        drone.setSpeed(droneEntity.getSpeed());
        input.setDrone(drone);
        // Set depot info

        // Set customer location

        TruckDroneDeliverySolver heuristicSolver = new TruckDroneDeliverySolver(input);
        heuristicSolver.solve();
    }
}
