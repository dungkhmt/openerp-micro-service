package openerp.openerpresourceserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.HubType;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "smartdelivery_hub")
public class Hub {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid1")
    private UUID hubId;

    private String name;

    private String code;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String address;

    private Double width;

    private Double length;


    @OneToMany(mappedBy = "hub", cascade = {CascadeType.MERGE,CascadeType.PERSIST}, orphanRemoval = true)
    @JsonIgnore
    private List<Shipper> shipperList;

    @OneToMany(mappedBy = "hub", cascade = {CascadeType.MERGE,CascadeType.PERSIST}, orphanRemoval = true)
    @JsonIgnore
    private List<Collector> collectorList;

    @OneToMany(mappedBy = "originHub")
    @JsonIgnore
    private List<Driver> originDriverList;

    @OneToMany(mappedBy = "finalHub")
    @JsonIgnore
    private List<Driver> finalDriverList;

    @OneToMany(mappedBy = "originHub")
    @JsonIgnore
    private List<Order> originHubOrders;

    @OneToMany(mappedBy = "finalHub")
    @JsonIgnore
    private List<Order> finalHubOrders;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;


}
