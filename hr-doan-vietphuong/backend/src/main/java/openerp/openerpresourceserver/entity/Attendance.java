package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "phuongvv_attendances")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AttendanceId.class)
public class Attendance {
    @Column(name = "id")
    @Id
    private Integer id;

    @Column(name = "time")
    @Id
    private LocalDateTime time;

    @Column(name = "date")
    private Integer date;

    @Column(name = "ip")
    private String ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id")
    private Employee employee;
}
