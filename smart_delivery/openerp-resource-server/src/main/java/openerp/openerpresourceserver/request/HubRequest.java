package openerp.openerpresourceserver.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import openerp.openerpresourceserver.entity.enumentity.HubType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    private String code;

    @NotNull
    private HubType hubType;

    @NotNull
    private BigDecimal longitude;

    @NotNull
    private BigDecimal latitude;

    @NotNull
    @Size(min = 1, max = 500)
    private String address;

    private String contactNumber;

    private Double width;

    private Double length;

    // You can add any other fields as required
}
