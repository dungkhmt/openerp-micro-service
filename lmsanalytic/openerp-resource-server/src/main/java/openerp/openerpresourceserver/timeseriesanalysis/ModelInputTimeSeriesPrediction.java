package openerp.openerpresourceserver.timeseriesanalysis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelInputTimeSeriesPrediction {
    private int windowsSize;
    private List<Double> sequences;
}
