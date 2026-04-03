package magicofconch.soraadmin.batch.streak.writer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import magicOfConch.dto.FcmRequest;

@Component
@RequiredArgsConstructor
public class StreakWriter implements ItemWriter<FcmRequest> {

	private final RabbitTemplate rabbitTemplate;

	@Value("${rabbitmq.notification.exchange}")
	private String exchange;

	@Value("${rabbitmq.notification.routing-key}")
	private String routingKey;

	@Override
	public void write(Chunk<? extends FcmRequest> chunk) throws Exception {

		for (FcmRequest request : chunk) {
			rabbitTemplate.convertAndSend(exchange, routingKey, request);
		}
	}
}
