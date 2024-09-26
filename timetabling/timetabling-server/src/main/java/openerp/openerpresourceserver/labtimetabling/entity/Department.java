package openerp.openerpresourceserver.labtimetabling.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "timetable_lab_department")
public class Department {
    @Id
    @Column(name = "id")
    private Long id;
    private String name;
}
