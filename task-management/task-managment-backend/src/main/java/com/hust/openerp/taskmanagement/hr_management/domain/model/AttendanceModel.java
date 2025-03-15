package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.AttendanceConfig;
import com.hust.openerp.taskmanagement.hr_management.constant.AttendanceType;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class AttendanceModel {
    private String userId;
    private Map<LocalDate, DayAttendance> attendances;

    @Getter
    @Setter
    public static class DayAttendance{
        private List<LocalDateTime> pointTimes = new ArrayList<>();

        /**
         * Calculates the total time between the first and last points in hours.
         *
         * @return the total time in hours as a float. Returns 0 if the list is empty or has only one point.
         */
        public float totalTimeByHours() {
            // Ensure the list has at least two points
            if (pointTimes.size() < 2) {
                return 0.0f;
            }

            // Get the first and last points
            LocalDateTime startTime = pointTimes.get(0);
            LocalDateTime endTime = pointTimes.get(pointTimes.size() - 1);

            // Calculate the duration in hours
            long durationInSeconds = Duration.between(startTime, endTime).getSeconds();
            return durationInSeconds / 3600.0f; // Convert seconds to hours
        }

        public AttendanceType getAttendanceType() {
            return getAttendanceType(totalTimeByHours());
        }

        public AttendanceType getAttendanceType(float totalTimeByHours) {
            if(pointTimes.size() < 2) {
                if(pointTimes.isEmpty()) {
                    return AttendanceType.ABSENT;
                }
                else return AttendanceType.MISSING;
            }
            if(totalTimeByHours < AttendanceConfig.LIMIT_TIME_BY_HOURS) {
                return AttendanceType.INCOMPLETE;
            }
            return AttendanceType.ABSENT;
        }
    }

    /**
     *
     * @param sortedModel group by userid -> sort acs time
     * @return attendance
     */
    public static List<AttendanceModel> populateFrom(List<CheckinoutModel> sortedModel){
        String id = null;
        int count = -1;
        var list = new ArrayList<AttendanceModel>();
        for(CheckinoutModel model: sortedModel){
            if(!model.getUserId().equals(id)){
                id = model.getUserId();
                list.add(new AttendanceModel(id, new HashMap<>()));
                count++;
            }
            var attendances = list.get(count).attendances;
            var date = model.getPointTime().toLocalDate();
            if(!attendances.containsKey(date)){
                attendances.put(date, new DayAttendance());
            }
            attendances.get(date).pointTimes.add(model.getPointTime());
        }
        return list;
    }

}
