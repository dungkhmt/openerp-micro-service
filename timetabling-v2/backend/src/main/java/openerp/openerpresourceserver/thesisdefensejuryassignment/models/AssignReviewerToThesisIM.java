package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class AssignReviewerToThesisIM {
    public String defenseJuryId;
    public List<ReviewerThesisIM> reviewerThesisList;

    public String getDefenseJuryId() {
        return defenseJuryId;
    }

    public void setDefenseJuryId(String defenseJuryId) {
        this.defenseJuryId = defenseJuryId;
    }

    public List<ReviewerThesisIM> getReviewerThesisList() {
        return reviewerThesisList;
    }

    public void setReviewerThesisList(List<ReviewerThesisIM> reviewerThesisList) {
        this.reviewerThesisList = reviewerThesisList;
    }
}
