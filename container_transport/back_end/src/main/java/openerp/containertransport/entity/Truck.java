package openerp.containertransport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openerp.containertransport.dto.TruckModel;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "container_transport_trucks")
public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "truck_code")
    private String truckCode;

    @Column(name = "facility_id")
    private Integer facilityId;

    @Column(name = "driver_id")
    private Integer driverId;

    @Column(name = "license plates")
    private String licensePlates;

    @Column(name = "brand_truck")
    private String brandTruck;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "updated_at")
    private long updatedAt;

    public static TruckModel convertToModel(Truck truck) {
        ModelMapper modelMapper = new ModelMapper();
        TruckModel truckModel = modelMapper.map(truck, TruckModel.class);
        return truckModel;
    }
}
