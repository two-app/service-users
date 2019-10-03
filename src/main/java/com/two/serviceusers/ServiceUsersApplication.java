package com.two.serviceusers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.two")
public class ServiceUsersApplication {

    public static void main(String[] args) {
        System.setProperty("org.jooq.no-logo", "true");
        SpringApplication.run(ServiceUsersApplication.class, args);
    }

}
