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
@Table(name = "timetabling_class_code")
public class ClassCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_code_id", updatable = false, nullable = false)
    private Long id;
    private String classCode;
    private String bundleClassCode;
}
