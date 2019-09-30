package com.two.serviceusers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.two")
public class ServiceUsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceUsersApplication.class, args);
    }

}
