package com.graduationproject.user_service.service;

import com.graduationproject.user_service.dto.request.UserRegistrationRequestDTO;
import com.graduationproject.user_service.dto.request.UserUpdateRequestDTO;
import com.graduationproject.user_service.dto.response.UserResponseDTO;
import java.util.List;

public interface UserService {
    UserResponseDTO registerUser(UserRegistrationRequestDTO request);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByUsername(String username);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateUser(Long id, UserUpdateRequestDTO request);
    void deleteUser(Long id);
} 