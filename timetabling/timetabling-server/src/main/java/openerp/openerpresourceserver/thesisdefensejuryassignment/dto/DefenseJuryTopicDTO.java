package openerp.openerpresourceserver.thesisdefensejuryassignment.dto;

import lombok.Data;

import java.util.List;

@Data

public class DefenseJuryTopicDTO {
    private String thesisDefensePlanId;
    private String thesisDefensePlanName;


    private List<JuryTopicTeacherDTO> juryTopicTeacherList;

    public String getThesisDefensePlanId() {
        return thesisDefensePlanId;
    }

    public void setThesisDefensePlanId(String thesisDefensePlanId) {
        this.thesisDefensePlanId = thesisDefensePlanId;
    }

    public String getThesisDefensePlanName() {
        return thesisDefensePlanName;
    }

    public void setThesisDefensePlanName(String thesisDefensePlanName) {
        this.thesisDefensePlanName = thesisDefensePlanName;
    }

    public List<JuryTopicTeacherDTO> getJuryTopicTeacherList() {
        return juryTopicTeacherList;
    }

    public void setJuryTopicTeacherList(List<JuryTopicTeacherDTO> juryTopicTeacherList) {
        this.juryTopicTeacherList = juryTopicTeacherList;
    }
}
