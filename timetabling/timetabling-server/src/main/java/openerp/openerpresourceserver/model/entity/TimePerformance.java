package openerp.openerpresourceserver.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "timetabling_performance_time")
public class TimePerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_time_id", updatable = false, nullable = false)
    private Long id;
    private String semester;
    private String classRoom;
    private String studyWeek;
    private String weekDay;
    private String performanceTime;
}
