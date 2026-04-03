package magicofconch.soraadmin.batch.streak.writer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import magicofconch.soraadmin.batch.streak.dto.FcmRequest;
import magicofconch.soraadmin.config.RabbitMQConfig;

@Component
@RequiredArgsConstructor
public class StreakWriter implements ItemWriter<FcmRequest> {

	private final RabbitTemplate rabbitTemplate;

	@Override
	public void write(Chunk<? extends FcmRequest> chunk) throws Exception {

		for (FcmRequest request : chunk) {
			rabbitTemplate.convertAndSend(
				RabbitMQConfig.EXCHANGE,
				RabbitMQConfig.ROUTING_KEY,
				request
			);
		}
	}
}
