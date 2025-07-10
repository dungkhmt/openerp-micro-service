package openerp.openerpresourceserver.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.TripStatus;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO for updating Trip entity
 * Contains only fields that are allowed to be updated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripUpdateDto {

    /**
     * Trip status
     */
    private TripStatus status;

    /**
     * Driver assignment
     */
    private UUID driverId;

    /**
     * Vehicle assignment
     */
    private UUID vehicleId;

    /**
     * Trip date
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * Planned start time
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime plannedStartTime;

    /**
     * Actual start time
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Instant startTime;

    /**
     * End time
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Instant endTime;

    /**
     * Current stop index
     */
    @Min(value = 0, message = "Chỉ số điểm dừng không được âm")
    private Integer currentStopIndex;

    /**
     * Last stop arrival time
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Instant lastStopArrivalTime;

    /**
     * Distance traveled in km
     */
    @DecimalMin(value = "0.0", message = "Khoảng cách di chuyển không được âm")
    private Double distanceTraveled;

    /**
     * Completion notes
     */
    @Size(max = 500, message = "Ghi chú hoàn thành không được vượt quá 500 ký tự")
    private String completionNotes;

    /**
     * Orders picked up count
     */
    @Min(value = 0, message = "Số đơn hàng đã lấy không được âm")
    private Integer ordersPickedUp;

    /**
     * Orders delivered count
     */
    @Min(value = 0, message = "Số đơn hàng đã giao không được âm")
    private Integer ordersDelivered;

    /**
     * Delay events in JSON format
     */
    private String delayEvents;
}