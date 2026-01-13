package magicofconch.sora.user.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.user.api.docs.NotificationControllerDocs;
import magicofconch.sora.user.api.dto.req.FcmRegisterReq;
import magicofconch.sora.user.api.dto.req.NotificationInfoReq;
import magicofconch.sora.user.service.NotificationService;
import magicofconch.sora.util.Response;

@RestController
@RequestMapping("/auth/notification")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDocs {

	private final NotificationService notificationService;

	@PostMapping("/token")
	@Override
	public ResponseEntity<Response> registerFcmToken(@RequestBody FcmRegisterReq req) {
		notificationService.registerFcm(req);
		return ResponseEntity.ok(Response.ok());
	}

	@PostMapping
	@Override
	public ResponseEntity<Response> updateNotification(@RequestBody NotificationInfoReq req) {
		notificationService.updateNotification(req);
		return ResponseEntity.ok(Response.ok());
	}
}
