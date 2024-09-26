package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
public class UpdateJuryTopicIM {
    private String name;
    private List<String>  academicKeywordList;


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
}
