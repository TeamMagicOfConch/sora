package magicofconch.sora.util.exception;


import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.util.Response;
import magicofconch.sora.util.ResponseCode;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Response<ResponseCode> handleCustomException(BusinessException ex) {
        return new Response<>(ex.getResponseCode());
    }
}
