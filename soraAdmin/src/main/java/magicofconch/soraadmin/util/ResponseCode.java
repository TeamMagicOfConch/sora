package magicofconch.soraadmin.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {

    // General
    SUCCESS(HttpStatus.OK, "GEN-000", "Success");

    private final HttpStatus status;
    private final String code;
    private final String message;


    ResponseCode(HttpStatus status, String code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }


}
