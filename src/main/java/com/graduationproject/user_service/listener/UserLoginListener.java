package com.graduationproject.user_service.listener;

import com.graduationproject.common.dto.LoginMessageDTO;
import com.graduationproject.common.dto.LoginResponseMessageDTO;
import com.graduationproject.user_service.entity.User;
import com.graduationproject.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLoginListener {
    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.auth-events}")
    private String exchangeAuthEvents;

    @Value("${rabbitmq.routing-key.auth-login-response}")
    private String routingKeyAuthLoginResponse;

    @RabbitListener(queues = "${rabbitmq.queue.auth-login}")
    public void handleLoginMessage(LoginMessageDTO message) {
        log.info("Received login request for email: {}", message.getEmail());
        try {
            User user = userService.findByEmail(message.getEmail());

            LoginResponseMessageDTO response = LoginResponseMessageDTO.builder()
                    .userId(user.getId().intValue())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .first_name(user.getFirstName())
                    .last_name(user.getLastName())
                    .fullName(user.getFullName())
                    .phone(user.getPhone())
                    .address(user.getAddress())
                    .isActivated(user.getIsActivated())
                    .isDeleted(user.getIsDeleted())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .correlationId(message.getCorrelationId())
                    .success(true)
                    .build();

            rabbitTemplate.convertAndSend(
                    exchangeAuthEvents,
                    routingKeyAuthLoginResponse,
                    response);
        } catch (Exception e) {
            log.error("Error processing login request for email: {}", message.getEmail(), e);

            LoginResponseMessageDTO response = LoginResponseMessageDTO.builder()
                    .correlationId(message.getCorrelationId())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();

            rabbitTemplate.convertAndSend(
                    exchangeAuthEvents,
                    routingKeyAuthLoginResponse,
                    response);
        }
    }
}
