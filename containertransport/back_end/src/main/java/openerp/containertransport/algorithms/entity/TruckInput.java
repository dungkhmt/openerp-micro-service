package openerp.containertransport.algorithms.entity;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TruckInput implements Serializable {
    private Integer locationId;
    private String truckCode;
    private Integer truckID;
    private int trailerTruck;
    private int weightContainer;
}
