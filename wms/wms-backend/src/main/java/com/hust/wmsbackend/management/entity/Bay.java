package com.hust.wmsbackend.management.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bay")
public class Bay {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid1")
    private UUID bayId;
    private UUID warehouseId;
    private String code;
    private Integer x;
    private Integer y;
    private Integer xLong; // width
    private Integer yLong; // length
}
