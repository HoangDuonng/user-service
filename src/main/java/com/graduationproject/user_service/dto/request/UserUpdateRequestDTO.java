package com.graduationproject.user_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserUpdateRequestDTO {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contain letters and numbers")
    private String username;

    private String first_name;
    private String last_name;

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^(84|0[3|5|7|8|9])+([0-9]{8})$", message = "Invalid Vietnamese phone number format")
    private String phone;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    private Integer gender;

    private String avatar;

    private String cover;

    private LocalDate dob;

    private Boolean isActivated;

    private List<Integer> roles;
}
