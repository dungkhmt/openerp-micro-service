package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceType;
import com.hust.openerp.taskmanagement.util.WorkTimeCalculator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AbsenceModel {
    private UUID id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
    private AbsenceStatus status;
    private AbsenceType type;
    private String userId;

    public Float getDurationTimeAbsence(CompanyConfigModel companyConfig){
        return WorkTimeCalculator.calculateWorkTimeByHours(startTime, endTime, companyConfig);
    }

    public boolean hasTimeOverlap(List<AbsenceModel> otherAbsences) {
        if (this.startTime == null || this.endTime == null || otherAbsences == null) {
            return false;
        }

        for (AbsenceModel other : otherAbsences) {
            if (other == null || other.getStartTime() == null || other.getEndTime() == null) {
                continue;
            }

            if (this.id != null && this.id.equals(other.getId())) {
                continue;
            }

            if(!this.getDate().isEqual(other.getDate())){
                continue;
            }

            if (this.startTime.isBefore(other.getEndTime()) && this.endTime.isAfter(other.getStartTime())) {
                return true;
            }
        }
        return false;
    }
}
