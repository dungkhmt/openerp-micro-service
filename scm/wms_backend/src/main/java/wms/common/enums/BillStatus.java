package wms.common.enums;

public enum BillStatus {
    CREATED("CREATED"),
    SPLITTED("SPLITTED"),

    ASSIGNED("ASSIGNED");
    private final String status;

    BillStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

}
