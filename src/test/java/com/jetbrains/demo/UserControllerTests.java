package com.jetbrains.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTests {

    @Autowired
    MockMvcTester mockMvcTester;

    @Test
    void shouldGetUserById() {
        var result = mockMvcTester.get().uri("/users/1").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("user")
                .model()
                .containsKeys("user")
                .containsEntry("user", new User(1L, "Siva", "siva@gmail.com", "siva"));
    }

    @Test
    void shouldCreateUserSuccessfully() {
        var result = mockMvcTester.post().uri("/users")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Test User 4")
                .param("email", "testuser4@gmail.com")
                .param("password", "testuser4")
                .exchange()
                ;

        assertThat(result)
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/users")
                .flash().containsKey("successMessage")
                .hasEntrySatisfying("successMessage",
                        value -> assertThat(value).isEqualTo("User saved successfully"))
                ;
    }

    @Test
    void shouldGetErrorsWhenUserDataIsNotValid() {
        var result = mockMvcTester.post().uri("/users")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "")
                .param("email", "testuser4gmail.com")
                .param("password", "pwd")
                .exchange()
                ;

        assertThat(result)
                .model()
                .extractingBindingResult("user")
                .hasErrorsCount(2)
                .hasFieldErrors("name", "email")
                ;
    }
}
