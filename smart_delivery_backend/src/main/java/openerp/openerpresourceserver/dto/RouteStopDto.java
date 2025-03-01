package openerp.openerpresourceserver.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteStopDto {
    private UUID id;

    @NotNull(message = "Hub ID is required")
    private UUID hubId;

    private String hubName;

    @NotNull(message = "Stop sequence is required")
    @Min(value = 1, message = "Sequence must be at least 1")
    private Integer stopSequence;

    private Integer estimatedWaitTime;
}