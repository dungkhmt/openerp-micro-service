package openerp.openerpresourceserver.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ShipperRequest {
    private UUID shipperId;

    @NotBlank
    private String name;

    @NotBlank
    private String phone;



    private String email;

    @NotBlank
    private String address;

}
