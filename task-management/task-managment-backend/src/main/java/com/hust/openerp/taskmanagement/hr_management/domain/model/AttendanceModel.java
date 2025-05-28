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
            return getAttendanceType(totalTimeByHours);
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
            return AttendanceType.PRESENT;
        }
    }

    /**
     * @param sortedModel   group by userid -> sort acs time
     * @param companyConfig
     * @return attendance
     */
    public static List<AttendanceModel> populateFrom(List<CheckinoutModel> sortedModel, CompanyConfigModel companyConfig){
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
        for(var attendance: list){
            for(var dayAttendance: attendance.attendances.values()){
                var startTime = dayAttendance.getStartTime() != null ? dayAttendance.getStartTime().toLocalTime() : null;
                var endTime = dayAttendance.getEndTime() != null ? dayAttendance.getEndTime().toLocalTime() : null;
                dayAttendance.setTotalTimeByHours(
                    WorkTimeCalculator.calculateWorkTimeByHours(
                        startTime,
                        endTime,
                        companyConfig)
                );
            }
        }
        return list;
    }

}
