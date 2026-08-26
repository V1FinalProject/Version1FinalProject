package com.example.tagging.claude;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Reads the API key from the ANTHROPIC_API_KEY environment variable - never
// commit a key to application.properties or any file in this repo.
@Configuration
public class AnthropicClientConfig {

    @Bean
    public AnthropicClient anthropicClient() {
        return AnthropicOkHttpClient.fromEnv();
    }
}
