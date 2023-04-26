package openerp.notification.config.rabbitmq;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class RabbitConfig {

    public static final String NOTIFICATION_HEADERS_EXCHANGE = "notification_headers";

    @Value("${spring.rabbitmq.listener.simple.auto-startup}")
    private boolean autoStartup;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    public RabbitTemplate rabbitTemplate(MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.registerModule(new JavaTimeModule()); // optional, if using Java 8 date/time types
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // optional, if using Java 8 date/time types

        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // Configuration setting:
    // https://docs.spring.io/spring-amqp/docs/current/reference/html/#containerAttributes
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setAutoStartup(autoStartup);
        factory.setMessageConverter(jsonMessageConverter());
//        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        factory.setConcurrentConsumers(rabbitConfig.getConcurrentConsumers());
//        factory.setMaxConcurrentConsumers(rabbitConfig.getMaxConcurrentConsumers());
//        factory.setPrefetchCount(rabbitConfig.getPrefetchCount());
        // factory.setChannelTransacted(true); //try if there are faults, but this will
        // slow down the process

        return factory;

    }

    @Bean
    public Queue queueInApp() {
        Map<String, Object> args = new HashMap<>();
//        args.put("x-expires", 60000); // TTL in milliseconds
        return new Queue(UUID.randomUUID().toString(), false, true, true, args);
    }

//    @Bean
//    public Queue queueEmail() {
//        Map<String, Object> args = new HashMap<>();
////        args.put("x-expires", 60000); // TTL in milliseconds
//        return new Queue( UUID.randomUUID().toString(), false, true, true, args);
//    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(NOTIFICATION_HEADERS_EXCHANGE, true, false);
    }

//    @Bean
//    @DependsOn({"rabbitAdmin", "queueEmail", "headersExchange"})
//    public Binding binding1(RabbitAdmin rabbitAdmin, HeadersExchange headersExchange, Queue queueEmail) {
//        Map<String, Object> headers = new HashMap<>();
//        headers.put("channels.email", true);
//        Binding binding = BindingBuilder.bind(queueEmail).to(headersExchange).whereAny(headers).match();
//        rabbitAdmin.declareQueue(queueEmail);
//        rabbitAdmin.declareBinding(binding);
//        return binding;
//    }

    @Bean
    @DependsOn({"rabbitAdmin", "queueInApp", "headersExchange"})
    public Binding bindingInAppChannel(RabbitAdmin rabbitAdmin, HeadersExchange headersExchange, Queue queueInApp) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("channels.inApp", true);
        Binding binding = BindingBuilder.bind(queueInApp).to(headersExchange).whereAny(headers).match();
        rabbitAdmin.declareQueue(queueInApp);
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

}
