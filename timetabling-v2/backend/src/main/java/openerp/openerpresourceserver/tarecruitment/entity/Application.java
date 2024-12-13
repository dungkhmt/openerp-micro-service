package openerp.openerpresourceserver.tarecruitment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.generaltimetabling.model.entity.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ta_application")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "class_call_id", referencedColumnName = "id")
    private ClassCall classCall;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_login_id")
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "mssv")
    private String mssv;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "cpa")
    private double CPA;

    @Column(name = "english_score")
    private String englishScore;

    @Column(name = "note")
    private String note;

    @Column(name = "application_status")
    private String applicationStatus;

    @Column(name = "assignStatus")
    private String assignStatus;

}
