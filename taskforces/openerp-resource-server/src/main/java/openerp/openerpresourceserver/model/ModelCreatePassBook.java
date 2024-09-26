package openerp.openerpresourceserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelCreatePassBook {
    private String passBookName;
    private String userId;
    private int duration;
    private Date startDate;
    private int amountMoney;
    private double rate;
}
