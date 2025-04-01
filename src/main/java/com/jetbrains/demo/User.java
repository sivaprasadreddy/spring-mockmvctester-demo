package com.jetbrains.demo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record User(
        Long id,
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        String email,
        @NotBlank(message = "Password is required")
        String password) {
}
