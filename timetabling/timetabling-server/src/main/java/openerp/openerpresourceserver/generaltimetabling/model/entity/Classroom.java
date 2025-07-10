package openerp.openerpresourceserver.generaltimetabling.model.entity;

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
@Table(name = "timetabling_classroom")
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroom_id", updatable = false, nullable = false)
    private Long id;
    private String classroom;
    private String building;
    private Long quantityMax;
    private String description;
}
