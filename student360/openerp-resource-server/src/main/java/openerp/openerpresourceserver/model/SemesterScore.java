package openerp.openerpresourceserver.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SemesterScore {
    private String semester;
    private long numStudentsWithGradeA;
    private long numStudentsWithGradeB;
    private long numStudentsWithGradeC;
    private long numStudentsWithGradeD;
    private long numStudentsWithGradeF;
    private double growthGradeA;
    private double growthGradeB;
    private double growthGradeC;
    private double growthGradeD;
    private double growthGradeF;
}
