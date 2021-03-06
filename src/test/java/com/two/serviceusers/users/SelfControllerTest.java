package com.two.serviceusers.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.two.http_api.model.PublicApiModel.UserRegistration;
import com.two.http_api.model.Tokens;
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
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = SelfController.class)
@AutoConfigureMockMvc
class SelfControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    UserService userService;

    @Nested
    class PostSelf {
        @Test
        @DisplayName("it should return 200 OK with tokens if the body is a valid user")
        void validUser() throws Exception {
            UserRegistration userRegistration = new UserRegistration(
                    "gerry2@two.com", "rawPassword", "Gerry", "Fletcher", true, true
            );

            Tokens tokens = new Tokens("refresh-token", "access-token");
            when(userService.storeUser(userRegistration)).thenReturn(tokens);

            postSelf(userRegistration).andExpect(status().isOk())
                    .andExpect(content().bytes(om.writeValueAsBytes(tokens)));

            verify(userService).storeUser(userRegistration);
        }

        @Test
        @DisplayName("it should return bad request if the body is empty")
        void emptyBody() throws Exception {
            postSelf(null).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Badly formed HTTP request."));
        }

        @Test
        @DisplayName("it should return a bad request with validation errors if the user is provided but invalid")
        void invalidUser() throws Exception {
            UserRegistration invalidUserRegistration = new UserRegistration(
                    "bademail", "password", "Gerry", "Fletcher", true, true
            );

            postSelf(invalidUserRegistration).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Email must be valid."));
        }

        @Test
        @DisplayName("it should return a bad request if terms and conditions not agreed")
        void termsNotAgreed() throws Exception {
            UserRegistration invalidUserRegistration = new UserRegistration(
                    "admin@two.com", "Passw0rd", "Gerry", "Fletcher", false, true
            );

            postSelf(invalidUserRegistration).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Terms & Conditions must be accepted."));
        }

        @Test
        @DisplayName("it should return a bad request if age not agreed")
        void ageNotCorrect() throws Exception {
            UserRegistration invalidUserRegistration = new UserRegistration(
                    "admin@two.com", "Passw0rd", "Gerry", "Fletcher", true, false
            );

            postSelf(invalidUserRegistration).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("You must be over 16 to join."));
        }

        private ResultActions postSelf(UserRegistration userRegistration) throws Exception {
            return mockMvc.perform(post("/self")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userRegistration))
            );
        }
    }

}
