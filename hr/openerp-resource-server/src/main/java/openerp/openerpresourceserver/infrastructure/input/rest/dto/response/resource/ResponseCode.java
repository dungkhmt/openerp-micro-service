package openerp.openerpresourceserver.infrastructure.input.rest.dto.response.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {
    OK(1000L, "OK"),
    POST_IS_NOT_EXISTED(9992L, "post is not existed"),
    CODE_VERIFY_IS_INCORRECT(9993L, "code verify is incorrect"),
    NO_DATA_OR_END_OF_LIST_DATA(9994L, "no data or end of list data"),
    USER_IS_NOT_VALIDATED(9995L, "user is not validated"),
    USER_EXISTED(9996L, "user existed"),
    METHOD_IS_INVALID(9997L, "method is invalid"),
    TOKEN_IS_INVALID(9998L, "token is invalid"),
    EXCEPTION_ERROR(9999L, "exception error"),
    CAN_NOT_CONNECT_TO_DATABASE(1001L, "can not connect to database"),
    PARAMETER_IS_NOT_ENOUGH(1002L, "parameter is not enough"),
    PARAMETER_TYPE_IS_INVALID(1003L, "parameter type is invalid"),
    PARAMETER_VALUE_IS_INVALID(1004L, "parameter value is invalid"),
    UNKNOWN_ERROR(1005L, "unknown error"),
    FILE_SIZE_IS_TOO_BIG(1006L, "file size is too big"),
    UPLOAD_FILE_FAILED(1007L, "upload file failed!"),
    MAXIMUM_NUMBER_OF_IMAGES(1008L, "maximum number of images"),
    NOT_ACCESS(1009L, "not access"),
    ACTION_HAS_BEEN_DONE_PREVIOUSLY_BY_THIS_USER(1010L, "action has been done previously"),
    INVALID_EMAIL_FORMAT(1011L, "invalid email format"),
    INVALID_PASSWORD_LENGTH(1012L, "Password length must be between 6 and 10 characters"),
    ADD_STUDENT_ERROR(1013L, "add student error"),
    ;
    private final long code;
    private final String message;
}
