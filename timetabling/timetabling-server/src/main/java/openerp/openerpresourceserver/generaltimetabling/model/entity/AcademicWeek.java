package openerp.openerpresourceserver.generaltimetabling.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Table(name="academic_weeks")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class AcademicWeek {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int weekIndex;

    private String startDayOfWeek;

    private String semester;
}
