package greensnapback0229.batchworker.worker;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import magicOfConch.dto.FcmRequest;

@Slf4j
@Component
public class FcmNotificationWorker {

	// TODO: 성능 테스트 완료 후 FcmUtil로 복원
	// private final FcmUtil fcmUtil;

	private final RestTemplate restTemplate = new RestTemplate();
	
	@Value("${streak.fcm-simulator.url}")
	private String fcmSimulatorUrl;

	@RabbitListener(queues = "${rabbitmq.notification.queue}")
	public void consumeAndSendFcm(FcmRequest request) {
		log.info("Received FCM request: token={}, osType={}", request.token(), request.osType());

		// TODO: 실제 FCM 발송으로 복원
		// fcmUtil.send(request);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<FcmRequest> entity = new HttpEntity<>(request, headers);
		restTemplate.postForEntity(fcmSimulatorUrl, entity, String.class);
	}
}
