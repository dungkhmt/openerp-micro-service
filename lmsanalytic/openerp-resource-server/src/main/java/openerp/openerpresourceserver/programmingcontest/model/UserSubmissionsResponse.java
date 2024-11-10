package openerp.openerpresourceserver.programmingcontest.model;

import openerp.openerpresourceserver.programmingcontest.entity.LmsContestSubmission;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSubmissionsResponse {
    private int numberOfSubmissions;
    private int numberOfAcceptedSubmissions;
    private int numberOfCompileErrorSubmissions;
    private List<LmsContestSubmission> data;
}
