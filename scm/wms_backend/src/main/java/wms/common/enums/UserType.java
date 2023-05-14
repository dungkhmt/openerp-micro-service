package wms.common.enums;

/**
 * Phan quyen cua nguoi dung <br>
 * 1: admin <br>
 * 2: warehouse manager<br>
 * 3: purchase admin <br>
 * 4: purchase staff <br>
 * 5: sale admin <br>
 * 6: sales staff <br>
 * 7: delivery admin <br>
 * 8: delivery staff <br>
 */
public enum UserType {

    ADMIN(1),
    WH_MANAGER(2),
    PURCHASE_ADMIN(3),
    PURCHASE_STAFF(4),
    SALES_ADMIN(5),
    SALES_STAFF(6),
    DELIVERY_ADMIN(7),
    DELIVERY_STAFF(8);

    private final int value;

    UserType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserType getEnumById(Integer id) {
        for (UserType position : values()) {
            if (position.getValue() == id) {
                return position;
            }
        }
        return null;
    }
}
