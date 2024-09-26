package openerp.containertransport.algorithms.entity;

import lombok.Data;

import java.util.List;

@Data
public class DepotTrailer {
    private int depotTrailerId;
    private List<String> trailerIds;
    private int numberTrailer;
}
