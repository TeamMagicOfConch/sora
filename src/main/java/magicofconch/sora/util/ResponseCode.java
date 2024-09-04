package magicofconch.sora.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {

    SUCCESS(200, "GEN-000", HttpStatus.OK, "Success");

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
