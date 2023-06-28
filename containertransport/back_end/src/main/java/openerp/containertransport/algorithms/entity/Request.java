package openerp.containertransport.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openerp.containertransport.algorithms.constants.Constants;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Request implements Serializable {
    private Long requestId;
    private long earliestTimePickup; // 2023-02-03 10:30:00
    private long latestTimePickup; // 2023-02-03 11:00:00
    private int fromLocationID;

    private long earliestTimeDelivery; // 2023-02-03 10:30:00
    private long latestTimeDelivery; // 2023-02-03 11:00:00
    private int toLocationID;

    private String type;
    private long containerID;
    private int weightContainer;
    private String orderCode;
    private Boolean isBreakRomooc;
}
