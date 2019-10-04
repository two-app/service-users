package com.two.serviceusers;

import com.two.http_api.model.Tokens;
import com.two.serviceusers.users.UserRegistration;
import com.two.serviceusers.users.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Validated
@AllArgsConstructor
public class SelfController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(SelfController.class);

    @PostMapping("/self")
    Tokens registerUser(@RequestBody @NotNull @Valid UserRegistration user) {
        logger.info("Registering user {}, with email {}, and age {}.", user.getName(), user.getEmail(), user.getAge());
        Tokens tokens = this.userService.storeUser(user);
        logger.info("Responding with tokens: {}.", tokens);
        return tokens;
    }

}
