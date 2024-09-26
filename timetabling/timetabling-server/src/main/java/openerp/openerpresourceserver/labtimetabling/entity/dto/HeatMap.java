package openerp.openerpresourceserver.labtimetabling.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class HeatMap {
    private List<HeatValue> heatValues = new ArrayList<>();
}
