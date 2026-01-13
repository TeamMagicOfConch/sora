package magicofconch.soraadmin.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FcmConfig {

	@Value("classpath:firebase/firebase-admin-sdk.json")
	private Resource serviceAccountJson;

	@Bean
	public FirebaseApp firebaseApp() throws IOException {
		try (InputStream is = serviceAccountJson.getInputStream()) {
			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(is))
				.build();

			if (FirebaseApp.getApps().isEmpty()) {
				return FirebaseApp.initializeApp(options);
			} else {
				return FirebaseApp.getInstance();
			}
		}
	}

	@Bean
	public FirebaseMessaging firebaseMessaging(FirebaseApp app) {
		return FirebaseMessaging.getInstance(app);
	}
}
