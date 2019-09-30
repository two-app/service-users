package com.two.serviceusers.users;

import lombok.Value;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Value
public class UserRegistration {
    @NotEmpty(message = "Email must be provided.")
    @Email
    private final String email;

    @NotEmpty(message = "Password must be provided.")
    @Length(min = 5)
    private final String password;

    @NotEmpty(message = "Name must be provided.")
    @Length(min = 5)
    private final String name;

    @Min(13)
    @Max(90)
    private final int age;
}
