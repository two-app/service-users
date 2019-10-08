package com.two.serviceusers.authentication;

import com.two.http_api.model.Tokens;
import com.two.serviceusers.users.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@RestController
@Validated
@AllArgsConstructor
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final UserService userService;

    @PostMapping("login")
    Tokens login(@RequestBody @Valid UserLogin userLogin) {
        logger.info("Attempting login for for email: {}.", userLogin.getEmail());
        Tokens tokens = this.userService.loginUser(userLogin.getEmail(), userLogin.getPassword());
        logger.info("Responding with tokens: {}.", tokens);
        return tokens;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserLogin {
        @NotEmpty(message = "Valid email must be provided.")
        @Email(message = "Valid email must be provided.")
        String email;

        @NotEmpty(message = "Valid password must be provided.")
        @Length(min = 3, message = "Valid password must be provided.")
        String password;
    }

}
