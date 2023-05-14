package wms.common.enums;

public enum Status {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    DELETED("DELETED");
    private final String status;

    Status(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
}
