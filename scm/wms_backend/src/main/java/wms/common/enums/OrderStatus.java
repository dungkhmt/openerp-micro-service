package wms.common.enums;

public enum OrderStatus {
    CREATED("CREATED"),
    ACCEPTED("ACCEPTED"),
    DELIVERING("DELIVERING"),
    DELIVERED("DELIVERED"),
    DELETED("DELETED"),
    REJECTED("REJECTED");
    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

}
