package openerp.containertransport.algorithms.entity;

import lombok.Data;

import java.util.List;

@Data
public class DepotTrailer {
    private String depotTrailerId;
    private List<String> trailerIds;
    private int numberTrailer;
}
