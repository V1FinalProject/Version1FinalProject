package com.example.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// Explicit ComponentScan is needed because com.example.tagging is a sibling
// package, not a sub-package of com.example.hello, so the default
// @SpringBootApplication scan would not find it.
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.hello", "com.example.tagging"})
public class HelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }
}
