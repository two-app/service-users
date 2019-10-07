package com.two.serviceusers;

import com.two.serviceusers.users.UserRegistration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class SelfControllerTest {

    @Autowired
    private ApplicationContext applicationContext;

    private WebTestClient webTestClient() {
        return WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Nested
    class PostSelf {
        @Test
        @DisplayName("it should return 200 OK with tokens if the body is a valid user")
        void validUser() {
            UserRegistration userRegistration = new UserRegistration(
                    "gerry2@two.com", "rawPassword", "Gerry", 22
            );

            webTestClient().post()
                    .uri("/self")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromObject(userRegistration))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.refreshToken").isNotEmpty()
                    .jsonPath("$.accessToken").isNotEmpty();
        }

        @Test
        @DisplayName("it should return a bad request if the body is empty")
        void emptyBody() {
            webTestClient().post()
                    .uri("/self")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.empty())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                    .expectBody()
                    .jsonPath("$.errors").isArray()
                    .jsonPath("$.errors").isEqualTo("Badly formed HTTP request.");
        }
    }

}
