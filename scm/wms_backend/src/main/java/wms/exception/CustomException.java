package wms.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomException extends Exception {

    public CustomException(Exception ex) {
        super(ex);
    }

    public CustomException(int code, Exception ex) {
        super(ex);
        this.code = code;
    }

    public CustomException(int code, String  message) {
        super(message);
        this.code = code;
    }


    public CustomException(String message) {
        super(new Exception(message));
        this.code = 500;
    }

    private Integer code;
    private Object data;
}
