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
import org.springframework.test.web.servlet.ResultActions;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
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

    private ObjectMapper om = new ObjectMapper();

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

            postSelf(userRegistration).andExpect(status().isOk())
                    .andExpect(content().bytes(om.writeValueAsBytes(tokens)));

            verify(userService).storeUser(userRegistration);
        }

        @Test
        @DisplayName("it should return bad request if the body is empty")
        void emptyBody() throws Exception {
            ErrorResponse expectedErrorResponse = new ErrorResponse(singletonList("Badly formed HTTP request."));

            postSelf(null).andExpect(status().isBadRequest())
                    .andExpect(content().bytes(om.writeValueAsBytes(expectedErrorResponse)));
        }

        @Test
        @DisplayName("it should return a bad request with validation errors if the user is provided but invalid")
        void invalidUser() throws Exception {
            UserRegistration invalidUserRegistration = new UserRegistration(
                    "bademail", "pass", "n", 1
            );

            postSelf(invalidUserRegistration).andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Email must be valid.")))
                    .andExpect(content().string(containsString("Password must be at least 5 characters long.")))
                    .andExpect(content().string(containsString("Name must be at least 5 characters long.")))
                    .andExpect(content().string(containsString("You must be at least 13.")));
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
