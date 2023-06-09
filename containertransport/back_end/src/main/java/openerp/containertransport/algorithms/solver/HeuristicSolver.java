package openerp.containertransport.algorithms.solver;

import lombok.extern.slf4j.Slf4j;
import openerp.containertransport.algorithms.constants.Constants;
import openerp.containertransport.algorithms.entity.*;
import openerp.containertransport.algorithms.entity.output.InfoRemoveRequest;
import openerp.containertransport.algorithms.entity.output.TransportContainerSolutionOutput;
import openerp.containertransport.algorithms.entity.output.TripOutput;
import openerp.containertransport.service.FacilityService;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HeuristicSolver {

    private List<Request> requestList;
    private List<Request> requestListScheduler;
    private List<Request> requestFalse;
    private Map<Integer, TruckInput> trucks = new HashMap<>(); // id, truck
    private Map<Integer, ContainerInput> containers = new HashMap<>(); // id, container
    private Map<Integer, TrailerInput> trailers = new HashMap<>(); // id, trailer
    private Map<Integer, TrailerInput> trailerScheduler = new HashMap<>();
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

        // update endPoint
        for (TruckInput truckInput : this.trucks.values().toArray(new TruckInput[0])) {
            TripOutput tripOutput = SerializationUtils.clone(this.transportContainerSolutionOutput.getTripOutputs().get(truckInput.getTruckID()));
            tripOutput.setTruckId(truckInput.getTruckID());
            tripOutput.setTruckCode(truckInput.getTruckCode());
            if (tripOutput.getPoints() == null || tripOutput.getPoints().size() == 0) {
                continue;
            }
            else {
                TripOutput tripOutputUpdate = updateEndPoint(tripOutput);
                this.transportContainerSolutionOutput.getTripOutputs().put(truckInput.getTruckID(), tripOutputUpdate);
                TripOutput tripOutputUpdateTmp = SerializationUtils.clone(tripOutputUpdate);
                this.transportContainerSolutionOutput.getTripOutputsTmp().put(truckInput.getTruckID(), tripOutputUpdateTmp);
            }
        }
        return this.transportContainerSolutionOutput;
    }

    public void convertInput(TransportContainerInput input) {
        // truck
        List<TruckInput> truckInputListInput = input.getTruckInputs();
        truckInputListInput.forEach((truckInput) -> {
            this.trucks.put(truckInput.getTruckID(), truckInput);
        });

        // trailer
        List<TrailerInput> trailerInputListInput = input.getTrailerInputs();
        trailerInputListInput.forEach((trailerInput) -> {
            this.trailers.put(trailerInput.getTrailerID(), trailerInput);
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

        // request
        List<Request> requests = input.getRequests();
        this.requestList = requests;

        // depot truck
        this.depotTrucks = input.getDepotTruck();

        // depot trailer
        this.depotTrailers = input.getDepotTrailer();

        // output
        this.transportContainerSolutionOutput = new TransportContainerSolutionOutput();
    }

    public void greedyAlgorithmTms() {
        initRouters();
        createRouter();
        int loop = 2;

        while (loop > 0) {
            BigDecimal bestTotalDistant = new BigDecimal(String.valueOf(this.transportContainerSolutionOutput.getTotalDistant()));

            // Find request when remove it, return total distant min (tong quang duong giam nhat)
            InfoRemoveRequest infoRemoveRequest = removeRequestInTrip(); // cap nhat distant and router trong tmp
            log.info("Remove request: {} in router truck: {}", infoRemoveRequest.getRequestId(), infoRemoveRequest.getTruckId());

            // Add request vao cho thich hop sao cho tong quang duong la nho nhat
            Request requestAdd = SerializationUtils.clone(this.requestList.stream().filter((item) -> item.getRequestId() == infoRemoveRequest.getRequestId()).findFirst().get());
            Point pick = createdPointFromRequest(Constants.ACTION.PICKUP_CONTAINER.getAction(), requestAdd);
            Point delivery = createdPointFromRequest(Constants.ACTION.DELIVERY_CONTAINER.getAction(), requestAdd);

            BigDecimal totalDistantSolutionLoop = new BigDecimal(String.valueOf(this.transportContainerSolutionOutput.getTotalDistantTmp()));

            int truckRouterSelected = 0;
            TripOutput tripOutputLoop = null;

            List<TruckInput> truckInputList = Arrays.stream(this.trucks.values().toArray(new TruckInput[0])).filter(truckInput -> truckInput.getTruckID() != infoRemoveRequest.getTruckId()).collect(Collectors.toList());
            // approval each router
            for(TruckInput truckInput : truckInputList) {
                log.info("add request = {} after removed in router of truck = {}", requestAdd.getOrderCode(), truckInput.getTruckID());
                TripOutput tripOutputTmp = SerializationUtils.clone(insertRequest(truckInput, requestAdd, pick, delivery));

                // Must insert to check
                TripOutput tripOutput = SerializationUtils.clone(this.transportContainerSolutionOutput.getTripOutputsTmp().get(truckInput.getTruckID()));
                int nbPointsBeforeInsert = tripOutput.getPoints() == null ? 0 : tripOutput.getPoints().size();
                List<Point> pointsAfterInsert = tripOutputTmp.getPoints();



                if(nbPointsBeforeInsert < pointsAfterInsert.size()) {

                    BigDecimal distantInTripTmp = new BigDecimal(String.valueOf(this.transportContainerSolutionOutput.getTripOutputsTmp().get(truckInput.getTruckID()).getTotalDistant()));

                    BigDecimal distantAfterSwitch;
                    if (distantInTripTmp.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) == 0) {
                        distantAfterSwitch = totalDistantSolutionLoop.add(tripOutputTmp.getTotalDistant());
                    }
                    else {
                        distantAfterSwitch = totalDistantSolutionLoop.subtract(distantInTripTmp).add(tripOutputTmp.getTotalDistant());
                    }

                    if(distantAfterSwitch.compareTo(bestTotalDistant) < 0) {
                        log.info("chose router truck: {}", truckInput.getTruckID());
                        // update best solution
                        totalDistantSolutionLoop = distantAfterSwitch;
                        if (truckRouterSelected != 0 &&
                                (this.transportContainerSolutionOutput.getTripOutputsTmp().get(truckRouterSelected).getPoints() == null
                                || this.transportContainerSolutionOutput.getTripOutputsTmp().get(truckRouterSelected).getPoints().size() == 0)) {
                            int trailerId = tripOutputLoop.getPoints().stream().filter(item -> item.getType().equals("Trailer")).findFirst().get().getTrailerId();
                            removeToTrailerScheduler(trailerId);
                        }
                        truckRouterSelected = truckInput.getTruckID();
                        tripOutputLoop = SerializationUtils.clone(tripOutputTmp);
                    }
                    else if (nbPointsBeforeInsert == 0) {
                        int trailerId = tripOutputTmp.getPoints().stream().filter(item -> item.getType().equals("Trailer")).findFirst().get().getTrailerId();
                        removeToTrailerScheduler(trailerId);
                    }
                }
            }

            log.info("truck Router Selected Add: {}", truckRouterSelected);
            // neu ko them vao dc vi tri khac tot hon
            if(truckRouterSelected == 0) {
                TripOutput tripOutputOld = SerializationUtils.clone(this.transportContainerSolutionOutput.getTripOutputs().get(infoRemoveRequest.getTruckId()));
                this.transportContainerSolutionOutput.getTripOutputsTmp().put(infoRemoveRequest.getTruckId(), tripOutputOld);
            }
            else {
                this.transportContainerSolutionOutput.setTotalDistant(totalDistantSolutionLoop);
                if(truckRouterSelected != infoRemoveRequest.getTruckId()) {
                    TripOutput tripOutputInRouterRemoved = SerializationUtils.clone(this.transportContainerSolutionOutput.getTripOutputsTmp().get(infoRemoveRequest.getTruckId()));
                    this.transportContainerSolutionOutput.getTripOutputs().put(infoRemoveRequest.getTruckId(), tripOutputInRouterRemoved);
                }
                this.transportContainerSolutionOutput.getTripOutputs().put(truckRouterSelected, tripOutputLoop);
            }

            // update solutionTmp
            updateSolutionTmp();

            loop -= 1;
        }
    }
    public void createRouter() {
        log.info("Create init solution");
        for(Request request : this.requestList) {
            log.info("create router with request: {}", request.getOrderCode());
            Point pick = createdPointFromRequest(Constants.ACTION.PICKUP_CONTAINER.getAction(), request);
            Point delivery = createdPointFromRequest(Constants.ACTION.DELIVERY_CONTAINER.getAction(), request);

            BigDecimal distantSolution = this.transportContainerSolutionOutput.getTotalDistant();
            AtomicReference<BigDecimal> distantSolutionLoop = new AtomicReference<>(BigDecimal.valueOf(Double.MAX_VALUE));

            int truckSelect = 0;
            // duyet tung router
            for(TruckInput truckInput : trucks.values().toArray(new TruckInput[0])) {
                log.info("Try add request = {} in router of truck = {}", request.getOrderCode(), truckInput.getTruckID());
                TripOutput tripOutputBefore  = SerializationUtils.clone(this.transportContainerSolutionOutput.getTripOutputs().get(truckInput.getTruckID()));
                BigDecimal distantTrip = this.transportContainerSolutionOutput.getTripOutputs().get(truckInput.getTruckID()).getTotalDistant();

                // insert request
                TripOutput tripOutputTmp = SerializationUtils.clone(insertRequest(truckInput, request, pick, delivery));

                // check truong hop cung min
                if((tripOutputBefore.getPoints() == null && tripOutputTmp.getPoints() != null)
                        || (tripOutputBefore.getPoints().size() < tripOutputTmp.getPoints().size())) {

                    AtomicReference<BigDecimal> distantSolutionTmp = new AtomicReference<>(BigDecimal.valueOf(0));
                    if (distantTrip.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) == 0) {
                        if (distantSolution.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) == 0) {
                            distantSolutionTmp.set(tripOutputTmp.getTotalDistant());
                        }
                        else {
                            distantSolutionTmp.set(distantSolution.add(tripOutputTmp.getTotalDistant()));
                        }
                    }
                    else {
                        distantSolutionTmp.set(distantSolution.subtract(distantTrip).add(tripOutputTmp.getTotalDistant()));
                    }

                    if(distantSolutionTmp.get().compareTo(distantSolutionLoop.get()) < 0) {
                        log.info("chose router: {}", truckInput.getTruckID());
                        distantSolutionLoop.set(distantSolutionTmp.get());
                        if(truckSelect != 0 && this.transportContainerSolutionOutput.getTripOutputsTmp().get(truckSelect).getPoints() == null) {
                            int trailerId = this.transportContainerSolutionOutput.getTripOutputs().get(truckSelect).getPoints().stream().filter(item -> item.getType().equals("Trailer")).findFirst().get().getTrailerId();
                            removeToTrailerScheduler(trailerId);
                        }
                        truckSelect = truckInput.getTruckID();
                        this.transportContainerSolutionOutput.getTripOutputs().put(truckInput.getTruckID(), tripOutputTmp);
                    }
                    else {
                        if (this.transportContainerSolutionOutput.getTripOutputsTmp().get(truckInput.getTruckID()).getPoints() == null) {
                            int trailerId = tripOutputTmp.getPoints().stream().filter(item -> item.getType().equals("Trailer")).findFirst().get().getTrailerId();
                            removeToTrailerScheduler(trailerId);
                        }
                    }
                }
            }

            log.info("Router is selecte: {}", truckSelect);

            for (TruckInput truckInput : this.trucks.values().toArray(new TruckInput[0])) {
                if(truckInput.getTruckID() != truckSelect) {
                    TripOutput tripOutput = SerializationUtils.clone(this.transportContainerSolutionOutput.getTripOutputsTmp().get(truckInput.getTruckID()));
                    this.transportContainerSolutionOutput.getTripOutputs().put(truckInput.getTruckID(), tripOutput);
                }
                else {
                    TripOutput tripOutput = SerializationUtils.clone(this.transportContainerSolutionOutput.getTripOutputs().get(truckInput.getTruckID()));
                    this.transportContainerSolutionOutput.getTripOutputsTmp().put(truckInput.getTruckID(), tripOutput);
                }
            }
            this.transportContainerSolutionOutput.setTotalDistant(distantSolutionLoop.get());
            this.transportContainerSolutionOutput.setTotalDistantTmp(distantSolutionLoop.get());
        }
    }

    public Point createdPointFromRequest(String action, Request request) {
        Point point = new Point();
        point.setId(request.getRequestId());
        point.setAction(action);
        point.setFacilityId( action.equals(Constants.ACTION.PICKUP_CONTAINER.getAction()) ? request.getFromLocationID() : request.getToLocationID());
        point.setOrderCode(request.getOrderCode());
        point.setContainerId(request.getContainerID());
        point.setSizeContainer(request.getWeightContainer());
        // can check neu break
        point.setNbTrailer(1);
        point.setType("Order");

        return point;
    }

    public void initRouters() {
        for (TruckInput truckInput : this.trucks.values().toArray(new TruckInput[0])) {
            TripOutput tripOutput = new TripOutput();
            tripOutput.setTotalTime(BigDecimal.valueOf(Double.MAX_VALUE));
            tripOutput.setTotalDistant(BigDecimal.valueOf(Double.MAX_VALUE));

            TripOutput tripOutputTmp = new TripOutput();
            tripOutputTmp.setTotalTime(BigDecimal.valueOf(Double.MAX_VALUE));
            tripOutputTmp.setTotalDistant(BigDecimal.valueOf(Double.MAX_VALUE));

            Map<Integer, TripOutput> tripOutputs = new HashMap<>();
            Map<Integer, TripOutput> tripOutputsTmp = new HashMap<>();
            if (this.transportContainerSolutionOutput.getTripOutputs() != null) {
                tripOutputs = this.transportContainerSolutionOutput.getTripOutputs();
                tripOutputsTmp = this.transportContainerSolutionOutput.getTripOutputsTmp();
            }
            tripOutputs.put(truckInput.getTruckID(), tripOutput);
            tripOutputsTmp.put(truckInput.getTruckID(), tripOutputTmp);

            this.transportContainerSolutionOutput.setTripOutputs(tripOutputs);
            this.transportContainerSolutionOutput.setTripOutputsTmp(tripOutputsTmp);
            this.transportContainerSolutionOutput.setTotalTime(BigDecimal.valueOf(Double.MAX_VALUE));
            this.transportContainerSolutionOutput.setTotalDistant(BigDecimal.valueOf(Double.MAX_VALUE));
        }
    }

    public TripOutput insertRequest(TruckInput truckInput, Request request, Point pick, Point delivery) {

        AtomicReference<BigDecimal> distantLoopATruck = new AtomicReference<>(BigDecimal.valueOf(Double.MAX_VALUE));
        TripOutput tripOutputLoopTmp = SerializationUtils.clone(this.transportContainerSolutionOutput.getTripOutputsTmp().get(truckInput.getTruckID()));
        TripOutput tripOutputLoop = SerializationUtils.clone(this.transportContainerSolutionOutput.getTripOutputsTmp().get(truckInput.getTruckID()));

        // neu router rong
        if(transportContainerSolutionOutput.getTripOutputsTmp().get(truckInput.getTruckID()).getPoints() == null) {
            // Lay ra diem trailer tot nhat
            Point pickTrailer = getBestTrailer(truckInput.getLocationId(), pick.getFacilityId());
            pickTrailer.setNbTrailer(1);
            pickTrailer.setWeightContainer(0);

            // update trailers
            insertToTrailerScheduler(pickTrailer.getTrailerId());

            // deep copy
            List<Point> pointsInTrip = new ArrayList<>();
            if(tripOutputLoopTmp.getPoints() != null) {
                // deep copy
                tripOutputLoopTmp.getPoints().forEach((item) -> {
                    Point p = (Point) SerializationUtils.clone(item);
                    pointsInTrip.add(p);
                });
            }
            pointsInTrip.add(pickTrailer);

            // cap nhat trang thai point
            pick.setNbTrailer(1);
            pick.setWeightContainer(request.getWeightContainer());
            pointsInTrip.add(pick);

            delivery.setNbTrailer(1);
            delivery.setWeightContainer(0);
            pointsInTrip.add(delivery);

            // tinh toan lai quang duong
            BigDecimal distantTmp = calcDistantRouter(truckInput.getLocationId(), pointsInTrip);

            if(distantTmp.compareTo(distantLoopATruck.get()) < 0) {
                distantLoopATruck.set(distantTmp);
                tripOutputLoop.setPoints(pointsInTrip);
                tripOutputLoop.setTotalDistant(distantTmp);
            }
        }
        else {
            // remove pickTrailer truoc do
            TripOutput tripOutput = removePickTrailer(SerializationUtils.clone(tripOutputLoopTmp));

            int nbPoint = tripOutput.getPoints().size();
            for (int x = 0; x < nbPoint; x++) {
                for (int y = x+1; y <= nbPoint; y++) {
                    TripOutput tripOutputLoopItem = SerializationUtils.clone(tripOutput);
                    List<Point> pointsInTrip = tripOutputLoopItem.getPoints();
                    // check lai vi tri add cuoi
                    pointsInTrip.add(x, pick);
                    pointsInTrip.add(y, delivery);

                    Boolean checkValid = checkValidateAddPoint(x, y, pointsInTrip);
                    if (!checkValid) {
                        continue;
                    }

                    // Update weightContainer everyone Point
                    // check
                    updateWeightContainer(pointsInTrip, x, y);

                    // insert Pickup Trailer point
                    List<Point> pointsInTripAfterAdd = insertPickTrailer(pointsInTrip, truckInput.getLocationId());

                    // Calc again router
                    BigDecimal distantTmp = calcDistantRouter(truckInput.getLocationId(), pointsInTripAfterAdd);

                    if(distantTmp.compareTo(distantLoopATruck.get()) < 0) {
                        distantLoopATruck.set(distantTmp);
                        tripOutputLoopItem.setPoints(pointsInTripAfterAdd);
                        tripOutputLoopItem.setTotalDistant(distantTmp);
                        tripOutputLoop = tripOutputLoopItem;
                    }
                }
            }

            // update trailer scheduler
            if (tripOutputLoop.getPoints().size() > tripOutputLoopTmp.getPoints().size()) {
                List<Point> pointsTrailer = tripOutputLoop.getPoints().stream().filter((item) -> item.getType().equals("Trailer")).collect(Collectors.toList());
                for (Point point : pointsTrailer) {
                    insertToTrailerScheduler(point.getTrailerId());
                }
            }

            if (tripOutputLoop.getPoints().size() == tripOutputLoopTmp.getPoints().size()) {
                List<Point> pointsTrailer = tripOutputLoopTmp.getPoints().stream().filter((item) -> item.getType().equals("Trailer")).collect(Collectors.toList());
                for (Point point : pointsTrailer) {
                    insertToTrailerScheduler(point.getTrailerId());
                }
            }
        }

        return tripOutputLoop;
    }

    public BigDecimal calcDistantRouter(int truckFacility, List<Point> points) {
        AtomicInteger prevFacility = new AtomicInteger(truckFacility);
        AtomicReference<BigDecimal> totalDistantTmp = new AtomicReference<>(BigDecimal.valueOf(0));

        points.forEach((point) -> {
            DistantKey distantKey = DistantKey.builder()
                    .fromFacility(prevFacility.get())
                    .toFacility(point.getFacilityId())
                    .build();

            totalDistantTmp.set(totalDistantTmp.get().add(this.distanceElementMap.get(distantKey).getDistance()));
            prevFacility.set(point.getFacilityId());
        });
        Point endPoint = points.get(points.size()-1);
        if (endPoint.getNbTrailer() != 0) {
            AtomicReference<BigDecimal> distantEndRouter = new AtomicReference<>(BigDecimal.valueOf(Long.MAX_VALUE));
            this.depotTrailers.forEach((depotTrailer) -> {
                DistantKey distantKeyPoint2Trailer = new DistantKey(endPoint.getFacilityId(), depotTrailer.getDepotTrailerId());
                BigDecimal distantPoint2Trailer = distanceElementMap.get(distantKeyPoint2Trailer).getDistance();
                this.depotTrucks.forEach((depotTruck) -> {
                    DistantKey distantKeyTrailer2Truck = new DistantKey(depotTrailer.getDepotTrailerId(), depotTruck.getDepotTruckId());
                    BigDecimal distantTrailer2Truck = distanceElementMap.get(distantKeyTrailer2Truck).getDistance();

                    BigDecimal distantEndRouterTmp = distantPoint2Trailer.add(distantTrailer2Truck);
                    if (distantEndRouterTmp.compareTo(distantEndRouter.get()) < 0) {
                        distantEndRouter.set(distantEndRouterTmp);
                    }
                });
            });
            totalDistantTmp.set(totalDistantTmp.get().add(distantEndRouter.get()));
        }
        else {
            AtomicReference<BigDecimal> distantEndRouter = new AtomicReference<>(BigDecimal.valueOf(Long.MAX_VALUE));
            this.depotTrucks.forEach((depotTruck) -> {
                DistantKey distantKeyPoint2Truck = new DistantKey(endPoint.getFacilityId(), depotTruck.getDepotTruckId());
                BigDecimal distantPoint2Truck = distanceElementMap.get(distantKeyPoint2Truck).getDistance();

                if (distantPoint2Truck.compareTo(distantEndRouter.get()) < 0) {
                    distantEndRouter.set(distantPoint2Truck);
                }
            });
            totalDistantTmp.set(totalDistantTmp.get().add(distantEndRouter.get()));
        }

        return totalDistantTmp.get();
    }

    // can check lai
    public List<Point> updateWeightContainer (List<Point> points, int x, int y) {
        int sizeContainer = points.get(x).getSizeContainer();
        if (x == 0 || points.get(x-1).getNbTrailer() == 0) {
            points.get(x).setWeightContainer(sizeContainer);
        }
        else {
            points.get(x).setWeightContainer(sizeContainer + points.get(x-1).getWeightContainer());
        }
        for(int i = x+1; i< y; i++) {
            if(points.get(i).getWeightContainer() != null) {
                points.get(i).setWeightContainer(points.get(i).getWeightContainer() + sizeContainer);
            }
        }

        points.get(y).setWeightContainer(points.get(y-1).getWeightContainer() - sizeContainer);

        return points;
    }

    public Point getBestTrailer(int fromFacility, int toFacility) {

        BigDecimal distant = BigDecimal.valueOf(Double.MAX_VALUE);
        Point pickTrailer = new Point();

        pickTrailer.setAction(Constants.ACTION.PICKUP_TRAILER.getAction());
        pickTrailer.setType("Trailer");
        for(TrailerInput trailerInput : this.trailers.values().toArray(new TrailerInput[0])) {
            BigDecimal distantTmp = BigDecimal.valueOf(0);

            DistantKey distantKeyFacility2TrailerDepot = new DistantKey(fromFacility, trailerInput.getFacilityId());
            BigDecimal distantFacility2TrailerDepot = this.distanceElementMap.get(distantKeyFacility2TrailerDepot).getDistance();

            DistantKey distantKeyTrailerDepot2Facility = new DistantKey(trailerInput.getFacilityId(), toFacility);
            BigDecimal distantTrailerDepot2Facility = this.distanceElementMap.get(distantKeyTrailerDepot2Facility).getDistance();

            distantTmp = distantTmp.add(distantFacility2TrailerDepot);
            distantTmp = distantTmp.add(distantTrailerDepot2Facility);

            if (distantTmp.compareTo(distant) < 0) {
                distant = distantTmp;
                pickTrailer.setTrailerId(trailerInput.getTrailerID());
                pickTrailer.setOrderCode(trailerInput.getTrailerCode());
                pickTrailer.setFacilityId(trailerInput.getFacilityId());
                pickTrailer.setNbTrailer(1);
            }
        }
        return pickTrailer;
    }

    public TripOutput removePickTrailer(TripOutput tripOutputTmp ) {
        List<Point> pointsInTrip = tripOutputTmp.getPoints();
        // !item.getAction().equals(Constants.ACTION.PICKUP_TRAILER)
        List<Point> pointsTrailer = pointsInTrip.stream().filter((item) -> item.getType().equals("Trailer")).collect(Collectors.toList());
        for (Point point : pointsTrailer) {
            removeToTrailerScheduler(point.getTrailerId());
        }
        List<Point> pointsTmp = pointsInTrip.stream().filter((item) -> !item.getType().equals("Trailer")).collect(Collectors.toList());
        tripOutputTmp.setPoints(pointsTmp);
        return tripOutputTmp;
    }

    public List<Point> insertPickTrailer(List<Point> pointsInTrip, int truckFacility) {
        List<Point> pointsAdd = pointsInTrip;
        int offset = 0;
        for (int p = 0; p < pointsInTrip.size(); p++) {
            if (p == 0) {
                Point pickTrailer = getBestTrailer(truckFacility, pointsInTrip.get(0).getFacilityId());
                pickTrailer.setWeightContainer(0);
//                insertToTrailerScheduler(pickTrailer.getTrailerId());
                pointsAdd.add(0, pickTrailer);
                offset += 1;
            }
            else if (pointsInTrip.get(p).getNbTrailer() == 0) {
                Point pickTrailer = getBestTrailer(pointsInTrip.get(p).getFacilityId(), pointsInTrip.get(p+1).getFacilityId());
                pickTrailer.setWeightContainer(0);
//                insertToTrailerScheduler(pickTrailer.getTrailerId());
                pointsAdd.add(p+1+offset, pickTrailer);
                offset += 1;
            }
        }
        return pointsAdd;
    }

    public void insertToTrailerScheduler(int trailerId) {
        this.trailerScheduler.put(trailerId, this.trailers.get(trailerId));
        this.trailers.remove(trailerId);
    }

    public void removeToTrailerScheduler(int trailerId) {
        this.trailers.put(trailerId, this.trailerScheduler.get(trailerId));
        this.trailerScheduler.remove(trailerId);
    }

    public InfoRemoveRequest removeRequestInTrip() {
        InfoRemoveRequest infoRemoveRequest = new InfoRemoveRequest();

        BigDecimal totalDistantBest = new BigDecimal(String.valueOf(this.transportContainerSolutionOutput.getTotalDistant()));
        long requestSelect = 0;
        int truckSelect = 0;
        TripOutput tripOutputLoopTmp = new TripOutput();

        for (Request request : this.requestList) {
            BigDecimal totalDistantLoop = new BigDecimal(String.valueOf(this.transportContainerSolutionOutput.getTotalDistant()));
            for (TruckInput truckInput : this.trucks.values().toArray(new TruckInput[0])) {

                BigDecimal totalDistantTripBefore = new BigDecimal(String.valueOf(this.transportContainerSolutionOutput.getTripOutputs().get(truckInput.getTruckID()).getTotalDistant()));

                TripOutput tripOutput = SerializationUtils.clone(this.transportContainerSolutionOutput.getTripOutputs().get(truckInput.getTruckID()));

                if(tripOutput.getPoints() == null) {
                    continue;
                }
                List<Point> points = tripOutput.getPoints().stream()
                        .filter((item) -> !item.getOrderCode().equals(request.getOrderCode())).collect(Collectors.toList());
                List<Point> pointsInTrip = new ArrayList<>();
                points.forEach((item) -> {
                    Point point = SerializationUtils.clone(item);
                    pointsInTrip.add(point);
                });

                // if request in router -> calc again distant
                if (points.size() < this.transportContainerSolutionOutput.getTripOutputs().get(truckInput.getTruckID()).getPoints().size()) {

                    BigDecimal distantTmp;
                    BigDecimal totalDistantTripAfter;
                    if (pointsInTrip.size() == 0) {
                        distantTmp = new BigDecimal(Double.MAX_VALUE);
                        totalDistantTripAfter = totalDistantLoop.subtract(totalDistantTripBefore);
                    }
                    else {
                        distantTmp = calcDistantRouter(truckInput.getLocationId(), pointsInTrip);
                        totalDistantTripAfter = totalDistantLoop.subtract(totalDistantTripBefore).add(distantTmp);
                    }

                    if (totalDistantTripAfter.compareTo(totalDistantLoop) < 0) {
                        requestSelect = request.getRequestId();
                        truckSelect = truckInput.getTruckID();
                        totalDistantLoop = totalDistantTripAfter;

                        tripOutput.setPoints(pointsInTrip);
                        tripOutput.setTotalDistant(distantTmp);
                        tripOutputLoopTmp = tripOutput;
                    }
                    break;
                }
            }

            if(totalDistantLoop.compareTo(totalDistantBest) < 0) {
                totalDistantBest = totalDistantLoop;
            }
        }

        if (tripOutputLoopTmp.getPoints() == null || tripOutputLoopTmp.getPoints().size() == 0) {
            TripOutput tripOutputBeforeRemove = SerializationUtils.clone(this.transportContainerSolutionOutput.getTripOutputs().get(truckSelect));
            List<Point> points = tripOutputBeforeRemove.getPoints();
            int trailerId = points.stream().filter(item -> item.getType().equals("Trailer")).findFirst().get().getTrailerId();
            removeToTrailerScheduler(trailerId);
        }
        // update weight container in point of router remove request
        List<Point> pointsRemoved = updateWeightContainer(tripOutputLoopTmp.getPoints(), 0, tripOutputLoopTmp.getPoints().size()-1);
        this.transportContainerSolutionOutput.getTripOutputsTmp().get(truckSelect).setPoints(pointsRemoved);
        this.transportContainerSolutionOutput.getTripOutputsTmp().get(truckSelect).setTotalDistant(tripOutputLoopTmp.getTotalDistant());
        this.transportContainerSolutionOutput.setTotalDistantTmp(totalDistantBest);

        infoRemoveRequest.setRequestId(requestSelect);
        infoRemoveRequest.setTruckId(truckSelect);

        return infoRemoveRequest;
    }

    // can kiem tra lai ham nay voi truong hop bij break trailer
    public Boolean checkValidateAddPoint(int x, int y, List<Point> pointsInTrip) {
        for (int i = x+1; i < y; i++) {
            if (pointsInTrip.get(i).getNbTrailer() == 0) {
                return false;
            }
        }

        // bo trailer di
        if(x == 0 || pointsInTrip.get(x-1).getNbTrailer() == 0) {
            int weightAllow = pointsInTrip.get(x).getSizeContainer();
            for(int i = x+1; i < y; i++) {
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
            for(int i = x+1; i < y; i++) {
                if(weightAllow + pointsInTrip.get(i).getWeightContainer() > 40) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updateSolutionTmp() {
        for (TruckInput truckInput : this.trucks.values().toArray(new TruckInput[0])) {
            TripOutput tripOutput = SerializationUtils.clone(this.transportContainerSolutionOutput.getTripOutputs().get(truckInput.getTruckID()));
            BigDecimal totalDistant = new BigDecimal(String.valueOf(this.transportContainerSolutionOutput.getTotalDistant()));
            this.transportContainerSolutionOutput.getTripOutputsTmp().put(truckInput.getTruckID(), tripOutput);
            this.transportContainerSolutionOutput.setTotalDistantTmp(totalDistant);
        }
    }

    public TripOutput updateEndPoint(TripOutput tripOutput) {
        List<Point> pointList = tripOutput.getPoints();
        Point endPoint = pointList.get(pointList.size()-1);
        AtomicInteger depotTrailerID = new AtomicInteger();
        AtomicInteger depotTruckID = new AtomicInteger();
        AtomicInteger trailerId = new AtomicInteger();

        Point pointDepart = new Point();
        pointDepart.setType("Truck");
        pointDepart.setAction(Constants.ACTION.DEPART.getAction());
        pointDepart.setOrderCode(tripOutput.getTruckCode());
        pointDepart.setFacilityId(this.trucks.get(tripOutput.getTruckId()).getLocationId());

        pointList.add(0, pointDepart);

        if (endPoint.getNbTrailer() != 0) {
            AtomicReference<BigDecimal> distantEndRouter = new AtomicReference<>(BigDecimal.valueOf(Long.MAX_VALUE));
            this.depotTrailers.forEach((depotTrailer) -> {
                DistantKey distantKeyPoint2Trailer = new DistantKey(endPoint.getFacilityId(), depotTrailer.getDepotTrailerId());
                BigDecimal distantPoint2Trailer = distanceElementMap.get(distantKeyPoint2Trailer).getDistance();
                this.depotTrucks.forEach((depotTruck) -> {
                    DistantKey distantKeyTrailer2Truck = new DistantKey(depotTrailer.getDepotTrailerId(), depotTruck.getDepotTruckId());
                    BigDecimal distantTrailer2Truck = distanceElementMap.get(distantKeyTrailer2Truck).getDistance();

                    BigDecimal distantEndRouterTmp = distantPoint2Trailer.add(distantTrailer2Truck);
                    if (distantEndRouterTmp.compareTo(distantEndRouter.get()) < 0) {
                        distantEndRouter.set(distantEndRouterTmp);
                        depotTrailerID.set(depotTrailer.getDepotTrailerId());
                        depotTruckID.set(depotTruck.getDepotTruckId());
                    }
                });
            });

            for (int i = pointList.size() - 1; i >=0 ; i--) {
                if (pointList.get(i).getTrailerId() != null) {
                    trailerId.set(pointList.get(i).getTrailerId());
                    break;
                }
            }
            Point pointDepotTrailer = new Point();
            pointDepotTrailer.setAction(Constants.ACTION.DELIVERY_TRAILER.getAction());
            pointDepotTrailer.setTrailerId(trailerId.get());
            pointDepotTrailer.setOrderCode(this.trailerScheduler.get(trailerId.get()).getTrailerCode());
            pointDepotTrailer.setType("Trailer");
            pointDepotTrailer.setFacilityId(depotTrailerID.get());

            pointList.add(pointDepotTrailer);
        }

        Point pointStop = new Point();
        pointStop.setType("Truck");
        pointStop.setAction(Constants.ACTION.STOP.getAction());
        pointStop.setOrderCode(tripOutput.getTruckCode());
        pointStop.setFacilityId(depotTruckID.get());

        pointList.add(pointStop);
        tripOutput.setPoints(pointList);

        return tripOutput;
    }

    public static void main(String[] args)
    {
        FacilityService facilityService;

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
