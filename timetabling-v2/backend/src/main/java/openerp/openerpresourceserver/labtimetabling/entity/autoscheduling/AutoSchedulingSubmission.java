package openerp.openerpresourceserver.labtimetabling.entity.autoscheduling;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.entity.Assign;
import openerp.openerpresourceserver.labtimetabling.entity.Semester_;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "timetable_lab_auto_scheduling_submission")
@NoArgsConstructor
public class AutoSchedulingSubmission {
    @Id
    private UUID id;
    private Long semester_id;
    private Long status;
    @ManyToOne
    @JoinColumn(name = "semester_id", insertable=false, updatable=false)
    private Semester_ semester;
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "submission_id")
//    private List<AutoSchedulingResult> resultList;

    @Column(name = "created_time")
    private Date created_time;
    private AutoSchedulingSubmission(Builder builder){
        this.id = builder.id;
        this.semester_id = builder.semester_id;
        this.created_time = builder.created_time;
        this.status = builder.status;
    }
    public static class Builder{
        private UUID id;
        private Long semester_id;
        private Date created_time;
        private Long status;
        public Builder(){
            this.id = UUID.randomUUID();
            this.semester_id = 0L;
            this.created_time = new Date();
        }
        public Builder setId(UUID id){
            this.id = id;
            return this;
        }
        public Builder setStatus(Long status){
            this.status = status;
            return this;
        }
        public Builder setSemesterId(Long id){
            this.semester_id = id;
            return this;
        }
        public AutoSchedulingSubmission build(){
            return new AutoSchedulingSubmission(this);
        }
    }
}
