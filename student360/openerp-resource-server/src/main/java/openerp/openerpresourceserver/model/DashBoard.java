package openerp.openerpresourceserver.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DashBoard {
    private long totalSubmissions;
    private long totalUserActive;
    private long totalProblem;
    private long totalQuizQuestion;
}
