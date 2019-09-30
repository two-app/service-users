package com.two.serviceusers.self;

import com.two.http_api.model.Tokens;
import com.two.serviceusers.users.UserRegistration;
import com.two.serviceusers.users.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@Validated
public class SelfController {

    private final UserService userService;

    public SelfController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/self")
    Tokens registerUser(@RequestBody @NotNull UserRegistration registerUser) {
        return this.userService.storeUser(registerUser);
    }

}