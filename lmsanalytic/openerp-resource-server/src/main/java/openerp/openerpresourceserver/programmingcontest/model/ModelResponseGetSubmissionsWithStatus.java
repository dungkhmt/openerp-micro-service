package openerp.openerpresourceserver.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseGetSubmissionsWithStatus {
    private UUID id;
    private UUID contestSubmissionId;
    private String userSubmissionId;
    private String status;
    private Date createdStamp;
}

