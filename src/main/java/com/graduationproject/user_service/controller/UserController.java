package com.graduationproject.user_service.controller;

import com.graduationproject.user_service.dto.request.UserRegistrationRequestDTO;
import com.graduationproject.user_service.dto.request.UserUpdateRequestDTO;
import com.graduationproject.common.dto.UserResponseDTO;
import com.graduationproject.user_service.service.UserService;
import com.graduationproject.user_service.dto.response.DetailResponseDTO;
import com.graduationproject.user_service.exception.ExternalServiceException;
import com.graduationproject.user_service.exception.ErrorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegistrationRequestDTO request) {
        UserResponseDTO createdUser = userService.registerUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        UserResponseDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<DetailResponseDTO<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        DetailResponseDTO<UserResponseDTO> response = new DetailResponseDTO<>(
                users,
                users.size(),
                "success",
                true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<DetailResponseDTO<UserResponseDTO>> getAllActiveUsers() {
        List<UserResponseDTO> users = userService.getAllUsers()
                .stream()
                .filter(u -> Boolean.FALSE.equals(u.getIsDeleted()))
                .toList();
        DetailResponseDTO<UserResponseDTO> response = new DetailResponseDTO<>(
                users,
                users.size(),
                "success",
                true);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO request,
            HttpServletRequest httpRequest) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok(updatedUser);
        } catch (ExternalServiceException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Cập nhật user thất bại: " + e.getMessage(),
                    LocalDateTime.now(),
                    httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Xoá user thành công"));
    }
}
