package openerp.openerpresourceserver.thesisdefensejuryassignment.dto;

import lombok.Data;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.AcademicKeyword;
import java.util.List;

@Data
public class JuryTopicDTO {
    private String name;
    private List<AcademicKeyword> academicKeywordList;

    public JuryTopicDTO(String name, List<AcademicKeyword> academicKeywordList) {
        this.name = name;
        this.academicKeywordList = academicKeywordList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AcademicKeyword> getAcademicKeywordList() {
        return academicKeywordList;
    }

    public void setAcademicKeywordList(List<AcademicKeyword> academicKeywordList) {
        this.academicKeywordList = academicKeywordList;
    }
}
