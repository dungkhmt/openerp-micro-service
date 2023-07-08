package openerp.containertransport.algorithms.solver;

import lombok.*;
import openerp.containertransport.algorithms.entity.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportContainerInput {
    private List<Request> requests;
    private List<TruckInput> truckInputs;
    private List<DepotTruck> depotTruck;
    private List<TrailerInput> trailerInputs;
    private List<DepotTrailer> depotTrailer;
    private List<ContainerInput> containerInputs;
    private List<DistanceElement> distances;
    private List<FacilityInput> facilityInputs;
    private BigDecimal startTime;
}
