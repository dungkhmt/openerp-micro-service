package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.algorithms.entity.*;
import openerp.containertransport.algorithms.entity.output.TransportContainerSolutionOutput;
import openerp.containertransport.algorithms.solver.HeuristicSolver;
import openerp.containertransport.algorithms.solver.TransportContainerInput;
import openerp.containertransport.dto.*;
import openerp.containertransport.service.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoSolutionRouterServiceImpl implements AutoSolutionRouterService {
    private final TruckService truckService;
    private final TrailerService trailerService;
    private final ContainerServiceImpl containerService;
    private final FacilityService facilityService;
    private final OrderService orderService;
    private final HeuristicSolver heuristicSolver;

    @Override
    public ShipmentModel autoSolutionRouter() {
        TransportContainerInput transportContainerInput = new TransportContainerInput();

        // get Trucks
        List<TruckModel> truckList = truckService.filterTruck(new TruckFilterRequestDTO()).getTruckModels();
        List<TruckInput> truckInputs = convertToTruckInput(truckList);
        transportContainerInput.setTruckInputs(truckInputs);

        // get Trailers
        List<TrailerModel> trailerModels = trailerService.filterTrailer(new TrailerFilterRequestDTO()).getTrailerModels();
        List<TrailerInput> trailerInputs = convertToTrailerInput(trailerModels);
        transportContainerInput.setTrailerInputs(trailerInputs);

        // request
        List<OrderModel> orderModels = orderService.filterOrders(new OrderFilterRequestDTO()).getOrderModels();
        List<Request> requests = convertToRequest(orderModels);
        transportContainerInput.setRequests(requests);

        // depot truck
        FacilityFilterRequestDTO facilityFilterTruck = new FacilityFilterRequestDTO();
        facilityFilterTruck.setType("Truck");
        List<FacilityModel> facilityModelTrucks = facilityService.filterFacility(facilityFilterTruck).getFacilityModels();
        List<DepotTruck> depotTrucks = convertTruckDepot(facilityModelTrucks);
        transportContainerInput.setDepotTruck(depotTrucks);

        // depot trailer
        FacilityFilterRequestDTO facilityFilterTrailer = new FacilityFilterRequestDTO();
        facilityFilterTruck.setType("Trailer");
        List<FacilityModel> facilityModelsTrailer = facilityService.filterFacility(facilityFilterTruck).getFacilityModels();
        List<DepotTrailer> depotTrailers = convertTrailerDepot(facilityModelsTrailer);
        transportContainerInput.setDepotTrailer(depotTrailers);

        // distant


        TransportContainerSolutionOutput transportContainerSolutionOutput = heuristicSolver.solve(transportContainerInput);
        return null;
    }

    public List<TruckInput> convertToTruckInput(List<TruckModel> truckModels) {
        List<TruckInput> truckInputs = new ArrayList<>();
        truckModels.forEach((item) -> {
            TruckInput truckInput = new TruckInput();
            truckInput.setTruckID((int) item.getId());
            truckInput.setLocationId((int) item.getFacilityResponsiveDTO().getFacilityId());

            truckInputs.add(truckInput);
        });
        return truckInputs;
    }

    public List<TrailerInput> convertToTrailerInput(List<TrailerModel> trailerModels) {
        List<TrailerInput> trailerInputs = new ArrayList<>();
        trailerModels.forEach((trailerModel) -> {
            TrailerInput trailerInput = new TrailerInput();
            trailerInput.setTrailerID((int) trailerModel.getId());
            trailerInput.setTrailerCode(trailerModel.getTrailerCode());
            trailerInput.setFacilityId((int) trailerModel.getFacilityResponsiveDTO().getFacilityId());
        });
        return trailerInputs;
    }

    public List<Request> convertToRequest(List<OrderModel> orderModels) {
        List<Request> requests = new ArrayList<>();
        orderModels.forEach(orderModel -> {
            Request request = new Request();
            request.setRequestId((int) orderModel.getId());
            request.setOrderCode(orderModel.getOrderCode());
            request.setContainerID(orderModel.getContainers().get(0).getId());
            request.setWeightContainer(orderModel.getContainers().get(0).getTypeContainer().getSize());
            request.setFromLocationID((int) orderModel.getFromFacility().getFacilityId());
            request.setToLocationID((int) orderModel.getToFacility().getFacilityId());
            requests.add(request);
        });
        return requests;
    }

    public List<DepotTruck> convertTruckDepot(List<FacilityModel> facilityModels) {
        List<DepotTruck> depotTrucks = new ArrayList<>();
        facilityModels.forEach(facilityModel -> {
            DepotTruck depotTruck = new DepotTruck();
            depotTruck.setDepotTruckId((int) facilityModel.getId());
            depotTrucks.add(depotTruck);
        });
        return depotTrucks;
    }

    public List<DepotTrailer> convertTrailerDepot(List<FacilityModel> facilityModels) {
        List<DepotTrailer> depotTrailers = new ArrayList<>();
        facilityModels.forEach(facilityModel -> {
            DepotTrailer depotTrailer = new DepotTrailer();
            depotTrailer.setDepotTrailerId((int) facilityModel.getId());
            depotTrailers.add(depotTrailer);
        });
        return depotTrailers;
    }
}
