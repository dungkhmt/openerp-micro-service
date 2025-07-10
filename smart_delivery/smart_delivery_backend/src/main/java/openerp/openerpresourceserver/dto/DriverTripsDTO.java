package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverTripsDTO {
    private List<TripDTO> activeTrips;
    private List<TripDTO> scheduledTrips;
    private List<TripDTO> completedTrips;
}