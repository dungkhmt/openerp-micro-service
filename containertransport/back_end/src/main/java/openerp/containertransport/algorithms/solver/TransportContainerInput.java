package openerp.containertransport.algorithms.solver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openerp.containertransport.algorithms.entity.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransportContainerInput {
    private List<Request> requests;
    private List<Truck> trucks;
    private List<DepotTruck> depotTruck;
    private List<Trailer> trailers;
    private List<DepotTrailer> depotTrailer;
    private List<Container> containers;
    private List<DistanceElement> distances;
    private Long startTime;
}
