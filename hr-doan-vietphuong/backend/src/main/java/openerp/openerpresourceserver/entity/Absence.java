package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.*;
import openerp.openerpresourceserver.converter.ListStringConverter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "phuongvv_absences")
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "length")
    private Double length;

    @Column(name = "type")
    private Integer type;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "status")
    private Integer status;

    @Column(name = "note")
    private String note;

    @Column(name = "reason")
    private String reason;

    @Column(name = "email_status")
    private Integer emailStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sub_type", referencedColumnName = "id")
    private AbsenceType absenceType;

    @Column(name = "lead_id")
    private Long leadId;

    @Convert(converter = ListStringConverter.class)
    @Column(name = "cc_list")
    private List<String> ccList;
}

