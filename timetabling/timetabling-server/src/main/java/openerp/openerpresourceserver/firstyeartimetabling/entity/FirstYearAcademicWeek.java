package openerp.openerpresourceserver.firstyeartimetabling.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="first_year_academic_weeks")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class FirstYearAcademicWeek {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int weekIndex;

    private String startDayOfWeek;

    private String semester;
}
