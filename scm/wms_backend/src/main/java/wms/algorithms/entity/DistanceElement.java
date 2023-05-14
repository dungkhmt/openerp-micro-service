package wms.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DistanceElement {
    private String id;
    private String fromLocationId;
    private String toLocationId;
    private double distance; // in meters
    private double travelTime;// in seconds
    private double flyingTime; // in seconds
}
