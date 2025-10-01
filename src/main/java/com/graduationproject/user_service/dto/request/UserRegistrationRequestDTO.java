package com.graduationproject.user_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegistrationRequestDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contain letters and numbers")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
        message = "Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character"
    )
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String first_name;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String last_name;

    @Pattern(
        regexp = "^(84|0[3|5|7|8|9])+([0-9]{8})$",
        message = "Invalid Vietnamese phone number format"
    )
    private String phone;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;
} 