package com.example.api.services.pickup_point.dto;

import com.example.api.services.common_dto.PickupPointOutput;
import com.example.api.services.common_dto.RideOutput;
import com.example.api.services.common_dto.StudentOutput;
import com.example.shared.db.dto.GetListPickupPointDTO;
import com.example.shared.db.entities.PickupPoint;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetListPickupPointOutput {
    private PickupPointOutput pickupPoint;
    private List<StudentOutput> students;
    private List<RideOutput> rides;


    public static GetListPickupPointOutput fromDto(GetListPickupPointDTO dto) {
        return GetListPickupPointOutput.builder()
            .pickupPoint(PickupPointOutput.fromEntity(dto.getPickupPoint()))
            .students(
                (dto.getStudents() == null) ? null :
                    dto.getStudents().stream().map(StudentOutput::fromEntity).toList()
            )
            .rides(
                (dto.getRides() == null) ? null :
                    dto.getRides().stream().map(RideOutput::fromEntity).toList()
            )
            .build();
    }

    public static GetListPickupPointOutput fromEntity(PickupPoint entity) {
        return GetListPickupPointOutput.builder()
            .pickupPoint(PickupPointOutput.fromEntity(entity))
            .students(null)
            .rides(null)
            .build();
    }
}
