package openerp.openerpresourceserver.labtimetabling.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "timetable_lab_semester")
@Data
@NoArgsConstructor
public class Semester_ implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String semester;
}
