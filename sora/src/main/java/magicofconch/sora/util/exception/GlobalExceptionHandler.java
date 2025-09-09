package magicofconch.sora.util.exception;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.util.Response;
import magicofconch.sora.util.ResponseCode;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<?> handleBusinessException(BusinessException e) {
		ResponseCode responseCode = e.getResponseCode();
		return ResponseEntity.status(responseCode.getStatus()).body(Response.errorResponse(responseCode));
	}

	@ExceptionHandler(RedirectException.class)
	public ResponseEntity handleRedirectException(RedirectException e) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(URI.create(e.getRedirectURI()));
		return new ResponseEntity(httpHeaders, HttpStatus.TEMPORARY_REDIRECT);
	}
}
