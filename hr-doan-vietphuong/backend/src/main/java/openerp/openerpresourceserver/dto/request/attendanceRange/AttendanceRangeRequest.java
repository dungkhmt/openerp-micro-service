package openerp.openerpresourceserver.dto.request.attendanceRange;

import lombok.Data;

import java.time.LocalTime;

@Data
public class AttendanceRangeRequest {
    private Long id;
    private String code;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime startLunch;
    private LocalTime endLunch;
    private Double fullHours;
    private Integer bonusTime;
    private Integer status;
}