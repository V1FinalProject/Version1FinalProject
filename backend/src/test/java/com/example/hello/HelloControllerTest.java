package com.example.hello;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelloControllerTest {

    private final HelloController controller = new HelloController();

    @Test
    void returnsHelloWorldGreeting() {
        assertThat(controller.helloWorld()).isEqualTo("Hello, World!");
    }
}
