package wms.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import wms.entity.UserRegister;

import javax.persistence.*;

@Data
public class TruckResponseDTO {
    private Long id;
    private String code;

    private String name;

    private String size;

    private double capacity;

    private double speed;

    private double transportCostPerUnit;

    private double waitingCost;

    private String userName;
}
