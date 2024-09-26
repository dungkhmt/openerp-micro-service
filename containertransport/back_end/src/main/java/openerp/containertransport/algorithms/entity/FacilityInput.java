package openerp.containertransport.algorithms.entity;

import lombok.Data;

@Data
public class FacilityInput {
    private int facilityId;
    private long timeProcessPickup;
    private long timeProcessDrop;
}
