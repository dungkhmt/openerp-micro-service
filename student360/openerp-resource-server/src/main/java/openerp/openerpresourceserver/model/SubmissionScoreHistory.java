package openerp.openerpresourceserver.model;

import jakarta.persistence.*;
public class SubmissionScoreHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String problemId;
    private Integer submissionCount;
    private String maxSubmissionScore;
    private String firstSubmissionScore;
}
