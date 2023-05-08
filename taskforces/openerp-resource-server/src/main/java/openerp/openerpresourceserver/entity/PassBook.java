package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "passbook_passbook")
public class PassBook {
    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CLOSED = "CLOSED";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pass_book_id")
    private UUID passBookId;

    @Column(name="pass_book_name")
    private String passBookName;

    @Column(name="amount_money_deposit")
    private int amountMoneyDeposit;

    @Column(name="user_id")
    private String userId; // customerId

    @Column(name="rate")
    private double rate;

    @Column(name="duration")
    private int duration; // in month

    @Column(name = "status_id")
    private String statusId;

    @Column(name="created_by_user_id")
    private String createdByUserId;

    @Column(name="created_date")
    private Date createdDate;

    @Column(name="end_date")
    private Date endDate;

    @Column(name="created_stamp")
    private Date createdStamp;

}
