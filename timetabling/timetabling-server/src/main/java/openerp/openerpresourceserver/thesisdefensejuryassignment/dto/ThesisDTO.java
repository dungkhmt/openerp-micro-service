package openerp.openerpresourceserver.thesisdefensejuryassignment.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.AcademicKeyword;

import java.util.List;
import java.util.UUID;

@Data
public class ThesisDTO {
    private UUID id;
    private String thesisName;
    private String thesisAbstract;
    private String supervisor;
    private String thesisDefensePlanId; // *
    private UUID defenseJuryId;

    private String defenseJuryName;

    private String scheduledReviewer;

    private String juryTopicName;

    private String secondJuryTopicName;
    private String studentName;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    private List<AcademicKeyword> academicKeywordList;

    public String getDefenseJuryName() {
        return defenseJuryName;
    }

    public void setDefenseJuryName(String defenseJuryName) {
        this.defenseJuryName = defenseJuryName;
    }

    public String getScheduledReviewer() {
        return scheduledReviewer;
    }

    public void setScheduledReviewer(String scheduledReviewer) {
        this.scheduledReviewer = scheduledReviewer;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getThesisName() {
        return thesisName;
    }

    public void setThesisName(String thesisName) {
        this.thesisName = thesisName;
    }

    public String getThesisAbstract() {
        return thesisAbstract;
    }

    public void setThesisAbstract(String thesisAbstract) {
        this.thesisAbstract = thesisAbstract;
    }


    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getThesisDefensePlanId() {
        return thesisDefensePlanId;
    }

    public void setThesisDefensePlanId(String thesisDefensePlanId) {
        this.thesisDefensePlanId = thesisDefensePlanId;
    }

    public UUID getDefenseJuryId() {
        return defenseJuryId;
    }

    public void setDefenseJuryId(UUID defenseJuryId) {
        this.defenseJuryId = defenseJuryId;
    }

    public String getJuryTopicName() {
        return juryTopicName;
    }

    public void setJuryTopicName(String juryTopicName) {
        this.juryTopicName = juryTopicName;
    }

    public String getSecondJuryTopicName() {
        return secondJuryTopicName;
    }

    public void setSecondJuryTopicName(String secondJuryTopicName) {
        this.secondJuryTopicName = secondJuryTopicName;
    }

    public List<AcademicKeyword> getAcademicKeywordList() {
        return academicKeywordList;
    }

    public void setAcademicKeywordList(List<AcademicKeyword> academicKeywordList) {
        this.academicKeywordList = academicKeywordList;
    }
}
