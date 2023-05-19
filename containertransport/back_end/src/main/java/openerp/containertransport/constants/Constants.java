package openerp.containertransport.constants;

import lombok.Getter;

import java.io.Serializable;

public class Constants implements Serializable {
    @Getter
    public enum ActionType {
        MONTHLY(1, "MONTHLY"),
        WEEKLY(2, "WEEKLY"),
        DAILY(3, "DAILY");
        private final Integer code;
        private final String value;

        ActionType(Integer code, String value) {
            this.code = code;
            this.value = value;
        }
    }
}
