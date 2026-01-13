package magicofconch.soraadmin.util;

import org.springframework.stereotype.Component;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;
import magicofconch.soraadmin.batch.streak.dto.FcmRequest;

@Component
@RequiredArgsConstructor
public class FcmUtil {

	private final FirebaseMessaging firebaseMessaging;

	public void sendMessage(FcmRequest fcmRequest) {

		switch (fcmRequest.osType()) {

			case IOS:
				sendIos(fcmRequest);
				break;

			case ANDROID:
				sendAndroid(fcmRequest);
				break;

			case WINDOW:
				sendWeb(fcmRequest);
				break;
		}

	}

	public void sendIos(FcmRequest fcmRequest) {

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
			e.printStackTrace();
		}

	}

	public void sendAndroid(FcmRequest fcmRequest) {

		Notification notification = Notification.builder()
			.setTitle(fcmRequest.body())
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
			e.printStackTrace();
		}

	}

	public void sendWeb(FcmRequest fcmRequest) {

		Notification notification = Notification.builder()
			.setTitle(fcmRequest.body())
			.setBody(fcmRequest.body())
			.build();

		Message message = Message.builder()
			.setToken(fcmRequest.token())
			.setNotification(notification)
			.build();

		try {
			firebaseMessaging.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
