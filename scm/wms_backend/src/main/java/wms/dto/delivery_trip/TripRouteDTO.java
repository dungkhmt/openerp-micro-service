package wms.dto.delivery_trip;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class TripRouteDTO {
    @NotBlank(message = "Trip code khong duoc de trong")
    private String tripCode;
}
