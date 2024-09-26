package openerp.containertransport.constants;

import lombok.Getter;

@Getter
public enum MetaData {
    // Successful responses
    SUCCESS(200, "Success"),
    NO_CONTENT(204, "No Content"),

    // Pending and reset account response
    PENDING_ACCOUNT(201, "Pending Account"),
    RESET_ACCOUNT(202, "Reset Account"),

    // Redirection messages
    MULTIPLE_CHOICE(300, "Multiple Choice"),
    BAD_CREDENTIALS(301, "Bad Credentials"),

    // Client error responses
    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    REQUEST_TIMEOUT(408, "Request Timeout"),

    // Server error responses
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    BAD_GATEWAY(502, "Bad Gateway"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),

    // Login response
    INVALID_TOKEN(1000, "Invalid token"),
    INCORRECT_ACCOUNT(1100, "Incorrect accountID/password"),
    MULTIPLE_LOGIN_FAILED(1200, "Too many login failures. Your account has been locked. Please reset your password to continue."),
    INVALID_PASSWORD(1300, "Invalid password, must contain at least 1 uppercase letter"),
    FIELD_NOT_BLANK(1500, "Field is not blank"),
    REPEAT_OLD_PASSWORD(1501, "Invalid old password, must be different with %s last password"),
    INVALID_2FA_TOKEN(1700, "Invalid 2FA token"),

    REQUIRED_JWT_ACCESS_TOKEN(1701, "Required jw_access_token"),
    MISSING_ACCOUNT_NO_JWT_REQUEST_TOKEN(1702, "Missing accountNo in payload"),
    NOT_MATCH_END_USER_IP(1703, "Not match endUserIP"),
    MISSING_END_USER_IP(1704, "Missing endUserIP in payload"),
    INVALID_CLIENT_ID(1705, "Invalid clientId, accepted values: pspl-poems-web / pspl-poems-mobile / pspl-msf");

    private final Integer metaCode;
    private final String message;

    MetaData(Integer metaCode, String message) {
        this.metaCode = metaCode;
        this.message = message;
    }
}
