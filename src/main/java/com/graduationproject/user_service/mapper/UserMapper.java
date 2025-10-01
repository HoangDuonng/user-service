package com.graduationproject.user_service.mapper;

import com.graduationproject.user_service.dto.request.UserRegistrationRequestDTO;
import com.graduationproject.common.dto.UserResponseDTO;
import com.graduationproject.user_service.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements BaseMapper<User, UserResponseDTO> {

    @Override
    public UserResponseDTO toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .first_name(user.getFirstName())
                .last_name(user.getLastName())
                .fullName(user.getFullName())
                .gender(user.getGender() != null ? user.getGender().toString() : null)
                .avatar(user.getAvatar())
                .cover(user.getCover())
                .dob(user.getDob() != null ? user.getDob().toString() : null)
                .phone(user.getPhone())
                .address(user.getAddress())
                .isActivated(user.getIsActivated())
                .isDeleted(user.getIsDeleted())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toEntity(UserRegistrationRequestDTO request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setFirstName(request.getFirst_name());
        user.setLastName(request.getLast_name());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        return user;
    }
}
