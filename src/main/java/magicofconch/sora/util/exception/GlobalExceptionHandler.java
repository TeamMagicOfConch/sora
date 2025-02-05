package magicofconch.sora.util.exception;


import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.util.Response;
import magicofconch.sora.util.ResponseCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Response<ResponseCode> handleBusinessException(BusinessException e) {
        return new Response<>(e.getResponseCode());
    }

    @ExceptionHandler(RedirectException.class)
    public ResponseEntity handleRedirectException(RedirectException e) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(e.getRedirectURI()));
        return new ResponseEntity(httpHeaders, HttpStatus.TEMPORARY_REDIRECT);
    }
}
