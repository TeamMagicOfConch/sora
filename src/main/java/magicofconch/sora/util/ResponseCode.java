package magicofconch.sora.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {

    //General
    SUCCESS(200, "GEN-000", HttpStatus.OK, "Success"),

    //Authentication
    NO_REFRESH_TOKEN(400, "SEC-001", HttpStatus.BAD_REQUEST, "Refresh token is blank"),
    REFRESH_TOKEN_EXPIRED(401, "SEC-001", HttpStatus.UNAUTHORIZED, "Refresh token is expired"),


    //User
    REGISTER_SUCCESS(200, "USR-000", HttpStatus.OK, "User is registered"),
    USER_ALREADY_REGISTERED(400, "USR-001", HttpStatus.BAD_REQUEST, "This User is already registered please login"),
    USER_NOT_FOUND(400, "USR-002", HttpStatus.BAD_REQUEST, "Can't find user"),
    LOGIN_FAIL(400, "USR-003", HttpStatus.BAD_REQUEST, "Login failed"),

    //Review
    REVIEW_ALREADY_EXIT(400, "RVW-001", HttpStatus.BAD_REQUEST, "Review is already exist"),

    //Validation
    ARGUMENT_NOT_VALID(400, "VAL-001", HttpStatus.BAD_REQUEST, "Argument not valid");

    private final Integer status;
    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ResponseCode(Integer status, String code, HttpStatus httpStatus, String message) {
        this.status = status;
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
