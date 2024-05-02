package openerp.openerpresourceserver.labtimetabling.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "timetable_lab_auto_scheduling_result")
@NoArgsConstructor
public class AutoSchedulingResult {

    @Id
    private UUID id;
    private String result;
    private Long semester_id;

    @ManyToOne
    @JoinColumn(name = "semester_id", insertable=false, updatable=false)
    private Semester_ semester;

    @Column(name = "created_time")
    private Date created_time;
}
