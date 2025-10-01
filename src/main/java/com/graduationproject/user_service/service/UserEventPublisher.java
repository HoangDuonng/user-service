package com.graduationproject.user_service.service;

import com.graduationproject.user_service.event.UserCreatedEventForAuthService;
import com.graduationproject.user_service.event.UserCreatedEventForAuthorizationService;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.user-events}")
    private String exchangeUserEvents;

    @Value("${rabbitmq.routing-key.user-created-auth}")
    private String routingKeyUserCreatedAuth;

    @Value("${rabbitmq.routing-key.user-created-authorization}")
    private String routingKeyUserCreatedAuthorization;

    public void publishUserCreated(UserCreatedEventForAuthService event) {
        try {
            rabbitTemplate.convertAndSend(
                exchangeUserEvents, // exchange
                routingKeyUserCreatedAuth,         // routing key
                event                   // object event
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish user created event", e);
        }
    }

    public void publishUserCreated(UserCreatedEventForAuthorizationService event) {
        try {
            rabbitTemplate.convertAndSend(
                exchangeUserEvents, // exchange
                routingKeyUserCreatedAuthorization,    // routing key
                event                   // object event
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish user created event", e);
        }
    }
}
