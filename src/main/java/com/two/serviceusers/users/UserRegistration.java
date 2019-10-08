package com.two.serviceusers.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistration {
    @NotEmpty(message = "Email must be provided.")
    @Email(message = "Email must be valid.")
    private String email;

    @NotEmpty(message = "Password must be provided.")
    @Length(min = 5, message = "Password must be at least 5 characters long.")
    private String password;

    @NotEmpty(message = "Name must be provided.")
    @Length(min = 5, message = "Name must be at least 5 characters long.")
    private String name;

    @Min(value = 13, message = "You must be at least 13.")
    @Max(value = 90, message = "You can't be older than 90.")
    private int age;
}
