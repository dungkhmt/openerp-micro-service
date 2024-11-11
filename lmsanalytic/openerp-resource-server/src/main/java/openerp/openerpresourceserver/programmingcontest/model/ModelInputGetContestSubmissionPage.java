package openerp.openerpresourceserver.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelInputGetContestSubmissionPage {
    private int offset;
    private int limit;
    private Date fromDate;
    private Date toDate;
}

