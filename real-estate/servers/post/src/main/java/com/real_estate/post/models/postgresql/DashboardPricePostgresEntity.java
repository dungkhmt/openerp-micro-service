package com.real_estate.post.models.postgresql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
        name = "dashboard_price"
)
public class DashboardPricePostgresEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dashboard_price_id")
    Long dashboardPriceId;

    @Column(name = "district_id")
    String districtId;

    @Column(name = "name_district")
    String nameDistrict;

    @Column(name = "type_property")
    String typeProperty;

    @Column(name = "highest_pricePerM2")
    Float highestPricePerM2;

    @Column(name = "lowest_pricePerM2")
    Float lowestPricePerM2;

    @Column(name = "medium_pricePerM2")
    Double mediumPricePerM2;

    @Column(name = "start_time")
    Long startTime;

    @Column(name = "end_time")
    Long endTime;
}
