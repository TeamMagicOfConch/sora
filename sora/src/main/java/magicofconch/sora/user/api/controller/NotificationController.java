package magicofconch.sora.user.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.user.api.dto.req.NotificationInfoReq;
import magicofconch.sora.user.service.NotificationService;
import magicofconch.sora.util.Response;

@RestController
@RequestMapping("/auth/notification")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	// /**
	//  * FCM 기기별 토큰 등록 및 알림 설정
	//  * @param req : fcm 등록 기기 정보 및 발행된 토큰
	//  * @return : 성공여부
	//  */
	// public ResponseEntity<Response> registerFcmToken(FcmRegisterReq req) {
	//
	// }

	/**
	 * 사용자 알림 설정 값 반영
	 * @param req : 알림 설정 값
	 * @return : 설정 성공 여부
	 */
	public ResponseEntity<Response> updateNotification(NotificationInfoReq req) {
		notificationService.updateNotification(req);

		return ResponseEntity.ok().body(Response.ok());
	}
}
