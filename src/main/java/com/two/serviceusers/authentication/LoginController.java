package com.two.serviceusers.authentication;

import com.two.http_api.api.PublicApiContracts;
import com.two.http_api.model.Tokens;
import com.two.serviceusers.users.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.two.http_api.model.PublicApiModel.UserLogin;

@RestController
@Validated
@AllArgsConstructor
public class LoginController implements PublicApiContracts.PostLogin {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final UserService userService;

    @PostMapping("/login")
    @Override
    public Tokens login(@RequestBody @Valid UserLogin userLogin) {
        logger.info("Attempting login for for email: {}.", userLogin.getEmail());
        Tokens tokens = this.userService.loginUser(userLogin.getEmail(), userLogin.getPassword());
        logger.info("Responding with tokens: {}.", tokens);
        return tokens;
    }

}
