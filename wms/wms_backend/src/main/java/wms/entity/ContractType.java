package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "contract_type")
public class ContractType {
    @Id
    @Column(name = "code", nullable = false, length = 20)
    private String id;

    @Column(name = "id", nullable = false)
    private Integer id1;

    @Column(name = "name", length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_code")
    private DistributingChannel channelCode;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}