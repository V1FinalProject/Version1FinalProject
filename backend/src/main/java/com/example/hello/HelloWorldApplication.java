package com.example.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

// Explicit ComponentScan is needed because com.example.tagging is a sibling
// package, not a sub-package of com.example.hello, so the default
// @SpringBootApplication scan would not find it. Spring Data's Mongo
// repository scan is a separate mechanism from @ComponentScan (it defaults to
// just this class's own package), so it needs the same base package spelled
// out again via @EnableMongoRepositories or it silently finds zero
// repositories - which fails every bean that depends on one.
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.hello", "com.example.tagging"})
@EnableMongoRepositories(basePackages = "com.example.tagging")
public class HelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }
}
