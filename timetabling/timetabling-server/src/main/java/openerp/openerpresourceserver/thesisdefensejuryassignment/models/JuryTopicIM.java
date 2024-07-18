package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class JuryTopicIM {
    private String name;
    private List<String> academicKeywordList;

    private String teacherId;

    private String thesisDefensePlanId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAcademicKeywordList() {
        return academicKeywordList;
    }

    public void setAcademicKeywordList(List<String> academicKeywordList) {
        this.academicKeywordList = academicKeywordList;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getThesisDefensePlanId() {
        return thesisDefensePlanId;
    }

    public void setThesisDefensePlanId(String thesisDefensePlanId) {
        this.thesisDefensePlanId = thesisDefensePlanId;
    }
}
