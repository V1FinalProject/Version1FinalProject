package com.example.tagging.nomination;

/** Body of PUT /api/nominations/{id}/favourite. */
public record FavouriteRequest(boolean favourite) {
}
