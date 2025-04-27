package openerp.openerpresourceserver.dto.request.additionalCheckIn;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class AdditionalCheckInRequest {
    @NotNull(message = "Absence Type Id must not be null")
    private Long absenceTypeId;
    @NotNull(message = "Date must not be null")
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private String note;
    private List<Long> organizationIdList;
    private List<String> ccList;
}

