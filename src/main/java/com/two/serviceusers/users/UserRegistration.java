package com.two.serviceusers.users;

import com.two.http_api.model.Age;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserRegistration {
    @NotEmpty(message = "Email must be provided.")
    @Email(message = "Email must be valid.")
    private String email;

    @NotEmpty(message = "Password must be provided.")
    @Length(min = 5, message = "Password must be at least 5 characters long.")
    private String password;

    @NotEmpty(message = "Name must be provided.")
    @Length(min = 5, message = "Name must be at least 5 characters long.")
    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Age(13)
    private LocalDate dob;
}
