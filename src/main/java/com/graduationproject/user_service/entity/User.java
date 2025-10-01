package com.graduationproject.user_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "full_name")
    private String fullName;

    @Column
    private Integer gender;

    @Column
    private String avatar;

    @Column
    private String cover;

    @Column(columnDefinition = "text")
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column
    private LocalDate dob;

    @Column(name = "is_activated")
    private Boolean isActivated;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
} 
