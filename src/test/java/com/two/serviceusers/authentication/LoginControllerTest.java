package com.two.serviceusers.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.two.http_api.model.Tokens;
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
import org.springframework.test.web.servlet.ResultActions;

import static com.two.http_api.model.PublicApiModel.UserLogin;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LoginController.class)
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    UserService userService;

    @Nested
    class PostLogin {
        @Test
        @DisplayName("it should return tokens for a valid login")
        void validLogin() throws Exception {
            UserLogin userLogin = new UserLogin(
                    "gerry@two.com",
                    "rawPassword"
            );

            Tokens tokens = new Tokens("test-refresh-token", "test-access-token");
            when(userService.loginUser(userLogin.getEmail(), userLogin.getPassword())).thenReturn(tokens);

            postLogin(userLogin).andExpect(status().isOk())
                    .andExpect(content().bytes(om.writeValueAsBytes(tokens)));

            verify(userService).loginUser(userLogin.getEmail(), userLogin.getPassword());
        }

        @Test
        @DisplayName("it should return bad request if the request body is empty")
        void emptyBody() throws Exception {
            postLogin(null).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Badly formed HTTP request."));
        }

        @Test
        @DisplayName("it should return a bad request with validation errors if the user is provided but invalid")
        void invalidUser() throws Exception {
            UserLogin userLogin = new UserLogin(
                    "bademail",
                    "password"
            );

            postLogin(userLogin).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Valid email must be provided."));
        }

        private ResultActions postLogin(UserLogin userLogin) throws Exception {
            return mvc.perform(post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userLogin))
            );
        }
    }

}