package openerp.openerpresourceserver.thesisdefensejuryassignment.dto;

import lombok.Data;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.JuryTopic;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Teacher;

@Data
public class JuryTopicTeacherDTO {
    private JuryTopic juryTopic;
    private Teacher teacher;

    public JuryTopicTeacherDTO(JuryTopic juryTopic, Teacher teacher) {
        this.juryTopic = juryTopic;
        this.teacher = teacher;
    }

    public JuryTopic getJuryTopic() {
        return juryTopic;
    }

    public void setJuryTopic(JuryTopic juryTopic) {
        this.juryTopic = juryTopic;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
