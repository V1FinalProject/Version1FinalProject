package com.example.tagging;

/**
 * A single tag raised against a nomination, with the reasoning behind it.
 */
public record FlagResult(String tagName, String reasoning) {
}
