package wms.common.enums;

public enum HttpMethodType {
    POST(0),
    GET(1),
    DELETE(2);
    private final Integer type;

    HttpMethodType(Integer type) {
        this.type = type;
    }
    public Integer getType() {
        return type;
    }

}
