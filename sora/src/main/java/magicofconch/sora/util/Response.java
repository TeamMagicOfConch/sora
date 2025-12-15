package magicofconch.sora.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Schema(description = "API 응답 객체")
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    
    @Schema(description = "HTTP 상태 코드", example = "200")
    private Integer status;
    
    @Schema(description = "응답 코드", example = "SUCCESS")
    private String code;
    
    @Schema(description = "응답 메시지 (에러 시에만 포함)", example = "성공적으로 처리되었습니다")
    private String message;
    
    @Schema(description = "응답 데이터")
    private T data;

    public static Response<Void> ok(){
        Response<Void> response = new Response<>();
        response.status = HttpStatus.OK.value();
        response.code = ResponseCode.SUCCESS.getCode();
        return response;
    }

    public static <T> Response<T> ok(T data) {
        Response<T> response = new Response<>();
        response.status = HttpStatus.OK.value();
        response.code = ResponseCode.SUCCESS.getCode();
        response.data = data;
        return response;
    }

    public static <T> Response<T> errorResponse(ResponseCode code) {
        Response<T> response = new Response<>();
        response.status = code.getStatus();
        response.code = code.getCode();
        response.data = null;
        response.message = code.getMessage();
        return response;
    }

    public Response(ResponseCode responseCode){
        this.code = responseCode.getCode();
        this.status = responseCode.getStatus();
        this.message = responseCode.getMessage();
        this.data = null;
    }
}
