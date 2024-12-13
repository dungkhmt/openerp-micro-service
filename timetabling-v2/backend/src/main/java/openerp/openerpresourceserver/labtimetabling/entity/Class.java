package openerp.openerpresourceserver.labtimetabling.entity;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import io.swagger.models.auth.In;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CollectionType;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "timetable_lab_class")
@NoArgsConstructor
public class Class implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Class (Long id){
        this.id = id;
    }

    private Long class_code;
    private Integer quantity;
    private Integer period;
    private String note;
    private String course_name;
    private Long department_id;
    private Long semester_id;
    private Integer lessons_per_semester;
    private Integer week_schedule_constraint;

    @Type(IntArrayType.class)
    private Integer[] avoid_week_schedule_constraint;

    @OneToMany(mappedBy = "lesson", cascade = {CascadeType.PERSIST})
    private List<Assign> assigns;

    @ManyToOne
    @JoinColumn(name = "semester_id", insertable=false, updatable=false)
    private Semester_ semester;
}
