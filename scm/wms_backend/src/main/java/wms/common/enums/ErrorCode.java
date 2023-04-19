package wms.common.enums;

public enum ErrorCode {
    UNKNOWN(9),
    ALREADY_EXIST(2),
    NON_EXIST(3),
    FORMAT(4),
    AUTHENTICATION(5),
    USER_ACTION_FAILED(6),
    UNMATCH(7),
    SYSTEM_ACTION_FAILED(8);

    private final int code;
    ErrorCode(int value) {
        this.code = value;
    }
    public int getCode() {
        return this.code;
    }
}
