package com.hust.wmsbackend.management.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "delivery_person")
public class DeliveryPerson {
    @Id
    private String userLoginId;
    private String fullName;
    private String phoneNumber;
}
