package wms.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Drone {
    private String ID;
    private double capacity; // maximum product weight
    private double durationCapacity; // max duration time
    private double speed;
    private double transportCostPerUnit;
    private double waitingCost;

    public List<Request> getEligibleServedByDroneCustomer(List<Request> customers) {
        List<Request> eligibleCustomers = new ArrayList<>();
        for (Request customer : customers) {
            if (isAbleToServeACustomer(customer)) {
                eligibleCustomers.add(customer);
            }
        }
        return eligibleCustomers;
    }
    public boolean isAbleToServeACustomer(Request customer) {
        return customer.getWeight() <= this.capacity;
    }

    public boolean isAbleToFly(double addedUpCost) {
        return addedUpCost <= durationCapacity;
    }
}
