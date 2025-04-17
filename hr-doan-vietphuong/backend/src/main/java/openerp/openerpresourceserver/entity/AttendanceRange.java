package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.*;
import openerp.openerpresourceserver.enums.AbsenceTimeEnum;
import openerp.openerpresourceserver.util.DateUtil;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "phuongvv_attendance_ranges")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private String code;

    private String description;
    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "start_lunch")
    private LocalTime startLunch;

    @Column(name = "end_lunch")
    private LocalTime endLunch;

    @Column(name = "full_hours")
    private Double fullHours;

    @Column(name = "bonus_time") // in minutes
    private Integer bonusTime;

    @Column(name = "status")
    private Integer status;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    public int getShift(final double leaveTime) {
        double morningShiftHours = DateUtil.getDifferenceInHours(startTime, startLunch);
        return leaveTime == morningShiftHours ? AbsenceTimeEnum.AM.ordinal() : AbsenceTimeEnum.PM.ordinal();
    }

}
