package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import openerp.openerpresourceserver.entity.enumentity.HubType;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubRequestDto {

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    private String code;

    @NotNull
    private HubType hubType;

    @NotNull
    private Double longitude;

    @NotNull
    private Double latitude;

    @NotNull
    @Size(min = 1, max = 500)
    private String address;

    private String contactNumber;

    private Double width;

    private Double length;

    // You can add any other fields as required
}
