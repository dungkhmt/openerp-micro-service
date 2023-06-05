package openerp.containertransport.algorithms.solver;

import lombok.extern.slf4j.Slf4j;
import openerp.containertransport.algorithms.constants.Constants;
import openerp.containertransport.algorithms.entity.*;
import openerp.containertransport.algorithms.entity.output.InfoRemoveRequest;
import openerp.containertransport.algorithms.entity.output.TransportContainerSolutionOutput;
import openerp.containertransport.algorithms.entity.output.TripOutput;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HeuristicSolver {

    private List<Request> requestList;
    private List<Request> requestListScheduler;
    private List<Request> requestFalse;
    private Map<Integer, Truck> trucks = new HashMap<>(); // id, truck
    private Map<Integer, Container> containers = new HashMap<>(); // id, container
    private Map<Integer, Trailer> trailers = new HashMap<>(); // id, trailer
    private List<DistanceElement> distanceElements; // toNode -> fromNode ->
    private Map<DistantKey, DistanceElement> distanceElementMap = new HashMap<>();
    private List<DepotTruck> depotTrucks;
    private List<DepotTrailer> depotTrailers;
    private Long startTime;
    private TransportContainerSolutionOutput transportContainerSolutionOutput;

    public TransportContainerSolutionOutput solve (TransportContainerInput input){

//        this.startTime = input.getStartTime();

        convertInput(input);
        greedyAlgorithmTms();
        return transportContainerSolutionOutput;
    }

    public void convertInput(TransportContainerInput input) {
        // truck
        List<Truck> truckListInput = input.getTrucks();
        truckListInput.forEach((truck) -> {
            this.trucks.put(truck.getTruckID(), truck);
        });

        // trailer
        List<Trailer> trailerListInput = input.getTrailers();
        trailerListInput.forEach((trailer) -> {
            this.trailers.put(trailer.getTrailerID(), trailer);
        });

        // distant
        List<DistanceElement> distanceElementsInput = input.getDistances();
        distanceElementsInput.forEach((distanceElement) -> {
            DistantKey distantKey = DistantKey.builder()
                    .fromFacility(distanceElement.getFromFacility())
                    .toFacility(distanceElement.getToFacility())
                    .build();
            this.distanceElementMap.put(distantKey, distanceElement);
        });

        // depot truck
        this.depotTrucks = input.getDepotTruck();

        // depot trailer
        this.depotTrailers = input.getDepotTrailer();

    }

    public void greedyAlgorithmTms() {
        initRouters();
        createRouter();
        int loop = 5;

        BigDecimal bestTotalDistant = new BigDecimal(String.valueOf(transportContainerSolutionOutput.getTotalDistant()));
        while (loop > 0) {


            // Find request when remove it, return total distant min (tong quang duong giam nhat)
            InfoRemoveRequest infoRemoveRequest = removeRequestInTrip(); // cap nhat distant and router trong tmp

            // Add request vao cho thich hop sao cho tong quang duong la nho nhat
            Request requestAdd = requestList.stream().filter((item) -> item.getRequestId() == infoRemoveRequest.getRequestId()).findFirst().get();
            Point pick = createdPointFromRequest(Constants.ACTION.PICKUP_CONTAINER.getAction(), requestAdd);
            Point delivery = createdPointFromRequest(Constants.ACTION.DELIVERY_CONTAINER.getAction(), requestAdd);


            // approval each router
            for(Truck truck : trucks.values().toArray(new Truck[0])) {
                log.info("add request = {} after removed in router of truck = {}", requestAdd.getOrderCode(), truck.getTruckID());
                TripOutput tripOutputTmp = insertRequest(truck, requestAdd, pick, delivery);

                // Must insert to check
                List<Point> pointsBeforeInsert = transportContainerSolutionOutput.getTripOutputsTmp().get(truck.getTruckID()).getPoints();
                List<Point> pointsAfterInsert = tripOutputTmp.getPoints();

                if(pointsBeforeInsert.size() < pointsAfterInsert.size()) {

                    BigDecimal distantInTripTmp = transportContainerSolutionOutput.getTripOutputsTmp().get(truck.getTruckID()).getTotalDistant();
                    BigDecimal distantAfterSwitch = transportContainerSolutionOutput.getTotalDistantTmp().subtract(distantInTripTmp).add(tripOutputTmp.getTotalDistantTmp());
                    if(distantAfterSwitch.compareTo(bestTotalDistant) < 0) {
                        // update best solution
                        bestTotalDistant = distantAfterSwitch;
                        transportContainerSolutionOutput.setTotalDistant(distantAfterSwitch);
                        if(truck.getTruckID() != infoRemoveRequest.getTruckId()) {
                            TripOutput tripOutputInRouterRemoved = transportContainerSolutionOutput.getTripOutputsTmp().get(infoRemoveRequest.getTruckId());
                            transportContainerSolutionOutput.getTripOutputs().put(infoRemoveRequest.getTruckId(), tripOutputInRouterRemoved);
                        }
                        transportContainerSolutionOutput.getTripOutputs().put(truck.getTruckID(), tripOutputTmp);
                    }
                }
            }
            // update solutionTmp
            updateSolutionTmp();

            loop -= 1;
        }
    }
    public void createRouter() {
        log.info("Create init solution");
        for(Request request : this.requestList) {
            log.info("create router with request: ", request.getOrderCode());
            Point pick = createdPointFromRequest(Constants.ACTION.PICKUP_CONTAINER.getAction(), request);
            Point delivery = createdPointFromRequest(Constants.ACTION.DELIVERY_CONTAINER.getAction(), request);

            BigDecimal distantSolution = this.transportContainerSolutionOutput.getTotalDistant();
            BigDecimal distantSolutionLoop = BigDecimal.valueOf(Double.MAX_VALUE);
            int truckSelect = 0;
            // duyet tung router
            for(Truck truck : trucks.values().toArray(new Truck[0])) {
                log.info("Try add request = {} in router of truck = {}", request.getOrderCode(), truck.getTruckID());
                TripOutput tripOutputBefore  = this.transportContainerSolutionOutput.getTripOutputs().get(truck.getTruckID());
                BigDecimal distantTrip = this.transportContainerSolutionOutput.getTripOutputs().get(truck.getTruckID()).getTotalDistant();

                TripOutput tripOutputTmp = insertRequest(truck, request, pick, delivery);
                if(tripOutputBefore.getPoints().size() < tripOutputTmp.getPoints().size()) {
                    BigDecimal distantSolutionTmp = distantSolution.subtract(distantTrip).add(tripOutputTmp.getTotalDistant());
                    if(distantSolutionTmp.compareTo(distantSolutionLoop) < 0) {
                        distantSolutionLoop = distantSolutionTmp;
                        truckSelect = truck.getTruckID();
                        this.transportContainerSolutionOutput.getTripOutputs().put(truck.getTruckID(), tripOutputTmp);
                    }
                }
            }
            for (Truck truck : this.trucks.values().toArray(new Truck[0])) {
                if(truck.getTruckID() != truckSelect) {
                    TripOutput tripOutput = this.transportContainerSolutionOutput.getTripOutputsTmp().get(truck.getTruckID());
                    this.transportContainerSolutionOutput.getTripOutputs().put(truck.getTruckID(), tripOutput);
                }
            }
            this.transportContainerSolutionOutput.setTotalDistant(distantSolutionLoop);
            this.transportContainerSolutionOutput.setTotalDistantTmp(distantSolutionLoop);
        }
    }

    public Point createdPointFromRequest(String action, Request request) {
        Point point = new Point();
        point.setAction(action);
        point.setFacilityId( action.equals(Constants.ACTION.PICKUP_CONTAINER.getAction()) ? request.getFromLocationID() : request.getToLocationID());
        point.setOrderCode(request.getOrderCode());
        point.setContainerId(request.getContainerID());
        point.setSizeContainer(request.getWeightContainer());
        point.setType("Order");

        return point;
    }

    public void initRouters() {
        for (Truck truck : this.trucks.values().toArray(new Truck[0])) {
            TripOutput tripOutput = new TripOutput();
            tripOutput.setTotalTime(BigDecimal.valueOf(Double.MAX_VALUE));
            tripOutput.setTotalDistant(BigDecimal.valueOf(Double.MAX_VALUE));

            Map<Integer, TripOutput> tripOutputs = this.transportContainerSolutionOutput.getTripOutputs();
            tripOutputs.put(truck.getTruckID(), tripOutput);

            this.transportContainerSolutionOutput.setTripOutputs(tripOutputs);
            this.transportContainerSolutionOutput.setTripOutputsTmp(tripOutputs);
            this.transportContainerSolutionOutput.setTotalTime(BigDecimal.valueOf(Double.MAX_VALUE));
            this.transportContainerSolutionOutput.setTotalDistant(BigDecimal.valueOf(Double.MAX_VALUE));
        }
    }

    public TripOutput insertRequest(Truck truck, Request request, Point pick, Point delivery) {

        BigDecimal distantLoopATruck = BigDecimal.valueOf(Long.MAX_VALUE);
        TripOutput tripOutputLoopTmp = this.transportContainerSolutionOutput.getTripOutputsTmp().get(truck.getTruckID());

        // neu router rong
        if(transportContainerSolutionOutput.getTripOutputs().get(truck.getTruckID()).getPoints().isEmpty()) {
            // Lay ra diem trailer tot nhat
            Point pickTrailer = getBestTrailer(truck.getLocationId(), pick.getFacilityId());
            pickTrailer.setNbTrailer(1);

            TripOutput tripOutput = transportContainerSolutionOutput.getTripOutputs().get(truck.getTruckID());
            List<Point> pointsInTrip = tripOutput.getPoints();
            pointsInTrip.add(pickTrailer);

            // cap nhat trang thai point
            pick.setNbTrailer(1);
            pick.setWeightContainer(request.getWeightContainer());
            pointsInTrip.add(pick);

            delivery.setNbTrailer(1);
            delivery.setWeightContainer(0);
            pointsInTrip.add(delivery);

            // tinh toan lai quang duong
            BigDecimal distantTmp = calcDistantRouter(truck.getLocationId(), pointsInTrip);

            if(distantTmp.compareTo(distantLoopATruck) < 0) {
                distantLoopATruck = distantTmp;
                tripOutput.setPoints(pointsInTrip);
                tripOutputLoopTmp = tripOutput;
            }
        }
        else {
            // remove pickTrailer truoc do
            TripOutput tripOutput = removePickTrailer(tripOutputLoopTmp);

            int nbPoint = tripOutput.getPoints().size();
            for (int x = 0; x <= nbPoint; x++) {
                for (int y = x+1; y <= nbPoint; y++) {
//                    TripOutput tripOutput = transportContainerSolutionOutput.getTripOutputs().get(truck.getTruckID());
                    List<Point> pointsInTrip = tripOutput.getPoints();
                    pointsInTrip.add(x, pick);
                    pointsInTrip.add(y, delivery);

                    Boolean checkValid = checkValidateAddPoint(x, y, pointsInTrip);
                    if (!checkValid) {
                        continue;
                    }

                    // insert Pickup Trailer point
                    List<Point> pointsInTripAfterAdd = insertPickTrailer(pointsInTrip, truck.getLocationId());

                    // Calc again router
                    BigDecimal distantTmp = calcDistantRouter(truck.getLocationId(), pointsInTripAfterAdd);

                    // Update weightContainer everyone Point
                    // check
                    updateWeightContainer(pointsInTrip, x, y);

                    if(distantTmp.compareTo(distantLoopATruck) < 0) {
                        distantLoopATruck = distantTmp;
                        tripOutput.setPoints(pointsInTripAfterAdd);
                        tripOutput.setTotalDistant(distantTmp);
                        tripOutputLoopTmp = tripOutput;
                    }
                }
            }
        }

        return tripOutputLoopTmp;
    }

    public BigDecimal calcDistantRouter(int truckFacility, List<Point> points) {
        AtomicInteger prevFacility = new AtomicInteger(truckFacility);
        BigDecimal totalDistantTmp = BigDecimal.valueOf(0);

        points.forEach((point) -> {
            DistantKey distantKey = DistantKey.builder()
                    .fromFacility(prevFacility.get())
                    .toFacility(point.getFacilityId())
                    .build();

            totalDistantTmp.add(distanceElementMap.get(distantKey).getDistance());
            prevFacility.set(point.getFacilityId());
        });

        return totalDistantTmp;
    }

    // can check lai
    public List<Point> updateWeightContainer (List<Point> points, int x, int y) {
        if (x == 0) {
            int sizeContainer = points.get(1).getSizeContainer();
            points.get(1).setWeightContainer(sizeContainer);
            for(int i = 2; i<= y; i++) {
                if(points.get(i).getWeightContainer() != null) {
                    points.get(i).setWeightContainer(points.get(i).getWeightContainer() + sizeContainer);
                }
            }
        }
        else {
            int sizeContainer = points.get(x).getSizeContainer();
            for(int i = x; i<= y; i++) {
                if(points.get(i).getWeightContainer() != null) {
                    points.get(i).setWeightContainer(points.get(i).getWeightContainer() + sizeContainer);
                }
            }
        }
        return points;
    }

    public Point getBestTrailer(int fromFacility, int toFacility) {

        BigDecimal distant = BigDecimal.valueOf(Double.MAX_VALUE);
        Point pickTrailer = new Point();

        pickTrailer.setAction(Constants.ACTION.PICKUP_TRAILER.getAction());
        pickTrailer.setType("Trailer");
        for(Trailer trailer : trailers.values().toArray(new Trailer[0])) {
            BigDecimal distantTmp = BigDecimal.valueOf(0);
            distanceElements.forEach((distanceElement) -> {
                // distant from fromfacility to trailer depot
                if(distanceElement.getFromFacility() == (fromFacility) &&
                        distanceElement.getToFacility() == trailer.getTrailerID() ){
                    distantTmp.add(distanceElement.getDistance());
                }
                // distant from trailer depot to toFacility
                if(distanceElement.getFromFacility() == trailer.getTrailerID() &&
                        distanceElement.getToFacility() == toFacility ){
                    distantTmp.add(distanceElement.getDistance());
                }
            });
            if (distantTmp.compareTo(distant) < 0) {
                distant = distantTmp;
                pickTrailer.setTrailerId(trailer.getTrailerID());
                pickTrailer.setOrderCode(trailer.getTrailerCode());
                pickTrailer.setFacilityId(trailer.getFacilityId());
                pickTrailer.setNbTrailer(1);
            }
        }
        return pickTrailer;
    }

    public TripOutput removePickTrailer(TripOutput tripOutputTmp ) {
        List<Point> pointsInTrip = tripOutputTmp.getPoints();
        List<Point> pointsTmp = pointsInTrip.stream().filter((item) -> !item.getAction().equals(Constants.ACTION.PICKUP_TRAILER)).collect(Collectors.toList());
        tripOutputTmp.setPoints(pointsTmp);
        return tripOutputTmp;
    }

    public List<Point> insertPickTrailer(List<Point> pointsInTrip, int truckFacility) {
        List<Point> pointsAdd = pointsInTrip;
        int offset = 0;
        for (int p = 0; p <= pointsInTrip.size(); p++) {
            if (p == 0) {
                Point pickTrailer = getBestTrailer(truckFacility, pointsInTrip.get(0).getFacilityId());
                pointsAdd.add(0, pickTrailer);
                offset += 1;
            }
            else if (pointsInTrip.get(p).getNbTrailer() == 0) {
                Point pickTrailer = getBestTrailer(pointsInTrip.get(p).getFacilityId(), pointsInTrip.get(p+1).getFacilityId());
                pointsAdd.add(p+1+offset, pickTrailer);
                offset += 1;
            }
        }
        return pointsAdd;
    }

    public InfoRemoveRequest removeRequestInTrip() {
        InfoRemoveRequest infoRemoveRequest = new InfoRemoveRequest();

        BigDecimal totalDistantLoop = new BigDecimal(String.valueOf(transportContainerSolutionOutput.getTotalDistant()));
        int requestSelect = 0;
        int truckSelect = 0;
        for (Request request : requestList) {
            for (Truck truck : trucks.values().toArray(new Truck[0])) {
                BigDecimal totalDistantTripBefore = new BigDecimal(String.valueOf(transportContainerSolutionOutput.getTripOutputs().get(truck.getTruckID()).getTotalDistant()));

                List<Point> points = transportContainerSolutionOutput.getTripOutputs().get(truck.getTruckID()).getPoints().stream()
                        .filter((item) -> !item.getOrderCode().equals(request.getOrderCode())).collect(Collectors.toList());
                List<Point> pointsInTrip = new ArrayList<>();
                points.forEach((item) -> {
                    Point point = SerializationUtils.clone(item);
                    pointsInTrip.add(point);
                });

                // if request in router -> calc again distant
                if (points.size() < transportContainerSolutionOutput.getTripOutputs().get(truck.getTruckID()).getPoints().size()) {
                    BigDecimal distantTmp = calcDistantRouter(truck.getLocationId(), pointsInTrip);
                    BigDecimal totalDistantTripAfter = totalDistantLoop.subtract(totalDistantTripBefore).add(distantTmp);
                    if (totalDistantTripAfter.compareTo(totalDistantLoop) < 0) {
                        requestSelect = request.getRequestId();
                        truckSelect = truck.getTruckID();

                        TripOutput tripOutput = transportContainerSolutionOutput.getTripOutputsTmp().get(truck.getTruckID());
                        tripOutput.setPoints(pointsInTrip);
                        tripOutput.setTotalDistantTmp(distantTmp);

                        // update router and distant
                        transportContainerSolutionOutput.setTotalDistantTmp(totalDistantTripAfter);
                        transportContainerSolutionOutput.getTripOutputsTmp().put(truck.getTruckID(), tripOutput);
                    }
                    break;
                }
            }
        }

        // update weight container in point of router remove request
        TripOutput tripOutputTmp = transportContainerSolutionOutput.getTripOutputsTmp().get(truckSelect);
        List<Point> pointsRemoved = updateWeightContainer(tripOutputTmp.getPoints(), 0, tripOutputTmp.getPoints().size());
        TripOutput tripOutput = transportContainerSolutionOutput.getTripOutputs().get(truckSelect);
        tripOutput.setPoints(pointsRemoved);
        transportContainerSolutionOutput.getTripOutputs().put(truckSelect, tripOutput);

        infoRemoveRequest.setRequestId(requestSelect);
        infoRemoveRequest.setTruckId(truckSelect);

        return infoRemoveRequest;
    }

    // can kiem tra lai ham nay voi truong hop bij break trailer
    public Boolean checkValidateAddPoint(int x, int y, List<Point> pointsInTrip) {
        // bo trailer di
        if(x == 0 || pointsInTrip.get(x-1).getNbTrailer() == 0) {
            int weightAllow = pointsInTrip.get(x).getSizeContainer();
            for(int i = x+1; i <= y; i++) {
                if(weightAllow + pointsInTrip.get(i).getWeightContainer() > 40) {
                    return false;
                }
            }
        }
        else {
            Point prevPick = pointsInTrip.get(x-1);
            Point prevDelivery = pointsInTrip.get(y-1);
            if (pointsInTrip.get(x).getSizeContainer() + prevPick.getWeightContainer() > 40) {
                return false;
            }
            int weightAllow = pointsInTrip.get(x).getSizeContainer();
            for(int i = x+1; i <= y; i++) {
                if(weightAllow + pointsInTrip.get(i).getWeightContainer() > 40) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updateSolutionTmp() {
        for (Truck truck : this.trucks.values().toArray(new Truck[0])) {
            TripOutput tripOutput = this.transportContainerSolutionOutput.getTripOutputs().get(truck.getTruckID());
            BigDecimal totalDistant = this.transportContainerSolutionOutput.getTotalDistant();
            this.transportContainerSolutionOutput.getTripOutputsTmp().put(truck.getTruckID(), tripOutput);
            this.transportContainerSolutionOutput.setTotalDistantTmp(totalDistant);
        }
    }

    public static void main(String[] args)
    {
        Map<String, String> hashMap = new HashMap<>();
        Truck truck1 = new Truck(1,1,1,1);
        Truck truck2 = new Truck(2,2,2,2);
        BigDecimal a = BigDecimal.valueOf(100);
        BigDecimal b = a;
        BigDecimal c = b.add(BigDecimal.valueOf(10));
        log.info("a {}", a);
        log.info("b {}", b);
        log.info("c {}", c);
//        List<Truck> truckList = new ArrayList<>();
//        truckList.add(truck1);
//        truckList.add(truck2);
//        List<Truck> truckListClone = new ArrayList<>();
//        truckList.forEach((item) -> {
//            Truck p = (Truck) SerializationUtils.clone(item);
//            truckListClone.add(p);
//        });
//        truckListClone.get(0).setTruckID(3);
//        log.info("b {}", truckList.get(0).getTruckID());
//        log.info("a {}", truckListClone.get(0).getTrailerTruck());
//        hashMap.put("RED", "#FF0000");
//        hashMap.put("BLUE", "#0000FF");
//
//        // get the values of the `HashMap` returned as an array
//        String[] values = hashMap.values().toArray(new String[0]);


    }
}
