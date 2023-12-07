package openerp.openerpresourceserver.model.entity;

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
@Table(name = "timetabling_module")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id", updatable = false, nullable = false)
    private Long id;
    private String moduleCode;
    private String moduleName;
    private String moduleNameByEnglish;
    private String mass;
}
