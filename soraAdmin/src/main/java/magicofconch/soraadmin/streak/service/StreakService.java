package magicofconch.soraadmin.streak.service;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreakService {

	private final FirebaseMessaging firebaseMessaging;

	public String sendNotification(String targetToken, String title, String body) {

		Notification notification = Notification.builder()
			.setTitle(title)
			.setBody(body)
			.build();

		Message message = Message.builder()
			.setToken(targetToken)
			.setNotification(notification)
			.build();

		try {
			return firebaseMessaging.send(message); // messageId 반환
		} catch (FirebaseMessagingException e) {
			log.info("messeging error");
			throw new RuntimeException();
		}
	}

}
