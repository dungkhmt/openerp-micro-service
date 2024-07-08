package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class AssignJuryTopicToThesisIM {
    private String thesisId;
    private int juryTopicId;
    private int secondJuryTopicId;

    public String getThesisId() {
        return thesisId;
    }

    public void setThesisId(String thesisId) {
        this.thesisId = thesisId;
    }

    public int getJuryTopicId() {
        return juryTopicId;
    }

    public void setJuryTopicId(int juryTopicId) {
        this.juryTopicId = juryTopicId;
    }

    public int getSecondJuryTopicId() {
        return secondJuryTopicId;
    }

    public void setSecondJuryTopicId(int secondJuryTopicId) {
        this.secondJuryTopicId = secondJuryTopicId;
    }
}
