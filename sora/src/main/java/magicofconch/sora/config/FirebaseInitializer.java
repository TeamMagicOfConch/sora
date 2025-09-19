package magicofconch.sora.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Component
public class FirebaseInitializer {

	@PostConstruct
	public void init() throws IOException {
		FileInputStream serviceAccount =
			new FileInputStream("src/main/resources/firebase-adminsdk.json");

		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}
	}
}
