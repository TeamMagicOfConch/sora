package magicofconch.sora.user.api.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import magicofconch.sora.user.api.dto.req.FcmRegisterReq;
import magicofconch.sora.user.api.dto.req.NotificationInfoReq;
import magicofconch.sora.util.Response;

@Tag(name = "Notification", description = "알림 관리 API")
public interface NotificationControllerDocs {

	@Operation(
		summary = "FCM 토큰 등록",
		description = "기기별 FCM 토큰을 등록합니다. 이미 등록된 기기(osId)의 경우 토큰을 업데이트합니다."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "FCM 토큰 등록 성공",
			content = @Content(schema = @Schema(implementation = Response.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "잘못된 요청 (유효하지 않은 토큰 형식 등)"
		),
		@ApiResponse(
			responseCode = "404",
			description = "등록되지 않은 기기 ID (OS_ID_NOT_FOUND)"
		)
	})
	ResponseEntity<Response> registerFcmToken(@RequestBody FcmRegisterReq req);

	@Operation(
		summary = "알림 설정 업데이트",
		description = "사용자의 알림 수신 동의 설정을 업데이트합니다. 현재는 Streak 알림 수신 여부만 지원합니다."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "알림 설정 업데이트 성공",
			content = @Content(schema = @Schema(implementation = Response.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "잘못된 요청"
		),
		@ApiResponse(
			responseCode = "401",
			description = "인증되지 않은 사용자"
		)
	})
	ResponseEntity<Response> updateNotification(@RequestBody NotificationInfoReq req);
}
