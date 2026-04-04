package greensnapback0229.batchworker.worker;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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

	private final RestTemplate restTemplate = new RestTemplate();
	private final AtomicInteger processedCount = new AtomicInteger(0);
	private final AtomicLong firstMessageTime = new AtomicLong(0);
	@Value("${streak.fcm-simulator.url}")
	private String fcmSimulatorUrl;

	@RabbitListener(queues = "${rabbitmq.notification.queue}")
	public void consumeAndSendFcm(FcmRequest request) {
		long now = System.currentTimeMillis();
		firstMessageTime.compareAndSet(0, now);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<FcmRequest> entity = new HttpEntity<>(request, headers);
		// restTemplate.postForEntity(fcmSimulatorUrl, entity, String.class);

		int count = processedCount.incrementAndGet();
		long elapsed = (now - firstMessageTime.get()) / 1000;

		if (count % 1000 == 0 || count == 10000) {
			log.info("Processed {}/10000, elapsed={}s, rate={} msg/s",
				count, elapsed, elapsed > 0 ? count / elapsed : count);
		}
	}
}
