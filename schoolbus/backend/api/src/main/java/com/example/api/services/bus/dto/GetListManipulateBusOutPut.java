package com.example.api.services.bus.dto;

import com.example.shared.db.entities.Bus;
import com.example.shared.db.entities.PickupPoint;
import com.example.shared.db.entities.Ride;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetListManipulateBusOutPut {
    private Bus bus;
    private Ride ride;
    private List<PickupPoint> pickupPoints;
}
