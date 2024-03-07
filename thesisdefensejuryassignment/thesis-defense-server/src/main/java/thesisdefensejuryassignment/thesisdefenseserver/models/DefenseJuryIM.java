package thesisdefensejuryassignment.thesisdefenseserver.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefenseJuryIM {

    private String name;
    //    private String program_name;
    private int maxThesis;
    private String thesisPlanName;
    private Date defenseDate;

    private List<String> academicKeywordList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getUserLoginID() {
//        return userLoginID;
//    }
//
//    public void setUserLoginID(String userLoginID) {
//        this.userLoginID = userLoginID;
//    }

    public int getMaxThesis() {
        return maxThesis;
    }

    public void setMaxThesis(int maxThesis) {
        this.maxThesis = maxThesis;
    }

    public String getThesisPlanName() {
        return thesisPlanName;
    }

    public void setThesisPlanName(String thesisPlanName) {
        this.thesisPlanName = thesisPlanName;
    }

    public Date getDefenseDate() {
        return defenseDate;
    }

    public void setDefenseDate(Date defenseDate) {
        this.defenseDate = defenseDate;
    }

    public List<String> getAcademicKeywordList() {
        return academicKeywordList;
    }

    public void setAcademicKeywordList(List<String> academicKeywordList) {
        this.academicKeywordList = academicKeywordList;
    }
}
