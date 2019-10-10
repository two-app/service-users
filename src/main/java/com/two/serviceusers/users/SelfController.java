package com.two.serviceusers.users;

import com.two.http_api.model.Tokens;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
@AllArgsConstructor
public class SelfController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(SelfController.class);

    @PostMapping("/self")
    Tokens registerUser(@Valid @RequestBody UserRegistration user) {
        logger.info("Registering user {}, with email {}, and age {}.", user.getName(), user.getEmail(), user.getAge());
        Tokens tokens = this.userService.storeUser(user);
        logger.info("Responding with tokens: {}.", tokens);
        return tokens;
    }

}
