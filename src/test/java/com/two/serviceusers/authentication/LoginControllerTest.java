package com.two.serviceusers.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.two.http_api.model.Tokens;
import com.two.serviceusers.exceptions.ErrorResponse;
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

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LoginController.class)
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    private ObjectMapper om = new ObjectMapper();

    @Nested
    class PostLogin {
        @Test
        @DisplayName("it should return tokens for a valid login")
        void validLogin() throws Exception {
            LoginController.UserLogin userLogin = new LoginController.UserLogin(
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
            ErrorResponse expectedErrorResponse = new ErrorResponse(singletonList("Badly formed HTTP request."));

            postLogin(null).andExpect(status().isBadRequest())
                    .andExpect(content().bytes(om.writeValueAsBytes(expectedErrorResponse)));
        }

        @Test
        @DisplayName("it should return a bad request with validation errors if the user is provided but invalid")
        void invalidUser() throws Exception {
            LoginController.UserLogin userLogin = new LoginController.UserLogin(
                    "bademail",
                    "p"
            );

            postLogin(userLogin).andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Valid email must be provided.")))
                    .andExpect(content().string(containsString("Valid password must be provided.")));
        }

        private ResultActions postLogin(LoginController.UserLogin userLogin) throws Exception {
            return mockMvc.perform(
                    post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsBytes(userLogin))
            );
        }
    }

}