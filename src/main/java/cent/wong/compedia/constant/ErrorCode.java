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
    PARSE_JWT_ERROR("SECURITY-003", "Error when parsing JWT. Please contact IT team"),

    // user error
    ALREADY_GRADUATE_STUDENT_ERROR("STUDENT-001", "Only active students that are allowed to register"),
    STUDENT_NOT_FOUND_PDDIKTI("STUDENT-002", "Student not found on PDDIKTI"),
    EMAIL_ALREADY_EXIST("STUDENT-003", "Email already exist"),
    INVALID_CREDENTIAL("STUDENT-004", "User credential is invalid"),
    STUDENT_NOT_FOUND("STUDENT-005", "Student not found"),

    // competition error
    COMPETITION_NOT_FOUND("COMPETITION-001", "Competition not found"),

    // mentor data error
    MENTOR_DATA_NOT_FOUND("MENTOR-DATA-001", "Mentor data not found");

    private final String errCode;

    private final String message;

    ErrorCode(String errCode, String message) {
        this.errCode = errCode;
        this.message = message;
    }
}
