package com.example.api.services.bus.dto;

import com.example.shared.db.dto.GetListBusDTO;
import com.example.shared.db.entities.Bus;
import com.example.shared.db.entities.Employee;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetListBusOutput {
    private Bus bus;
    private Employee driver;
    private Employee driverMate;

    public static GetListBusOutput fromDto(GetListBusDTO dto) {
        return GetListBusOutput.builder()
            .bus(dto.getBus())
            .driver(dto.getDriver())
            .driverMate(dto.getDriverMate())
            .build();
    }
}
