package magicofconch.sora.util;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ResponseCode {

	//General
	SUCCESS(200, "GEN-000", HttpStatus.OK, "Success"),

	//Authentication
	NO_REFRESH_TOKEN(400, "SEC-001", HttpStatus.BAD_REQUEST, "Refresh token is blank"),
	REFRESH_TOKEN_EXPIRED(401, "SEC-001", HttpStatus.UNAUTHORIZED, "Refresh token is expired"),
	REFRESH_TOKEN_NOT_MATCH(401, "SEC-003", HttpStatus.UNAUTHORIZED, "Refresh token is expired"),

	//User
	REGISTER_SUCCESS(200, "USR-000", HttpStatus.OK, "User is registered"),
	USER_ALREADY_REGISTERED(400, "USR-001", HttpStatus.BAD_REQUEST, "This User is already registered please login"),
	USER_NOT_FOUND(404, "USR-002", HttpStatus.NOT_FOUND, "Can't find user"),
	LOGIN_FAIL(404, "USR-003", HttpStatus.NOT_FOUND, "Login failed"),

	//Review
	REVIEW_GENERAL_FAIL(400, "RVW-000", HttpStatus.BAD_REQUEST, "Review fail for system problem"),
	REVIEW_ALREADY_EXIST(400, "RVW-001", HttpStatus.BAD_REQUEST, "Review is already exist"),
	REVIEW_NOT_EXIST(400, "RVW-002", HttpStatus.BAD_REQUEST, "Review is not exist"),
	REVIEW_JSON_ERROR(400, "RVW-003", HttpStatus.BAD_REQUEST, "Can't resolve request body"),
	REVIEW_INQUIRY_PARAM_WRONG(400, "RVW-004", HttpStatus.BAD_REQUEST, "Inquiry param is wrong"),

	//Streak
	STREAK_NOT_REGISTERED(400, "STR-004", HttpStatus.BAD_REQUEST, "Stream is already registered"),

	//OS ID
	OS_ID_NOT_FOUND(400, "OID-001", HttpStatus.NOT_FOUND, "Os Id not found"),

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
