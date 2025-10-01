package com.graduationproject.user_service.service.impl;

import com.graduationproject.user_service.dto.request.UserRegistrationRequestDTO;
import com.graduationproject.user_service.dto.request.UserUpdateRequestDTO;
import com.graduationproject.common.dto.UserResponseDTO;
import com.graduationproject.user_service.entity.User;
import com.graduationproject.user_service.exception.DuplicateResourceException;
import com.graduationproject.user_service.exception.ResourceNotFoundException;
import com.graduationproject.user_service.repository.UserRepository;
import com.graduationproject.user_service.service.UserService;
import com.graduationproject.user_service.service.UserEventPublisher;
import com.graduationproject.user_service.event.UserCreatedEventForAuthService;
import com.graduationproject.user_service.event.UserCreatedEventForAuthorizationService;
import com.graduationproject.user_service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

import java.util.List;
import com.graduationproject.user_service.exception.ExternalServiceException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;
    private final UserMapper userMapper;

    @Value("${authorization-service.url}")
    private String authorizationServiceUrl;

    @Value("${authorization-service.token}")
    private String authorizationServiceToken;

    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRegistrationRequestDTO request) {
        log.debug("Registering new user with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.error("Username already exists: {}", request.getUsername());
            throw new DuplicateResourceException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Email already exists: {}", request.getEmail());
            throw new DuplicateResourceException("Email already exists");
        }

        User user = userMapper.toEntity(request);
        user.setIsActivated(true);
        user.setIsDeleted(false);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Publish event to RabbitMQ
        try {
            UserCreatedEventForAuthService event = UserCreatedEventForAuthService.builder()
                    .userId(savedUser.getId().toString())
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .password(request.getPassword())
                    .build();
            userEventPublisher.publishUserCreated(event);
            log.info("User created event published for user ID: {}", savedUser.getId());

            UserCreatedEventForAuthorizationService eventForAuthorizationService = UserCreatedEventForAuthorizationService
                    .builder()
                    .userId(savedUser.getId())
                    .role("client")
                    .build();
            userEventPublisher.publishUserCreated(eventForAuthorizationService);
            log.info("User created event published for user ID: {}", savedUser.getId());
        } catch (Exception e) {
            log.error("Failed to publish user created event for user ID: {}", savedUser.getId(), e);
            throw new RuntimeException("Failed to publish user created event", e);
        }

        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.debug("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        log.debug("Fetching all users");
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO request) {
        log.debug("Updating user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                log.error("Username already exists: {}", request.getUsername());
                throw new DuplicateResourceException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.error("Email already exists: {}", request.getEmail());
                throw new DuplicateResourceException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getFirst_name() != null) {
            user.setFirstName(request.getFirst_name());
        }
        if (request.getLast_name() != null) {
            user.setLastName(request.getLast_name());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getCover() != null) {
            user.setCover(request.getCover());
        }
        if (request.getDob() != null) {
            user.setDob(request.getDob());
        }
        if (request.getIsActivated() != null) {
            user.setIsActivated(request.getIsActivated());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());
        // Gọi sang authorization-service nếu có roles
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            try {
                String urlStr = authorizationServiceUrl + "/api/roles/user/" + updatedUser.getId();
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + authorizationServiceToken);
                conn.setDoOutput(true);
                String jsonBody = new com.fasterxml.jackson.databind.ObjectMapper()
                        .writeValueAsString(request.getRoles());
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonBody.getBytes());
                }
                int responseCode = conn.getResponseCode();
                if (responseCode < 200 || responseCode >= 300) {
                    throw new ExternalServiceException("Update roles failed, response code: " + responseCode);
                }
            } catch (Exception e) {
                throw new ExternalServiceException("Error calling authorization-service: " + e.getMessage(), e);
            }
        }
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setIsDeleted(true);
        userRepository.save(user);
        log.info("User soft-deleted successfully with ID: {}", id);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
