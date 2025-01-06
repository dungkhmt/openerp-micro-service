package openerp.openerpresourceserver.infrastructure.input.rest.dto.checkinout.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import openerp.openerpresourceserver.constant.AttendanceType;
import openerp.openerpresourceserver.domain.model.AttendanceModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MonthAttendanceResponse {
    private Map<String, Map<LocalDate, DayAttendance>> userAttendances;

    @Getter
    @Setter
    @Builder
    public static class DayAttendance{
        private List<LocalDateTime> pointTimes;
        private float totalTimeByHours;
        private AttendanceType attendanceType;
    }

    public static MonthAttendanceResponse fromModels(Collection<AttendanceModel> models) {
        var response = new MonthAttendanceResponse();
        response.userAttendances = new HashMap<>();
        for (AttendanceModel model : models) {
            var dayAttendanceMap = new HashMap<LocalDate, DayAttendance>();
            for (var date : model.getAttendances().keySet()){
                var attendanceDayOfModel = model.getAttendances().get(date);
                var dayAttendance = DayAttendance.builder()
                        .pointTimes(attendanceDayOfModel.getPointTimes())
                        .attendanceType(attendanceDayOfModel.getAttendanceType())
                        .totalTimeByHours(attendanceDayOfModel.totalTimeByHours())
                        .build();
                dayAttendanceMap.put(date, dayAttendance);
            }
            response.userAttendances.put(model.getUserId(), dayAttendanceMap);
        }
        return response;
    }
}
