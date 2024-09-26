package openerp.openerpresourceserver.labtimetabling.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "opening-class-plan")
public class ClassPlan implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String semester;
    private String department_id;
    private String course_code;
    private String course_name;
    private String amount;
    private String note;
    private String program;
    private String year;
    private String avoid;
    private String expected_schedule;

    private Long number_of_prev_sem_classes;
    private Long registered_quantity;
    private Long expected_number_of_classes;
    private Long number_of_lessons;
    private Long total_lessons;

}
