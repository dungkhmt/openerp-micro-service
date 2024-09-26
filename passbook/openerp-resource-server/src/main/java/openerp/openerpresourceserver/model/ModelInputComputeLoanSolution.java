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
public class ModelInputComputeLoanSolution {
    private String userId;
    private int loan;
    private String date;
    private double loadRate;
    private double discountRate;
}
