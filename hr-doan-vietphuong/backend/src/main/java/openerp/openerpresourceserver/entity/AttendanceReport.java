package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "phuongvv_attendance_reports")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "attendance_range_id")
    private Long attendanceRangeId;

    @Column(name = "date")
    private Integer date;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "attendance_time")
    private Double attendanceTime;

    @Column(name = "raw_start_time")
    private LocalDateTime rawStartTime;

    @Column(name = "raw_end_time")
    private LocalDateTime rawEndTime;

    @Column(name = "raw_attendance_time")
    private Double rawAttendanceTime;

    @Column(name = "leave_time")
    private Double leaveTime;

    @Column(name = "status")
    private Integer status;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "employee_id",
            referencedColumnName = "employee_id",
            insertable = false,
            updatable = false)
    private Employee employee;
}