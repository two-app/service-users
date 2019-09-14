package com.two.serviceusers.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class UserController {

    @GetMapping("/user")
    public void getUser(@RequestParam String email) {
        System.out.println("Reached! " + email);
    }

}
