package com.graduationproject.user_service.service;

import com.graduationproject.user_service.event.UserCreatedEvent;
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

    @Value("${rabbitmq.routing-key.user-created}")
    private String routingKeyUserCreated;

    public void publishUserCreated(UserCreatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    exchangeUserEvents,
                    routingKeyUserCreated,
                    event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish user created event", e);
        }
    }
}
