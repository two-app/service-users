package com.two.serviceusers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.two.http_api.model.Tokens;
import com.two.serviceusers.exceptions.ErrorResponse;
import com.two.serviceusers.users.UserRegistration;
import com.two.serviceusers.users.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = SelfController.class)
@AutoConfigureMockMvc
public class SelfControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    ObjectMapper om = new ObjectMapper();

    @Nested
    class PostSelf {
        @Test
        @DisplayName("it should return 200 OK with tokens if the body is a valid user")
        void validUser() throws Exception {
            UserRegistration userRegistration = new UserRegistration(
                    "gerry2@two.com", "rawPassword", "Gerry", 22
            );

            Tokens tokens = new Tokens("refresh-token", "access-token");
            when(userService.storeUser(userRegistration)).thenReturn(tokens);

            mockMvc.perform(post("/self")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userRegistration))
            )
                    .andExpect(status().isOk())
                    .andExpect(content().bytes(om.writeValueAsBytes(tokens)));
        }

        @Test
        @DisplayName("it should return bad request if the body is empty")
        void emptyBody() throws Exception {
            ErrorResponse expectedErrorResponse = new ErrorResponse(singletonList("Badly formed HTTP request."));

            mockMvc.perform(post("/self")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().bytes(om.writeValueAsBytes(expectedErrorResponse)));
        }

        @Test
        @DisplayName("it should return a bad request with validation errors if the user is provided but invalid")
        void invalidUser() throws Exception {
            UserRegistration invalidUserRegistration = new UserRegistration(
                    "bademail", "pass", "n", 1
            );

            mockMvc.perform(post("/self")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invalidUserRegistration))
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Email must be valid.")))
                    .andExpect(content().string(containsString("Password must be at least 5 characters long.")))
                    .andExpect(content().string(containsString("Name must be at least 5 characters long.")))
                    .andExpect(content().string(containsString("You must be at least 13.")));
        }
    }

}

//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@AutoConfigureWebTestClient
//class SelfControllerTest {
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    private WebTestClient webTestClient() {
//        return WebTestClient.bindToApplicationContext(applicationContext).build();
//    }
//
//    @Nested
//    class PostSelf {
//        @Test
//        @DisplayName("it should return 200 OK with tokens if the body is a valid user")
//        void validUser() {
//            UserRegistration userRegistration = new UserRegistration(
//                    "gerry2@two.com", "rawPassword", "Gerry", 22
//            );
//
//            webTestClient().post()
//                    .uri("/self")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(BodyInserters.fromObject(userRegistration))
//                    .accept(MediaType.APPLICATION_JSON)
//                    .exchange()
//                    .expectStatus().isOk()
//                    .expectBody()
//                    .jsonPath("$.refreshToken").isNotEmpty()
//                    .jsonPath("$.accessToken").isNotEmpty();
//        }
//
//        @Test
//        @DisplayName("it should return a bad request if the body is empty")
//        void emptyBody() {
//            webTestClient().post()
//                    .uri("/self")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(BodyInserters.empty())
//                    .accept(MediaType.APPLICATION_JSON)
//                    .exchange()
//                    .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
//                    .expectBody()
//                    .jsonPath("$.errors").isArray()
//                    .jsonPath("$.errors").isEqualTo("Badly formed HTTP request.");
//        }
//    }
//
//}
