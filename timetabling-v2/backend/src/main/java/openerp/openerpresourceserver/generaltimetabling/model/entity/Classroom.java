package openerp.openerpresourceserver.generaltimetabling.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "timetabling_classroom")
public class Classroom {

    @Id
    @Column(name = "classroom_id", updatable = false, nullable = false)
    private String id;

    private String classroom;

    @ManyToOne
    private Building building;

    @Column(name = "quantity_max")
    private Long quantityMax;

    @Column(name = "description", length = 255)
    private String description;
}
