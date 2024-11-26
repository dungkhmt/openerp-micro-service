package openerp.openerpresourceserver.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseGetContestSubmissionPage {
    List<ContestSubmissionEntity> submissions;
}
