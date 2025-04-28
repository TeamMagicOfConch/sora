package magicofconch.soraadmin.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class Response<T> {
    private HttpStatus status;
    private String code;
    private String message;
    private T data;

    public static Response<Void> ok(){
        Response<Void> response = new Response<>();
        response.status = org.springframework.http.HttpStatus.OK;
        response.code = ResponseCode.SUCCESS.getCode();
        response.message = ResponseCode.SUCCESS.getMessage();

        return response;
    }

    public static <T> Response<T> toResponse(T data, ResponseCode responseCode) {
        Response<T> response = new Response<>();
        response.status = responseCode.getStatus();
        response.code = responseCode.getCode();
        response.message = responseCode.getMessage();
        response.data = data;

        return response;
    }

    public Response(ResponseCode responseCode){
        this.code = responseCode.getCode();
        this.status = responseCode.getStatus();
        this.message = responseCode.getMessage();
        this.data = null;
    }
}
