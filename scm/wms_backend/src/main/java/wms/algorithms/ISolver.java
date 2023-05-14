package wms.algorithms;

import wms.algorithms.entity.TruckDroneDeliveryInput;
import wms.algorithms.entity.TruckDroneDeliverySolutionOutput;

public interface ISolver {
    public TruckDroneDeliverySolutionOutput solve(TruckDroneDeliveryInput input);
}
