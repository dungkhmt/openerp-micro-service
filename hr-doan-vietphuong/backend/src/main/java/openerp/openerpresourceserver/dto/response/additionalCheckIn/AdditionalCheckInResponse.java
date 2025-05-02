package openerp.openerpresourceserver.dto.response.additionalCheckIn;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class AdditionalCheckInResponse {
    private Long id;
    private String email;
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private Integer type;
    private String subType;
    private String note;
    private Integer status;
    private Integer emailStatus;
}

