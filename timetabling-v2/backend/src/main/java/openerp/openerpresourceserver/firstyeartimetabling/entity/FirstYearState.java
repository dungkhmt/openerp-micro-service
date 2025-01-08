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
@Table(name = "first_year_timetabling_state")
public class FirstYearState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "state_id", updatable = false, nullable = false)
    private Long id;
    private String state;
}
