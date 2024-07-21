package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor

public class UpdateDefenseJuryIM {
    private String id;
    private String name;
    private Date defenseDate;
    private int defenseRoomId;
    private List<Integer> defenseSessionId;
    private int maxThesis;

//    private int juryTopicId;
//
//    public int getJuryTopicId() {
//        return juryTopicId;
//    }
//
//    public void setJuryTopicId(int juryTopicId) {
//        this.juryTopicId = juryTopicId;
//    }
    //    private List<String> academicKeywordList;

//    public List<String> getAcademicKeywordList() {
//        return academicKeywordList;
//    }
//
//    public void setAcademicKeywordList(List<String> academicKeywordList) {
//        this.academicKeywordList = academicKeywordList;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDefenseDate() {
        return defenseDate;
    }

    public void setDefenseDate(Date defenseDate) {
        this.defenseDate = defenseDate;
    }

    public int getDefenseRoomId() {
        return defenseRoomId;
    }

    public void setDefenseRoomId(int defenseRoomId) {
        this.defenseRoomId = defenseRoomId;
    }

    public List<Integer> getDefenseSessionId() {
        return defenseSessionId;
    }

    public void setDefenseSessionId(List<Integer> defenseSessionId) {
        this.defenseSessionId = defenseSessionId;
    }

    public int getMaxThesis() {
        return maxThesis;
    }

    public void setMaxThesis(int maxThesis) {
        this.maxThesis = maxThesis;
    }
}
