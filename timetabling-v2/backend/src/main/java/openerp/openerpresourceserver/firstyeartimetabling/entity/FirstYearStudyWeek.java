package openerp.openerpresourceserver.firstyeartimetabling.entity;

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
@Table(name = "first_year_timetabling_studyweek")
public class FirstYearStudyWeek {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyweek_id", updatable = false, nullable = false)
    private Long id;
    private String studyweek;
}
