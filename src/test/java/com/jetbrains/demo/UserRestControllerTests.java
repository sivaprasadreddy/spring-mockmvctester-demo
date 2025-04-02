package com.jetbrains.demo;

import com.jetbrains.demo.UserRestController.RegistrationResponse;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class UserRestControllerTests {
    @Autowired
    MockMvcTester mockMvcTester;

    @Test
    void getUserByIdSuccessful() {
        assertThat(mockMvcTester.get().uri("/api/users/1")).hasStatusOk();
    }

    @Test
    void userRegistrationSuccessful() {
        String requestBody = """
                {
                    "email": "siva@gmail.com",
                    "password": "secret",
                    "name": "Siva"
                }
                """;

        // using fluent API for executing request and assertions
        assertThat(mockMvcTester
                .post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                    .hasStatus(HttpStatus.CREATED)
                    .bodyJson()
                    .isLenientlyEqualTo("""
                            {
                              "name": "Siva",
                              "email": "siva@gmail.com",
                              "role": "ROLE_USER"
                            }
                            """);

        //split test execution and assertions
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(testResult)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .isLenientlyEqualTo("""
                        {
                          "name": "Siva",
                          "email": "siva@gmail.com",
                          "role": "ROLE_USER"
                        }
                        """);

        // compare response json with a classpath resource
        var expected = new ClassPathResource("/user-registration-response.json",
                UserRestControllerTests.class);
        assertThat(testResult)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .isLenientlyEqualTo(expected);

        // compare response by mapping it to a java object
        assertThat(testResult)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(RegistrationResponse.class)
                .satisfies(response -> {
                    assertThat(response.name()).isEqualTo("Siva");
                    assertThat(response.email()).isEqualTo("siva@gmail.com");
                    assertThat(response.role()).isEqualTo("ROLE_USER");
                });
    }

    @Test
    void shouldFailToRegisterWithExistingEmail() {
        String requestBody = """
                {
                    "email": "admin@gmail.com",
                    "password": "secret",
                    "name": "Administrator"
                }
                """;

        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(testResult)
                .failure()
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with email admin@gmail.com already exists");
    }

    @Test
    void assertUnhandledException() {
        String requestBody = """
                {
                    "email": "user123@gmail.com",
                    "password": "password",
                    "name": "User"
                }
                """;

        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(testResult)
                .failure()
                .isInstanceOf(ServletException.class)
                .cause().isInstanceOf(IllegalArgumentException.class)
                .hasMessage("You chose password poorly!");
    }
}