package cent.wong.compedia.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // common error
    INTERNAL_SERVER_ERROR("INTERNAL-001", "Internal Server Error"),
    BAD_REQUEST("INTERNAL-002", "Bad Request"),

    // security error
    AUTHORIZATION_HEADER_REQUIRED("SECURITY-001", "Authorization header is required"),
    JWT_FORMAT_INVALID("SECURITY-002", "Invalid JWT token format"),
    PARSE_JWT_ERROR("SECURITY-003", "Error when parsing JWT. Please contact IT team");

    private final String errCode;

    private final String message;

    ErrorCode(String errCode, String message) {
        this.errCode = errCode;
        this.message = message;
    }
}
