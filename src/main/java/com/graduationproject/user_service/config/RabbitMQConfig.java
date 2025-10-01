package com.graduationproject.user_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    // User Events Exchange
    @Value("${rabbitmq.exchange.user-events}")
    private String exchangeUserEvents;

    // Auth Events Exchange
    @Value("${rabbitmq.exchange.auth-events}")
    private String exchangeAuthEvents;

    @Value("${rabbitmq.routing-key.user-created-auth}")
    private String routingKeyUserCreatedAuth;

    @Value("${rabbitmq.routing-key.user-created-authorization}")
    private String routingKeyUserCreatedAuthorization;

    @Value("${rabbitmq.routing-key.auth-login}")
    private String routingKeyAuthLogin;

    @Value("${rabbitmq.routing-key.auth-login-response}")
    private String routingKeyAuthLoginResponse;

    @Value("${rabbitmq.queue.user-created-auth}")
    private String queueUserCreatedAuth;

    @Value("${rabbitmq.queue.user-created-authorization}")
    private String queueUserCreatedAuthorization;

    // Auth Login Queue
    @Value("${rabbitmq.queue.auth-login}")
    private String queueAuthLogin;

    // Auth Login Response Queue
    @Value("${rabbitmq.queue.auth-login-response}")
    private String queueAuthLoginResponse;

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(exchangeUserEvents);
    }

    @Bean
    public DirectExchange authEventsExchange() {
        return new DirectExchange(exchangeAuthEvents);
    }

    @Bean
    public Queue userCreatedAuthQueue() {
        return new Queue(queueUserCreatedAuth);
    }

    @Bean
    public Queue userCreatedAuthorizationQueue() {
        return new Queue(queueUserCreatedAuthorization);
    }

    @Bean
    public Queue authLoginQueue() {
        return new Queue(queueAuthLogin);
    }

    @Bean
    public Queue authLoginResponseQueue() {
        return new Queue(queueAuthLoginResponse);
    }

    @Bean
    public Binding bindingAuth() {
        return BindingBuilder.bind(userCreatedAuthQueue())
                .to(userEventsExchange())
                .with(routingKeyUserCreatedAuth);
    }

    @Bean
    public Binding bindingAuthorization() {
        return BindingBuilder.bind(userCreatedAuthorizationQueue())
                .to(userEventsExchange())
                .with(routingKeyUserCreatedAuthorization);
    }

    @Bean
    public Binding authLoginBinding() {
        return BindingBuilder.bind(authLoginQueue())
                .to(authEventsExchange())
                .with(routingKeyAuthLogin);
    }

    @Bean
    public Binding authLoginResponseBinding() {
        return BindingBuilder.bind(authLoginResponseQueue())
                .to(authEventsExchange())
                .with(routingKeyAuthLoginResponse);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("LoginResponseMessageDTO", com.graduationproject.common.dto.LoginResponseMessageDTO.class);
        idClassMapping.put("LoginMessageDTO", com.graduationproject.common.dto.LoginMessageDTO.class);
        typeMapper.setIdClassMapping(idClassMapping);
        typeMapper.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
