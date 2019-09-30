package com.two.serviceusers.users;

import lombok.Value;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Value
public class UserRegistration {
    @Email
    private final String email;
    @Length(min = 5)
    private final String password;

    @Length(min = 5)
    private final String name;

    @Min(13)
    @Max(90)
    private final int age;
}
