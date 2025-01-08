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
@Table(name = "first_year_timetabling_open_batch")
public class FirstYearOpenBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "open_batch_id", updatable = false, nullable = false)
    private Long id;
    private String openBatch;
}
