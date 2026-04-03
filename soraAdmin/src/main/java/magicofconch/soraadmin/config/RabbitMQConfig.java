package magicofconch.soraadmin.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	public static final String EXCHANGE = "notification-exchange";
	public static final String QUEUE = "notification-queue";
	public static final String DLQ = "notification-queue.dlq";
	public static final String ROUTING_KEY = "notification.streak";
	public static final String DLQ_ROUTING_KEY = "notification.streak.dlq";

	@Bean
	public DirectExchange notificationExchange() {
		return new DirectExchange(EXCHANGE);
	}

	@Bean
	public Queue notificationQueue() {
		return QueueBuilder.durable(QUEUE)
			.withArgument("x-dead-letter-exchange", EXCHANGE)
			.withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
			.build();
	}

	@Bean
	public Queue notificationDlq() {
		return QueueBuilder.durable(DLQ).build();
	}

	@Bean
	public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
		return BindingBuilder.bind(notificationQueue)
			.to(notificationExchange)
			.with(ROUTING_KEY);
	}

	@Bean
	public Binding dlqBinding(Queue notificationDlq, DirectExchange notificationExchange) {
		return BindingBuilder.bind(notificationDlq)
			.to(notificationExchange)
			.with(DLQ_ROUTING_KEY);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
