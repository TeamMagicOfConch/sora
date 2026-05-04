package greensnapback0229.batchworker.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	@Value("${rabbitmq.notification.exchange}")
	private String exchange;

	@Value("${rabbitmq.notification.queue}")
	private String queue;

	@Value("${rabbitmq.notification.dlq}")
	private String dlq;

	@Value("${rabbitmq.notification.routing-key}")
	private String routingKey;

	@Value("${rabbitmq.notification.dlq-routing-key}")
	private String dlqRoutingKey;

	@Bean
	public DirectExchange notificationExchange() {
		return new DirectExchange(exchange);
	}

	@Bean
	public Queue notificationQueue() {
		return QueueBuilder.durable(queue)
			.withArgument("x-dead-letter-exchange", exchange)
			.withArgument("x-dead-letter-routing-key", dlqRoutingKey)
			.build();
	}

	@Bean
	public Queue notificationDlq() {
		return QueueBuilder.durable(dlq).build();
	}

	@Bean
	public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
		return BindingBuilder.bind(notificationQueue)
			.to(notificationExchange)
			.with(routingKey);
	}

	@Bean
	public Binding dlqBinding(Queue notificationDlq, DirectExchange notificationExchange) {
		return BindingBuilder.bind(notificationDlq)
			.to(notificationExchange)
			.with(dlqRoutingKey);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

}
