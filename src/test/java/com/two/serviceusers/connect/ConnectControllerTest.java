package com.two.serviceusers.connect;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.two.http_api.model.Tokens;
import org.junit.jupiter.api.DisplayName;
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
@WebMvcTest(controllers = ConnectController.class)
@AutoConfigureMockMvc
class ConnectControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    ConnectService connectService;

    @Test
    @DisplayName("it should return new tokens after connecting the users")
    void connectsUsersAndReturnsTokens() throws Exception {
        Tokens tokens = new Tokens("refresh-token", "access-token");
        when(connectService.connectUsers(1, "partnerConnectCode")).thenReturn(tokens);

        postConnect(JWT(), "partnerConnectCode")
                .andExpect(status().isOk())
                .andExpect(content().bytes(om.writeValueAsBytes(tokens)));

        verify(connectService).connectUsers(1, "partnerConnectCode");

    }


    @Test
    @DisplayName("it should throw a Bad Request exception if the user is already connected")
    void alreadyConnected() throws Exception {
        postConnect(connectedJWT(), "testConnectCode")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User is already connected."));
    }

    private ResultActions postConnect(String jwt, String partnerConnectCode) throws Exception {
        return mvc.perform(post("/connect/" + partnerConnectCode)
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    private String connectedJWT() {
        return JWT.create()
                .withClaim("uid", 1)
                .withClaim("pid", 2)
                .withClaim("cid", 3)
                .sign(Algorithm.HMAC256("test"));
    }

    private String JWT() {
        return JWT.create()
                .withClaim("uid", 1)
                .withClaim("connectCode", "userConnectCode")
                .sign(Algorithm.HMAC256("test"));
    }

}