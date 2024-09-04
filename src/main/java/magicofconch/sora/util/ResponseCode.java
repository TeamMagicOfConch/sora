package magicofconch.sora.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {

    //General exception
    SUCCESS(200, "GEN-000", HttpStatus.OK, "Success"),

    //Validation exception response
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
