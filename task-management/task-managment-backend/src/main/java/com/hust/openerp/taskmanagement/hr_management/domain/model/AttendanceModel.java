package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.AttendanceConfig;
import com.hust.openerp.taskmanagement.hr_management.constant.AttendanceType;
import com.hust.openerp.taskmanagement.util.WorkTimeCalculator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
        private float totalTimeByHours;
        private AttendanceType attendanceType;

        public LocalDateTime getStartTime(){
            return pointTimes.get(0);
        }

        public LocalDateTime getEndTime(){
            if(pointTimes.size() > 1){
                return pointTimes.get(pointTimes.size()-1);
            }
            return null;
        }


        public AttendanceType getAttendanceType() {
            if(attendanceType == AttendanceType.LATE){
                return AttendanceType.LATE;
            }
            return getAttendanceType(totalTimeByHours);
        }

        public AttendanceType getAttendanceType(float totalTimeByHours) {
            if(attendanceType == AttendanceType.LATE){
                return AttendanceType.LATE;
            }
            if(pointTimes.size() < 2) {
                if(pointTimes.isEmpty()) {
                    return AttendanceType.ABSENT;
                }
                else return AttendanceType.MISSING;
            }
            if(totalTimeByHours < AttendanceConfig.LIMIT_TIME_BY_HOURS) {
                return AttendanceType.PRESENT;
            }
            return AttendanceType.PRESENT;
        }
    }

    /**
     * @param sortedModel   group by userid -> sort acs time
     * @param companyConfig
     * @return attendance
     */
    public static List<AttendanceModel> populateFrom(List<CheckinoutModel> sortedModel, CompanyConfigModel companyConfig){
        Map<String, AttendanceModel> userAttendanceMap = new HashMap<>();
        for (CheckinoutModel model : sortedModel) {
            String userId = model.getUserId();
            AttendanceModel attendance = userAttendanceMap.computeIfAbsent(userId, id -> new AttendanceModel(id, new HashMap<>()));
            var attendances = attendance.attendances;
            var date = model.getPointTime().toLocalDate();
            attendances.computeIfAbsent(date, d -> new DayAttendance()).pointTimes.add(model.getPointTime());
        }
        for (var attendance : userAttendanceMap.values()) {
            for (var dayAttendance : attendance.attendances.values()) {
                var startTime = dayAttendance.getStartTime() != null ? dayAttendance.getStartTime().toLocalTime() : null;
                var endTime = dayAttendance.getEndTime() != null ? dayAttendance.getEndTime().toLocalTime() : null;
                dayAttendance.setTotalTimeByHours(
                    WorkTimeCalculator.calculateWorkTimeByHours(
                        startTime,
                        endTime,
                        companyConfig)
                );
                if(startTime != null && endTime != null && startTime.isAfter(companyConfig.getStartWorkTime())){
                    dayAttendance.setAttendanceType(AttendanceType.LATE);
                }
            }
        }
        return new ArrayList<>(userAttendanceMap.values());
    }

}
