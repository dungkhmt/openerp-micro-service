package wms.dto.category;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class DistributingChannelDTO {
    @NotBlank(message = "Truong name khong duoc bo trong")
    private String name;
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "100", inclusive = true)
    private double promotion;
}
