package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

public class ReviewerThesisIM {
    private String scheduledReviewerId;
    private String thesisId;

    public String getScheduledReviewerId() {
        return scheduledReviewerId;
    }

    public void setScheduledReviewerId(String scheduledReviewerId) {
        this.scheduledReviewerId = scheduledReviewerId;
    }

    public String getThesisId() {
        return thesisId;
    }

    public void setThesisId(String thesisId) {
        this.thesisId = thesisId;
    }
}
