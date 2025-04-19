package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {
    OK(1000L, "OK"),
    API_HANDLER_EXCEPTION(9090L, "api handler exception"),
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
    ADD_STAFF_ERROR(1013L, "add staff error"),
    CREATE_STAFF_ERROR(1014L, "create staff error"),
    STAFF_NOT_EXIST(1015L, "staff not exist"),
    USER_NOT_EXIST(1016L, "user not exist"),
    STAFF_EXISTED(1017L, "staff existed"),
    DEPARTMENT_CODE_EXISTED(1018L, "department code existed"),
    DEPARTMENT_NAME_EXISTED(1019L, "department name existed"),
    DEPARTMENT_NOT_EXISTED(1020L, "department not exist"),
    JOB_POSITION_CODE_EXISTED(1021L, "job position code existed"),
    JOB_POSITION_NAME_EXISTED(1022L, "job position name existed"),
    JOB_POSITION_NOT_EXISTED(1023L, "job position not existed"),
    ASSIGN_JOB_POSITION_EXCEPTION(1024L, "assign job position exception"),
    STAFF_SALARY_NOT_EXISTED(1025L, "staff salary not existed"),
    CHECKPOINT_CONFIGURE_NOT_EXISTED(1026L, "checkpoint configure not existed"),
    CHECKPOINT_PERIOD_NOT_EXISTED(1027L, "checkpoint period not existed"),
    CHECKPOINT_PERIOD_UPDATE_ERROR(1028L, "checkpoint period update error"),
    HOLIDAY_NOT_EXISTED(1029L, "holiday not existed"),
    HOLIDAY_UPDATE_ERROR(1030L, "holiday update error"),
    HOLIDAY_CREATE_ERROR(1031L, "holiday create error"),
    HOLIDAY_DELETE_ERROR(1032L, "holiday delete error"),
    GET_COMPANY_CONFIG_ERROR(1033L, "get company config error"),
    FORMAT_CONFIG_ERROR(1034L, "format config error"),
    CONFIG_NOT_EXISTED(1035L, "config not existed"),
    UPDATE_ABSENCE_ERROR(1036L, "update absence error"),
    CREATE_ABSENCE_ERROR(1037L, "create absence error"),
    VALIDATE_ABSENCE_ERROR(1038L, "validate absence error"),
    CANCEL_ABSENCE_ERROR(1039L, "cancel absence error"),
    ABSENCE_NOT_EXISTED(1040L, "absence not existed"),
    UNAUTHORIZED(1041L, "unauthorized"),
    UNAUTHENTICATED(1042L, "unauthenticated"),
    ;
    private final long code;
    private final String message;
}
