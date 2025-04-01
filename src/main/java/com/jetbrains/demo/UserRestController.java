package com.jetbrains.demo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class UserRestController {
    private static final Logger log = LoggerFactory.getLogger(UserRestController.class);
    private final UserRepository userRepository;

    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/users/{id}")
    ResponseEntity<User> getUserById(@PathVariable Long id) {
        var user = userRepository.findById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/api/users")
    ResponseEntity<RegistrationResponse> creatUser(@RequestBody @Valid RegistrationRequest req) {
        log.info("Registration request for email: {}", req.email());
        if(req.email().equalsIgnoreCase("admin@gmail.com")) {
            throw new UserAlreadyExistsException("User with email "+ req.email()+" already exists");
        }
        if(req.password().equalsIgnoreCase("password")) {
            throw new IllegalArgumentException("You chose password poorly!");
        }
        userRepository.create(new User(null, req.name(), req.email(), req.password()));
        var response = new RegistrationResponse(req.name(), req.email(), "ROLE_USER");
        return ResponseEntity.status(CREATED.value()).body(response);
    }

    public record RegistrationRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Invalid email address")
            String email,
            @NotBlank(message = "Password is required")
            String password,
            @NotBlank(message = "Name is required")
            String name) {
    }

    public record RegistrationResponse(
            String name,
            String email,
            String role) {
    }
}
