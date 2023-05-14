package wms.common.enums;

public enum CommonStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");
    private final String status;

    CommonStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

}
