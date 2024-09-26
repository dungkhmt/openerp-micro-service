package wms.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import wms.algorithms.entity.DroneRoute;
import wms.algorithms.entity.TruckRoute;

import javax.persistence.Id;
import java.util.List;


@Getter
@Setter
@Document(collection = "route_solution")
public class RouteSchedulingOutput {
    @Id
    private String id;
    @Field(value = "truckRoute")
    private TruckRoute truckRoute;
    @Field(value = "droneRoutes")
    private List<DroneRoute> droneRoutes;
    @Field(value = "tripCode")
    private String tripCode;
    @Field(value = "totalCost")
    private double totalCost;
    @Field(value = "totalTruckCost")
    private double totalTruckCost;
    @Field(value = "totalDroneCost")
    private double totalDroneCost;
    @Field(value = "totalTruckWait")
    private double totalTruckWait;
    @Field(value = "totalDroneWait")
    private double totalDroneWait;
    @Field(value = "totalTSPCost")
    private double totalTSPCost;
}
