package openerp.containertransport.algorithms.constants;

import lombok.Getter;

import java.io.Serializable;

public class Constants implements Serializable {
    @Getter
    public enum RequestType {
        IN_BOUND_FULL("in_bound_full"),
        IN_BOUND_EMPTY("in_bound_empty"),
        OUT_BOUND_FULL("out_bound_full"),
        OUT_BOUND_EMPTY("out_bound_empty");
        private final String type;

        RequestType(String type) {
            this.type = type;
        }
    }
    @Getter
    public enum ACTION {
        PICKUP_TRAILER("PICKUP_TRAILER"),
        DELIVERY_TRAILER("DELIVERY_TRAILER"),
        PICKUP_CONTAINER("PICKUP_CONTAINER"),
        DELIVERY_CONTAINER("DELIVERY_CONTAINER"),
        STOP("STOP"),
        DEPART("DEPART");
        private final String action;
        ACTION(String action) {
            this.action = action;
        }
    }
}
