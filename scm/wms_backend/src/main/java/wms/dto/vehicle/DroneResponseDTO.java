package wms.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import wms.entity.UserRegister;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Data
public class DroneResponseDTO {
    private Long id;
    private String code;

    private String name;

    private double capacity;

    private double speed;

    private double transportCostPerUnit;

    private double waitingCost;

    private double durationTime;

    private String userName;
}
