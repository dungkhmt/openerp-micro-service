package wms.dto.shipment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.ZonedDateTime;

@Data
public class ShipmentDTO {
    @NotBlank(message = "Title khong duoc de trong")
    private String title;
    @Positive(message = "So luong phai lon hon khong")
    private Integer maxSize;
    private String startedDate;
    private String endedDate;
}
