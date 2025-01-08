package openerp.openerpresourceserver.firstyeartimetabling.entity;

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
@Table(name = "first_year_timetabling_module")
public class FirstYearModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id", updatable = false, nullable = false)
    private Long id;
    private String moduleCode;
    private String moduleName;
    private String moduleNameByEnglish;
    private String mass;
}
