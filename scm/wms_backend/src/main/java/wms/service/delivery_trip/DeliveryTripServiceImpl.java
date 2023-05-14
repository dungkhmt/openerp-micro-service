package wms.service.delivery_trip;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wms.algorithms.TruckDroneDeliverySolver;
import wms.algorithms.entity.*;
import wms.algorithms.utils.Utils;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.delivery_trip.DeliveryTripDTO;
import wms.dto.delivery_trip.TripRouteDTO;
import wms.dto.product.ProductDTO;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.*;
import wms.service.BaseService;
import wms.service.delivery_bill.IDeliveryBillService;
import wms.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private MongoTemplate mongoTemplate;
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
        Facility facility = facilityRepo.getFacilityByCode(deliveryTripDTO.getFacilityCode());
        if (createdBy == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this shipment, can't create");
        }
        if (shipment == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Not found shipment for this trip, can't create");
        }
        if (facility == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Not found facility for this trip, can't create");
        }
        DeliveryTrip newDeliveryTrip = DeliveryTrip.builder()
                .code("TRIP" + GeneralUtils.generateCodeFromSysTime())
                .facility(facility)
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
        return deliveryTripRepo.getDeliveryTripsByFacility(facility.getCode());
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
    public void createTripRoute(TripRouteDTO tripRouteDTO) throws CustomException {
        TruckDroneDeliveryInput input = new TruckDroneDeliveryInput();
        DeliveryTrip trip = deliveryTripRepo.getDeliveryTripByCode(tripRouteDTO.getTripCode());
        List<ShipmentItem> shipmentItems = shipmentItemRepo.getShipmentItemOfATrip(tripRouteDTO.getTripCode());
        List<Node> points = new ArrayList<>(); // All coordinations got
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
        drone.setID(droneEntity.getCode());
        drone.setCapacity(droneEntity.getCapacity());
        drone.setWaitingCost(droneEntity.getWaitingCost());
        drone.setDurationCapacity(droneEntity.getDurationTime());
        drone.setTransportCostPerUnit(droneEntity.getTransportCostPerUnit());
        drone.setSpeed(droneEntity.getSpeed());
        input.setDrone(drone);
        // Set depot info
        Depot depot = new Depot();
        depot.setLocationID("depot");
        input.setDepot(depot);
        Node depotNode = new Node(Double.parseDouble(trip.getFacility().getLatitude()),
                Double.parseDouble(trip.getFacility().getLongitude()), "depot");
        points.add(depotNode);
        // Set customer location
        Set<Customer> customers = new HashSet<>();
        for (ShipmentItem shipmentItem : shipmentItems) {
            Customer customer = shipmentItem.getDeliveryBill().getSaleOrder().getCustomer();
            customers.add(customer);
        }
        int count = 0;
        for (Customer customer: customers) {
            count++;
            Node node = new Node();
            node.setX(Double.parseDouble(customer.getLatitude()));
            node.setY(Double.parseDouble(customer.getLongitude()));
            node.setName("C" + count);
            points.add(node);
        }

        input.setLocations(points);
        List<List<DistanceElement>> listDistances = new ArrayList<>();
        List<Request> listCustomerRequests = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            List<DistanceElement> distances = new ArrayList<>();
            for (int j = 0; j < points.size(); j++) {
                DistanceElement distanceElement = new DistanceElement();
                distanceElement.setId(i + "_" + j);
                distanceElement.setFromLocationId(points.get(i).getName());
                distanceElement.setToLocationId(points.get(j).getName());
//              double distance = Utils.calculateEuclideanDistance(points.get(i), points.get(j));
                double distance = Utils.calculateCoordinationDistance(points.get(i).getX(), points.get(i).getY(),
                        points.get(j).getX(), points.get(j).getY());
                distanceElement.setDistance(distance);
                distanceElement.setTravelTime(distance / input.getTruck().getSpeed());
                distanceElement.setFlyingTime(distance / input.getDrone().getSpeed());
                distances.add(distanceElement);
            }
            listDistances.add(distances);
            if (i == 0) continue;
            Request newRequest = new Request();
            newRequest.setEarliestTime("10h");
            newRequest.setLatestTime("11h");
            newRequest.setWeight(100);
            newRequest.setID(points.get(i).getName());
            newRequest.setLocationID(points.get(i).getName());
            listCustomerRequests.add(newRequest);
        }
        input.setDistances(listDistances);
        input.setRequests(listCustomerRequests);
        TruckDroneDeliverySolver heuristicSolver = new TruckDroneDeliverySolver(input);
        heuristicSolver.solve();

        TruckDroneDeliverySolutionOutput finalSolution = heuristicSolver.getSolution();
        List<Node> truckRoute = finalSolution.convertTruckRouteToNode(finalSolution.getTruckRoute());
        List<List<Node>> droneRoutes = new ArrayList<>();
        for (DroneRoute ele: finalSolution.getDroneRoutes()) {
            List<Node> droneRoute = finalSolution.convertDroneRouteToNode(ele);
            droneRoutes.add(droneRoute);
        }
        for (Node node : truckRoute) {
            log.info("Truck node info {} ({}, {})", node.getName(), node.getX(), node.getY());
        }
        for (List<Node> droneNode : droneRoutes) {
            log.info("====================================");
            for (Node node : droneNode) {
                log.info("Drone node info {} ({}, {})", node.getName(), node.getX(), node.getY());
            }
        }

        RouteSchedulingOutput output = new RouteSchedulingOutput();
        output.setDroneRoutes(finalSolution.getDroneRoutes());
        output.setTruckRoute(finalSolution.getTruckRoute());
        output.setTotalCost(finalSolution.getTotalCost());
        output.setTotalTruckCost(finalSolution.getTotalTruckCost());
        output.setTotalDroneCost(finalSolution.getTotalDroneCost());
        output.setTotalTruckWait(finalSolution.getTotalTruckWait());
        output.setTotalDroneWait(finalSolution.getTotalDroneWait());
        output.setTripCode(trip.getCode());
        mongoTemplate.save(output);
    }

    @Override
    public RouteSchedulingOutput getTripRoute(String tripCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tripCode").is(tripCode));
        return mongoTemplate.findOne(query, RouteSchedulingOutput.class);
    }
}
