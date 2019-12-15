package com.two.serviceusers.users;

import com.two.http_api.api.PublicApiContracts;
import com.two.http_api.model.Tokens;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.two.http_api.model.PublicApiModel.UserRegistration;

@RestController
@Validated
@AllArgsConstructor
public class SelfController implements PublicApiContracts.PostSelf {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(SelfController.class);

    @PostMapping("/self")
    @Override
    public Tokens registerUser(@Valid @RequestBody UserRegistration user) {
        logger.info("Registering user {} {}, with email {}.", user.getFirstName(), user.getLastName(), user.getEmail());
        Tokens tokens = this.userService.storeUser(user);
        logger.info("Responding with tokens: {}.", tokens);
        return tokens;
    }

}
