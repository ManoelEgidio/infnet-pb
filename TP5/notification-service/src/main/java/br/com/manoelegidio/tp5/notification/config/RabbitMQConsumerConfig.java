package br.com.manoelegidio.tp5.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração do Consumidor RabbitMQ no microsserviço de notificações:
 * - Garante a topologia de Filas, Exchanges e Bindings (idempotente)
 * - Configura serializador Jackson JSON
 * - Configura Dead Letter Exchange (DLX) para mensagens rejeitadas
 */
@Configuration
public class RabbitMQConsumerConfig {

    public static final String DIRECT_EXCHANGE = "task.direct.exchange";
    public static final String TOPIC_EXCHANGE = "task.topic.exchange";
    public static final String FANOUT_EXCHANGE = "task.fanout.exchange";
    public static final String DLX_EXCHANGE = "task.dlx.exchange";

    public static final String NOTIFICATION_QUEUE = "task.notification.queue";
    public static final String URGENT_QUEUE = "task.urgent.queue";
    public static final String BROADCAST_QUEUE = "task.broadcast.queue";
    public static final String DEAD_LETTER_QUEUE = "task.dead-letter.queue";

    public static final String ROUTING_KEY_CREATED = "task.created";
    public static final String ROUTING_KEY_STATUS = "task.status.changed";
    public static final String ROUTING_KEY_COMPLETED = "task.completed";
    public static final String ROUTING_KEY_DELETED = "task.deleted";
    public static final String TOPIC_PATTERN_URGENT = "task.priority.URGENT";
    public static final String ROUTING_KEY_DLQ = "task.dead-letter";

    @Bean
    public DirectExchange directExchange() {
        return ExchangeBuilder.directExchange(DIRECT_EXCHANGE).durable(true).build();
    }

    @Bean
    public TopicExchange topicExchange() {
        return ExchangeBuilder.topicExchange(TOPIC_EXCHANGE).durable(true).build();
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return ExchangeBuilder.fanoutExchange(FANOUT_EXCHANGE).durable(true).build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(DLX_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue notificationQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", ROUTING_KEY_DLQ);
        return QueueBuilder.durable(NOTIFICATION_QUEUE).withArguments(args).build();
    }

    @Bean
    public Queue urgentQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", ROUTING_KEY_DLQ);
        return QueueBuilder.durable(URGENT_QUEUE).withArguments(args).build();
    }

    @Bean
    public Queue broadcastQueue() {
        return QueueBuilder.durable(BROADCAST_QUEUE).build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding bindingCreated(Queue notificationQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(notificationQueue).to(directExchange).with(ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding bindingStatus(Queue notificationQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(notificationQueue).to(directExchange).with(ROUTING_KEY_STATUS);
    }

    @Bean
    public Binding bindingCompleted(Queue notificationQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(notificationQueue).to(directExchange).with(ROUTING_KEY_COMPLETED);
    }

    @Bean
    public Binding bindingDeleted(Queue notificationQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(notificationQueue).to(directExchange).with(ROUTING_KEY_DELETED);
    }

    @Bean
    public Binding bindingUrgent(Queue urgentQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(urgentQueue).to(topicExchange).with(TOPIC_PATTERN_URGENT);
    }

    @Bean
    public Binding bindingBroadcast(Queue broadcastQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(broadcastQueue).to(fanoutExchange);
    }

    @Bean
    public Binding bindingDeadLetter(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(ROUTING_KEY_DLQ);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        // defaultRequeueRejected = false: mensagens rejeitadas com erro são enviadas para a Dead Letter Queue (DLQ)!
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
