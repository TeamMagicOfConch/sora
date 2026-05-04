package greensnapback0229.batchworker.util;

import org.springframework.stereotype.Component;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import magicOfConch.dto.FcmRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmUtil {

	private final FirebaseMessaging firebaseMessaging;

	public void send(FcmRequest fcmRequest) {
		switch (fcmRequest.osType()) {
			case IOS -> sendIos(fcmRequest);
			case ANDROID -> sendAndroid(fcmRequest);
			case WINDOW -> sendWeb(fcmRequest);
		}
	}

	private void sendIos(FcmRequest fcmRequest) {
		ApsAlert apsAlert = ApsAlert.builder()
			.setTitle(fcmRequest.title())
			.setBody(fcmRequest.body())
			.build();

		ApnsConfig apnsConfig = ApnsConfig.builder()
			.setAps(Aps.builder()
				.setAlert(apsAlert)
				.setSound("default")
				.setContentAvailable(true)
				.build())
			.build();

		Message message = Message.builder()
			.setToken(fcmRequest.token())
			.setApnsConfig(apnsConfig)
			.build();

		try {
			firebaseMessaging.send(message);
		} catch (Exception e) {
			log.error("Failed to send iOS FCM: {}", e.getMessage());
			throw new RuntimeException("FCM send failed", e);
		}
	}

	private void sendAndroid(FcmRequest fcmRequest) {
		Notification notification = Notification.builder()
			.setTitle(fcmRequest.title())
			.setBody(fcmRequest.body())
			.build();

		AndroidConfig androidConfig = AndroidConfig.builder()
			.setPriority(AndroidConfig.Priority.HIGH)
			.build();

		Message message = Message.builder()
			.setToken(fcmRequest.token())
			.setNotification(notification)
			.setAndroidConfig(androidConfig)
			.build();

		try {
			firebaseMessaging.send(message);
		} catch (Exception e) {
			log.error("Failed to send Android FCM: {}", e.getMessage());
			throw new RuntimeException("FCM send failed", e);
		}
	}

	private void sendWeb(FcmRequest fcmRequest) {
		Notification notification = Notification.builder()
			.setTitle(fcmRequest.title())
			.setBody(fcmRequest.body())
			.build();

		Message message = Message.builder()
			.setToken(fcmRequest.token())
			.setNotification(notification)
			.build();

		try {
			firebaseMessaging.send(message);
		} catch (Exception e) {
			log.error("Failed to send Web FCM: {}", e.getMessage());
			throw new RuntimeException("FCM send failed", e);
		}
	}
}
